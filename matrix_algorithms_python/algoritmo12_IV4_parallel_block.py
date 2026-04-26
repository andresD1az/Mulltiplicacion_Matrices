# Algoritmo 12: IV.4 Parallel Block
# Complejidad: O(n^3 / p)
# Version paralela de IV.3 Sequential Block
# Usa orden i-k-j dentro del bloque y distribuye bloques de filas entre hilos

import threading

BLOQUE = 64
HILOS  = __import__('os').cpu_count()

def multiplicar(A, B):
    n = len(A)
    C = [[0] * n for _ in range(n)]

    def procesar_bloque(ii):
        iM = min(ii + BLOQUE, n)
        for kk in range(0, n, BLOQUE):
            for jj in range(0, n, BLOQUE):
                kM = min(kk + BLOQUE, n)
                jM = min(jj + BLOQUE, n)
                for i in range(ii, iM):
                    for k in range(kk, kM):
                        aik = A[i][k]  # reuso de registro
                        for j in range(jj, jM):
                            C[i][j] += aik * B[k][j]

    hilos = []
    for ii in range(0, n, BLOQUE):
        t = threading.Thread(target=procesar_bloque, args=(ii,))
        hilos.append(t)
        t.start()
    for t in hilos:
        t.join()
    return C
