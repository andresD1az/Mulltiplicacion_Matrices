import os
import csv
from datetime import datetime

# rutas relativas al directorio donde se ejecuta el script
CSV = "resultados.csv"
TXT = "resultados.txt"

# Cuenta cuantos casos hay ya en el CSV para numerar el nuevo
def contar_casos():
    if not os.path.exists(CSV):
        return 0
    max_caso = 0
    with open(CSV, "r") as f:
        for linea in f:
            if linea.startswith("Caso "):
                try:
                    num = int(linea.split(" ")[1])
                    if num > max_caso:
                        max_caso = num
                except:
                    pass
    return max_caso

def format_ms(ms):
    if ms < 0:       return "ERROR"
    if ms < 1000:    return f"{ms} ms"
    if ms < 60000:   return f"{ms/1000:.2f} s"
    if ms < 3600000: return f"{ms/60000:.2f} min"
    return f"{ms/3600000:.2f} h"

def format_ops(ops):
    if ops < 1_000:         return str(ops)
    if ops < 1_000_000:     return f"{ops/1000:.1f}K"
    if ops < 1_000_000_000: return f"{ops/1_000_000:.1f}M"
    return f"{ops/1_000_000_000:.1f}B"

def analizar(ratio):
    if ratio <= 0:   return ""
    if ratio < 0.5:  return "(mucho mejor)"
    if ratio < 0.9:  return "(mejor)"
    if ratio <= 1.1: return "(acorde)"
    if ratio <= 2.0: return "(algo lento)"
    return "(mucho mas lento)"

def guardar(tamano, digitos, nombres, estimados, reales, k, exponentes, factores):
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    numero_caso = contar_casos() + 1
    caso_label  = f"Caso {numero_caso} (n={tamano})"

    # crear CSV con encabezado si no existe
    escribir_header = not os.path.exists(CSV) or os.path.getsize(CSV) == 0
    with open(CSV, "a", newline="") as f:
        writer = csv.writer(f)
        if escribir_header:
            writer.writerow(["Caso","Fecha","Tamano","Digitos","Algoritmo",
                             "Complejidad","Operaciones","Estimado_ms","Real_ms","Ratio"])
        for i in range(len(nombres)):
            ops   = round(factores[i] * (tamano ** exponentes[i]))
            ratio = reales[i] / estimados[i] if estimados[i] > 0 and reales[i] >= 0 else 0
            writer.writerow([caso_label, timestamp, tamano, digitos,
                             nombres[i], f"O(n^{exponentes[i]:.3f})", ops,
                             estimados[i], reales[i] if reales[i] >= 0 else -1,
                             f"{ratio:.2f}"])

    # reporte TXT legible
    sep = "=" * 70
    lin = "-" * 70
    with open(TXT, "a") as f:
        f.write(f"\n{sep}\n")
        f.write(f"{caso_label} | {timestamp}\n")
        f.write(f"Matriz : {tamano} x {tamano}\n")
        f.write(f"Digitos: {digitos} por valor\n")
        f.write(f"{sep}\n\n")
        f.write(f"{'Algoritmo':<28} {'Complejidad':<12} {'Ops':<10} {'Estimado':<12} {'Real':<12} {'Ratio'}\n")
        f.write(f"{lin}\n")
        for i in range(len(nombres)):
            ops     = round(factores[i] * (tamano ** exponentes[i]))
            real_str = format_ms(reales[i])
            ratio   = reales[i] / estimados[i] if estimados[i] > 0 and reales[i] >= 0 else 0
            f.write(f"{nombres[i]:<28} O(n^{exponentes[i]:.3f})  {format_ops(ops):<10} "
                    f"{format_ms(estimados[i]):<12} {real_str:<12} {ratio:.2f}x {analizar(ratio)}\n")
        f.write(f"{lin}\n\n")

    print(f"\nResultados guardados como {caso_label}")
    print(f"  CSV: {os.path.abspath(CSV)}")
    print(f"  TXT: {os.path.abspath(TXT)}")
