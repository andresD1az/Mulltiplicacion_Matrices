# Algoritmo 13: IV.5 Enhanced Parallel Block
# Complejidad: O(n^3 / p) con minimo overhead de sincronizacion
# Combina orden i-k-j + particion de filas sin solapamiento + sin locks

import threading
import os

BLOQUE = 64
HILOS  = os.cpu_count()

def multiplicar(A, B):
    n = len(A)
    C = [[0] * n for _ in range(n)]

    def procesar_rango(fila_inicio, fila_fin):
        for ii in range(fila_inicio, fila_fin, BLOQUE):
            for kk in range(0, n, BLOQUE):
                for jj in range(0, n, BLOQUE):
                    iM = min(ii + BLOQUE, fila_fin)
                    kM = min(kk + BLOQUE, n)
                    jM = min(jj + BLOQUE, n)
                    for i in range(ii, iM):
                        for k in range(kk, kM):
                            aik = A[i][k]  # reuso de registro
                            for j in range(jj, jM):
                                C[i][j] += aik * B[k][j]

    filas_por_hilo = max(1, n // HILOS)
    hilos = []
    for h in range(HILOS):
        inicio = h * filas_por_hilo
        fin    = n if h == HILOS - 1 else inicio + filas_por_hilo
        if inicio >= n:
            break
        t = threading.Thread(target=procesar_rango, args=(inicio, fin))
        hilos.append(t)
        t.start()
    for t in hilos:
        t.join()
    return C
