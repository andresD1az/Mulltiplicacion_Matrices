package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 7: StrassenWinograd
 * Tipo: Recursivo (Divide y Venceras)
 * Descripcion: Variante de Strassen propuesta por Winograd que reduce
 * el numero de sumas/restas de matrices de 18 a 15 por nivel de recursion,
 * manteniendo las 7 multiplicaciones de Strassen.
 *
 * Usa variables intermedias S1..S8 y T1..T7 para reutilizar sumas:
 *   S1 = A10 + A11        T1 = B01 - B00
 *   S2 = S1 - A00         T2 = B11 - T1
 *   S3 = A00 - A10        T3 = B11 - B01
 *   S4 = A01 - S2         T4 = B10 - T2
 *
 *   M1 = A00 * B00
 *   M2 = A01 * B10
 *   M3 = S2  * T2
 *   M4 = S3  * T3
 *   M5 = S1  * T1
 *   M6 = S4  * B11
 *   M7 = A11 * T4
 *
 *   C00 = M1 + M2
 *   U1  = M1 + M3
 *   U2  = U1 + M4
 *   C11 = U2 + M6
 *   C10 = U2 + M5
 *   C01 = U1 + M5 + M7
 *
 * Complejidad: O(n^2.807) con menos operaciones de suma que StrassenNaiv
 */
public class Algoritmo7_StrassenWinograd {

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) {
        int n = A.length;
        if (n == 1) return new BigInteger[][]{{A[0][0].multiply(B[0][0])}};
        if (n == 2) {
            // Caso base con la formulacion Winograd
            BigInteger s1 = A[1][0].add(A[1][1]);
            BigInteger s2 = s1.subtract(A[0][0]);
            BigInteger s3 = A[0][0].subtract(A[1][0]);
            BigInteger s4 = A[0][1].subtract(s2);
            BigInteger t1 = B[0][1].subtract(B[0][0]);
            BigInteger t2 = B[1][1].subtract(t1);
            BigInteger t3 = B[1][1].subtract(B[0][1]);
            BigInteger t4 = B[1][0].subtract(t2);
            BigInteger m1 = A[0][0].multiply(B[0][0]);
            BigInteger m2 = A[0][1].multiply(B[1][0]);
            BigInteger m3 = s2.multiply(t2);
            BigInteger m4 = s3.multiply(t3);
            BigInteger m5 = s1.multiply(t1);
            BigInteger m6 = s4.multiply(B[1][1]);
            BigInteger m7 = A[1][1].multiply(t4);
            BigInteger u1 = m1.add(m3);
            BigInteger u2 = u1.add(m4);
            return new BigInteger[][]{
                {m1.add(m2), u1.add(m5).add(m7)},
                {u2.add(m5), u2.add(m6)}
            };
        }
        int h = n / 2;
        BigInteger[][] A00=sub(A,0,0,h), A01=sub(A,0,h,h);
        BigInteger[][] A10=sub(A,h,0,h), A11=sub(A,h,h,h);
        BigInteger[][] B00=sub(B,0,0,h), B01=sub(B,0,h,h);
        BigInteger[][] B10=sub(B,h,0,h), B11=sub(B,h,h,h);

        BigInteger[][] S1=add(A10,A11), S2=subs(S1,A00), S3=subs(A00,A10), S4=subs(A01,S2);
        BigInteger[][] T1=subs(B01,B00), T2=subs(B11,T1), T3=subs(B11,B01), T4=subs(B10,T2);

        BigInteger[][] M1=multiplicar(A00,B00), M2=multiplicar(A01,B10);
        BigInteger[][] M3=multiplicar(S2,T2),   M4=multiplicar(S3,T3);
        BigInteger[][] M5=multiplicar(S1,T1),   M6=multiplicar(S4,B11);
        BigInteger[][] M7=multiplicar(A11,T4);

        BigInteger[][] U1=add(M1,M3), U2=add(U1,M4);
        BigInteger[][] C00=add(M1,M2);
        BigInteger[][] C01=add(add(U1,M5),M7);
        BigInteger[][] C10=add(U2,M5);
        BigInteger[][] C11=add(U2,M6);

        BigInteger[][] C = new BigInteger[n][n];
        for (int i=0;i<h;i++) for (int j=0;j<h;j++) {
            C[i][j]=C00[i][j]; C[i][j+h]=C01[i][j];
            C[i+h][j]=C10[i][j]; C[i+h][j+h]=C11[i][j];
        }
        return C;
    }

    static BigInteger[][] sub(BigInteger[][] M, int r, int c, int h) {
        BigInteger[][] R=new BigInteger[h][h];
        for (int i=0;i<h;i++) for (int j=0;j<h;j++) R[i][j]=M[r+i][c+j];
        return R;
    }
    static BigInteger[][] add(BigInteger[][] X, BigInteger[][] Y) {
        int n=X.length; BigInteger[][] R=new BigInteger[n][n];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) R[i][j]=X[i][j].add(Y[i][j]);
        return R;
    }
    static BigInteger[][] subs(BigInteger[][] X, BigInteger[][] Y) {
        int n=X.length; BigInteger[][] R=new BigInteger[n][n];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) R[i][j]=X[i][j].subtract(Y[i][j]);
        return R;
    }
}
