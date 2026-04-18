package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 4: Loop Unrolling x2 (NaivLoopUnrollingTwo)
 * Tipo: Iterativo
 *
 * Tecnica de optimizacion que desenvuelve el cuerpo del ciclo interno
 * dos veces, incrementando el salto de ciclo a 2. En cada iteracion
 * procesa dos productos simultaneamente:
 *   aux += A[i][k]*B[k][j] + A[i][k+1]*B[k+1][j]
 *
 * Para la primera iteracion esto equivale a: a11*b11 + a12*b21
 * Se toman pares de valores de cada fila y columna.
 *
 * Maneja correctamente el caso en que el numero de columnas/filas
 * sea impar, procesando el ultimo elemento por separado.
 *
 * Objetivo: mejorar el reuso de registros y reducir la cantidad
 * de repeticiones e instrucciones del ciclo.
 *
 * Complejidad: O(n^3) con menor overhead de ciclo
 * Fuente: Multiplicacion de matrices grandes - 2025-1.pdf, paginas 3-4
 */
public class Algoritmo4_LoopUnrollingTwo {

    public static int[][] multiplicar(int[][] A, int[][] B) {
        int n = A.length, m = B[0].length, p = B.length;
        int[][] C = new int[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                int aux = 0, k = 0;
                for (; k < p - 1; k += 2)
                    aux += A[i][k] * B[k][j] + A[i][k+1] * B[k+1][j];
                if (k < p) aux += A[i][k] * B[k][j];
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
                for (; k < p - 1; k += 2)
                    aux = aux.add(A[i][k].multiply(B[k][j]))
                             .add(A[i][k+1].multiply(B[k+1][j]));
                if (k < p) aux = aux.add(A[i][k].multiply(B[k][j]));
                C[i][j] = aux;
            }
        return C;
    }
}
