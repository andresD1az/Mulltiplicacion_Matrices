package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 14: V.3 Sequential Block
 * Tipo: Iterativo por bloques con transpuesta
 * Descripcion: Combina la tecnica de bloques con la transpuesta de B.
 * Transpone B antes de multiplicar para que ambas matrices se accedan
 * por filas (cache-friendly), luego aplica la multiplicacion por bloques
 * con orden i-k-j sobre la transpuesta.
 * Es la combinacion mas eficiente de cache entre los algoritmos de bloques.
 * Complejidad: O(n^3) con mejor localidad de cache que IV.3
 */
public class Algoritmo14_V3_SequentialBlock {

    static final int BLOQUE = 64;

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) {
        int n = A.length;

        // Transponer B para acceso cache-friendly
        BigInteger[][] BT = new BigInteger[n][n];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) BT[j][i] = B[i][j];

        BigInteger[][] C = new BigInteger[n][n];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) C[i][j]=BigInteger.ZERO;

        // Multiplicacion por bloques sobre A y BT (ambas accedidas por filas)
        for (int ii=0; ii<n; ii+=BLOQUE)
            for (int jj=0; jj<n; jj+=BLOQUE)
                for (int kk=0; kk<n; kk+=BLOQUE) {
                    int iM=Math.min(ii+BLOQUE,n), jM=Math.min(jj+BLOQUE,n), kM=Math.min(kk+BLOQUE,n);
                    for (int i=ii; i<iM; i++)
                        for (int j=jj; j<jM; j++) {
                            BigInteger s = BigInteger.ZERO;
                            for (int k=kk; k<kM; k++)
                                s = s.add(A[i][k].multiply(BT[j][k]));
                            C[i][j] = C[i][j].add(s);
                        }
                }
        return C;
    }
}
