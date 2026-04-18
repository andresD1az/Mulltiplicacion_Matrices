package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 3: NaivLoopUnrollingFour
 * Tipo: Iterativo
 * Descripcion: Desenvolvimiento del ciclo interno de a 4 pasos.
 * Procesa cuatro productos por iteracion. Maneja los 4 casos de residuo
 * segun p % 4 == 0, 1, 2, 3.
 * Complejidad: O(n^3) con overhead de ciclo reducido al 25%
 * Fuente: Multiplicacion de matrices grandes - 2025-1.pdf, paginas 10-11
 */
public class Algoritmo3_NaivLoopUnrollingFour {

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
                for (; k < p; k++)
                    aux = aux.add(A[i][k].multiply(B[k][j]));
                C[i][j] = aux;
            }
        return C;
    }
}
