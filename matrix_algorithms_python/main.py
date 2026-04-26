import time
import threading
import os
import sys

from matrix_utils         import generar_big, padear_pot2
from algoritmo1_naiv_on_array            import multiplicar as alg1
from algoritmo2_naiv_loop_unrolling_two  import multiplicar as alg2
from algoritmo3_naiv_loop_unrolling_four import multiplicar as alg3
from algoritmo4_winograd_original        import multiplicar as alg4
from algoritmo5_winograd_scaled          import multiplicar as alg5
from algoritmo6_strassen_naiv            import multiplicar as alg6
from algoritmo7_strassen_winograd        import multiplicar as alg7
from algoritmo8_III3_sequential_block    import multiplicar as alg8
from algoritmo9_III4_parallel_block      import multiplicar as alg9
from algoritmo10_III5_enhanced_parallel_block import multiplicar as alg10
from algoritmo11_IV3_sequential_block    import multiplicar as alg11
from algoritmo12_IV4_parallel_block      import multiplicar as alg12
from algoritmo13_IV5_enhanced_parallel_block  import multiplicar as alg13
from algoritmo14_V3_sequential_block     import multiplicar as alg14
from algoritmo15_V4_parallel_block       import multiplicar as alg15
from resultado_guardador import guardar

# ============================================================
# CAMBIA ESTOS DOS VALORES Y EJECUTA
# Caso 1 sugerido: N_POTENCIA=9  (512x512),  DIGITOS=6
# Caso 2 sugerido: N_POTENCIA=11 (2048x2048), DIGITOS=6
N_POTENCIA = 11    # tamano = 2^N_POTENCIA
DIGITOS    = 4    # digitos por valor (enunciado pide minimo 6)
# ============================================================

NOMBRES = [
    "1.NaivOnArray",
    "2.NaivLoopUnrollingTwo",
    "3.NaivLoopUnrollingFour",
    "4.WinogradOriginal",
    "5.WinogradScaled",
    "6.StrassenNaiv",
    "7.StrassenWinograd",
    "8.III.3-SeqBlock",
    "9.III.4-ParBlock",
    "10.III.5-EnhParBlock",
    "11.IV.3-SeqBlock",
    "12.IV.4-ParBlock",
    "13.IV.5-EnhParBlock",
    "14.V.3-SeqBlock",
    "15.V.4-ParBlock",
]

EXPONENTE = [3.0, 3.0, 3.0, 3.0, 3.0, 2.807, 2.807,
             3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0]

FACTOR_OPS = [1.0, 0.5, 0.25, 0.5, 0.5, 1.0, 0.83,
              1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0]

def log_memoria(momento):
    # muestra uso de memoria del proceso en MB
    try:
        import psutil
        proc = psutil.Process(os.getpid())
        mb = proc.memory_info().rss / 1_000_000
        print(f"[MEM][{momento}] usado={mb:.0f}MB")
    except ImportError:
        pass  # psutil no instalado, se omite

def format_ms(ms):
    if ms < 1000:    return f"{ms} ms"
    if ms < 60000:   return f"{ms/1000:.2f} s"
    if ms < 3600000: return f"{ms/60000:.2f} min"
    return f"{ms/3600000:.2f} h"

def correr(idx, fn, args):
    # ejecuta un algoritmo, mide tiempo y captura errores
    nombre = NOMBRES[idx]
    ahora  = time.strftime("%H:%M:%S")
    print(f"[INICIO] {nombre} a las {ahora}")
    t = time.time()
    try:
        fn(*args)
        ms = int((time.time() - t) * 1000)
        print(f"[FIN]    {nombre} | duracion: {format_ms(ms)}")
        return ms
    except Exception as e:
        print(f"[ERROR]  {nombre} -> {type(e).__name__}: {e}")
        return -1

def linea():
    print("-" * 60)

def main():
    tamano = 2 ** N_POTENCIA

    # calibracion: mide NaivOnArray con matriz 16x16 para estimar tiempos
    cA = generar_big(16, DIGITOS)
    cB = generar_big(16, DIGITOS)
    t_cal = time.time()
    alg1(cA, cB)
    ms_cal = max(1, int((time.time() - t_cal) * 1000))
    k = ms_cal / (16 ** 3)  # ms por operacion BigInteger en esta maquina

    print()
    print(f"Matriz  : {tamano} x {tamano} (2^{N_POTENCIA})")
    print(f"Digitos : {DIGITOS} por valor")
    print(f"Hilos   : {len(NOMBRES)} (uno por algoritmo)")
    print(f"CPU     : {os.cpu_count()} nucleos disponibles")
    print()

    print("ESTIMADO DE TIEMPO:")
    linea()
    print(f"{'Algoritmo':<28} {'Tiempo estimado'}")
    linea()
    for i in range(len(NOMBRES)):
        ops = FACTOR_OPS[i] * (tamano ** EXPONENTE[i])
        est = round(k * ops)
        print(f"{NOMBRES[i]:<28} {format_ms(est)}")
    linea()
    print()

    print(f"Generando matrices {tamano}x{tamano} con {DIGITOS} digitos...")
    log_memoria("antes-generar")
    A  = generar_big(tamano, DIGITOS)
    B  = generar_big(tamano, DIGITOS)
    log_memoria("despues-generar")
    Ap = padear_pot2(A)
    Bp = padear_pot2(B)
    log_memoria("despues-padear")
    print(f"Matrices generadas. Lanzando {len(NOMBRES)} hilos...")
    print()

    tiempos   = [-1] * len(NOMBRES)
    resultados = [None] * len(NOMBRES)
    lock = threading.Lock()

    def tarea(idx, fn, args):
        ms = correr(idx, fn, args)
        with lock:
            tiempos[idx] = ms

    # lanzar todos los algoritmos en paralelo
    hilos = [
        threading.Thread(target=tarea, args=(0,  alg1,  (A,  B))),
        threading.Thread(target=tarea, args=(1,  alg2,  (A,  B))),
        threading.Thread(target=tarea, args=(2,  alg3,  (A,  B))),
        threading.Thread(target=tarea, args=(3,  alg4,  (A,  B))),
        threading.Thread(target=tarea, args=(4,  alg5,  (A,  B))),
        threading.Thread(target=tarea, args=(5,  alg6,  (Ap, Bp))),
        threading.Thread(target=tarea, args=(6,  alg7,  (Ap, Bp))),
        threading.Thread(target=tarea, args=(7,  alg8,  (A,  B))),
        threading.Thread(target=tarea, args=(8,  alg9,  (A,  B))),
        threading.Thread(target=tarea, args=(9,  alg10, (A,  B))),
        threading.Thread(target=tarea, args=(10, alg11, (A,  B))),
        threading.Thread(target=tarea, args=(11, alg12, (A,  B))),
        threading.Thread(target=tarea, args=(12, alg13, (A,  B))),
        threading.Thread(target=tarea, args=(13, alg14, (A,  B))),
        threading.Thread(target=tarea, args=(14, alg15, (A,  B))),
    ]
    for h in hilos: h.start()
    for h in hilos: h.join()

    # calcular estimados para guardar
    estimados = [round(k * FACTOR_OPS[i] * (tamano ** EXPONENTE[i])) for i in range(len(NOMBRES))]

    print()
    print("RESULTADOS FINALES")
    linea()
    print(f"{'Algoritmo':<28} {'Estimado':<15} {'Real':<12} {'Ratio'}")
    linea()
    for i in range(len(NOMBRES)):
        real_str = format_ms(tiempos[i]) if tiempos[i] >= 0 else "ERROR"
        ratio    = tiempos[i] / estimados[i] if estimados[i] > 0 and tiempos[i] >= 0 else 0
        ratio_str = f"{ratio:.2f}x" if tiempos[i] >= 0 else "N/A"
        print(f"{NOMBRES[i]:<28} {format_ms(estimados[i]):<15} {real_str:<12} {ratio_str}")
    linea()

    guardar(tamano, DIGITOS, NOMBRES, estimados, tiempos, k, EXPONENTE, FACTOR_OPS)
    print("Prueba finalizada.")

if __name__ == "__main__":
    main()
