package matrices;

import java.math.BigInteger;

/**
 * Algoritmo 10: Winograd
 * Tipo: Iterativo
 *
 * Algoritmo propuesto por Winograd (1968) que reduce el numero de
 * multiplicaciones mediante el precalculo de factores de fila y columna.
 *
 * Paso 1 - Precalcular factores de fila para A:
 *   rowFactor[i] = SUM( A[i][2k] * A[i][2k+1] )  para k = 0..n/2-1
 *
 * Paso 2 - Precalcular factores de columna para B:
 *   colFactor[j] = SUM( B[2k][j] * B[2k+1][j] )  para k = 0..n/2-1
 *
 * Paso 3 - Calcular C[i][j] usando los factores:
 *   C[i][j] = -rowFactor[i] - colFactor[j]
 *           + SUM( (A[i][2k]+B[2k+1][j]) * (A[i][2k+1]+B[2k][j]) )
 *
 * La expansion algebraica de cada termino del paso 3 incluye los
 * productos A*A y B*B que ya estan precalculados en los factores,
 * evitando multiplicaciones redundantes.
 *
 * Paso 4 - Si n es impar, agregar el ultimo elemento sin par.
 *
 * Complejidad: O(n^3) con aproximadamente n^3/2 multiplicaciones
 * (reduccion de ~15% respecto al naive)
 */
public class Algoritmo10_Winograd {

    public static int[][] multiplicar(int[][] A, int[][] B) {
        int n = A.length;
        int[] rf = new int[n], cf = new int[n];
        for (int i=0;i<n;i++) for (int k=0;k<n/2;k++) rf[i]+=A[i][2*k]*A[i][2*k+1];
        for (int j=0;j<n;j++) for (int k=0;k<n/2;k++) cf[j]+=B[2*k][j]*B[2*k+1][j];
        int[][] C = new int[n][n];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) {
            C[i][j]=-rf[i]-cf[j];
            for (int k=0;k<n/2;k++) C[i][j]+=(A[i][2*k]+B[2*k+1][j])*(A[i][2*k+1]+B[2*k][j]);
            if (n%2!=0) C[i][j]+=A[i][n-1]*B[n-1][j];
        }
        return C;
    }

    public static BigInteger[][] multiplicar(BigInteger[][] A, BigInteger[][] B) {
        int n = A.length;
        BigInteger[] rf=new BigInteger[n], cf=new BigInteger[n];
        for (int i=0;i<n;i++){rf[i]=BigInteger.ZERO;for(int k=0;k<n/2;k++)rf[i]=rf[i].add(A[i][2*k].multiply(A[i][2*k+1]));}
        for (int j=0;j<n;j++){cf[j]=BigInteger.ZERO;for(int k=0;k<n/2;k++)cf[j]=cf[j].add(B[2*k][j].multiply(B[2*k+1][j]));}
        BigInteger[][] C=new BigInteger[n][n];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) {
            C[i][j]=rf[i].negate().subtract(cf[j]);
            for (int k=0;k<n/2;k++) C[i][j]=C[i][j].add(A[i][2*k].add(B[2*k+1][j]).multiply(A[i][2*k+1].add(B[2*k][j])));
            if (n%2!=0) C[i][j]=C[i][j].add(A[i][n-1].multiply(B[n-1][j]));
        }
        return C;
    }
}
