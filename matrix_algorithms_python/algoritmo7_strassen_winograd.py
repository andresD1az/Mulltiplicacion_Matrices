# Algoritmo 7: StrassenWinograd
# Complejidad: O(n^2.807) con menos sumas que StrassenNaiv (15 vs 18)
# Usa variables S1..S4 y T1..T4 para reutilizar sumas intermedias
# M1=A00*B00  M2=A01*B10  M3=S2*T2  M4=S3*T3
# M5=S1*T1   M6=S4*B11   M7=A11*T4
# C00=M1+M2  U1=M1+M3  U2=U1+M4
# C11=U2+M6  C10=U2+M5  C01=U1+M5+M7

def _sub(M, r, c, h):
    return [[M[r+i][c+j] for j in range(h)] for i in range(h)]

def _add(X, Y):
    n = len(X)
    return [[X[i][j] + Y[i][j] for j in range(n)] for i in range(n)]

def _subs(X, Y):
    n = len(X)
    return [[X[i][j] - Y[i][j] for j in range(n)] for i in range(n)]

def multiplicar(A, B):
    n = len(A)
    if n == 1:
        return [[A[0][0] * B[0][0]]]
    if n == 2:
        # caso base 2x2 con formulacion Winograd
        s1 = A[1][0] + A[1][1]
        s2 = s1 - A[0][0]
        s3 = A[0][0] - A[1][0]
        s4 = A[0][1] - s2
        t1 = B[0][1] - B[0][0]
        t2 = B[1][1] - t1
        t3 = B[1][1] - B[0][1]
        t4 = B[1][0] - t2
        m1 = A[0][0] * B[0][0]
        m2 = A[0][1] * B[1][0]
        m3 = s2 * t2
        m4 = s3 * t3
        m5 = s1 * t1
        m6 = s4 * B[1][1]
        m7 = A[1][1] * t4
        u1 = m1 + m3
        u2 = u1 + m4
        return [[m1+m2,    u1+m5+m7],
                [u2+m5,    u2+m6]]
    h = n // 2
    A00=_sub(A,0,0,h); A01=_sub(A,0,h,h)
    A10=_sub(A,h,0,h); A11=_sub(A,h,h,h)
    B00=_sub(B,0,0,h); B01=_sub(B,0,h,h)
    B10=_sub(B,h,0,h); B11=_sub(B,h,h,h)

    S1=_add(A10,A11); S2=_subs(S1,A00); S3=_subs(A00,A10); S4=_subs(A01,S2)
    T1=_subs(B01,B00); T2=_subs(B11,T1); T3=_subs(B11,B01); T4=_subs(B10,T2)

    M1=multiplicar(A00,B00); M2=multiplicar(A01,B10)
    M3=multiplicar(S2,T2);   M4=multiplicar(S3,T3)
    M5=multiplicar(S1,T1);   M6=multiplicar(S4,B11)
    M7=multiplicar(A11,T4)

    U1=_add(M1,M3); U2=_add(U1,M4)
    C00=_add(M1,M2)
    C01=_add(_add(U1,M5),M7)
    C10=_add(U2,M5)
    C11=_add(U2,M6)

    C = [[0]*n for _ in range(n)]
    for i in range(h):
        for j in range(h):
            C[i][j]     = C00[i][j]
            C[i][j+h]   = C01[i][j]
            C[i+h][j]   = C10[i][j]
            C[i+h][j+h] = C11[i][j]
    return C
