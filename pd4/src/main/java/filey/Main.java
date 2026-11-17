package filey;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        try (Connection conexion = ConexionBD.abrir()) {
            System.out.println("Conexión establecida con " + conexion.getCatalog());
            new Controlador(new Consultas(conexion), new VistaConsola()).listar();
        } catch (SQLException e) {
            System.err.println("Error de base de datos: " + e.getMessage());
            System.exit(1);
        }
    }
}
