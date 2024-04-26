package Controladores;


import BBDD.ConexionBBDD;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.panaderiadam1b.Main;


import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class ControladorVP implements Initializable {


    @FXML
    VBox contenedorV = new VBox();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {



    }

    public void cargarProductos(){
        Connection conexion = null;

        conexion = ConexionBBDD.conectar(conexion);

        for (int rep = 0; rep < 60; rep ++){

            HBox contenedor = new HBox();
            ImageView imagen = new ImageView(new Image(getClass().getResource("/imagenes/a.jpg").toString()));
            contenedor.getChildren().add(imagen);

            contenedorV.getChildren().add(contenedor);
        }
    }
}