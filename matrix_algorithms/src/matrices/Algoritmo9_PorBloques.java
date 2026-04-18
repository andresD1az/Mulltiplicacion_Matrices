package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 9: Por Bloques (Tiling / Blocking)
 * Tipo: Iterativo
 *
 * Tecnica de optimizacion que divide las matrices en bloques de
 * tamano BLOQUE x BLOQUE para maximizar el reuso de datos en cache.
 *
 * En lugar de recorrer toda la matriz de una vez, trabaja con
 * submatrices que caben en la cache L1/L2. Para cada bloque (ii,jj,kk)
 * realiza la multiplicacion parcial:
 *   C[i][j] += A[i][k] * B[k][j]
 *   para i en [ii, ii+BLOQUE), k en [kk, kk+BLOQUE), j en [jj, jj+BLOQUE)
 *
 * El valor de A[i][k] se carga una sola vez en un registro y se
 * reutiliza para todos los j del bloque, reduciendo accesos a memoria.
 *
 * Tecnica fundamental en implementaciones de alto rendimiento
 * como BLAS (Basic Linear Algebra Subprograms).
 * Tamano de bloque por defecto: 64 (ajustable segun cache L1).
 *
 * Complejidad: O(n^3) con mejor comportamiento de cache
 */
public class Algoritmo9_PorBloques {

    public static int[][] multiplicar(int[][] A, int[][] B) {
        return multiplicar(A, B, 64);
    }

    public static int[][] multiplicar(int[][] A, int[][] B, int bloque) {
        int n = A.length;
        int[][] C = new int[n][n];
        for (int ii=0;ii<n;ii+=bloque) for (int kk=0;kk<n;kk+=bloque) for (int jj=0;jj<n;jj+=bloque) {
            int iM=Math.min(ii+bloque,n), kM=Math.min(kk+bloque,n), jM=Math.min(jj+bloque,n);
            for (int i=ii;i<iM;i++) for (int k=kk;k<kM;k++) {
                int aik=A[i][k];
                for (int j=jj;j<jM;j++) C[i][j]+=aik*B[k][j];
            }
        }
        return C;
    }

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) {
        return multiplicar(A, B, 64);
    }

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B, int bloque) {
        int n = A.length;
        BigInteger[][] C = new BigInteger[n][n];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) C[i][j]=BigInteger.ZERO;
        for (int ii=0;ii<n;ii+=bloque) for (int kk=0;kk<n;kk+=bloque) for (int jj=0;jj<n;jj+=bloque) {
            int iM=Math.min(ii+bloque,n), kM=Math.min(kk+bloque,n), jM=Math.min(jj+bloque,n);
            for (int i=ii;i<iM;i++) for (int k=kk;k<kM;k++) {
                BigInteger aik=A[i][k];
                for (int j=jj;j<jM;j++) C[i][j]=C[i][j].add(aik.multiply(B[k][j]));
            }
        }
        return C;
    }
}
