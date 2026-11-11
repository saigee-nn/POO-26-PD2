package filey.datos;

import filey.notificaciones.Observador;

public final class Editorial implements Observador {
    private final int id;
    private final String nombre;

    public Editorial(int id, String nombre) {
        if (id <= 0 || nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("La editorial requiere ID positivo y nombre.");
        }
        this.id = id;
        this.nombre = nombre.strip();
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public void actualizar(String mensaje) {
        System.out.println("[Aviso a " + nombre + "] " + mensaje);
    }
}
