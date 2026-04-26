# Algoritmo 11: IV.3 Sequential Block
# Complejidad: O(n^3) con mejor reuso de registros que III.3
# Cambia el orden de ciclos a ii-kk-jj (bloques) luego i-k-j (dentro del bloque)
# El orden i-k-j carga A[i][k] una sola vez y lo reutiliza para todos los j
# Es el orden mas eficiente para matrices almacenadas por filas (row-major)

BLOQUE = 64

def multiplicar(A, B, bloque=BLOQUE):
    n = len(A)
    C = [[0] * n for _ in range(n)]

    for ii in range(0, n, bloque):
        for kk in range(0, n, bloque):       # kk antes que jj = orden i-k-j
            for jj in range(0, n, bloque):
                iM = min(ii + bloque, n)
                kM = min(kk + bloque, n)
                jM = min(jj + bloque, n)
                for i in range(ii, iM):
                    for k in range(kk, kM):
                        aik = A[i][k]        # A[i][k] se carga una vez para todos los j
                        for j in range(jj, jM):
                            C[i][j] += aik * B[k][j]
    return C
