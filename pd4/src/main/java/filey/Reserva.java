package filey;

import java.time.LocalDate;

record Reserva(int id, int standId, int editorialId, LocalDate fecha) {}
