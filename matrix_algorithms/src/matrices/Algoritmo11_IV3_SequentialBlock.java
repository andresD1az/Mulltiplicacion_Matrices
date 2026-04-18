package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 11: IV.3 Sequential Block
 * Tipo: Iterativo por bloques - orden i-k-j
 * Descripcion: Variante de Sequential Block con el orden de ciclos
 * cambiado a i-k-j dentro del bloque. Este orden carga A[i][k] una
 * sola vez y lo reutiliza para todos los j del bloque (reuso de registro).
 * Es el orden mas eficiente para matrices almacenadas por filas (row-major).
 * Complejidad: O(n^3) con mejor reuso de registros que III.3
 */
public class Algoritmo11_IV3_SequentialBlock {

    static final int BLOQUE = 64;

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) {
        return multiplicar(A, B, BLOQUE);
    }

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B, int bloque) {
        int n = A.length;
        BigInteger[][] C = new BigInteger[n][n];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) C[i][j]=BigInteger.ZERO;

        for (int ii=0; ii<n; ii+=bloque)
            for (int kk=0; kk<n; kk+=bloque)
                for (int jj=0; jj<n; jj+=bloque) {
                    int iM=Math.min(ii+bloque,n), kM=Math.min(kk+bloque,n), jM=Math.min(jj+bloque,n);
                    // Orden i-k-j: A[i][k] se carga una vez para todos los j
                    for (int i=ii; i<iM; i++)
                        for (int k=kk; k<kM; k++) {
                            BigInteger aik = A[i][k];
                            for (int j=jj; j<jM; j++)
                                C[i][j] = C[i][j].add(aik.multiply(B[k][j]));
                        }
                }
        return C;
    }
}
