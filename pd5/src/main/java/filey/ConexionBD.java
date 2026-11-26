package filey;

import java.sql.*;

final class ConexionBD {
    static Connection abrir() throws SQLException {
        String clave = System.getenv("BD_CLAVE");
        if (clave == null || clave.isBlank()) throw new SQLException("Configura BD_CLAVE en PowerShell.");
        String url = "jdbc:postgresql://ep-sweet-silence-bbztfszx-pooler.c-15.us-east-1.aws.neon.tech/neondb?sslmode=verify-full&sslfactory=org.postgresql.ssl.DefaultJavaSSLFactory&channelBinding=require";
        return DriverManager.getConnection(url, "neondb_owner", clave);
    }
}
