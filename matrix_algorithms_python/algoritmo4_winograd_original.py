# Algoritmo 4: WinogradOriginal
# Complejidad: O(n^3) con ~15% menos multiplicaciones que Naiv
# Precalcula factores de fila y columna para reutilizarlos
#
# Paso 1 - rf[i] = suma de A[i][2k] * A[i][2k+1]
# Paso 2 - cf[j] = suma de B[2k][j] * B[2k+1][j]
# Paso 3 - C[i][j] = -rf[i] - cf[j] + suma de (A[i][2k]+B[2k+1][j]) * (A[i][2k+1]+B[2k][j])
# Paso 4 - si n es impar, agrega el ultimo termino A[i][n-1]*B[n-1][j]
def multiplicar(A, B):
    n = len(A)

    # Paso 1: precalcular factores de fila de A
    rf = [0] * n
    for i in range(n):
        for k in range(n // 2):
            rf[i] += A[i][2*k] * A[i][2*k+1]

    # Paso 2: precalcular factores de columna de B
    cf = [0] * n
    for j in range(n):
        for k in range(n // 2):
            cf[j] += B[2*k][j] * B[2*k+1][j]

    # Paso 3: calculo principal usando los factores precalculados
    C = [[0] * n for _ in range(n)]
    for i in range(n):
        for j in range(n):
            C[i][j] = -rf[i] - cf[j]  # inicia con -rf[i] - cf[j]
            for k in range(n // 2):
                C[i][j] += (A[i][2*k] + B[2*k+1][j]) * (A[i][2*k+1] + B[2*k][j])

    # Paso 4: correccion para n impar
    if n % 2 != 0:
        for i in range(n):
            for j in range(n):
                C[i][j] += A[i][n-1] * B[n-1][j]

    return C
