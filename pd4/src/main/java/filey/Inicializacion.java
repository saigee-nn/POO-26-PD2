package filey;

import java.io.IOException;
import java.nio.file.*;

final class Inicializacion {
    static String leer(String archivo) throws IOException {
        return Files.readString(Path.of("database", archivo));
    }
}
