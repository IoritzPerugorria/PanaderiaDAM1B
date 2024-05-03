package BBDD;

import java.sql.*;

public class ConexionBBDD {

    private static String url = "jdbc:mysql://10.168.58.3:3306/BDPANADERIA";
    private static String localUrl = "jdbc:mysql://localhost/BDPANADERIA";
    private static String username = "root";
    private static String password = "Dam1bSql01";
    private static String localPassword = "root";



    public static Connection conectar(Connection connection){
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Conexion establecida");


        } catch (SQLException e) {
            throw new IllegalStateException("No se ha podido establecer conexion", e);
        }

        return connection;
    }


    public static Connection desconectar(Connection connection){
        try{
            if (connection != null){
                connection.close();
            }

        }catch (SQLException e){
            throw new IllegalStateException("No se ha podido cerrar la conexion", e);
        }
        return connection;
    }
}
