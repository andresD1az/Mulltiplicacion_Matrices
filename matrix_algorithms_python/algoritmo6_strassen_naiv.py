# Algoritmo 6: StrassenNaiv
# Complejidad: O(n^2.807)
# Divide cada matriz en 4 submatrices y hace 7 multiplicaciones recursivas
# M1=(A00+A11)*(B00+B11)   C00=M1+M4-M5+M7
# M2=(A10+A11)*B00         C01=M3+M5
# M3=A00*(B01-B11)         C10=M2+M4
# M4=A11*(B10-B00)         C11=M1-M2+M3+M6
# M5=(A00+A01)*B11
# M6=(A10-A00)*(B00+B01)
# M7=(A01-A11)*(B10+B11)
# Requiere n = potencia de 2

def _sub(M, r, c, h):
    # extrae submatriz de h x h desde posicion (r, c)
    return [[M[r+i][c+j] for j in range(h)] for i in range(h)]

def _add(X, Y):
    n = len(X)
    return [[X[i][j] + Y[i][j] for j in range(n)] for i in range(n)]

def _subs(X, Y):
    n = len(X)
    return [[X[i][j] - Y[i][j] for j in range(n)] for i in range(n)]

def _combinar(C, C00, C01, C10, C11, h):
    # ensambla las 4 submatrices en la matriz resultado C
    for i in range(h):
        for j in range(h):
            C[i][j]     = C00[i][j]
            C[i][j+h]   = C01[i][j]
            C[i+h][j]   = C10[i][j]
            C[i+h][j+h] = C11[i][j]

def multiplicar(A, B):
    n = len(A)
    if n == 1:
        return [[A[0][0] * B[0][0]]]
    if n == 2:
        # caso base 2x2
        m1 = (A[0][0]+A[1][1]) * (B[0][0]+B[1][1])
        m2 = (A[1][0]+A[1][1]) * B[0][0]
        m3 = A[0][0] * (B[0][1]-B[1][1])
        m4 = A[1][1] * (B[1][0]-B[0][0])
        m5 = (A[0][0]+A[0][1]) * B[1][1]
        m6 = (A[1][0]-A[0][0]) * (B[0][0]+B[0][1])
        m7 = (A[0][1]-A[1][1]) * (B[1][0]+B[1][1])
        return [[m1+m4-m5+m7, m3+m5],
                [m2+m4,       m1-m2+m3+m6]]
    h = n // 2
    A00=_sub(A,0,0,h); A01=_sub(A,0,h,h)
    A10=_sub(A,h,0,h); A11=_sub(A,h,h,h)
    B00=_sub(B,0,0,h); B01=_sub(B,0,h,h)
    B10=_sub(B,h,0,h); B11=_sub(B,h,h,h)

    M1=multiplicar(_add(A00,A11), _add(B00,B11))
    M2=multiplicar(_add(A10,A11), B00)
    M3=multiplicar(A00, _subs(B01,B11))
    M4=multiplicar(A11, _subs(B10,B00))
    M5=multiplicar(_add(A00,A01), B11)
    M6=multiplicar(_subs(A10,A00), _add(B00,B01))
    M7=multiplicar(_subs(A01,A11), _add(B10,B11))

    C00=_add(_subs(_add(M1,M4),M5),M7)
    C01=_add(M3,M5)
    C10=_add(M2,M4)
    C11=_add(_subs(_add(M1,M3),M2),M6)

    C = [[0]*n for _ in range(n)]
    _combinar(C, C00, C01, C10, C11, h)
    return C
