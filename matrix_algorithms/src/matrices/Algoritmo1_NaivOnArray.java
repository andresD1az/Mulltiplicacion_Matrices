package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 1: NaivOnArray
 * Tipo: Iterativo
 * Descripcion: Multiplicacion tradicional con tres ciclos anidados.
 * Usa variable auxiliar para acumular el producto antes de asignar a C[i][j].
 * C[i][j] = SUM( A[i][k] * B[k][j] ) para k = 0..n-1
 * Complejidad: O(n^3)
 * Fuente: Multiplicacion de matrices grandes - 2025-1.pdf, pagina 1
 */
public class Algoritmo1_NaivOnArray {

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
