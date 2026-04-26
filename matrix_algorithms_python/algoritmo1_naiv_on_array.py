# Algoritmo 1: NaivOnArray
# Complejidad: O(n^3)
# Multiplicacion clasica con tres ciclos anidados i-j-k
# C[i][j] = suma de A[i][k] * B[k][j] para k = 0..n-1
def multiplicar(A, B):
    n = len(A)
    C = [[0] * n for _ in range(n)]
    for i in range(n):
        for j in range(n):
            aux = 0  # acumulador del producto punto
            for k in range(n):
                aux += A[i][k] * B[k][j]
            C[i][j] = aux
    return C
