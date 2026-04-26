# Algoritmo 9: III.4 Parallel Block
# Complejidad: O(n^3 / p) donde p es el numero de procesadores
# Variante paralela de Sequential Block
# Distribuye bloques de filas entre multiples hilos
# Usa Lock por fila para evitar condiciones de carrera

import threading

BLOQUE = 64
HILOS  = __import__('os').cpu_count()

def multiplicar(A, B):
    n = len(A)
    C = [[0] * n for _ in range(n)]
    locks = [threading.Lock() for _ in range(n)]  # un lock por fila

    def procesar_bloque(ii):
        iM = min(ii + BLOQUE, n)
        for jj in range(0, n, BLOQUE):
            for kk in range(0, n, BLOQUE):
                jM = min(jj + BLOQUE, n)
                kM = min(kk + BLOQUE, n)
                for i in range(ii, iM):
                    for j in range(jj, jM):
                        s = 0
                        for k in range(kk, kM):
                            s += A[i][k] * B[k][j]
                        with locks[i]:  # sincronizacion por fila
                            C[i][j] += s

    # lanzar un hilo por bloque de filas
    hilos = []
    for ii in range(0, n, BLOQUE):
        t = threading.Thread(target=procesar_bloque, args=(ii,))
        hilos.append(t)
        t.start()
    for t in hilos:
        t.join()
    return C
