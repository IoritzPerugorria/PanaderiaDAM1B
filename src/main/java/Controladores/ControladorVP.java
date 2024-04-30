package Controladores;


import BBDD.ConexionBBDD;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ControladorVP implements Initializable {


    @FXML
    VBox Tienda = new VBox(); //Contendero vertical de la Tienda
    @FXML
    VBox Almacen = new VBox(); //Contendero vertical del Inventario
    @FXML
    VBox Cocina = new VBox(); //Contendero vertical de la Cocina
    @FXML
    ScrollPane scrollTienda = new ScrollPane();
    @FXML
    ScrollPane scrollAlmacen = new ScrollPane();
    @FXML
    ScrollPane scrollCocina = new ScrollPane();
    @FXML
    ArrayList<Button> idBotones = new ArrayList<>(); // Coleccion de los ids de todos los botones de "comprar"




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.cargarProductos();

        this.ajustarAnclas();
    }


    /**
     * Este metodo ajusta las anclas de los tabs para que los scrollpane
     * de dentro se ajusten al tamaño del tab
     */
    public void ajustarAnclas(){
        AnchorPane.setTopAnchor(scrollTienda, 0.0);
        AnchorPane.setBottomAnchor(scrollTienda, 0.0);
        AnchorPane.setLeftAnchor(scrollTienda, 0.0);
        AnchorPane.setRightAnchor(scrollTienda, 0.0);

        AnchorPane.setTopAnchor(scrollAlmacen, 0.0);
        AnchorPane.setBottomAnchor(scrollAlmacen, 0.0);
        AnchorPane.setLeftAnchor(scrollAlmacen, 0.0);
        AnchorPane.setRightAnchor(scrollAlmacen, 0.0);

        AnchorPane.setTopAnchor(scrollCocina, 0.0);
        AnchorPane.setBottomAnchor(scrollCocina, 0.0);
        AnchorPane.setLeftAnchor(scrollCocina, 0.0);
        AnchorPane.setRightAnchor(scrollCocina, 0.0);
    }

    /**
     * El metodo se conecta a la BBDD y crea HBox-es en los que se muestran
     * informacion de cada producto. Primero se llama a ConexionBBDD para
     * inicializar la conexion, y luego selecciona t0do de una tabla. Despues
     * imprime la informacion en la poscion que le corresponde.
     */
    public void cargarProductos(){
        Tienda.getChildren().clear();
        Almacen.getChildren().clear();
        Cocina.getChildren().clear();

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

                boton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        comprar(boton.getId());
                    }
                });

                idBotones.add(boton); // se añade a la coleccion de todos los botones
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
            Almacen.getChildren().add(new Label("ERROR: No se han podido cargar los datos"));
            Cocina.getChildren().add(new Label("ERROR: No se han podido cargar los datos"));
        }
        finally {
            ConexionBBDD.desconectar(conexion);
        }
    }

    public void comprar(String nombre){
        Connection conexion = null;
        PreparedStatement ps1;
        PreparedStatement ps2 = null;
        ResultSet rs;

        try {
            conexion = ConexionBBDD.conectar(conexion);


             ps1 = conexion.prepareStatement("SELECT STOCK FROM PRODUCTOS WHERE NOMBRE = ?");
            ps1.setString(1, nombre);

            rs = ps1.executeQuery();
            while (rs.next()) {


                System.out.println(rs.getInt("STOCK"));


                if (!(rs.getInt("STOCK") <= 0)) {
                     ps2 = conexion.prepareStatement("UPDATE PRODUCTOS SET STOCK = STOCK - 1 WHERE NOMBRE = ?");
                    ps2.setString(1, nombre);

                    
                }
                else{
                    ps2 = conexion.prepareStatement("UPDATE PRODUCTOS SET STOCK = 0 WHERE NOMBRE = ?");
                    ps2.setString(1, nombre);
                    
                }
                ps2.executeUpdate();

                this.cargarProductos();

            }
        }
        catch (SQLException e){

        }
        finally {
            conexion = ConexionBBDD.desconectar(conexion);
        }
    }
}