package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 7: Naive Recursivo
 * Tipo: Recursivo (Divide y Venceras)
 *
 * Version recursiva del algoritmo tradicional de multiplicacion.
 * Divide cada matriz en cuatro submatrices de tamano n/2 x n/2
 * y realiza 8 multiplicaciones recursivas (sin la optimizacion
 * de Strassen que las reduce a 7):
 *
 *   C00 = A00*B00 + A01*B10
 *   C01 = A00*B01 + A01*B11
 *   C10 = A10*B00 + A11*B10
 *   C11 = A10*B01 + A11*B11
 *
 * Caso base: matriz 1x1, producto escalar directo.
 * Requiere matrices cuadradas de tamano potencia de 2.
 *
 * Complejidad: O(n^3) - igual que el iterativo pero con overhead
 * adicional por las llamadas recursivas y la gestion de submatrices.
 */
public class Algoritmo7_NaivRecursivo {

    public static int[][] multiplicar(int[][] A, int[][] B) {
        int n = A.length;
        if (n == 1) return new int[][]{{A[0][0] * B[0][0]}};
        int h = n/2;
        int[][] A00=ex(A,0,0,h),A01=ex(A,0,h,h),A10=ex(A,h,0,h),A11=ex(A,h,h,h);
        int[][] B00=ex(B,0,0,h),B01=ex(B,0,h,h),B10=ex(B,h,0,h),B11=ex(B,h,h,h);
        int[][] C00=add(multiplicar(A00,B00),multiplicar(A01,B10));
        int[][] C01=add(multiplicar(A00,B01),multiplicar(A01,B11));
        int[][] C10=add(multiplicar(A10,B00),multiplicar(A11,B10));
        int[][] C11=add(multiplicar(A10,B01),multiplicar(A11,B11));
        int[][] C=new int[n][n];
        for(int i=0;i<h;i++) for(int j=0;j<h;j++){C[i][j]=C00[i][j];C[i][j+h]=C01[i][j];C[i+h][j]=C10[i][j];C[i+h][j+h]=C11[i][j];}
        return C;
    }

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) {
        int n = A.length;
        if (n == 1) return new BigInteger[][]{{A[0][0].multiply(B[0][0])}};
        int h = n/2;
        BigInteger[][] A00=exb(A,0,0,h),A01=exb(A,0,h,h),A10=exb(A,h,0,h),A11=exb(A,h,h,h);
        BigInteger[][] B00=exb(B,0,0,h),B01=exb(B,0,h,h),B10=exb(B,h,0,h),B11=exb(B,h,h,h);
        BigInteger[][] C00=addb(multiplicar(A00,B00),multiplicar(A01,B10));
        BigInteger[][] C01=addb(multiplicar(A00,B01),multiplicar(A01,B11));
        BigInteger[][] C10=addb(multiplicar(A10,B00),multiplicar(A11,B10));
        BigInteger[][] C11=addb(multiplicar(A10,B01),multiplicar(A11,B11));
        BigInteger[][] C=new BigInteger[n][n];
        for(int i=0;i<h;i++) for(int j=0;j<h;j++){C[i][j]=C00[i][j];C[i][j+h]=C01[i][j];C[i+h][j]=C10[i][j];C[i+h][j+h]=C11[i][j];}
        return C;
    }

    private static int[][] ex(int[][] M,int r,int c,int h){int[][]R=new int[h][h];for(int i=0;i<h;i++)for(int j=0;j<h;j++)R[i][j]=M[r+i][c+j];return R;}
    private static int[][] add(int[][] X,int[][] Y){int n=X.length;int[][]R=new int[n][n];for(int i=0;i<n;i++)for(int j=0;j<n;j++)R[i][j]=X[i][j]+Y[i][j];return R;}
    private static BigInteger[][] exb(BigInteger[][] M,int r,int c,int h){BigInteger[][]R=new BigInteger[h][h];for(int i=0;i<h;i++)for(int j=0;j<h;j++)R[i][j]=M[r+i][c+j];return R;}
    private static BigInteger[][] addb(BigInteger[][] X,BigInteger[][] Y){int n=X.length;BigInteger[][]R=new BigInteger[n][n];for(int i=0;i<n;i++)for(int j=0;j<n;j++)R[i][j]=X[i][j].add(Y[i][j]);return R;}
}
