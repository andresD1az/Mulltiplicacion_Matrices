package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 1: NaivStandard
 * Tipo: Iterativo
 *
 * Implementacion de la multiplicacion tradicional de matrices.
 * Se fundamenta en tres ciclos anidados. El ciclo mas interno acumula
 * el producto en una variable auxiliar mediante la operacion:
 *   auxiliar += A[i][k] * B[k][j]
 * y luego asigna el resultado con: C[i][j] = auxiliar
 *
 * Cada elemento C[i][j] se define como:
 *   C[i][j] = SUM( A[i][k] * B[k][j] ) para k = 0..n-1
 *
 * Complejidad: O(n^3)
 * Fuente: Multiplicacion de matrices grandes - 2025-1.pdf, pagina 1
 */
public class Algoritmo1_NaivStandard {

    public static int[][] multiplicar(int[][] A, int[][] B) {
        int n = A.length, m = B[0].length, p = B.length;
        int[][] C = new int[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                int aux = 0;
                for (int k = 0; k < p; k++)
                    aux += A[i][k] * B[k][j];
                C[i][j] = aux;
            }
        return C;
    }

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) {
        int n = A.length, m = B[0].length, p = B.length;
        BigInteger[][] C = new BigInteger[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                BigInteger aux = BigInteger.ZERO;
                for (int k = 0; k < p; k++)
                    aux = aux.add(A[i][k].multiply(B[k][j]));
                C[i][j] = aux;
            }
        return C;
    }
}
