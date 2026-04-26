import random
import math

# Genera una matriz n x n de enteros grandes con la cantidad de digitos indicada
# Python tiene enteros de precision arbitraria nativamente (equivalente a BigInteger)
def generar_big(n, digitos):
    minimo = 10 ** (digitos - 1)  # menor numero con esa cantidad de digitos
    maximo = 10 ** digitos - 1    # mayor numero con esa cantidad de digitos
    return [[random.randint(minimo, maximo) for _ in range(n)] for _ in range(n)]

# Rellena una matriz hasta la siguiente potencia de 2 con ceros
# Necesario para Strassen que requiere n = potencia de 2
def padear_pot2(m):
    n = len(m)
    p = 1
    while p < n:
        p *= 2
    if p == n:
        return m
    r = [[0] * p for _ in range(p)]
    for i in range(n):
        for j in range(n):
            r[i][j] = m[i][j]
    return r
