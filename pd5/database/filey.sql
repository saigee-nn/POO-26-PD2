CREATE SCHEMA IF NOT EXISTS pd4;
CREATE TABLE IF NOT EXISTS pd4.stand (
    id integer PRIMARY KEY, ancho numeric NOT NULL CHECK (ancho > 0),
    largo numeric NOT NULL CHECK (largo > 0), precio_por_metro numeric(10,2) NOT NULL CHECK (precio_por_metro > 0),
    estado text NOT NULL
);
CREATE TABLE IF NOT EXISTS pd4.editorial (id integer PRIMARY KEY, nombre text NOT NULL);
CREATE TABLE IF NOT EXISTS pd4.reserva (
    id integer PRIMARY KEY, stand_id integer NOT NULL REFERENCES pd4.stand(id),
    editorial_id integer NOT NULL REFERENCES pd4.editorial(id), fecha date NOT NULL
);
ALTER TABLE pd4.reserva ADD COLUMN IF NOT EXISTS vencimiento date;
ALTER TABLE pd4.reserva ADD COLUMN IF NOT EXISTS limite_liquidacion date;
CREATE TABLE IF NOT EXISTS pd4.pago (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reserva_id integer NOT NULL REFERENCES pd4.reserva(id),
    importe numeric(12,2) NOT NULL CHECK (importe > 0),
    fecha date NOT NULL
);
