package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 8: Transpuesta (Cache-Friendly)
 * Tipo: Iterativo
 *
 * Optimizacion de la multiplicacion tradicional basada en la
 * localidad de cache. El problema del algoritmo naive es que
 * accede a la matriz B por columnas, lo que genera muchos
 * fallos de cache en matrices grandes.
 *
 * Solucion: transponer B antes de multiplicar. Al acceder a
 * B^T por filas en lugar de columnas, ambas matrices A[i] y
 * BT[j] se recorren de forma secuencial en memoria:
 *   C[i][j] = SUM( A[i][k] * BT[j][k] )
 *
 * Esto mejora significativamente el rendimiento en matrices
 * grandes gracias al mejor aprovechamiento de la cache L1/L2.
 *
 * Complejidad: O(n^3) con mejor comportamiento de cache
 */
public class Algoritmo8_Transpuesta {

    public static int[][] multiplicar(int[][] A, int[][] B) {
        int n = A.length, m = B[0].length, p = B.length;
        int[][] BT = new int[p][m];
        for (int i = 0; i < p; i++) for (int j = 0; j < m; j++) BT[j][i] = B[i][j];
        int[][] C = new int[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                int s = 0;
                for (int k = 0; k < p; k++) s += A[i][k] * BT[j][k];
                C[i][j] = s;
            }
        return C;
    }

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) {
        int n = A.length, m = B[0].length, p = B.length;
        BigInteger[][] BT = new BigInteger[p][m];
        for (int i = 0; i < p; i++) for (int j = 0; j < m; j++) BT[j][i] = B[i][j];
        BigInteger[][] C = new BigInteger[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                BigInteger s = BigInteger.ZERO;
                for (int k = 0; k < p; k++) s = s.add(A[i][k].multiply(BT[j][k]));
                C[i][j] = s;
            }
        return C;
    }
}
