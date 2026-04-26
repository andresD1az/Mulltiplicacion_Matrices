# Algoritmo 3: NaivLoopUnrollingFour
# Complejidad: O(n^3) con overhead de ciclo reducido al 25%
# Desenrolla el ciclo interno de a 4 pasos por iteracion
# El residuo (n % 4 elementos) se procesa con un ciclo simple al final
def multiplicar(A, B):
    n = len(A)
    C = [[0] * n for _ in range(n)]
    for i in range(n):
        for j in range(n):
            aux = 0
            k = 0
            # procesa de a 4 elementos por iteracion
            while k < n - 3:
                aux += (A[i][k]   * B[k][j]   +
                        A[i][k+1] * B[k+1][j] +
                        A[i][k+2] * B[k+2][j] +
                        A[i][k+3] * B[k+3][j])
                k += 4
            # elementos restantes (0, 1, 2 o 3 segun n % 4)
            while k < n:
                aux += A[i][k] * B[k][j]
                k += 1
            C[i][j] = aux
    return C
