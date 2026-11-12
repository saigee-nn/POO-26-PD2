# PD3: Calculadora MVC

Calculadora de escritorio hecha con Java Swing a partir del ejemplo visto en clase. Permite acumular una suma o una multiplicación con el valor escrito en `Input`. El resultado aparece en `Total` y `Clear` reinicia el acumulador a `1`, como en la versión de partida.

## Clases

- `CalcMVC`: crea el modelo, la vista y el controlador, y muestra la ventana.
- `CalcModel`: guarda el total como `BigDecimal` y realiza la suma, la multiplicación y el reinicio.
- `CalcView`: presenta los campos y botones; comunica los clics al controlador mediante listeners.
- `CalcController`: atiende los botones, solicita la operación al modelo y actualiza la vista. Si el texto no es un número válido, muestra un mensaje de error.

## Cambios de esta actividad

Se añadió el botón `Add`, el método `addBy` del modelo y su listener en el controlador. La suma se aplica al total acumulado, igual que la multiplicación. Se conserva `BigDecimal` para admitir enteros grandes y decimales, así como el reinicio a `1` de la calculadora original.

## Compilar y ejecutar

Desde esta carpeta, con un JDK instalado:

```powershell
javac CalcMVC.java CalcModel.java CalcView.java CalcController.java
java CalcMVC
```

También se puede abrir la carpeta en Visual Studio Code y ejecutar `CalcMVC.java` con la extensión de Java instalada. Los archivos `.class` se generan al compilar y no forman parte de la entrega.

## Diagrama de clases

La versión SVG está en `diagramas/diagrama-clases.svg`. El archivo editable `diagramas/diagrama-clases.puml` contiene el mismo diagrama.

## Video

https://youtu.be/szFk8_SVzvY
