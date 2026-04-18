package matrices;

/**
 * Algoritmo 3: Kahan
 * Tipo: Iterativo
 *
 * Mejora propuesta por Kahan (1965) para la estabilidad numerica del
 * proceso de multiplicacion. Optimiza la suma acumulada:
 *   SUM( x[i] ) para i = 1..n
 *
 * mediante la compensacion del error de redondeo en punto flotante.
 * En cada iteracion del ciclo interno se aplica:
 *   y = producto - error
 *   t = suma + y
 *   error = (t - suma) - y
 *   suma = t
 *
 * Esto captura los bits perdidos en cada suma y los reincorpora
 * en la siguiente iteracion, reduciendo el error acumulado.
 *
 * Trabaja con matrices de tipo double (punto flotante).
 * Complejidad: O(n^3)
 * Fuente: Multiplicacion de matrices grandes - 2025-1.pdf, paginas 2-3
 */
public class Algoritmo3_Kahan {

    public static double[][] multiplicar(double[][] A, double[][] B) {
        int n = A.length, m = B[0].length, p = B.length;
        double[][] C = new double[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                double suma = 0.0, error = 0.0;
                for (int k = 0; k < p; k++) {
                    double y = A[i][k] * B[k][j] - error;
                    double t = suma + y;
                    error = (t - suma) - y;
                    suma = t;
                }
                C[i][j] = suma;
            }
        return C;
    }
}
