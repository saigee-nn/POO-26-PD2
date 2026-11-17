BEGIN;
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
INSERT INTO pd4.stand VALUES (1,2,5,100,'RESERVADO'), (2,3,4,100,'DISPONIBLE') ON CONFLICT (id) DO NOTHING;
INSERT INTO pd4.editorial VALUES (1,'Lecturas del Mayab'), (2,'Letras del Sur') ON CONFLICT (id) DO NOTHING;
INSERT INTO pd4.reserva VALUES (1,1,1,DATE '2026-11-12') ON CONFLICT (id) DO NOTHING;
COMMIT;
