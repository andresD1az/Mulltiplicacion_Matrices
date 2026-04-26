# Algoritmo 2: NaivLoopUnrollingTwo
# Complejidad: O(n^3) con menor overhead de ciclo
# Desenrolla el ciclo interno de a 2 pasos por iteracion
# Si n es impar, el ultimo elemento se procesa por separado
def multiplicar(A, B):
    n = len(A)
    C = [[0] * n for _ in range(n)]
    for i in range(n):
        for j in range(n):
            aux = 0
            k = 0
            # procesa de a 2 elementos por iteracion
            while k < n - 1:
                aux += A[i][k] * B[k][j] + A[i][k+1] * B[k+1][j]
                k += 2
            # elemento restante si n es impar
            if k < n:
                aux += A[i][k] * B[k][j]
            C[i][j] = aux
    return C
