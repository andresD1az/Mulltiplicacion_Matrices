package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 8: III.3 Sequential Block
 * Tipo: Iterativo por bloques
 * Descripcion: Multiplicacion por bloques secuencial (tiling).
 * Divide las matrices en bloques de tamano BLOQUE x BLOQUE para
 * maximizar el reuso de datos en cache L1/L2.
 * El orden de los ciclos es i-j-k dentro de cada bloque.
 * Complejidad: O(n^3) con mejor localidad de cache
 */
public class Algoritmo8_III3_SequentialBlock {

    static final int BLOQUE = 64;

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) {
        return multiplicar(A, B, BLOQUE);
    }

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B, int bloque) {
        int n = A.length;
        BigInteger[][] C = new BigInteger[n][n];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) C[i][j]=BigInteger.ZERO;

        for (int ii=0; ii<n; ii+=bloque)
            for (int jj=0; jj<n; jj+=bloque)
                for (int kk=0; kk<n; kk+=bloque) {
                    int iM=Math.min(ii+bloque,n), jM=Math.min(jj+bloque,n), kM=Math.min(kk+bloque,n);
                    for (int i=ii; i<iM; i++)
                        for (int j=jj; j<jM; j++) {
                            BigInteger s = BigInteger.ZERO;
                            for (int k=kk; k<kM; k++)
                                s = s.add(A[i][k].multiply(B[k][j]));
                            C[i][j] = C[i][j].add(s);
                        }
                }
        return C;
    }
}
