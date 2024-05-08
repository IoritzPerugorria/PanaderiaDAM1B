package Controladores;


import Modelo.ConexionBBDD;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;


import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.panaderiadam1b.Main;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
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

    ArrayList<HBox> productosTienda = new ArrayList<>();
    ArrayList<HBox> productosAlmacen = new ArrayList<>();
    ArrayList< HBox> productosCocina = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<ArrayList<Object>> tabla = this.cargar("producto");
        this.cargarRegular(tabla, "Tienda");
        this.cargarRegular(tabla, "AlmacenProductos");
        tabla = this.cargar("ingrediente");
        this.cargarRegular(tabla, "AlmacenIngredientes");
        this.cargarTienda();
        this.cargarAlmacen();

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

    public void cargarTienda(){
        Tienda.getChildren().clear();
        for (HBox contenedor : productosTienda){
            Tienda.getChildren().add(contenedor);
        }
    }
    public void cargarAlmacen(){
        Almacen.getChildren().clear();
        for(HBox contenedor : productosAlmacen){
            Almacen.getChildren().add(contenedor);
        }
    }
    public void cargarCocina(){
        Cocina.getChildren().clear();
        for(HBox contenedor : productosCocina){
            Cocina.getChildren().add(contenedor);
        }
    }

    /**
     * El metodo se conecta a la BBDD y crea HBox-es en los que se muestran
     * informacion de cada producto. Primero se llama a ConexionBBDD para
     * inicializar la conexion, y luego selecciona t0do de una tabla. Despues
     * imprime la informacion en la poscion que le corresponde.
     */
    public ArrayList<ArrayList<Object>> cargar(String tipo){
        Connection conexion = null;
        Statement script;
        ResultSet rs;

        ArrayList<ArrayList<Object>> tabla = new ArrayList<>();

        try {
            conexion = ConexionBBDD.conectar(conexion);
            script = conexion.createStatement();

            if (tipo.equals("ingrediente")){
                rs = script.executeQuery("SELECT * FROM INGREDIENTES");
            }
            else{
                rs = script.executeQuery("SELECT * FROM PRODUCTOS");
            }

            while (rs.next()) {
                ArrayList<Object> valores = new ArrayList<>();

                String id = rs.getString("ID"); //ID

                String nombre = (rs.getString("NOMBRE")); // Nombre

                double precio = (rs.getDouble("PRECIO")); // Precio

                String stock = (rs.getString("STOCK")); // Stock

                String imagen = (rs.getString("IMAGEN")); //Imagen

                valores.add(id);
                valores.add(nombre);
                valores.add(precio);
                valores.add(stock);
                valores.add(imagen);

                tabla.add(valores);
            }
        }
        catch (SQLException e){
            System.out.println("No se ha podido conectar a la BBDD");
        }
        finally {
            ConexionBBDD.desconectar(conexion);
        }
        return tabla;
    }

    public void cargarRegular(ArrayList<ArrayList<Object>> tabla, String pantalla){
        for (ArrayList<Object> valores : tabla) {

            // cargar Imagen
            Image imagen = new Image(getClass().getResource("/imagenes/" + valores.get(4)).toString());
            ImageView imagenVista = new ImageView(imagen);
            ;
            imagenVista.setFitHeight(100); // Ajustar altura
            imagenVista.setFitWidth(100); // Ajustar anchura

            // Cargar nombre
            Label nombre = new Label((String) valores.get(1));
            // Cargar precio
            double precioNumero = (Double) valores.get(2);
            String precioCadena = String.valueOf(precioNumero);
            Label precio = new Label(precioCadena);
            // Cargar stock
            Label stock = new Label((String) valores.get(3));

            //Añadir precio y stock a contenedor vertical
            VBox precioStock = new VBox(precio, stock);

            Button boton = new Button();
            boton.setId((String) valores.get(1));

            boton.setText("Comprar");

            boton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    comprar(boton.getId());
                }
            });

            HBox contenedor = new HBox(); // Contenedor horizontal (fila de producto)

            // Anadir los items al HBOX
            contenedor.getChildren().add(imagenVista);
            contenedor.getChildren().add(nombre);
            contenedor.getChildren().add(precioStock);
            contenedor.getChildren().add(boton);
            contenedor.setAlignment(Pos.CENTER);

            contenedor.setSpacing(60);

            switch (pantalla){
                case "Tienda":
                    productosTienda.add(contenedor);
                    break;
                case "AlmacenProductos", "AlmacenIngredientes":
                    productosAlmacen.add(contenedor);
                    break;
            }
        }
    }

    public void cargarCocina(ArrayList<ArrayList<Object>> productos){

    }

    public ArrayList<ArrayList<Object>> obtenerIngredientes(String id){
        ArrayList<ArrayList<Object>> tablas = new ArrayList<>();

        Connection conexion = null;
        PreparedStatement ps = null;

        try{
            conexion = ConexionBBDD.conectar(conexion);
            ps = conexion.prepareStatement("SELECT I.IMAGEN, N.CANTIDAD FROM PRODUCTOS AS P INNER JOIN NECESITA AS N ON N.PR_ID = P.ID INNER JOIN INGREDIENTES AS I ON N.ING_ID = I.ID WHERE P.ID = ?");
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                ArrayList<Object> valores = new ArrayList<>();
                valores.add(rs.getString("I.IMAGEN"));
                valores.add(rs.getString("N.CANTIDAD"));
                tablas.add(valores);
            }
        }
        catch (SQLException e){
            System.out.println("Error al consultar los ingredientes");
        }
        finally {
            conexion = ConexionBBDD.desconectar(conexion);
        }
        return tablas;
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

            }
        }
        catch (SQLException e){
            System.out.println("Error al comprar");
        }
        finally {
            conexion = ConexionBBDD.desconectar(conexion);
        }
    }




    @FXML
    public void anadir(ActionEvent actionEvent){
        try{

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("anadirNuevo-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            stage.setTitle("Panaderia");
            stage.setScene(scene);


            stage.show();
        }
        catch (IOException e){
            System.out.println("ERROR");
        }
    }
}
