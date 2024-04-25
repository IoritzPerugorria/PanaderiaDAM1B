package org.example.panaderiadam1b;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        this.pruebaConexion();
    }

    public static void main(String[] args) {
        launch();
    }

    public void pruebaConexion(){
        String url = "jdbc:mysql://10.168.58.3:3306/BDPANADERIA";
        String username = "root";
        String password = "Dam1bSql01";

        Connection connection = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            connection = DriverManager.getConnection(url, username, password);
            st = connection.createStatement();
            rs = st.executeQuery("SELECT * FROM PRODUCTOS");

            System.out.println("PRODUCTOS:\n");
            while (rs.next()) {
                System.out.print(rs.getString("ID"));
                System.out.print("\t\t");
                System.out.print(rs.getString("NOMBRE"));
                System.out.print("\t\t");
                System.out.print(rs.getString("PRECIO"));
                System.out.print("\t\t");
                System.out.println(rs.getString("STOCK"));

            }
            System.out.print("\n\n");

            rs = st.executeQuery("SELECT * FROM INGREDIENTES");

            System.out.println("INGREDIENTES:\n");
            while (rs.next()) {
                System.out.print(rs.getString("ID"));
                System.out.print("\t\t");
                System.out.print(rs.getString("NOMBRE"));
                System.out.print("\t\t");
                System.out.print(rs.getString("PRECIO"));
                System.out.print("\t\t");
                System.out.println(rs.getString("STOCK"));
            }

        } catch (SQLException e) {
            throw new IllegalStateException("aLGO HA IDO MAL XD", e);
        } finally {
            try{
                if (connection != null && st != null && rs != null){
                    connection.close();
                    st.close();
                    rs.close();
                }

            } catch (SQLException e){
                throw new IllegalStateException("error xd", e);
            }

        }
    }
}