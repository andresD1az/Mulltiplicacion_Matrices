package matrices;

import java.math.BigInteger;

// Algoritmo 4: WinogradOriginal
// Complejidad: O(n^3) con ~15% menos multiplicaciones que Naiv
// Precalcula factores de fila y columna para reutilizarlos
//
// Paso 1 - rowFactor[i] = suma de A[i][2k] * A[i][2k+1]
// Paso 2 - colFactor[j] = suma de B[2k][j] * B[2k+1][j]
// Paso 3 - C[i][j] = -rowFactor[i] - colFactor[j]
//                  + suma de (A[i][2k]+B[2k+1][j]) * (A[i][2k+1]+B[2k][j])
// Paso 4 - si n es impar, agrega el ultimo termino A[i][n-1]*B[n-1][j]
public class Algoritmo4_WinogradOriginal {

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) {
        int n = A.length;
        BigInteger[] rf = new BigInteger[n]; // factores de fila
        BigInteger[] cf = new BigInteger[n]; // factores de columna

        // Paso 1: precalcular factores de fila de A
        for (int i = 0; i < n; i++) {
            rf[i] = BigInteger.ZERO;
            for (int k = 0; k < n / 2; k++)
                rf[i] = rf[i].add(A[i][2*k].multiply(A[i][2*k+1]));
        }

        // Paso 2: precalcular factores de columna de B
        for (int j = 0; j < n; j++) {
            cf[j] = BigInteger.ZERO;
            for (int k = 0; k < n / 2; k++)
                cf[j] = cf[j].add(B[2*k][j].multiply(B[2*k+1][j]));
        }

        // Paso 3: calculo principal usando los factores precalculados
        BigInteger[][] C = new BigInteger[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                C[i][j] = rf[i].negate().subtract(cf[j]); // inicia con -rf[i] - cf[j]
                for (int k = 0; k < n / 2; k++)
                    C[i][j] = C[i][j].add(
                        A[i][2*k].add(B[2*k+1][j])
                                 .multiply(A[i][2*k+1].add(B[2*k][j])));
            }

        // Paso 4: correccion para n impar
        if (n % 2 != 0)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    C[i][j] = C[i][j].add(A[i][n-1].multiply(B[n-1][j]));

        return C;
    }
}
