package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 5: WinogradScaled
 * Tipo: Iterativo
 * Descripcion: Variante de Winograd con escalado previo de las matrices
 * para mejorar la estabilidad numerica. Antes de aplicar Winograd,
 * escala cada fila de A y cada columna de B por un factor lambda
 * calculado como la norma infinito de cada fila/columna.
 * Esto reduce el error de redondeo en aritmetica de punto flotante.
 * Con BigInteger el escalado se aplica como division entera por el maximo
 * de cada fila/columna para normalizar los valores.
 * Complejidad: O(n^3)
 */
public class Algoritmo5_WinogradScaled {

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) {
        int n = A.length;

        // Escalar: dividir cada fila de A por su valor maximo
        // y cada columna de B por su valor maximo
        BigInteger[][] As = new BigInteger[n][n];
        BigInteger[][] Bs = new BigInteger[n][n];

        for (int i = 0; i < n; i++) {
            BigInteger maxFila = BigInteger.ONE;
            for (int k = 0; k < n; k++) {
                BigInteger abs = A[i][k].abs();
                if (abs.compareTo(maxFila) > 0) maxFila = abs;
            }
            for (int k = 0; k < n; k++)
                As[i][k] = maxFila.equals(BigInteger.ZERO)
                    ? BigInteger.ZERO : A[i][k].divide(maxFila);
        }

        for (int j = 0; j < n; j++) {
            BigInteger maxCol = BigInteger.ONE;
            for (int k = 0; k < n; k++) {
                BigInteger abs = B[k][j].abs();
                if (abs.compareTo(maxCol) > 0) maxCol = abs;
            }
            for (int k = 0; k < n; k++)
                Bs[k][j] = maxCol.equals(BigInteger.ZERO)
                    ? BigInteger.ZERO : B[k][j].divide(maxCol);
        }

        // Aplicar Winograd sobre las matrices escaladas
        BigInteger[] rf = new BigInteger[n];
        BigInteger[] cf = new BigInteger[n];

        for (int i = 0; i < n; i++) {
            rf[i] = BigInteger.ZERO;
            for (int k = 0; k < n / 2; k++)
                rf[i] = rf[i].add(As[i][2*k].multiply(As[i][2*k+1]));
        }
        for (int j = 0; j < n; j++) {
            cf[j] = BigInteger.ZERO;
            for (int k = 0; k < n / 2; k++)
                cf[j] = cf[j].add(Bs[2*k][j].multiply(Bs[2*k+1][j]));
        }

        BigInteger[][] C = new BigInteger[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                C[i][j] = rf[i].negate().subtract(cf[j]);
                for (int k = 0; k < n / 2; k++)
                    C[i][j] = C[i][j].add(
                        As[i][2*k].add(Bs[2*k+1][j])
                                  .multiply(As[i][2*k+1].add(Bs[2*k][j])));
            }
        if (n % 2 != 0)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    C[i][j] = C[i][j].add(As[i][n-1].multiply(Bs[n-1][j]));

        return C;
    }
}
