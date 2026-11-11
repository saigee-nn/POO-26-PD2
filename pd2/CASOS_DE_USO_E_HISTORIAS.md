# FILEY · Casos de uso e historias de usuario

El administrador opera el sistema de la feria. La editorial solicita el stand, realiza los abonos y recibe los avisos. En la demostración de consola, `Main` reproduce las operaciones de ambos actores mediante llamadas a las clases Java.

## Casos de uso

### CU-01 · Configurar el calendario de la feria
**Actor:** Administrador. **Requisitos:** RF-10 y RF-11.

1. Al iniciar el sistema, el administrador indica las fechas límite de reserva, descuento y liquidación.
2. Define cuántos días tiene una editorial para cubrir el mínimo y con cuánta anticipación se emiten avisos.
3. El sistema valida el calendario y lo utiliza al crear reservas y comprobar fechas.

**Excepciones:** Se rechazan plazos de pago de cero días o negativos, anticipaciones negativas y fechas de reserva o descuento posteriores a la liquidación.

### CU-02 · Dar de alta un stand
**Actor:** Administrador. **Requisitos:** RF-01 a RF-03.

1. El administrador indica el identificador, el ancho, el largo y el precio por metro cuadrado.
2. El sistema calcula la superficie multiplicando las dimensiones y obtiene el precio total aplicando la tarifa a esa superficie.
3. El stand queda DISPONIBLE. Por ejemplo, uno de 2 × 5 m a $100 por m² cuesta $1,000.

**Excepciones:** Se rechaza un identificador duplicado o no positivo, dimensiones o tarifa no positivas, o un precio total inferior a un centavo después del redondeo.

### CU-03 · Registrar una editorial
**Actor:** Administrador. **Requisitos:** RF-04 y RF-15.

1. El administrador proporciona el identificador y nombre de la editorial.
2. El sistema registra a la editorial y la suscribe a los avisos de sus reservas.

**Excepciones:** Si el identificador ya existe, es menor o igual a cero o el nombre está vacío, el alta se rechaza.

### CU-04 · Consultar disponibilidad y reservar
**Actor:** Editorial, con registro de la operación por el administrador. **Requisitos:** RF-03, RF-05, RF-06 y RF-11.

1. La editorial solicita los stands disponibles y elige uno.
2. El administrador indica la editorial, el stand y la fecha de solicitud.
3. El sistema crea la reserva y asocia su identificador con la editorial y el stand. El stand pasa a RESERVADO.
4. El vencimiento es la primera de dos fechas: la fecha de creación más el plazo permitido, o el límite global de reserva.

**Excepciones:** La operación se rechaza si falta alguno de los registros, si el stand dejó de estar DISPONIBLE o si pasó la fecha límite. La fecha límite todavía admite reservas.

### CU-05 · Registrar un pago y consultar el saldo
**Actores:** Editorial y administrador. **Requisitos:** RF-06 a RF-09 y RF-13.

1. La editorial realiza un abono y el administrador registra la fecha, el importe y el identificador de la reserva.
2. El sistema incorpora el pago al historial y calcula el total abonado, el saldo pendiente y el porcentaje cubierto.
3. Si el acumulado alcanza el 15% del precio total, el stand pasa a OCUPADO. La decisión utiliza importes exactos.
4. El administrador puede consultar la reserva y registrar los abonos restantes hasta la fecha de liquidación inclusive.

**Alternativas y excepciones:** Por debajo del 15% el stand sigue RESERVADO. Se rechazan importes no positivos, fracciones de centavo, sobrepagos y pagos fuera del plazo aplicable. Tampoco se admite una fecha anterior a la creación o al último pago.

### CU-06 · Bloquear y desbloquear un stand
**Actor:** Administrador. **Requisitos:** RF-03 y RF-14.

1. El administrador selecciona un stand DISPONIBLE y solicita su bloqueo.
2. El stand pasa a BLOQUEADO y deja de aparecer en la consulta de disponibles.
3. Al terminar la operación administrativa, el administrador lo desbloquea. El stand vuelve a DISPONIBLE.

**Excepciones:** Solo se puede bloquear un stand DISPONIBLE y solo se puede desbloquear uno BLOQUEADO. Un stand bloqueado rechaza solicitudes de reserva.

### CU-07 · Comprobar vencimientos y avisar a las editoriales
**Actor principal:** Administrador. **Destinataria:** Editorial titular. **Requisitos:** RF-12, RF-13 y RF-15 a RF-17.

1. El administrador ejecuta la comprobación indicando una fecha.
2. Si pasó el vencimiento de una reserva cuyo acumulado sigue por debajo del 15%, el sistema la marca como vencida, libera el stand y conserva su historial.
3. Para las reservas vigentes con saldo, el sistema comprueba si la fecha cae dentro del intervalo de aviso, incluido el propio día límite.
4. La editorial recibe en consola los avisos que correspondan. El aviso del mínimo de reserva aplica mientras falte cubrir el 15%. Los de pronto pago y liquidación dependen de sus respectivas fechas.

**Alternativas:** Repetir la comprobación evita duplicar un aviso ya emitido para la misma reserva, tipo y vencimiento. Las reservas liquidadas o vencidas dejan de generar avisos. Si el mínimo ya se cubrió, superar la fecha de liquidación conserva el stand OCUPADO y cierra el plazo de pago.

## Historias de usuario

| ID | Historia | Criterio de aceptación | Caso |
|---|---|---|---|
| HU-01 | Como administrador, quiero establecer los plazos de la feria para aplicar el calendario de esta edición. | Con un plazo de tres días y un límite global el día 6, una reserva creada el día 5 vence el día 6. | CU-01, CU-04 |
| HU-02 | Como administrador, quiero calcular el precio de cada stand a partir de sus dimensiones para informar a las editoriales. | Un stand nuevo de 2 × 5 m a $100 por m² aparece disponible, tiene 10 m² y cuesta $1,000. | CU-02 |
| HU-03 | Como administrador, quiero registrar a cada editorial para identificar a la titular de una reserva. | Registrar dos editoriales con el mismo identificador rechaza la segunda alta y conserva la primera. | CU-03 |
| HU-04 | Como editorial, quiero elegir un stand libre para apartarlo a mi nombre. | Tras reservarlo, la consulta muestra mi editorial como titular y el stand deja de aparecer entre los disponibles. | CU-04 |
| HU-05 | Como editorial, quiero abonar en varios pagos y consultar cuánto debo para saber cuándo aseguro mi stand. | Para un total de $1,000, un pago de $100 deja el stand RESERVADO. Otro de $50 lo cambia a OCUPADO y deja $850 pendientes. | CU-05 |
| HU-06 | Como administrador, quiero apartar temporalmente un stand para impedir reservas durante una operación administrativa. | Mientras esté BLOQUEADO se rechaza la reserva. Al desbloquearlo, vuelve a aparecer entre los disponibles. | CU-06 |
| HU-07 | Como editorial, quiero recibir avisos de mis vencimientos para decidir cuándo realizar el siguiente pago. | Dentro del plazo de aviso, la editorial titular recibe el mensaje de su reserva. Repetir la comprobación ese día no vuelve a emitirlo. | CU-07 |
| HU-08 | Como administrador, quiero recuperar los stands cuyas reservas vencieron para ofrecerlos a otras editoriales. | Para un stand de $1,000 con $100 abonados, comprobar el día posterior al vencimiento libera el stand y conserva los $100 en el historial de la reserva vencida. | CU-07 |
