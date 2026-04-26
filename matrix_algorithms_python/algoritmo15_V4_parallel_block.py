# Algoritmo 15: V.4 Parallel Block con transpuesta
# Complejidad: O(n^3 / p) con mejor localidad de cache
# Combina las tres tecnicas:
#   1. Transpuesta de B para acceso cache-friendly
#   2. Multiplicacion por bloques para reuso de cache
#   3. Paralelismo con particion de filas sin sincronizacion

import threading
import os

BLOQUE = 64
HILOS  = os.cpu_count()

def multiplicar(A, B):
    n = len(A)

    # transponer B
    BT = [[B[i][j] for i in range(n)] for j in range(n)]

    C = [[0] * n for _ in range(n)]

    def procesar_rango(fila_inicio, fila_fin):
        for ii in range(fila_inicio, fila_fin, BLOQUE):
            for jj in range(0, n, BLOQUE):
                for kk in range(0, n, BLOQUE):
                    iM = min(ii + BLOQUE, fila_fin)
                    jM = min(jj + BLOQUE, n)
                    kM = min(kk + BLOQUE, n)
                    for i in range(ii, iM):
                        for j in range(jj, jM):
                            s = 0
                            for k in range(kk, kM):
                                s += A[i][k] * BT[j][k]  # ambos accesos por fila
                            C[i][j] += s

    # particion de filas sin solapamiento
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
