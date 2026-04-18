package matrices;

import java.math.BigInteger;
import java.util.concurrent.*;

/**
 * Algoritmo 10: III.5 Enhanced Parallel Block
 * Tipo: Iterativo por bloques con paralelismo mejorado
 * Descripcion: Mejora de Parallel Block que elimina la sincronizacion
 * por fila asignando a cada hilo filas completas sin solapamiento.
 * Cada hilo tiene su propio rango de filas exclusivo, eliminando
 * la necesidad de synchronized y reduciendo la contention.
 * Complejidad: O(n^3 / p) con menor overhead de sincronizacion
 */
public class Algoritmo10_III5_EnhancedParallelBlock {

    static final int BLOQUE = 64;
    static final int HILOS  = Runtime.getRuntime().availableProcessors();

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) throws Exception {
        int n = A.length;
        BigInteger[][] C = new BigInteger[n][n];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) C[i][j]=BigInteger.ZERO;

        ExecutorService pool = Executors.newFixedThreadPool(HILOS);
        java.util.List<Future<?>> tareas = new java.util.ArrayList<>();

        // Dividir filas entre hilos sin solapamiento (sin synchronized)
        int filasPorHilo = Math.max(1, n / HILOS);
        for (int h=0; h<HILOS; h++) {
            final int filaInicio = h * filasPorHilo;
            final int filaFin    = (h == HILOS-1) ? n : filaInicio + filasPorHilo;
            if (filaInicio >= n) break;
            tareas.add(pool.submit(() -> {
                for (int ii=filaInicio; ii<filaFin; ii+=BLOQUE)
                    for (int jj=0; jj<n; jj+=BLOQUE)
                        for (int kk=0; kk<n; kk+=BLOQUE) {
                            int iM=Math.min(ii+BLOQUE,filaFin);
                            int jM=Math.min(jj+BLOQUE,n), kM=Math.min(kk+BLOQUE,n);
                            for (int i=ii; i<iM; i++)
                                for (int k=kk; k<kM; k++) {
                                    BigInteger aik = A[i][k];
                                    for (int j=jj; j<jM; j++)
                                        C[i][j] = C[i][j].add(aik.multiply(B[k][j]));
                                }
                        }
            }));
        }
        pool.shutdown();
        for (Future<?> f : tareas) f.get();
        return C;
    }
}
