package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 2: NaivStandard2
 * Tipo: Iterativo
 *
 * Segunda implementacion de la multiplicacion tradicional de matrices.
 * Usa tres ciclos anidados igual que NaivStandard, pero en lugar de
 * usar una variable auxiliar, acumula directamente en la posicion
 * de la matriz resultado mediante la operacion:
 *   C[i][j] += A[i][k] * B[k][j]
 *
 * La diferencia con NaivStandard es que no hay asignacion final separada.
 *
 * Complejidad: O(n^3)
 * Fuente: Multiplicacion de matrices grandes - 2025-1.pdf, pagina 2
 */
public class Algoritmo2_NaivStandard2 {

    public static int[][] multiplicar(int[][] A, int[][] B) {
        int n = A.length, m = B[0].length, p = B.length;
        int[][] C = new int[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                C[i][j] = 0;
                for (int k = 0; k < p; k++)
                    C[i][j] += A[i][k] * B[k][j];
            }
        return C;
    }

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) {
        int n = A.length, m = B[0].length, p = B.length;
        BigInteger[][] C = new BigInteger[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                C[i][j] = BigInteger.ZERO;
                for (int k = 0; k < p; k++)
                    C[i][j] = C[i][j].add(A[i][k].multiply(B[k][j]));
            }
        return C;
    }
}
