package filey;

import java.util.List;

final class VistaConsola {
    void mostrar(String titulo, List<?> filas) {
        System.out.println("\n" + titulo);
        filas.forEach(System.out::println);
    }
}
