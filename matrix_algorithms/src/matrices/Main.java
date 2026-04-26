package matrices;

import java.math.BigInteger;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// Punto de entrada del programa.
// Ejecuta los 15 algoritmos de multiplicacion de matrices en paralelo,
// mide sus tiempos reales, los compara con el estimado teorico
// y guarda todo en resultados.csv y resultados.txt para graficar.
public class Main {

    // Nombres de los 15 algoritmos segun la Tabla 1 del enunciado
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

    // Exponente del orden de complejidad de cada algoritmo
    // Naiv y bloques = O(n^3), Strassen = O(n^2.807)
    static final double[] EXPONENTE = {
        3.0, 3.0, 3.0, 3.0, 3.0,
        2.807, 2.807,
        3.0, 3.0, 3.0,
        3.0, 3.0, 3.0,
        3.0, 3.0
    };

    // Factor que ajusta el numero de operaciones reales respecto a n^exp
    // LoopUnrollingTwo hace la mitad de iteraciones (0.5), Four un cuarto (0.25), etc.
    static final double[] FACTOR_OPS = {
        1.0, 0.5, 0.25, 0.5, 0.5,
        1.0, 0.83,
        1.0, 1.0, 1.0,
        1.0, 1.0, 1.0,
        1.0, 1.0
    };

    // Formato de hora para los logs en consola
    static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    //  CONFIGURACION DE LA PRUEBA 
    // Cambia estos dos valores y presiona Run para ejecutar un caso de prueba.
    // El tamano de la matriz sera 2^N_POTENCIA x 2^N_POTENCIA.
    // Caso 1 sugerido: N_POTENCIA=9  (512x512),   DIGITOS=6
    // Caso 2 sugerido: N_POTENCIA=11 (2048x2048), DIGITOS=6
    static final int N_POTENCIA = 12;   // tamano = 2^N_POTENCIA
    static final int DIGITOS    = 4;    // digitos por valor (enunciado pide minimo 6)

    public static void main(String[] args) throws Exception {

        int n           = N_POTENCIA;
        int digitosFijo = DIGITOS;
        int modoValores = 2; // modo 2 = usa DIGITOS fijo; modo 1 = calcula segun tamano

        // Validacion basica del tamano
        if (n > 30) { System.out.println("ERROR: n demasiado grande."); return; }

        // Calcula el tamano real de la matriz: 2^n
        int tamano = (int) Math.pow(2, n);

        // Determina cuantos digitos tendra cada BigInteger
        int digitos;
        if (modoValores == 2) {
            digitos = digitosFijo; // usa exactamente el valor de DIGITOS
        } else {
            // modo automatico: ajusta digitos segun el tamano para no agotar RAM
            long cuadrado = (long) tamano * tamano;
            digitos = (int) Math.max(1, 50_000_000L / cuadrado);
        }

        // Muestra informacion de memoria disponible en la JVM
        long ramMaxMB   = Runtime.getRuntime().maxMemory()  / 1_000_000;
        long ramUsadaMB = (Runtime.getRuntime().totalMemory()
                         - Runtime.getRuntime().freeMemory()) / 1_000_000;
        long ramLibreMB = ramMaxMB - ramUsadaMB;
        System.out.printf("RAM JVM max : %d MB%n", ramMaxMB);
        System.out.printf("RAM libre   : %d MB%n", ramLibreMB);
        System.out.println("(Sin limite - modo estres maximo)");

        //  CALIBRACION 
        // Mide cuanto tarda NaivOnArray en una matriz 16x16 para calcular
        // la constante k = ms por operacion BigInteger en esta maquina.
        // Con k se estima el tiempo de cada algoritmo antes de correrlo.
        BigInteger[][] cA = MatrixUtils.generarBig(16, digitos);
        BigInteger[][] cB = MatrixUtils.generarBig(16, digitos);
        long tCal = System.currentTimeMillis();
        Algoritmo1_NaivOnArray.multiplicar(cA, cB);
        long msCal = Math.max(1, System.currentTimeMillis() - tCal);
        double k = (double) msCal / Math.pow(16, 3); // ms por operacion

        // Muestra resumen de la prueba
        System.out.println();
        System.out.println("Matriz  : " + tamano + " x " + tamano + " (2^" + n + ")");
        System.out.println("Digitos : " + digitos + " por valor");
        System.out.printf("RAM libre JVM: %d MB%n", ramLibreMB);
        System.out.println("Hilos   : " + NOMBRES.length + " (uno por algoritmo)");
        System.out.println("CPU     : " + Runtime.getRuntime().availableProcessors() + " nucleos disponibles");
        System.out.println();

        // Muestra el tiempo estimado de cada algoritmo antes de ejecutar
        // Estimado = k * factor * n^exponente
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

        //  GENERACION DE MATRICES 
        // Genera A y B con BigInteger de 'digitos' digitos cada uno.
        // Ap y Bp son versiones rellenas a la siguiente potencia de 2,
        // necesarias para Strassen que requiere n = potencia de 2.
        log("Generando matrices " + tamano + "x" + tamano + " con " + digitos + " digitos...");
        logMemoria("antes-generar");
        BigInteger[][] A  = MatrixUtils.generarBig(tamano, digitos);
        BigInteger[][] B  = MatrixUtils.generarBig(tamano, digitos);
        logMemoria("despues-generar");
        BigInteger[][] Ap = MatrixUtils.padearPot2Big(A); // para Strassen
        BigInteger[][] Bp = MatrixUtils.padearPot2Big(B); // para Strassen
        logMemoria("despues-padear");
        log("Matrices generadas. Lanzando " + NOMBRES.length + " hilos...");
        System.out.println();

        //  EJECUCION EN PARALELO 
        // Cada algoritmo corre en su propio hilo del pool.
        // Si un algoritmo falla (OutOfMemoryError, etc.) registra -1 y continua.
        long[] tiempos = new long[NOMBRES.length];
        ExecutorService pool = Executors.newFixedThreadPool(NOMBRES.length);
        List<Future<long[]>> futuros = new ArrayList<>();

        // Algoritmos iterativos (Runnable - no lanzan Exception checked)
        futuros.add(pool.submit(() -> correr(0,  () -> Algoritmo1_NaivOnArray.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correr(1,  () -> Algoritmo2_NaivLoopUnrollingTwo.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correr(2,  () -> Algoritmo3_NaivLoopUnrollingFour.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correr(3,  () -> Algoritmo4_WinogradOriginal.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correr(4,  () -> Algoritmo5_WinogradScaled.multiplicar(A, B))));
        // Strassen usa Ap/Bp (matrices rellenas a potencia de 2)
        futuros.add(pool.submit(() -> correr(5,  () -> Algoritmo6_StrassenNaiv.multiplicar(Ap, Bp))));
        futuros.add(pool.submit(() -> correr(6,  () -> Algoritmo7_StrassenWinograd.multiplicar(Ap, Bp))));
        futuros.add(pool.submit(() -> correr(7,  () -> Algoritmo8_III3_SequentialBlock.multiplicar(A, B))));
        // Algoritmos paralelos internos (ThrowingSupplier - lanzan Exception checked)
        futuros.add(pool.submit(() -> correrEx(8,  () -> Algoritmo9_III4_ParallelBlock.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correrEx(9,  () -> Algoritmo10_III5_EnhancedParallelBlock.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correr(10, () -> Algoritmo11_IV3_SequentialBlock.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correrEx(11, () -> Algoritmo12_IV4_ParallelBlock.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correrEx(12, () -> Algoritmo13_IV5_EnhancedParallelBlock.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correr(13, () -> Algoritmo14_V3_SequentialBlock.multiplicar(A, B))));
        futuros.add(pool.submit(() -> correrEx(14, () -> Algoritmo15_V4_ParallelBlock.multiplicar(A, B))));

        // Espera a que todos los hilos terminen
        pool.shutdown();
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        // Recoge los tiempos reales de cada hilo
        for (Future<long[]> f : futuros) {
            long[] r = f.get();
            tiempos[(int) r[0]] = r[1]; // r[0]=indice, r[1]=tiempo en ms
        }

        // Calcula los tiempos estimados finales con la k calibrada
        long[] estimados = new long[NOMBRES.length];
        for (int i = 0; i < NOMBRES.length; i++)
            estimados[i] = Math.round(k * FACTOR_OPS[i] * Math.pow(tamano, EXPONENTE[i]));

        // Muestra tabla de resultados: estimado vs real vs ratio
        System.out.println();
        System.out.println("RESULTADOS FINALES");
        linea();
        System.out.printf("%-28s %-15s %-12s %-10s%n",
            "Algoritmo", "Estimado", "Real", "Ratio");
        linea();
        for (int i = 0; i < NOMBRES.length; i++) {
            String realStr = tiempos[i] < 0 ? "ERROR" : formatMs(tiempos[i]);
            // ratio = real / estimado: ~1.0 acorde, <1 mas rapido, >1 mas lento
            double ratio = (estimados[i] > 0 && tiempos[i] >= 0)
                ? (double) tiempos[i] / estimados[i] : 0;
            System.out.printf("%-28s %-15s %-12s %s%n",
                NOMBRES[i],
                formatMs(estimados[i]),
                realStr,
                tiempos[i] >= 0 ? String.format("%.2fx", ratio) : "N/A");
        }
        linea();

        // Guarda los resultados en CSV y TXT de forma acumulativa
        // Cada ejecucion se numera como Caso 1, Caso 2, etc.
        ResultadoGuardador.guardar(tamano, digitos, NOMBRES,
            estimados, tiempos, k, EXPONENTE, FACTOR_OPS);

        System.out.println("Prueba finalizada.");
    }

    // Interfaz para algoritmos que lanzan Exception checked (los paralelos internos)
    interface ThrowingSupplier { BigInteger[][] get() throws Exception; }

    // Ejecuta un algoritmo paralelo (que lanza Exception) en su hilo
    // Captura cualquier error (incluyendo OutOfMemoryError) y devuelve -1
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

    // Ejecuta un algoritmo iterativo (Runnable) en su hilo
    // Registra memoria al inicio y al fin para ver el consumo del algoritmo
    // Captura cualquier error y devuelve -1 sin detener los demas hilos
    static long[] correr(int idx, Runnable tarea) {
        LocalTime inicio = LocalTime.now();
        log("[INICIO] " + NOMBRES[idx] + " a las " + inicio.format(HORA));
        logMemoria(NOMBRES[idx] + "-inicio");
        long t = System.currentTimeMillis();
        try {
            tarea.run();
            long ms = System.currentTimeMillis() - t;
            logMemoria(NOMBRES[idx] + "-fin");
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

    // Convierte milisegundos a formato legible: ms, s, min o h
    static String formatMs(long ms) {
        if (ms < 1000)    return ms + " ms";
        if (ms < 60000)   return String.format("%.2f s", ms / 1000.0);
        if (ms < 3600000) return String.format("%.2f min", ms / 60000.0);
        return String.format("%.2f h", ms / 3600000.0);
    }

    // Imprime un mensaje con timestamp en formato [HH:mm:ss]
    static void log(String msg) {
        System.out.println("[" + LocalTime.now().format(HORA) + "] " + msg);
    }

    // Imprime una linea separadora de 60 guiones
    static void linea() {
        for (int i = 0; i < 60; i++) System.out.print("-");
        System.out.println();
    }

    // Imprime el uso actual de memoria de la JVM en MB
    // Util para ver cuanto consume cada algoritmo durante su ejecucion
    static void logMemoria(String momento) {
        Runtime rt = Runtime.getRuntime();
        long total = rt.totalMemory() / 1_000_000;
        long libre = rt.freeMemory()  / 1_000_000;
        long usado = total - libre;
        long max   = rt.maxMemory()   / 1_000_000;
        System.out.printf("[MEM][%s] usado=%dMB libre=%dMB total=%dMB max=%dMB%n",
            momento, usado, libre, total, max);
    }

    // Sugiere el mayor N_POTENCIA que cabe en la RAM disponible
    // Usado como referencia cuando hay poco heap disponible
    static int sugerirN(long ramLibreMB) {
        for (int p = 14; p >= 1; p--) {
            long tam = (long) Math.pow(2, p);
            long nec = tam * tam * 6 * 2 / 1_000_000;
            if (nec <= ramLibreMB * 0.8) return p;
        }
        return 4;
    }
}
