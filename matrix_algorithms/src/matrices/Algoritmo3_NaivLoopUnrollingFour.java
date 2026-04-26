package matrices;

import java.math.BigInteger;

// Algoritmo 3: NaivLoopUnrollingFour
// Complejidad: O(n^3) con overhead de ciclo reducido al 25%
// Desenrolla el ciclo interno de a 4 pasos por iteracion
// Procesa 4 productos simultaneamente por iteracion del ciclo k
// El residuo (p % 4 elementos) se procesa con un ciclo simple al final
public class Algoritmo3_NaivLoopUnrollingFour {

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) {
        int n = A.length, m = B[0].length, p = B.length;
        BigInteger[][] C = new BigInteger[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                BigInteger aux = BigInteger.ZERO;
                int k = 0;
                // procesa de a 4 elementos por iteracion
                for (; k < p - 3; k += 4)
                    aux = aux.add(A[i][k].multiply(B[k][j]))
                             .add(A[i][k+1].multiply(B[k+1][j]))
                             .add(A[i][k+2].multiply(B[k+2][j]))
                             .add(A[i][k+3].multiply(B[k+3][j]));
                // elementos restantes (0, 1, 2 o 3 segun p % 4)
                for (; k < p; k++)
                    aux = aux.add(A[i][k].multiply(B[k][j]));
                C[i][j] = aux;
            }
        return C;
    }
}
