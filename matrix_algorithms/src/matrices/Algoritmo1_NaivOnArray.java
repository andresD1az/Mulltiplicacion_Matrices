package matrices;

import java.math.BigInteger;

// Algoritmo 1: NaivOnArray
// Complejidad: O(n^3)
// Multiplicacion clasica con tres ciclos anidados i-j-k
// Usa variable auxiliar para acumular antes de asignar a C[i][j]
// C[i][j] = suma de A[i][k] * B[k][j] para k = 0..n-1
public class Algoritmo1_NaivOnArray {

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) {
        int n = A.length, m = B[0].length, p = B.length;
        BigInteger[][] C = new BigInteger[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                BigInteger aux = BigInteger.ZERO; // acumulador del producto punto
                for (int k = 0; k < p; k++)
                    aux = aux.add(A[i][k].multiply(B[k][j]));
                C[i][j] = aux;
            }
        return C;
    }
}
