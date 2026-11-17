-- Recupera únicamente la reserva original que se entregó sin historial.
WITH original AS (
    UPDATE pd4.reserva r
       SET vencimiento = r.fecha + 3, limite_liquidacion = r.fecha + 10
      FROM pd4.stand s, pd4.editorial e
     WHERE r.id = 1 AND r.stand_id = 1 AND r.editorial_id = 1
       AND r.fecha = DATE '2026-11-12'
       AND r.vencimiento IS NULL AND r.limite_liquidacion IS NULL
       AND s.id = r.stand_id AND s.ancho = 2 AND s.largo = 5
       AND s.precio_por_metro = 100 AND s.estado = 'RESERVADO'
       AND e.id = r.editorial_id AND e.nombre = 'Lecturas del Mayab'
       AND NOT EXISTS (SELECT 1 FROM pd4.pago p WHERE p.reserva_id = r.id)
       AND NOT EXISTS (SELECT 1 FROM pd4.reserva otra WHERE otra.stand_id = s.id AND otra.id <> r.id)
    RETURNING r.id, r.stand_id, r.fecha
), pagos AS (
    INSERT INTO pd4.pago (reserva_id, importe, fecha)
    SELECT o.id, movimiento.importe, o.fecha + movimiento.dias
      FROM original o
      CROSS JOIN (VALUES (0, 100.00), (2, 50.00), (10, 850.00)) AS movimiento(dias, importe)
     ORDER BY movimiento.dias
    RETURNING reserva_id
)
UPDATE pd4.stand SET estado = 'OCUPADO'
 WHERE id IN (SELECT o.stand_id FROM original o JOIN pagos p ON p.reserva_id = o.id);
