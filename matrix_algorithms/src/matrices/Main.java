package matrices;

import java.math.BigInteger;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Punto de entrada unico.
 * 15 algoritmos de multiplicacion de matrices segun:
 * "Multiplicacion de matrices Grandes - 2026-1.docx"
 * Todos corren en paralelo. Log en tiempo real. Estimado previo.
 */
public class Main {

    static final String[] NOMBRES = {
        "1.NaivOnArray",
        "2.NaivLoopUnrollingTwo",
        "3.NaivLoopUnrollingFour",
        "4.WinogradOriginal",
        "5.WinogradScaled",
        "6.StrassenNaiv",
        "7.StrassenWinograd",
        "8.III.3-SeqBlock",
        "9.III.4-ParBlock",
        "10.III.5-EnhParBlock",
        "11.IV.3-SeqBlock",
        "12.IV.4-ParBlock",
        "13.IV.5-EnhParBlock",
        "14.V.3-SeqBlock",
        "15.V.4-ParBlock"
    };

    static final double[] EXPONENTE = {
        3.0, 3.0, 3.0, 3.0, 3.0,
        2.807, 2.807,
        3.0, 3.0, 3.0,
        3.0, 3.0, 3.0,
        3.0, 3.0
    };

    static final double[] FACTOR_OPS = {
        1.0, 0.5, 0.25, 0.5, 0.5,
        1.0, 0.83,
        1.0, 1.0, 1.0,
        1.0, 1.0, 1.0,
        1.0, 1.0
    };

    static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");


    //  CAMBIA ESTOS DOS VALORES Y PRESIONA RUN
    // ============================================================
    static final int N_POTENCIA = 13;   // tamano = 2^N_POTENCIA  
    static final int DIGITOS    = 6 ;   // digitos por valor (minimo 6)

    public static void main(String[] args) throws Exception {
        // Siempre usa las constantes N_POTENCIA y DIGITOS definidas arriba
        // Los argumentos externos son ignorados - cambia las constantes directamente
        int n           = N_POTENCIA;
        int digitosFijo = DIGITOS;
        int modoValores = 2;

        if (n > 30) { System.out.println("ERROR: n demasiado grande."); return; }

        int tamano = (int) Math.pow(2, n);
        int digitos;
        if (modoValores == 2) {
            digitos = Math.max(6, digitosFijo); // minimo 6 segun requerimiento
        } else {
            long cuadrado = (long) tamano * tamano;
            digitos = (int) Math.max(6, 50_000_000L / cuadrado);
        }

        long ramLibreMB = Runtime.getRuntime().maxMemory() / 1_000_000;
        long ramNecesariaMB = (long) tamano * tamano * digitos * 2L / 1_000_000;
        if (modoValores == 1 && ramNecesariaMB > ramLibreMB * 0.8) {
            System.out.println("ADVERTENCIA: necesita ~" + ramNecesariaMB +
                " MB pero hay ~" + ramLibreMB + " MB disponibles.");
            System.out.println("Usa n<=" + sugerirN(ramLibreMB) + " o corre con:");
            System.out.println("  java -Xmx" + (ramNecesariaMB/1000+2) +
                "g -cp matrix_algorithms/bin matrices.Main");
            return;
        }

        // Calibracion para estimado
        BigInteger[][] cA = MatrixUtils.generarBig(16, digitos);
        BigInteger[][] cB = MatrixUtils.generarBig(16, digitos);
        long tCal = System.currentTimeMillis();
        Algoritmo1_NaivOnArray.multiplicar(cA, cB);
        long msCal = Math.max(1, System.currentTimeMillis() - tCal);
        double k = (double) msCal / Math.pow(16, 3);

        System.out.println();
        System.out.println("Matriz  : " + tamano + " x " + tamano + " (2^" + n + ")");
        System.out.println("Digitos : " + digitos + " por valor (minimo 6 segun requerimiento)");
        System.out.printf("RAM est.: ~%.1f MB%n", (double) ramNecesariaMB);
        System.out.println("Hilos   : " + NOMBRES.length + " (uno por algoritmo)");
        System.out.println("CPU     : " + Runtime.getRuntime().availableProcessors() + " nucleos disponibles");
        System.out.println();

        System.out.println("ESTIMADO DE TIEMPO:");
        linea();
        System.out.printf("%-28s %-20s%n", "Algoritmo", "Tiempo estimado");
        linea();
        for (int i = 0; i < NOMBRES.length; i++) {
            double ops = FACTOR_OPS[i] * Math.pow(tamano, EXPONENTE[i]);
            long est = Math.round(k * ops);
            System.out.printf("%-28s %s%n", NOMBRES[i], formatMs(est));
        }
        linea();
        System.out.println();

        log("Generando matrices " + tamano + "x" + tamano + " con " + digitos + " digitos...");
        BigInteger[][] A  = MatrixUtils.generarBig(tamano, digitos);
        BigInteger[][] B  = MatrixUtils.generarBig(tamano, digitos);
        BigInteger[][] Ap = MatrixUtils.padearPot2Big(A);
        BigInteger[][] Bp = MatrixUtils.padearPot2Big(B);
        log("Matrices generadas. Lanzando " + NOMBRES.length + " hilos...");
        System.out.println();

        long[] tiempos = new long[NOMBRES.length];
        ExecutorService pool = Executors.newFixedThreadPool(NOMBRES.length);
        List<Future<long[]>> futuros = new ArrayList<>();

        futuros.add(pool.submit(() -> correr(0,  () -> Algoritmo1_NaivOnArray.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correr(1,  () -> Algoritmo2_NaivLoopUnrollingTwo.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correr(2,  () -> Algoritmo3_NaivLoopUnrollingFour.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correr(3,  () -> Algoritmo4_WinogradOriginal.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correr(4,  () -> Algoritmo5_WinogradScaled.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correr(5,  () -> Algoritmo6_StrassenNaiv.multiplicar(Ap, Bp))));
        futuros.add(pool.submit(() -> correr(6,  () -> Algoritmo7_StrassenWinograd.multiplicar(Ap, Bp))));
        futuros.add(pool.submit(() -> correr(7,  () -> Algoritmo8_III3_SequentialBlock.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correrEx(8,  () -> Algoritmo9_III4_ParallelBlock.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correrEx(9,  () -> Algoritmo10_III5_EnhancedParallelBlock.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correr(10, () -> Algoritmo11_IV3_SequentialBlock.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correrEx(11, () -> Algoritmo12_IV4_ParallelBlock.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correrEx(12, () -> Algoritmo13_IV5_EnhancedParallelBlock.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correr(13, () -> Algoritmo14_V3_SequentialBlock.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correrEx(14, () -> Algoritmo15_V4_ParallelBlock.multiplicar(A, B))));

        pool.shutdown();
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        for (Future<long[]> f : futuros) {
            long[] r = f.get();
            tiempos[(int) r[0]] = r[1];
        }

        // Calcular estimados para guardar
        long[] estimados = new long[NOMBRES.length];
        for (int i = 0; i < NOMBRES.length; i++)
            estimados[i] = Math.round(k * FACTOR_OPS[i] * Math.pow(tamano, EXPONENTE[i]));

        System.out.println();
        System.out.println("RESULTADOS FINALES");
        linea();
        System.out.printf("%-28s %-15s %-12s %-10s%n",
            "Algoritmo", "Estimado", "Real", "Ratio");
        linea();
        for (int i = 0; i < NOMBRES.length; i++) {
            String realStr = tiempos[i] < 0 ? "ERROR" : formatMs(tiempos[i]);
            double ratio = (estimados[i] > 0 && tiempos[i] >= 0)
                ? (double) tiempos[i] / estimados[i] : 0;
            System.out.printf("%-28s %-15s %-12s %s%n",
                NOMBRES[i],
                formatMs(estimados[i]),
                realStr,
                tiempos[i] >= 0 ? String.format("%.2fx", ratio) : "N/A");
        }
        linea();

        // Guardar resultados en archivo (acumula cada ejecucion para graficar)
        ResultadoGuardador.guardar(tamano, digitos, NOMBRES,
            estimados, tiempos, k, EXPONENTE, FACTOR_OPS);

        System.out.println("Prueba finalizada.");
    }

    // Para metodos que lanzan Exception (algoritmos paralelos)
    interface ThrowingSupplier { BigInteger[][] get() throws Exception; }

    static long[] correrEx(int idx, ThrowingSupplier tarea) {
        LocalTime inicio = LocalTime.now();
        log("[INICIO] " + NOMBRES[idx] + " a las " + inicio.format(HORA));
        long t = System.currentTimeMillis();
        try {
            tarea.get();
            long ms = System.currentTimeMillis() - t;
            log("[FIN]   " + NOMBRES[idx] +
                " | inicio: " + inicio.format(HORA) +
                " | fin: " + LocalTime.now().format(HORA) +
                " | duracion: " + formatMs(ms));
            return new long[]{idx, ms};
        } catch (Exception | Error e) {
            log("[ERROR] " + NOMBRES[idx] + " -> " + e.getClass().getSimpleName());
            return new long[]{idx, -1};
        }
    }

    static long[] correr(int idx, Runnable tarea) {
        LocalTime inicio = LocalTime.now();
        log("[INICIO] " + NOMBRES[idx] + " a las " + inicio.format(HORA));
        long t = System.currentTimeMillis();
        try {
            tarea.run();
            long ms = System.currentTimeMillis() - t;
            log("[FIN]   " + NOMBRES[idx] +
                " | inicio: " + inicio.format(HORA) +
                " | fin: " + LocalTime.now().format(HORA) +
                " | duracion: " + formatMs(ms));
            return new long[]{idx, ms};
        } catch (Exception | Error e) {
            log("[ERROR] " + NOMBRES[idx] + " -> " + e.getClass().getSimpleName());
            return new long[]{idx, -1};
        }
    }

    static String formatMs(long ms) {
        if (ms < 1000)    return ms + " ms";
        if (ms < 60000)   return String.format("%.2f s", ms / 1000.0);
        if (ms < 3600000) return String.format("%.2f min", ms / 60000.0);
        return String.format("%.2f h", ms / 3600000.0);
    }

    static void log(String msg) {
        System.out.println("[" + LocalTime.now().format(HORA) + "] " + msg);
    }

    static void linea() {
        for (int i = 0; i < 60; i++) System.out.print("-");
        System.out.println();
    }

    static int sugerirN(long ramLibreMB) {
        for (int p = 14; p >= 1; p--) {
            long tam = (long) Math.pow(2, p);
            long nec = tam * tam * 6 * 2 / 1_000_000;
            if (nec <= ramLibreMB * 0.8) return p;
        }
        return 4;
    }
}
