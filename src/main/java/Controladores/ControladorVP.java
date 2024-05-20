package Controladores;


import Enumerados.Pestanas;
import BBDD.ConexionBBDD;
import Modulo.Cartera;
import Modulo.Rol;
import Modulo.Usuario;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.util.*;

// GRACIAS NIDAE POR algo que no me acuerdo la verdad xd

public class ControladorVP implements Initializable {

    @FXML
    private Tab tiendaTab;
    @FXML
    private Tab almacenTab;
    @FXML
    private Tab cocinaTab;

    @FXML
    private VBox Tienda = new VBox(); //Contendero vertical de la Tienda
    @FXML
    private VBox Almacen = new VBox(); //Contendero vertical del Inventario
    @FXML
    private VBox Cocina = new VBox(); //Contendero vertical de la Cocina
    @FXML
    private ScrollPane scrollTienda = new ScrollPane();
    @FXML
    private ScrollPane scrollAlmacen = new ScrollPane();
    @FXML
    private ScrollPane scrollCocina = new ScrollPane();

    private final ArrayList<HBox> productosTienda = new ArrayList<>();
    private final ArrayList<HBox> productosAlmacen = new ArrayList<>();
    private final ArrayList<HBox> productosCocina = new ArrayList<>();

    private Connection conexion; // La misma conexion se usa en tod0 el controlador para optimizar las cargas

    private Rol rol;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = ConexionBBDD.conectar(conexion); // Abrir la conexion
    }

    public void setRol(Rol rol){
        this.rol = rol;
        this.cargar();
    }

    public void cargar() {
        System.out.println(rol.toString());
        productosTienda.clear();
        productosAlmacen.clear();
        productosCocina.clear();

        ArrayList<ArrayList<Object>> productos;
        ArrayList<ArrayList<Object>> ingredientes;

        switch (rol){
            case CLIENTE:
                this.tiendaTab.setDisable(false);

                productos = this.cargarDatos("producto");

                for (ArrayList<Object> producto : productos) {
                    this.anadirProductosOIngredientes(producto, Pestanas.TIENDA);
                }

                this.cargarTienda();

                break;
            case PANADERO:
                this.cocinaTab.setDisable(false);

                productos = this.cargarDatos("producto");

                for (ArrayList<Object> producto : productos) {
                    this.anadirProductosCocina(producto);
                }

                this.cargarCocina();

                break;
            case ALMACENERO:
                this.almacenTab.setDisable(false);

                productos = this.cargarDatos("producto");
                for (ArrayList<Object> producto : productos) {
                    this.anadirProductosOIngredientes(producto, Pestanas.ALMACENPRODUCTOS);
                }

                ingredientes = this.cargarDatos("ingrediente");
                for (ArrayList<Object> ingrediente : ingredientes) {
                    this.anadirProductosOIngredientes(ingrediente, Pestanas.ALMACENINGREDIENTES);
                }

                this.cargarAlmacen();

                break;
            case ADMINISTRADOR:
                this.tiendaTab.setDisable(false);
                this.almacenTab.setDisable(false);
                this.cocinaTab.setDisable(false);

                productos = this.cargarDatos("producto");
                for (ArrayList<Object> producto : productos) {
                    this.anadirProductosOIngredientes(producto, Pestanas.TIENDA);
                    this.anadirProductosCocina(producto);
                    this.anadirProductosOIngredientes(producto, Pestanas.ALMACENPRODUCTOS);
                }

                ingredientes = this.cargarDatos("ingrediente");
                for (ArrayList<Object> ingrediente : ingredientes) {
                    this.anadirProductosOIngredientes(ingrediente, Pestanas.ALMACENINGREDIENTES);
                }

                this.cargarTienda();
                this.cargarAlmacen();
                this.cargarCocina();

                break;
        }

        this.ajustarAnclas(); //Ajustar las posiciones del scrollpane

    }

    public void cargarTienda() {
        Tienda.getChildren().clear();

        for (HBox contenedor : productosTienda) {
            Tienda.getChildren().add(contenedor);
        }

    }

    public void cargarAlmacen() {
        Almacen.getChildren().clear();

        for (HBox contenedor : productosAlmacen) {
            Almacen.getChildren().add(contenedor);
        }
    }

    public void cargarCocina() {
        Cocina.getChildren().clear();

        for (HBox contenedor : productosCocina) {
            Cocina.getChildren().add(contenedor);
        }
    }

    /**
     * El metodo se conecta a la BBDD y extrae toda la informacion sobre
     * ingredientes o productos, dependiendo el parametro de entrada.
     */
    public ArrayList<ArrayList<Object>> cargarDatos(String tipo) {
        Statement script;
        ResultSet rs;

        ArrayList<ArrayList<Object>> tabla = new ArrayList<>();

        try {
            script = conexion.createStatement();

            if (tipo.equals("ingrediente")) {
                rs = script.executeQuery("SELECT * FROM INGREDIENTES");
            } else {
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
        } catch (SQLException e) {
            System.out.println("No se ha podido conectar a la BBDD");
        }
        return tabla;
    }

    /**
     * Para cargar tienda y almacen
     */
    public void anadirProductosOIngredientes(ArrayList<Object> producto, Pestanas pestana) {

        if (Integer.parseInt((String) producto.get(3)) != 0) {
            ImageView imagenVista = this.cargarImagen((String) producto.get(4)); // Cargar imagen

            Label nombre = new Label((String) producto.get(1)); // Cargar nombre
            nombre.setMinWidth(100);


            double precioNumero = (Double) producto.get(2); // Cargar precio

            String precioCadena = String.valueOf(precioNumero); // Pasar de double a String
            Label precio = new Label("Precio: " + precioCadena + "€"); // Mostrar precio en Label

            Label stock = new Label("Stock: " + producto.get(3)); // Cargar stock

            VBox precioStock = new VBox(precio, stock); //Añadir precio y stock a un contenedor vertical
            precioStock.setSpacing(20);
            precioStock.setAlignment(Pos.CENTER);
            precioStock.setMinWidth(100);

            TextField texto = new TextField();
            texto.setPromptText("Cantidad...");
            texto.setMaxWidth(100);

            texto.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue,
                                    String newValue) {
                    if (!newValue.matches("\\d*")) {
                        texto.setText(newValue.replaceAll("\\D", ""));
                    }
                }
            });

            HBox contenedor = new HBox(); // Contenedor horizontal (fila de producto)
            contenedor.setAlignment(Pos.CENTER);

            contenedor.setSpacing(60); //TODO ajustar dinamicamente el espaciado


            // Anadir los items al HBOX
            contenedor.getChildren().add(imagenVista);
            contenedor.getChildren().add(nombre);
            contenedor.getChildren().add(precioStock);
            if (!(pestana == Pestanas.ALMACENPRODUCTOS)) {
                contenedor.getChildren().add(texto);
            }

            Button boton = new Button();

            switch (pestana) {
                case TIENDA:
                    boton.setText("Comprar");
                    break;
                case ALMACENPRODUCTOS:
                    boton.setText("Editar");
                    break;
                case ALMACENINGREDIENTES:
                    boton.setText("Comprar Ingrediente");
                    break;
                case COCINA:
                    boton.setText("Cocinar");
                    break;
            }


            boton.setOnAction(new EventHandler<>() {
                @Override
                public void handle(ActionEvent event) {
                    switch (pestana) {
                        case TIENDA:
                            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                            alerta.setTitle("Confirmacion");
                            alerta.setContentText("Seguro que quieres comprar " + texto.getText() + " " + nombre.getText() + "?");
                            Optional<ButtonType> result = alerta.showAndWait();


                            if (result.filter(buttonType -> buttonType == ButtonType.OK).isPresent()) {
                                if (texto.getText().isBlank()) {
                                    comprar(boton.getId(), "1");
                                } else {
                                    comprar(boton.getId(), texto.getText());
                                }

                            }

                            break;
                        case ALMACENPRODUCTOS:
                            editar(boton.getId());
                            break;
                        case ALMACENINGREDIENTES:
                            comprarIngrediente(boton.getId());
                            break;
                        case COCINA:
                            cocinar(boton.getId());
                            break;
                    }
                }
            });


            boton.setId((String) producto.get(1)); // El ID del boton sera el nombre del producto/ingrediente

            contenedor.getChildren().add(boton);


            // Dependiendo de la pestana, añade el contenedor a una pestana un otra
            switch (pestana) {
                case TIENDA:
                    productosTienda.add(contenedor);
                    break;
                case ALMACENINGREDIENTES, ALMACENPRODUCTOS:
                    productosAlmacen.add(contenedor);
                    break;
            }
        }
    }


    /**
     * Este metodo añade un producto al ScrollPane de la cocina. Esta
     * en un metodo separado al resto, ya que esta pantalla es muy
     * diferente a las otras. Por cada producto se cargan los ingredientes
     * que son necesarios para hacer el producto y los muestra tabien.
     *
     * @param producto: el producto a mostrar
     */
    public void anadirProductosCocina(ArrayList<Object> producto) {

        // cargar Imagen
        ImageView imagenVista = this.cargarImagen((String) producto.get(4));

        Label nombre = new Label((String) producto.get(1)); //Nombre del producto

        VBox contenedorProd = new VBox(imagenVista, nombre); //Contenedor de la imagen y el nombre del producto
        contenedorProd.setAlignment(Pos.CENTER);

        ArrayList<VBox> contenedorIngredientes = new ArrayList<>();

        ArrayList<ArrayList<Object>> ingreCociona = this.obtenerIngredientes((String) producto.get(0)); //Obtener los ingredientes del producto
        for (ArrayList<Object> ingrediente : ingreCociona) {
            ImageView imagenVistaIngre = this.cargarImagen((String) ingrediente.get(0));

            Label nombreIngre = new Label((String) ingrediente.get(1));
            Label cantidadIngre = new Label("Necesarios: " + ingrediente.get(2));

            VBox contenedor = new VBox(imagenVistaIngre, nombreIngre, cantidadIngre);
            contenedor.setAlignment(Pos.CENTER);

            contenedorIngredientes.add(contenedor);
        }

        HBox contenedor = new HBox(contenedorProd);

        contenedor.setAlignment(Pos.CENTER_LEFT);
        contenedor.setSpacing(30); //TODO ajustar dinamicamente el espaciado

        contenedor.getChildren().add(this.cargarImagen("equal.png"));

        for (VBox ingres : contenedorIngredientes) {
            contenedor.getChildren().add(ingres);
        }

        Button boton = new Button();
        boton.setText("Cocinar");

        boton.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {

                cocinar(boton.getId());

            }
        });

        Button boton1 = new Button();
        boton1.setText("Eliminar");
        boton1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                eliminar(boton1.getId());
            }
        });

        boton.setId((String) producto.get(1));
        boton1.setId((String) producto.get(1));

        contenedor.getChildren().add(boton);
        contenedor.getChildren().add(boton1);

        productosCocina.add(contenedor);


    }

    public ArrayList<ArrayList<Object>> obtenerIngredientes(String id) {
        ArrayList<ArrayList<Object>> tablas = new ArrayList<>();
        PreparedStatement ps;

        try {
            ps = conexion.prepareStatement("SELECT I.IMAGEN, N.CANTIDAD, I.NOMBRE FROM PRODUCTOS AS P INNER JOIN NECESITA AS N ON N.PR_ID = P.ID INNER JOIN INGREDIENTES AS I ON N.ING_ID = I.ID WHERE P.ID = ?");
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ArrayList<Object> valores = new ArrayList<>();
                valores.add(rs.getString("I.IMAGEN"));
                valores.add(rs.getString("I.NOMBRE"));
                valores.add(rs.getString("N.CANTIDAD"));

                tablas.add(valores);
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar los ingredientes");
        }
        return tablas;
    }


    /**
     * Comprar un producto
     */
    public void comprar(String nombre, String cantidad) {

        PreparedStatement ps1;
        PreparedStatement ps2;
        ResultSet rs;

        String resultado = "";

        try {
            ps1 = conexion.prepareStatement("SELECT STOCK FROM PRODUCTOS WHERE NOMBRE = ?");
            ps1.setString(1, nombre);

            rs = ps1.executeQuery();
            while (rs.next()) {


                System.out.println(rs.getInt("STOCK"));

                if ((rs.getInt("STOCK") - Integer.parseInt(cantidad) < 0)) {
                    resultado = "No hay Stock suficiente";
                } else if (rs.getInt("STOCK") > 0) {
                    ps2 = conexion.prepareStatement("UPDATE PRODUCTOS SET STOCK = STOCK - ? WHERE NOMBRE = ?");
                    ps2.setString(1, cantidad);
                    ps2.setString(2, nombre);
                    resultado = "Compra finalizada Satisfactoriamente";
                    ps2.executeUpdate();
                } else {
                    resultado = "error?";
                }
            }
            PreparedStatement ps3 = conexion.prepareStatement("SELECT PRECIO FROM PRODUCTOS WHERE NOMBRE = ?");
            ps3.setString(1, nombre);
            ResultSet rs1 = ps3.executeQuery();
            Double precio = null;
            if(rs1.next()){
                precio = rs1.getDouble("PRECIO");
            }
            Usuario usuario1 = ControladorLogin.getUsuario();

            Cartera cartera = new Cartera();
            cartera.compra(precio, usuario1, cantidad);

        } catch (SQLException e) {
            resultado = "ERROR AL COMPRAR";
        } finally {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle(resultado);
            alerta.setContentText(resultado);
            alerta.show();
        }
        this.cargar();
    }

    /**
     * Para el boton editar
     *
     * @param nombre: el nombre del producto
     */
    public void editar(String nombre) {
        System.out.println("editar" + nombre);
    }

    /**
     * para el boton añadir Ingrediente
     *
     * @param nombre: el nombre del ingrediente
     */
    public void comprarIngrediente(String nombre) {

    }

    /**
     * Para el boton cocinar
     *
     * @param nombre: el nombre del producto
     */
    public void cocinar(String nombre) {
        System.out.println("cocinar" + nombre);
    }


    /**
    * Para el boton eliminar en cocina
    * @param nombre: el nombre del producto
    * */
    public void eliminar(String nombre){
        try{
            PreparedStatement st = conexion.prepareStatement("SELECT ID FROM PRODUCTOS WHERE NOMBRE = ?");
            st.setString(1, nombre);
            ResultSet rs = st.executeQuery();
            String idProducto = null;
            if(rs.next()){
                idProducto = rs.getString("ID");
            }

            PreparedStatement st2 = conexion.prepareStatement("DELETE FROM NECESITA WHERE PR_ID = ?");
            st2.setString(1, idProducto);
            st2.executeUpdate();

            PreparedStatement st3 = conexion.prepareStatement("DELETE FROM PRODUCTOS WHERE ID = ?");
            st3.setString(1, idProducto);
            st3.executeUpdate();


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Receta eliminada correctamente");
            alert.showAndWait();
            cargar();
        }
        catch(SQLException e){
            throw new IllegalStateException("No se ha podido eliminar la receta");
        }
    }


    @FXML
    public void anadir() {
        try {

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("anadirNuevo-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            stage.setTitle("Panaderia");
            stage.setScene(scene);


            stage.show();
        } catch (IOException e) {
            System.out.println("ERROR");
        }
    }

    @FXML
    public void anadirReceta(){
        try {

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("anadirReceta-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            stage.setTitle("Panaderia");
            stage.setScene(scene);


            stage.show();
        } catch (IOException e) {
            System.out.println("ERROR");
        }
    }

    /**
     * Usado por los metodos que cargan las pestañas, crea las
     * ImageView de los productos para mostrarlas
     *
     * @param ruta: el nombre del archivo
     *              TODO: aplicar un escalado de verdad.
     */
    public ImageView cargarImagen(String ruta) {
        ImageView imagenVista = null;
        Image imagen;
        try {
             imagen = new Image(Objects.requireNonNull(getClass().getResource("/imagenes/" + ruta)).toString());


        } catch (NullPointerException n) {
             imagen = new Image(Objects.requireNonNull(getClass().getResource("/imagenes/missing.png")).toString());

        }

        imagenVista = new ImageView(imagen);
        imagenVista.setFitHeight(100); // Ajustar altura
        imagenVista.setFitWidth(100); // Ajustar anchura


        return imagenVista;
    }

    /**
     * Este metodo ajusta las anclas de los tabs para que los scrollpane
     * de dentro se ajusten al tamaño del tab
     */
    public void ajustarAnclas() {
        AnchorPane.setTopAnchor(scrollTienda, 0.0);
        AnchorPane.setBottomAnchor(scrollTienda, 0.0);
        AnchorPane.setLeftAnchor(scrollTienda, 0.0);
        AnchorPane.setRightAnchor(scrollTienda, 0.0);

        AnchorPane.setTopAnchor(scrollCocina, 50.0);
        AnchorPane.setBottomAnchor(scrollAlmacen, 0.0);
        AnchorPane.setLeftAnchor(scrollAlmacen, 0.0);
        AnchorPane.setRightAnchor(scrollAlmacen, 0.0);

        AnchorPane.setTopAnchor(scrollCocina, 50.0);
        AnchorPane.setBottomAnchor(scrollCocina, 0.0);
        AnchorPane.setLeftAnchor(scrollCocina, 0.0);
        AnchorPane.setRightAnchor(scrollCocina, 0.0);
    }
}
