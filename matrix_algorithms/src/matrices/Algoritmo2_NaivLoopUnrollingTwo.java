package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 2: NaivLoopUnrollingTwo
 * Tipo: Iterativo
 * Descripcion: Desenvolvimiento del ciclo interno de a 2 pasos.
 * En cada iteracion procesa dos productos simultaneamente:
 *   aux += A[i][k]*B[k][j] + A[i][k+1]*B[k+1][j]
 * Maneja el caso de n impar procesando el ultimo elemento por separado.
 * Complejidad: O(n^3) con menor overhead de ciclo
 * Fuente: Multiplicacion de matrices grandes - 2025-1.pdf, paginas 3-4
 */
public class Algoritmo2_NaivLoopUnrollingTwo {

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
