package matrices;

import java.math.BigInteger;
import java.util.Random;

public class MatrixUtils {

    public static int[][] generarInt(int n) {
        int[][] m = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                m[i][j] = (int)(Math.random() * 10);
        return m;
    }

    public static double[][] generarDouble(int n) {
        double[][] m = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                m[i][j] = Math.random() * 10;
        return m;
    }

    public static BigInteger[][] generarBig(int n, int digitos) {
        Random rng = new Random();
        int bits = (int)(digitos * 3.32193) + 1;
        BigInteger[][] m = new BigInteger[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                BigInteger v = new BigInteger(bits, rng);
                m[i][j] = v.equals(BigInteger.ZERO) ? BigInteger.ONE : v;
            }
        return m;
    }

    // Rellena hasta la siguiente potencia de 2 (para Strassen y NaivRecursivo)
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

    public static double[][] toDouble(int[][] m) {
        double[][] d = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                d[i][j] = m[i][j];
        return d;
    }

    public static double[][] bigToDouble(BigInteger[][] m) {
        double[][] d = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                d[i][j] = m[i][j].doubleValue();
        return d;
    }

    public static void imprimirInt(int[][] m) {
        for (int[] f : m) {
            for (int v : f) System.out.printf("%8d", v);
            System.out.println();
        }
    }

    public static void imprimirBig(BigInteger[][] m) {
        for (BigInteger[] f : m) {
            for (BigInteger v : f) System.out.printf("  %20s", v);
            System.out.println();
        }
    }
}
