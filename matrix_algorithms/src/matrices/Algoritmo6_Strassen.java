package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 6: Strassen
 * Tipo: Recursivo (Divide y Venceras)
 *
 * Metodo de Strassen para multiplicacion de matrices. Utiliza la
 * estrategia divide y venceras dividiendo cada matriz A y B de
 * tamano n x n (donde n = 2^k) en cuatro submatrices de n/2 x n/2:
 *
 *   A = | A00  A01 |    B = | B00  B01 |
 *       | A10  A11 |        | B10  B11 |
 *
 * Calcula 7 productos intermedios (en lugar de 8):
 *   M1 = (A00+A11) * (B00+B11)
 *   M2 = (A10+A11) * B00
 *   M3 = A00 * (B01-B11)
 *   M4 = A11 * (B10-B00)
 *   M5 = (A00+A01) * B11
 *   M6 = (A10-A00) * (B00+B01)
 *   M7 = (A01-A11) * (B10+B11)
 *
 * Los cuadrantes del resultado se obtienen como:
 *   C00 = M1 + M4 - M5 + M7
 *   C01 = M3 + M5
 *   C10 = M2 + M4
 *   C11 = M1 - M2 + M3 + M6
 *
 * Caso base: matriz 2x2 con los valores directos del PDF.
 * Requiere matrices cuadradas de tamano potencia de 2.
 *
 * Complejidad: O(n^2.807) - mejor que O(n^3) del metodo tradicional
 * Fuente: Multiplicacion de matrices grandes - 2025-1.pdf, paginas 4-9
 */
public class Algoritmo6_Strassen {

    public static int[][] multiplicar(int[][] A, int[][] B) {
        int n = A.length;
        if (n == 1) return new int[][]{{A[0][0] * B[0][0]}};
        if (n == 2) {
            int m1=(A[0][0]+A[1][1])*(B[0][0]+B[1][1]), m2=(A[1][0]+A[1][1])*B[0][0];
            int m3=A[0][0]*(B[0][1]-B[1][1]),            m4=A[1][1]*(B[1][0]-B[0][0]);
            int m5=(A[0][0]+A[0][1])*B[1][1],            m6=(A[1][0]-A[0][0])*(B[0][0]+B[0][1]);
            int m7=(A[0][1]-A[1][1])*(B[1][0]+B[1][1]);
            return new int[][]{{m1+m4-m5+m7, m3+m5},{m2+m4, m1-m2+m3+m6}};
        }
        int h = n/2;
        int[][] A00=s(A,0,0,h),A01=s(A,0,h,h),A10=s(A,h,0,h),A11=s(A,h,h,h);
        int[][] B00=s(B,0,0,h),B01=s(B,0,h,h),B10=s(B,h,0,h),B11=s(B,h,h,h);
        int[][] M1=multiplicar(add(A00,A11),add(B00,B11)), M2=multiplicar(add(A10,A11),B00);
        int[][] M3=multiplicar(A00,sub(B01,B11)),          M4=multiplicar(A11,sub(B10,B00));
        int[][] M5=multiplicar(add(A00,A01),B11),          M6=multiplicar(sub(A10,A00),add(B00,B01));
        int[][] M7=multiplicar(sub(A01,A11),add(B10,B11));
        int[][] C=new int[n][n];
        int[][] C00=add(sub(add(M1,M4),M5),M7), C01=add(M3,M5), C10=add(M2,M4), C11=add(sub(add(M1,M3),M2),M6);
        for(int i=0;i<h;i++) for(int j=0;j<h;j++){C[i][j]=C00[i][j];C[i][j+h]=C01[i][j];C[i+h][j]=C10[i][j];C[i+h][j+h]=C11[i][j];}
        return C;
    }

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) {
        int n = A.length;
        if (n == 1) return new BigInteger[][]{{A[0][0].multiply(B[0][0])}};
        if (n == 2) {
            BigInteger m1=A[0][0].add(A[1][1]).multiply(B[0][0].add(B[1][1]));
            BigInteger m2=A[1][0].add(A[1][1]).multiply(B[0][0]);
            BigInteger m3=A[0][0].multiply(B[0][1].subtract(B[1][1]));
            BigInteger m4=A[1][1].multiply(B[1][0].subtract(B[0][0]));
            BigInteger m5=A[0][0].add(A[0][1]).multiply(B[1][1]);
            BigInteger m6=A[1][0].subtract(A[0][0]).multiply(B[0][0].add(B[0][1]));
            BigInteger m7=A[0][1].subtract(A[1][1]).multiply(B[1][0].add(B[1][1]));
            return new BigInteger[][]{{m1.add(m4).subtract(m5).add(m7),m3.add(m5)},{m2.add(m4),m1.subtract(m2).add(m3).add(m6)}};
        }
        int h=n/2;
        BigInteger[][] A00=sb(A,0,0,h),A01=sb(A,0,h,h),A10=sb(A,h,0,h),A11=sb(A,h,h,h);
        BigInteger[][] B00=sb(B,0,0,h),B01=sb(B,0,h,h),B10=sb(B,h,0,h),B11=sb(B,h,h,h);
        BigInteger[][] M1=multiplicar(addb(A00,A11),addb(B00,B11)), M2=multiplicar(addb(A10,A11),B00);
        BigInteger[][] M3=multiplicar(A00,subb(B01,B11)),           M4=multiplicar(A11,subb(B10,B00));
        BigInteger[][] M5=multiplicar(addb(A00,A01),B11),           M6=multiplicar(subb(A10,A00),addb(B00,B01));
        BigInteger[][] M7=multiplicar(subb(A01,A11),addb(B10,B11));
        BigInteger[][] C=new BigInteger[n][n];
        BigInteger[][] C00=addb(subb(addb(M1,M4),M5),M7),C01=addb(M3,M5),C10=addb(M2,M4),C11=addb(subb(addb(M1,M3),M2),M6);
        for(int i=0;i<h;i++) for(int j=0;j<h;j++){C[i][j]=C00[i][j];C[i][j+h]=C01[i][j];C[i+h][j]=C10[i][j];C[i+h][j+h]=C11[i][j];}
        return C;
    }

    private static int[][] s(int[][] M,int r,int c,int h){int[][]R=new int[h][h];for(int i=0;i<h;i++)for(int j=0;j<h;j++)R[i][j]=M[r+i][c+j];return R;}
    private static int[][] add(int[][] X,int[][] Y){int n=X.length;int[][]R=new int[n][n];for(int i=0;i<n;i++)for(int j=0;j<n;j++)R[i][j]=X[i][j]+Y[i][j];return R;}
    private static int[][] sub(int[][] X,int[][] Y){int n=X.length;int[][]R=new int[n][n];for(int i=0;i<n;i++)for(int j=0;j<n;j++)R[i][j]=X[i][j]-Y[i][j];return R;}
    private static BigInteger[][] sb(BigInteger[][] M,int r,int c,int h){BigInteger[][]R=new BigInteger[h][h];for(int i=0;i<h;i++)for(int j=0;j<h;j++)R[i][j]=M[r+i][c+j];return R;}
    private static BigInteger[][] addb(BigInteger[][] X,BigInteger[][] Y){int n=X.length;BigInteger[][]R=new BigInteger[n][n];for(int i=0;i<n;i++)for(int j=0;j<n;j++)R[i][j]=X[i][j].add(Y[i][j]);return R;}
    private static BigInteger[][] subb(BigInteger[][] X,BigInteger[][] Y){int n=X.length;BigInteger[][]R=new BigInteger[n][n];for(int i=0;i<n;i++)for(int j=0;j<n;j++)R[i][j]=X[i][j].subtract(Y[i][j]);return R;}
}
