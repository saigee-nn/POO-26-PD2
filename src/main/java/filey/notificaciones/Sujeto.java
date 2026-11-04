package filey.notificaciones;

public interface Sujeto {
    void suscribir(int idEditorial, Observador observador);
    void retirar(int idEditorial, Observador observador);
    void notificar(int idEditorial, String mensaje);
}
