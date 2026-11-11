## Requisitos funcionales

- **RF-01.** El sistema deberá registrar stands con identificador numérico positivo único, ancho y largo positivos en metros y precio positivo por metro cuadrado.
- **RF-02.** El sistema deberá calcular la superficie como ancho por largo y el precio total como superficie por precio por metro cuadrado, redondeado a dos decimales.
- **RF-03.** El sistema deberá consultar los stands disponibles y gestionar los estados DISPONIBLE, RESERVADO, OCUPADO y BLOQUEADO, rechazando transiciones no permitidas.
- **RF-04.** El sistema deberá registrar editoriales con identificador numérico positivo único y nombre no vacío.
- **RF-05.** El sistema deberá crear reservas con identificador único para editoriales y stands registrados, únicamente cuando el stand esté DISPONIBLE y no se haya superado la fecha límite de reserva; la creación lo cambiará a RESERVADO.
- **RF-06.** El sistema deberá conservar y consultar las reservas existentes, incluyendo su editorial, stand, fechas, precio total y pagos.
- **RF-07.** El sistema deberá registrar pagos positivos con precisión de centavos y fecha, rechazando sobrepagos, pagos fuera de plazo, pagos anteriores a la creación de la reserva y pagos anteriores al último pago registrado.
- **RF-08.** El sistema deberá consultar el importe acumulado, el saldo pendiente y el porcentaje abonado de una reserva.
- **RF-09.** El sistema deberá cambiar automáticamente un stand RESERVADO a OCUPADO cuando el importe acumulado alcance al menos el 15% del precio total, mediante una comparación exacta independiente del redondeo del porcentaje mostrado.
- **RF-10.** El sistema deberá permitir configurar, al crear el sistema, las fechas límite de descuento, liquidación y reserva, los días permitidos para alcanzar el pago mínimo de una reserva y los días de anticipación de los avisos. La fecha de descuento y la de reserva no deberán superar la de liquidación.
- **RF-11.** El vencimiento de cada reserva deberá ser la fecha menor entre su creación más los días permitidos y la fecha límite global de reserva. Las fechas límite serán inclusivas.
- **RF-12.** Al comprobar las fechas, el sistema deberá marcar como vencidas las reservas que hayan superado su plazo sin alcanzar el 15%, liberar sus stands y conservar sus pagos e historial. Las reservas vencidas no admitirán pagos posteriores ni se reactivarán al reutilizar su stand.
- **RF-13.** Las reservas que alcancen el 15% deberán conservar el stand OCUPADO y admitir pagos hasta cubrir el total, como máximo en la fecha de liquidación. Superar dicha fecha no liberará automáticamente un stand OCUPADO.
- **RF-14.** El administrador deberá poder bloquear únicamente stands DISPONIBLES y devolver stands BLOQUEADOS a DISPONIBLE.
- **RF-15.** El sistema deberá suscribir automáticamente las editoriales registradas a las notificaciones y permitir agregar y retirar observadores por editorial.
- **RF-16.** El sistema deberá emitir avisos mediante Observer dentro del intervalo inclusivo entre la fecha límite menos los días de anticipación y la propia fecha límite: vencimiento de reserva mientras no se alcance el 15%, vencimiento del descuento y vencimiento de liquidación mientras exista saldo. No se emitirán avisos para reservas vencidas o liquidadas.
- **RF-17.** El sistema deberá dirigir cada aviso a los observadores de la editorial correspondiente, mostrarlo en consola y evitar repetir el mismo tipo de aviso para una reserva y fecha límite durante una ejecución.
- **RF-18.** El sistema deberá ofrecer una demostración en consola de altas, consultas, reservas, pagos inferiores e iguales al 15%, bloqueo, desbloqueo, vencimientos, notificaciones y liquidación.

## Requisitos no funcionales

- **RNF-01.** El proyecto deberá compilar y ejecutarse con JDK 17, sin frameworks ni dependencias externas.
- **RNF-02.** El código deberá organizarse en los paquetes `filey.datos`, `filey.negocio` y `filey.notificaciones`, con un punto de entrada `filey.Main`.
- **RNF-03.** Los atributos deberán ser privados y las colecciones devueltas por consultas deberán impedir modificaciones de su estructura desde el exterior.
- **RNF-04.** Los importes deberán representarse mediante `BigDecimal` y las fechas mediante `LocalDate`, sin fechas de calendario fijas en las entidades ni en los servicios.
- **RNF-05.** El sistema deberá validar identificadores, valores obligatorios, dimensiones, importes, fechas y estados, rechazando las operaciones inválidas mediante excepciones sin registrar pagos o reservas incompletos.
- **RNF-06.** El proyecto deberá utilizar nombres descriptivos, responsabilidades separadas y comentarios útiles en español para facilitar su mantenimiento académico.
- **RNF-07.** La aplicación deberá ejecutarse de forma secuencial y conservar sus datos únicamente en memoria, sin requerir servicios de red ni persistencia.
- **RNF-08.** La comprobación de fechas deberá recibir una fecha explícita para permitir demostraciones y verificaciones reproducibles sin esperar al transcurso del tiempo real.
