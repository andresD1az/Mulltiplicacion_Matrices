package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 6: StrassenNaiv
 * Tipo: Recursivo (Divide y Venceras)
 * Descripcion: Algoritmo de Strassen clasico. Divide cada matriz en 4
 * submatrices y realiza 7 multiplicaciones recursivas en lugar de 8.
 *
 *   M1=(A00+A11)*(B00+B11)   C00=M1+M4-M5+M7
 *   M2=(A10+A11)*B00         C01=M3+M5
 *   M3=A00*(B01-B11)         C10=M2+M4
 *   M4=A11*(B10-B00)         C11=M1-M2+M3+M6
 *   M5=(A00+A01)*B11
 *   M6=(A10-A00)*(B00+B01)
 *   M7=(A01-A11)*(B10+B11)
 *
 * Requiere n = potencia de 2. Caso base: n==2.
 * Complejidad: O(n^2.807)
 * Fuente: Multiplicacion de matrices grandes - 2025-1.pdf, paginas 4-9
 */
public class Algoritmo6_StrassenNaiv {

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) {
        int n = A.length;
        if (n == 1) return new BigInteger[][]{{A[0][0].multiply(B[0][0])}};
        if (n == 2) {
            BigInteger m1 = A[0][0].add(A[1][1]).multiply(B[0][0].add(B[1][1]));
            BigInteger m2 = A[1][0].add(A[1][1]).multiply(B[0][0]);
            BigInteger m3 = A[0][0].multiply(B[0][1].subtract(B[1][1]));
            BigInteger m4 = A[1][1].multiply(B[1][0].subtract(B[0][0]));
            BigInteger m5 = A[0][0].add(A[0][1]).multiply(B[1][1]);
            BigInteger m6 = A[1][0].subtract(A[0][0]).multiply(B[0][0].add(B[0][1]));
            BigInteger m7 = A[0][1].subtract(A[1][1]).multiply(B[1][0].add(B[1][1]));
            return new BigInteger[][]{
                {m1.add(m4).subtract(m5).add(m7), m3.add(m5)},
                {m2.add(m4), m1.subtract(m2).add(m3).add(m6)}
            };
        }
        int h = n / 2;
        BigInteger[][] A00=sub(A,0,0,h), A01=sub(A,0,h,h);
        BigInteger[][] A10=sub(A,h,0,h), A11=sub(A,h,h,h);
        BigInteger[][] B00=sub(B,0,0,h), B01=sub(B,0,h,h);
        BigInteger[][] B10=sub(B,h,0,h), B11=sub(B,h,h,h);

        BigInteger[][] M1=multiplicar(add(A00,A11), add(B00,B11));
        BigInteger[][] M2=multiplicar(add(A10,A11), B00);
        BigInteger[][] M3=multiplicar(A00, subs(B01,B11));
        BigInteger[][] M4=multiplicar(A11, subs(B10,B00));
        BigInteger[][] M5=multiplicar(add(A00,A01), B11);
        BigInteger[][] M6=multiplicar(subs(A10,A00), add(B00,B01));
        BigInteger[][] M7=multiplicar(subs(A01,A11), add(B10,B11));

        BigInteger[][] C00=add(subs(add(M1,M4),M5),M7);
        BigInteger[][] C01=add(M3,M5);
        BigInteger[][] C10=add(M2,M4);
        BigInteger[][] C11=add(subs(add(M1,M3),M2),M6);

        BigInteger[][] C = new BigInteger[n][n];
        for (int i=0;i<h;i++) for (int j=0;j<h;j++) {
            C[i][j]=C00[i][j]; C[i][j+h]=C01[i][j];
            C[i+h][j]=C10[i][j]; C[i+h][j+h]=C11[i][j];
        }
        return C;
    }

    static BigInteger[][] sub(BigInteger[][] M, int r, int c, int h) {
        BigInteger[][] R = new BigInteger[h][h];
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
