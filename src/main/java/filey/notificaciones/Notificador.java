package filey.notificaciones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Sujeto concreto; cada editorial recibe únicamente sus propios avisos. */
public final class Notificador implements Sujeto {
    private final Map<Integer, List<Observador>> observadores = new HashMap<>();

    @Override
    public void suscribir(int idEditorial, Observador observador) {
        if (idEditorial <= 0) { throw new IllegalArgumentException("El ID debe ser positivo."); }
        Objects.requireNonNull(observador);
        List<Observador> lista = observadores.computeIfAbsent(idEditorial, id -> new ArrayList<>());
        if (!lista.contains(observador)) { lista.add(observador); }
    }

    @Override
    public void retirar(int idEditorial, Observador observador) {
        List<Observador> lista = observadores.get(idEditorial);
        if (lista != null) {
            lista.remove(observador);
            if (lista.isEmpty()) { observadores.remove(idEditorial); }
        }
    }

    @Override
    public void notificar(int idEditorial, String mensaje) {
        Objects.requireNonNull(mensaje);
        for (Observador observador : List.copyOf(observadores.getOrDefault(idEditorial, List.of()))) {
            observador.actualizar(mensaje);
        }
    }
}
