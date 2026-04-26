# Algoritmo 5: WinogradScaled
# Complejidad: O(n^3)
# Variante de Winograd con escalado previo de las matrices
# Escala cada fila de A y cada columna de B por su valor maximo
# para normalizar los valores antes de aplicar Winograd
def multiplicar(A, B):
    n = len(A)

    # Escalar cada fila de A por su valor maximo
    As = [[0] * n for _ in range(n)]
    for i in range(n):
        max_fila = max(abs(A[i][k]) for k in range(n)) or 1
        for k in range(n):
            As[i][k] = A[i][k] // max_fila

    # Escalar cada columna de B por su valor maximo
    Bs = [[0] * n for _ in range(n)]
    for j in range(n):
        max_col = max(abs(B[k][j]) for k in range(n)) or 1
        for k in range(n):
            Bs[k][j] = B[k][j] // max_col

    # Aplicar Winograd sobre las matrices escaladas
    rf = [0] * n
    for i in range(n):
        for k in range(n // 2):
            rf[i] += As[i][2*k] * As[i][2*k+1]

    cf = [0] * n
    for j in range(n):
        for k in range(n // 2):
            cf[j] += Bs[2*k][j] * Bs[2*k+1][j]

    C = [[0] * n for _ in range(n)]
    for i in range(n):
        for j in range(n):
            C[i][j] = -rf[i] - cf[j]
            for k in range(n // 2):
                C[i][j] += (As[i][2*k] + Bs[2*k+1][j]) * (As[i][2*k+1] + Bs[2*k][j])

    if n % 2 != 0:
        for i in range(n):
            for j in range(n):
                C[i][j] += As[i][n-1] * Bs[n-1][j]

    return C
