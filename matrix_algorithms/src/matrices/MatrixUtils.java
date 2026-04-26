package matrices;

import java.math.BigInteger;
import java.util.Random;

// Utilidades para generar y manipular matrices
// Soporta tres tipos: int, double y BigInteger
public class MatrixUtils {

    // Genera una matriz n x n de enteros aleatorios entre 0 y 9
    public static int[][] generarInt(int n) {
        int[][] m = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                m[i][j] = (int)(Math.random() * 10);
        return m;
    }

    // Genera una matriz n x n de doubles aleatorios entre 0 y 10
    public static double[][] generarDouble(int n) {
        double[][] m = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                m[i][j] = Math.random() * 10;
        return m;
    }

    // Genera una matriz n x n de BigInteger con la cantidad de digitos indicada
    // Convierte digitos decimales a bits: bits = digitos * log2(10) + 1
    // Garantiza que ningun valor sea cero (lo reemplaza por 1)
    public static BigInteger[][] generarBig(int n, int digitos) {
        Random rng = new Random();
        int bits = (int)(digitos * 3.32193) + 1; // log2(10) ≈ 3.32193
        BigInteger[][] m = new BigInteger[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                BigInteger v = new BigInteger(bits, rng);
                m[i][j] = v.equals(BigInteger.ZERO) ? BigInteger.ONE : v;
            }
        return m;
    }

    // Rellena una matriz int hasta la siguiente potencia de 2
    // Necesario para Strassen que requiere n = potencia de 2
    // Si ya es potencia de 2, devuelve la misma matriz sin copiar
    public static int[][] padearPot2(int[][] m) {
        int n = m.length, p = 1;
        while (p < n) p *= 2;
        if (p == n) return m;
        int[][] r = new int[p][p];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                r[i][j] = m[i][j];
        return r;
    }

    // Rellena una matriz BigInteger hasta la siguiente potencia de 2
    // Las posiciones nuevas se inicializan en ZERO
    public static BigInteger[][] padearPot2Big(BigInteger[][] m) {
        int n = m.length, p = 1;
        while (p < n) p *= 2;
        if (p == n) return m;
        BigInteger[][] r = new BigInteger[p][p];
        for (int i = 0; i < p; i++)
            for (int j = 0; j < p; j++)
                r[i][j] = (i < n && j < n) ? m[i][j] : BigInteger.ZERO;
        return r;
    }

    // Convierte una matriz int a double
    public static double[][] toDouble(int[][] m) {
        double[][] d = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                d[i][j] = m[i][j];
        return d;
    }

    // Convierte una matriz BigInteger a double (puede perder precision en numeros grandes)
    public static double[][] bigToDouble(BigInteger[][] m) {
        double[][] d = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                d[i][j] = m[i][j].doubleValue();
        return d;
    }

    // Imprime una matriz int en consola con formato de 8 caracteres por celda
    public static void imprimirInt(int[][] m) {
        for (int[] f : m) {
            for (int v : f) System.out.printf("%8d", v);
            System.out.println();
        }
    }

    // Imprime una matriz BigInteger en consola con formato de 20 caracteres por celda
    public static void imprimirBig(BigInteger[][] m) {
        for (BigInteger[] f : m) {
            for (BigInteger v : f) System.out.printf("  %20s", v);
            System.out.println();
        }
    }
}
