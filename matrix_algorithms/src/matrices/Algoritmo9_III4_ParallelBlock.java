package matrices;

import java.math.BigInteger;
import java.util.concurrent.*;

/**
 * Algoritmo 9: III.4 Parallel Block
 * Tipo: Iterativo por bloques con paralelismo
 * Descripcion: Variante paralela de Sequential Block. Distribuye los
 * bloques de filas entre multiples hilos usando un ExecutorService.
 * Cada hilo procesa un subconjunto de bloques de filas de forma independiente.
 * Complejidad: O(n^3 / p) donde p es el numero de procesadores
 */
public class Algoritmo9_III4_ParallelBlock {

    static final int BLOQUE = 64;
    static final int HILOS  = Runtime.getRuntime().availableProcessors();

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) throws Exception {
        int n = A.length;
        BigInteger[][] C = new BigInteger[n][n];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) C[i][j]=BigInteger.ZERO;

        ExecutorService pool = Executors.newFixedThreadPool(HILOS);
        java.util.List<Future<?>> tareas = new java.util.ArrayList<>();

        // Paralelizar sobre bloques de filas
        for (int ii=0; ii<n; ii+=BLOQUE) {
            final int iiF = ii;
            tareas.add(pool.submit(() -> {
                int iM = Math.min(iiF+BLOQUE, n);
                for (int jj=0; jj<n; jj+=BLOQUE)
                    for (int kk=0; kk<n; kk+=BLOQUE) {
                        int jM=Math.min(jj+BLOQUE,n), kM=Math.min(kk+BLOQUE,n);
                        for (int i=iiF; i<iM; i++)
                            for (int j=jj; j<jM; j++) {
                                BigInteger s = BigInteger.ZERO;
                                for (int k=kk; k<kM; k++)
                                    s = s.add(A[i][k].multiply(B[k][j]));
                                synchronized (C[i]) {
                                    C[i][j] = C[i][j].add(s);
                                }
                            }
                    }
            }));
        }
        pool.shutdown();
        for (Future<?> f : tareas) f.get();
        return C;
    }
}
