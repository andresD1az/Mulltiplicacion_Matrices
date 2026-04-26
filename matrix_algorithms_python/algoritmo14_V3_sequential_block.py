# Algoritmo 14: V.3 Sequential Block con transpuesta
# Complejidad: O(n^3) con mejor localidad de cache que IV.3
# Transpone B antes de multiplicar para que ambas matrices se accedan por filas
# Con BT transpuesta: C[i][j] = suma de A[i][k] * BT[j][k]
# Ambos accesos son por filas = cache-friendly

BLOQUE = 64

def multiplicar(A, B):
    n = len(A)

    # transponer B para acceso cache-friendly
    BT = [[B[i][j] for i in range(n)] for j in range(n)]

    C = [[0] * n for _ in range(n)]

    # multiplicacion por bloques sobre A y BT (ambas accedidas por filas)
    for ii in range(0, n, BLOQUE):
        for jj in range(0, n, BLOQUE):
            for kk in range(0, n, BLOQUE):
                iM = min(ii + BLOQUE, n)
                jM = min(jj + BLOQUE, n)
                kM = min(kk + BLOQUE, n)
                for i in range(ii, iM):
                    for j in range(jj, jM):
                        s = 0
                        for k in range(kk, kM):
                            s += A[i][k] * BT[j][k]  # ambos accesos por fila
                        C[i][j] += s
    return C
