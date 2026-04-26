# Algoritmo 10: III.5 Enhanced Parallel Block
# Complejidad: O(n^3 / p) con menor overhead de sincronizacion
# Mejora de Parallel Block que asigna rangos de filas exclusivos a cada hilo
# Sin solapamiento entre hilos = sin necesidad de locks
# Cada hilo trabaja en su propio rango de filas de forma independiente

import threading
import os

BLOQUE = 64
HILOS  = os.cpu_count()

def multiplicar(A, B):
    n = len(A)
    C = [[0] * n for _ in range(n)]

    def procesar_rango(fila_inicio, fila_fin):
        # cada hilo tiene su rango exclusivo de filas, sin sincronizacion
        for ii in range(fila_inicio, fila_fin, BLOQUE):
            for jj in range(0, n, BLOQUE):
                for kk in range(0, n, BLOQUE):
                    iM = min(ii + BLOQUE, fila_fin)
                    jM = min(jj + BLOQUE, n)
                    kM = min(kk + BLOQUE, n)
                    for i in range(ii, iM):
                        for k in range(kk, kM):
                            aik = A[i][k]  # carga A[i][k] una sola vez
                            for j in range(jj, jM):
                                C[i][j] += aik * B[k][j]

    # dividir filas entre hilos sin solapamiento
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
