package matrices;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Guarda los resultados de cada prueba en dos archivos:
 *   - resultados.csv : para graficar en Excel
 *   - resultados.txt : reporte legible con explicacion del calculo teorico
 */
public class ResultadoGuardador {

    static final String CSV = "matrix_algorithms/resultados.csv";
    static final String TXT = "matrix_algorithms/resultados.txt";
    static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void guardar(
            int tamano, int digitos,
            String[] nombres, long[] estimados, long[] reales,
            double k, double[] exponentes, double[] factores) throws IOException {

        String timestamp = LocalDateTime.now().format(FMT);
        boolean csvExiste = new File(CSV).exists();

        // --- CSV para Excel ---
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV, true))) {
            if (!csvExiste) {
                pw.println("Fecha,Tamano,Digitos,Algoritmo,Complejidad,Operaciones,Estimado_ms,Real_ms,Ratio");
            }
            for (int i = 0; i < nombres.length; i++) {
                long ops = Math.round(factores[i] * Math.pow(tamano, exponentes[i]));
                double ratio = (estimados[i] > 0 && reales[i] >= 0)
                    ? (double) reales[i] / estimados[i] : 0;
                pw.printf("%s,%d,%d,%s,O(n^%.3f),%d,%d,%d,%.2f%n",
                    timestamp, tamano, digitos, nombres[i],
                    exponentes[i], ops,
                    estimados[i],
                    reales[i] >= 0 ? reales[i] : -1,
                    ratio);
            }
        }

        // --- TXT reporte legible ---
        try (PrintWriter pw = new PrintWriter(new FileWriter(TXT, true))) {
            pw.println();
            pw.println(linea('=', 70));
            pw.println("PRUEBA: " + timestamp);
            pw.println("Matriz : " + tamano + " x " + tamano);
            pw.println("Digitos: " + digitos + " por valor");
            pw.println(linea('=', 70));
            pw.println();

            pw.println("COMO SE CALCULA EL TIEMPO TEORICO:");
            pw.println(linea('-', 70));
            pw.println("1. Calibracion: se mide NaivOnArray con matriz 16x16");
            pw.println("   k = tiempo_real / 16^3 = tiempo / 4096 operaciones");
            pw.printf( "   k = %.6f ms por operacion BigInteger en esta maquina%n", k);
            pw.println();
            pw.println("2. Para cada algoritmo:");
            pw.println("   operaciones = factor * n^exponente");
            pw.println("   tiempo_estimado = k * operaciones");
            pw.println();
            pw.println("   Exponentes por complejidad teorica:");
            pw.println("   NaivOnArray, bloques : O(n^3)     exponente=3.0,   factor=1.0");
            pw.println("   LoopUnrollingTwo     : O(n^3)     exponente=3.0,   factor=0.5");
            pw.println("   LoopUnrollingFour    : O(n^3)     exponente=3.0,   factor=0.25");
            pw.println("   WinogradOriginal     : O(n^3)     exponente=3.0,   factor=0.5");
            pw.println("   StrassenNaiv         : O(n^2.807) exponente=2.807, factor=1.0");
            pw.println("   StrassenWinograd     : O(n^2.807) exponente=2.807, factor=0.83");
            pw.println();
            pw.println("3. Ejemplo con n=" + tamano + ":");
            long opsEj = (long) Math.pow(tamano, 3);
            pw.printf( "   NaivOnArray: %.6f * 1.0 * %d^3 = %.6f * %d = %.0f ms%n",
                k, tamano, k, opsEj, k * opsEj);
            pw.println();

            pw.println(linea('-', 70));
            pw.printf("%-28s %-12s %-10s %-12s %-12s %-14s%n",
                "Algoritmo", "Complejidad", "Ops", "Estimado", "Real", "Ratio");
            pw.println(linea('-', 70));

            for (int i = 0; i < nombres.length; i++) {
                long ops = Math.round(factores[i] * Math.pow(tamano, exponentes[i]));
                String realStr = reales[i] >= 0 ? formatMs(reales[i]) : "ERROR";
                double ratio = (estimados[i] > 0 && reales[i] >= 0)
                    ? (double) reales[i] / estimados[i] : 0;
                String analisis = analizar(ratio);
                pw.printf("%-28s %-12s %-10s %-12s %-12s %-14s%n",
                    nombres[i],
                    "O(n^" + String.format("%.3f", exponentes[i]) + ")",
                    formatOps(ops),
                    formatMs(estimados[i]),
                    realStr,
                    reales[i] >= 0 ? String.format("%.2fx %s", ratio, analisis) : "N/A");
            }
            pw.println(linea('-', 70));
            pw.println();
            pw.println("INTERPRETACION DEL RATIO (real / estimado):");
            pw.println("  < 0.9x : mas rapido que la teoria (JIT, cache, paralelismo)");
            pw.println("  ~ 1.0x : acorde a la teoria");
            pw.println("  > 1.1x : mas lento (overhead BigInteger, GC, contention hilos)");
            pw.println();
        }

        System.out.println();
        System.out.println("Resultados guardados en:");
        System.out.println("  " + new File(CSV).getAbsolutePath());
        System.out.println("  " + new File(TXT).getAbsolutePath());
    }

    static String linea(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(c);
        return sb.toString();
    }

    static String analizar(double ratio) {
        if (ratio <= 0)   return "";
        if (ratio < 0.5)  return "(mucho mejor)";
        if (ratio < 0.9)  return "(mejor)";
        if (ratio <= 1.1) return "(acorde)";
        if (ratio <= 2.0) return "(algo lento)";
        return "(mucho mas lento)";
    }

    static String formatMs(long ms) {
        if (ms < 0)       return "ERROR";
        if (ms < 1000)    return ms + " ms";
        if (ms < 60000)   return String.format("%.2f s", ms / 1000.0);
        if (ms < 3600000) return String.format("%.2f min", ms / 60000.0);
        return String.format("%.2f h", ms / 3600000.0);
    }

    static String formatOps(long ops) {
        if (ops < 1_000)         return ops + "";
        if (ops < 1_000_000)     return String.format("%.1fK", ops / 1000.0);
        if (ops < 1_000_000_000) return String.format("%.1fM", ops / 1_000_000.0);
        return String.format("%.1fB", ops / 1_000_000_000.0);
    }
}
