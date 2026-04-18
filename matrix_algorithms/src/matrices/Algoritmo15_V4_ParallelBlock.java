package matrices;

import java.math.BigInteger;
import java.util.concurrent.*;

/**
 * Algoritmo 15: V.4 Parallel Block
 * Tipo: Iterativo por bloques con transpuesta y paralelismo
 * Descripcion: Version paralela de V.3. Combina las tres tecnicas:
 *   1. Transpuesta de B para acceso cache-friendly
 *   2. Multiplicacion por bloques para reuso de cache
 *   3. Paralelismo con particion de filas sin sincronizacion
 * Es el algoritmo mas completo de la familia de bloques.
 * Complejidad: O(n^3 / p) con mejor localidad de cache
 */
public class Algoritmo15_V4_ParallelBlock {

    static final int BLOQUE = 64;
    static final int HILOS  = Runtime.getRuntime().availableProcessors();

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) throws Exception {
        int n = A.length;

        // Transponer B
        BigInteger[][] BT = new BigInteger[n][n];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) BT[j][i] = B[i][j];

        BigInteger[][] C = new BigInteger[n][n];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) C[i][j]=BigInteger.ZERO;

        ExecutorService pool = Executors.newFixedThreadPool(HILOS);
        java.util.List<Future<?>> tareas = new java.util.ArrayList<>();

        // Particion de filas sin solapamiento
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
                                for (int j=jj; j<jM; j++) {
                                    BigInteger s = BigInteger.ZERO;
                                    for (int k=kk; k<kM; k++)
                                        s = s.add(A[i][k].multiply(BT[j][k]));
                                    C[i][j] = C[i][j].add(s);
                                }
                        }
            }));
        }
        pool.shutdown();
        for (Future<?> f : tareas) f.get();
        return C;
    }
}
