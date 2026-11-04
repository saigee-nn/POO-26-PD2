# FILEY — Programación Orientada a Objetos

Backend académico en Java 17 para administrar stands, editoriales, reservas y pagos de la Feria Internacional de la Lectura Yucatán. Incluye fechas configurables y notificaciones en consola mediante Observer. Los datos permanecen en memoria durante la ejecución; no requiere interfaz gráfica, base de datos ni dependencias externas.

## Compilar y ejecutar

Instalar un **JDK 17** y abrir una terminal en la carpeta `FILEY-POO-Java` extraída del ZIP.

En Linux/macOS (Bash):

```bash
mkdir -p out
find src/main/java -name '*.java' > fuentes.txt
javac --release 17 -encoding UTF-8 -d out @fuentes.txt
java -cp out filey.Main
```

En Windows (PowerShell):

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$fuentes = (Get-ChildItem src/main/java -Recurse -Filter *.java).FullName
javac --release 17 -encoding UTF-8 -d out $fuentes
java -cp out filey.Main
```

La demostración muestra pagos del 10% y 15%, bloqueo y desbloqueo, vencimiento de una reserva, los tres avisos y la liquidación. Para probar otros plazos, cambiar los argumentos de `Configuracion` en `Main` y volver a compilar.
