package matrices;

import java.math.BigInteger;
import java.util.concurrent.*;

/**
 * Algoritmo 12: IV.4 Parallel Block
 * Tipo: Iterativo por bloques con paralelismo - orden i-k-j
 * Descripcion: Version paralela de IV.3 Sequential Block.
 * Usa el orden i-k-j dentro del bloque y distribuye bloques de filas
 * entre hilos. Combina el reuso de registros de IV.3 con el paralelismo.
 * Complejidad: O(n^3 / p)
 */
public class Algoritmo12_IV4_ParallelBlock {

    static final int BLOQUE = 64;
    static final int HILOS  = Runtime.getRuntime().availableProcessors();

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) throws Exception {
        int n = A.length;
        BigInteger[][] C = new BigInteger[n][n];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) C[i][j]=BigInteger.ZERO;

        ExecutorService pool = Executors.newFixedThreadPool(HILOS);
        java.util.List<Future<?>> tareas = new java.util.ArrayList<>();

        for (int ii=0; ii<n; ii+=BLOQUE) {
            final int iiF = ii;
            tareas.add(pool.submit(() -> {
                int iM = Math.min(iiF+BLOQUE, n);
                for (int kk=0; kk<n; kk+=BLOQUE)
                    for (int jj=0; jj<n; jj+=BLOQUE) {
                        int kM=Math.min(kk+BLOQUE,n), jM=Math.min(jj+BLOQUE,n);
                        for (int i=iiF; i<iM; i++)
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
