package Controladores;


import BBDD.ConexionBBDD;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;



import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ControladorVP implements Initializable {


    @FXML
    VBox Tienda = new VBox(); //Contendero vertical de la Tienda
    @FXML
    VBox Inventario = new VBox(); //Contendero vertical del Inventario
    @FXML
    VBox Cocina = new VBox(); //Contendero vertical de la Cocina

    ArrayList<String> idBotones = new ArrayList<>(); // Coleccion de los ids de todos los botones de "comprar"


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.cargarProductos();
    }

    /**
     * El metodo se conecta a la BBDD y crea HBox-es en los que se muestran
     * informacion de cada producto. Primero se llama a ConexionBBDD para
     * inicializar la conexion, y luego selecciona t0do de una tabla. Despues
     * imprime la informacion en la poscion que le corresponde.
     */
    public void cargarProductos(){
        Connection conexion = null;
        Statement script;
        ResultSet rs;

        try {
            conexion = ConexionBBDD.conectar(conexion);
            script = conexion.createStatement();
            rs = script.executeQuery("SELECT * FROM PRODUCTOS");
            int contador = 0;

            while (rs.next()){

                Label label = new Label(rs.getString("NOMBRE")); // Nombre
                label.setFont(new Font(30));

                Label label2 = new Label("Precio: " + rs.getString("PRECIO")); // Precio
                label2.setFont(new Font(30));
                Label label3 = new Label("Stock: " + rs.getString("STOCK")); // Stock
                label3.setFont(new Font(30));

                VBox contenedorInfo = new VBox(label2, label3); //Contenedor Vertical para precio y stock
                contenedorInfo.setAlignment(Pos.CENTER); // Centrar verticalmente

                Button boton = new Button();
                boton.setId(rs.getString("NOMBRE"));
                idBotones.add(rs.getString("NOMBRE"));
                boton.setText("Comprar");


                // cargar imagen
                Image imagen = new Image(getClass().getResource("/imagenes/" + rs.getString("IMAGEN")).toString());
                ImageView imagenVista = new ImageView(imagen);

                imagenVista.setFitHeight(100); // Ajustar altura
                imagenVista.setFitWidth(100); // Ajustar anchura

                HBox contenedor = new HBox(); // Contenedor horizontal (fila de producto)

                // Anadir los items al HBOX
                contenedor.getChildren().add(imagenVista);
                contenedor.getChildren().add(label);
                contenedor.getChildren().add(contenedorInfo);
                contenedor.getChildren().add(boton);
                contenedor.setAlignment(Pos.CENTER);

                contenedor.setSpacing(60);


                Tienda.getChildren().add(contenedor);
                contador ++;
            }
        }
        catch (SQLException e){
            // En caso de haber un error, cada pestaña muestra un mensaje de error
            Tienda.getChildren().add(new Label("ERROR: No se han podido cargar los datos"));
            Inventario.getChildren().add(new Label("ERROR: No se han podido cargar los datos"));
            Cocina.getChildren().add(new Label("ERROR: No se han podido cargar los datos"));
        }
        finally {
            ConexionBBDD.desconectar(conexion);
        }
    }
}