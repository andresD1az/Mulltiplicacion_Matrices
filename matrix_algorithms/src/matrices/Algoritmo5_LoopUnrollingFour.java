package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 5: Loop Unrolling x4 (NaivLoopUnrollingFour)
 * Tipo: Iterativo
 *
 * Extension del desenvolvimiento de ciclos a factor 4. En cada
 * iteracion del ciclo interno procesa cuatro productos a la vez:
 *   aux += A[i][k]*B[k][j] + A[i][k+1]*B[k+1][j]
 *        + A[i][k+2]*B[k+2][j] + A[i][k+3]*B[k+3][j]
 *
 * Maneja los cuatro casos de residuo segun p % 4:
 *   p % 4 == 0: se desenvuelve completamente
 *   p % 4 == 1: se procesan los ultimos 1 elementos por separado
 *   p % 4 == 2: se procesan los ultimos 2 elementos por separado
 *   p % 4 == 3: se procesan los ultimos 3 elementos por separado
 *
 * Reduce el overhead del ciclo al 25% respecto al naive.
 * Complejidad: O(n^3)
 * Fuente: Multiplicacion de matrices grandes - 2025-1.pdf, paginas 10-11
 */
public class Algoritmo5_LoopUnrollingFour {

    public static int[][] multiplicar(int[][] A, int[][] B) {
        int n = A.length, m = B[0].length, p = B.length;
        int[][] C = new int[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                int aux = 0, k = 0;
                for (; k < p - 3; k += 4)
                    aux += A[i][k]*B[k][j] + A[i][k+1]*B[k+1][j]
                         + A[i][k+2]*B[k+2][j] + A[i][k+3]*B[k+3][j];
                for (; k < p; k++) aux += A[i][k] * B[k][j];
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
                int k = 0;
                for (; k < p - 3; k += 4)
                    aux = aux.add(A[i][k].multiply(B[k][j]))
                             .add(A[i][k+1].multiply(B[k+1][j]))
                             .add(A[i][k+2].multiply(B[k+2][j]))
                             .add(A[i][k+3].multiply(B[k+3][j]));
                for (; k < p; k++) aux = aux.add(A[i][k].multiply(B[k][j]));
                C[i][j] = aux;
            }
        return C;
    }
}
