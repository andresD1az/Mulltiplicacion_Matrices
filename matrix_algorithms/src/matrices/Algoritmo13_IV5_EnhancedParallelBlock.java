package matrices;

import java.math.BigInteger;
import java.util.concurrent.*;

/**
 * Algoritmo 13: IV.5 Enhanced Parallel Block
 * Tipo: Iterativo por bloques con paralelismo mejorado - orden i-k-j
 * Descripcion: Mejora de IV.4 que asigna rangos de filas exclusivos
 * a cada hilo (sin solapamiento ni sincronizacion). Combina:
 *   - Orden i-k-j para reuso de registros
 *   - Particion de filas sin synchronized
 *   - Tamano de bloque adaptado al numero de hilos
 * Complejidad: O(n^3 / p) con minimo overhead de sincronizacion
 */
public class Algoritmo13_IV5_EnhancedParallelBlock {

    static final int BLOQUE = 64;
    static final int HILOS  = Runtime.getRuntime().availableProcessors();

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) throws Exception {
        int n = A.length;
        BigInteger[][] C = new BigInteger[n][n];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) C[i][j]=BigInteger.ZERO;

        ExecutorService pool = Executors.newFixedThreadPool(HILOS);
        java.util.List<Future<?>> tareas = new java.util.ArrayList<>();

        int filasPorHilo = Math.max(1, n / HILOS);
        for (int h=0; h<HILOS; h++) {
            final int filaInicio = h * filasPorHilo;
            final int filaFin    = (h == HILOS-1) ? n : filaInicio + filasPorHilo;
            if (filaInicio >= n) break;
            tareas.add(pool.submit(() -> {
                for (int ii=filaInicio; ii<filaFin; ii+=BLOQUE)
                    for (int kk=0; kk<n; kk+=BLOQUE)
                        for (int jj=0; jj<n; jj+=BLOQUE) {
                            int iM=Math.min(ii+BLOQUE,filaFin);
                            int kM=Math.min(kk+BLOQUE,n), jM=Math.min(jj+BLOQUE,n);
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
