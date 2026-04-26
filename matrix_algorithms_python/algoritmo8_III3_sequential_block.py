# Algoritmo 8: III.3 Sequential Block
# Complejidad: O(n^3) con mejor localidad de cache
# Divide las matrices en bloques de BLOQUE x BLOQUE
# Los bloques caben en cache L2, reduciendo accesos a RAM
# Orden de ciclos: ii-jj-kk (bloques) luego i-j-k (dentro del bloque)

BLOQUE = 64

def multiplicar(A, B, bloque=BLOQUE):
    n = len(A)
    C = [[0] * n for _ in range(n)]

    for ii in range(0, n, bloque):
        for jj in range(0, n, bloque):
            for kk in range(0, n, bloque):
                iM = min(ii + bloque, n)
                jM = min(jj + bloque, n)
                kM = min(kk + bloque, n)
                for i in range(ii, iM):
                    for j in range(jj, jM):
                        s = 0
                        for k in range(kk, kM):
                            s += A[i][k] * B[k][j]
                        C[i][j] += s
    return C
