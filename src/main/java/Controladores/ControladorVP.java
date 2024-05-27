package Controladores;

import BBDD.ConexionBBDD;
import Enumerados.Pestanas;
import Modulo.Cartera;
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

    private final ArrayList<HBox> productosTienda = new ArrayList<>();
    private final ArrayList<HBox> productosAlmacen = new ArrayList<>();
    private final ArrayList<HBox> productosCocina = new ArrayList<>();

    private ControladorVP current;
    @FXML
    private ImageView fotoPerfil; // Foto de perfil
    @FXML
    private Tab tiendaTab;
    @FXML
    private Tab almacenTab;
    @FXML
    private Tab cocinaTab;
    @FXML
    private Label rol;
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

    private Connection conexion; // La misma conexion se usa en tod0 el controlador para optimizar las cargas
    private Usuario usuario; // El usuario que esta logeado


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conexion = ConexionBBDD.conectar(conexion); // Abrir la conexion
    }

    public void setRol(Usuario usuario) { // Recive el usuario desde el ControladorLogin, y lo guarda
        this.usuario = usuario;
        this.cargar(); // Se cargan las vistas
    }

    /**
     * Este metodo se encarga de inicializar los items globales, y luego, dependiendo
     * del rol del usuario, manda a cargar las pestañas correspondientes. Antes tod0
     * lo que se encuentra en el metodo estaba en initialize, pero al requerir datos
     * del usuario, se separo en este metodo aparte, que se llama al tener el usuario.
     *
     * Este metodo tambien se usa para actualizar las vistas.
     */
    public void cargar() {
        rol.setText(usuario.getRol().toString()); // El texto del Rol

        fotoPerfil.setImage(this.cargarImage(usuario.getFotoPerfil())); // Colocar foto y ajustar tamaño
        fotoPerfil.setFitWidth(100);
        fotoPerfil.setFitHeight(100);

        // Vacia los tabs, en caso de que se actualizen los datos
        productosTienda.clear();
        productosAlmacen.clear();
        productosCocina.clear();

        ArrayList<ArrayList<Object>> productos;
        ArrayList<ArrayList<Object>> ingredientes;

        switch (usuario.getRol()) { // Depende del rol del usuario, realiza unas acciones u otras
            case CLIENTE:
                this.tiendaTab.setDisable(false); // Se puede usar la tienda

                productos = this.cargarDatos(0); // Carga todos los productos

                for (ArrayList<Object> producto : productos) {
                    this.anadirProductosOIngredientes(producto, Pestanas.TIENDA); //Por cada producto, crea un HBox con sus datos.
                }

                this.cargarTienda(); // Carga los HBox-es creados anteriormente en la pestaña tienda.

                break;
            case PANADERO:
                this.cocinaTab.setDisable(false); // Se puede usar la cocina

                productos = this.cargarDatos(0); // Carga todos los productos

                for (ArrayList<Object> producto : productos) {
                    this.anadirProductosCocina(producto); //Por cada producto, crea un HBox con sus datos y sus ingredinetes necesarios.
                }

                this.cargarCocina(); // Carga los HBox-es creados anteriormente en la pestaña cocina.

                break;
            case ALMACENERO:
                this.almacenTab.setDisable(false); // Se puede usar el almacen

                productos = this.cargarDatos(0); // Carga todos los productos
                for (ArrayList<Object> producto : productos) {
                    this.anadirProductosOIngredientes(producto, Pestanas.ALMACENPRODUCTOS); // Por cada producto, crea un HBox con sus datos
                }

                ingredientes = this.cargarDatos(0); // Carga todos los ingredientes
                for (ArrayList<Object> ingrediente : ingredientes) {
                    this.anadirProductosOIngredientes(ingrediente, Pestanas.ALMACENINGREDIENTES); // Por cada ingredientes, crea un HBox con sus datos
                }

                this.cargarAlmacen(); // Carga los HBox-es creados anteriormente en la pestaña inventario.

                break;
            case ADMINISTRADOR: // Si el rol es administrador, hace lo de los tres roles anteriores a la vez.
                this.tiendaTab.setDisable(false);
                this.almacenTab.setDisable(false);
                this.cocinaTab.setDisable(false);

                productos = this.cargarDatos(0);
                for (ArrayList<Object> producto : productos) {
                    this.anadirProductosOIngredientes(producto, Pestanas.TIENDA);
                    this.anadirProductosCocina(producto);
                    this.anadirProductosOIngredientes(producto, Pestanas.ALMACENPRODUCTOS);
                }

                ingredientes = this.cargarDatos(1);
                for (ArrayList<Object> ingrediente : ingredientes) {
                    this.anadirProductosOIngredientes(ingrediente, Pestanas.ALMACENINGREDIENTES);
                }

                this.cargarTienda();
                this.cargarAlmacen();
                this.cargarCocina();

                break;
        }

        this.ajustarAnclas(); //Ajustar las posiciones del scrollpane, para que se ajusten bien al tamaño de la ventana

    }

    /**
     * El metodo coloca todos los HBox-es que estan en
     * prodcutosTienda, y los coloca en el VBox Tienda.
     */
    public void cargarTienda() {
        Tienda.getChildren().clear();

        for (HBox contenedor : productosTienda) {
            Tienda.getChildren().add(contenedor);
        }

    }

    /**
     * El metodo coloca todos los HBox-es que estan en
     * productosAlmacen, y los coloca en el VBox Almacen.
     */
    public void cargarAlmacen() {
        Almacen.getChildren().clear();

        for (HBox contenedor : productosAlmacen) {
            Almacen.getChildren().add(contenedor);
        }
    }

    /**
     * El metodo coloca todos los HBox-es que estan en
     * productosCocina, y los coloca en el VBox Cocina.
     */
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
    public ArrayList<ArrayList<Object>> cargarDatos(int tipo) {
        Statement script;
        ResultSet rs = null;

        ArrayList<ArrayList<Object>> tabla = new ArrayList<>(); // Aqui se guardaran los datos.

        try {
            script = conexion.createStatement();

            // Dependiendo del parametro de entrada, realiza un Select de ingredientes o productos.
            if (tipo == 1) {
                rs = script.executeQuery("SELECT * FROM INGREDIENTES");
            } else if (tipo == 0) {
                rs = script.executeQuery("SELECT * FROM PRODUCTOS");
            }

            while (rs.next()) { //Itera los resultados. Crea un atributo por cada columna de la tabla, y los añade a una coleccion de Object.
                ArrayList<Object> valores = new ArrayList<>();

                String id = rs.getString("ID"); //ID

                String nombre = (rs.getString("NOMBRE")); // Nombre

                double precio = (rs.getDouble("PRECIO")); // Precio

                String stock = (rs.getString("STOCK")); // Stock

                String imagen = (rs.getString("IMAGEN")); //Imagen

                // Añade los atributos a una coleccion
                valores.add(id);
                valores.add(nombre);
                valores.add(precio);
                valores.add(stock);
                valores.add(imagen);

                tabla.add(valores); // Añade la coleccion a otra coleccion
            }
        } catch (SQLException e) { // Si hay un porblema con la conexion.
            System.out.println("No se ha podido conectar a la BBDD");
        }
        return tabla;
    }

    /**
     * Este metodo recive como parametro la pestaña que quiera cargar y
     * un listado de los datos de un producto, y crea HBox-es que contienen
     * datos del producto/ingrediente, junto a sus botones correspondientes.
     */
    public void anadirProductosOIngredientes(ArrayList<Object> producto, Pestanas pestana) {

        if (!(pestana == Pestanas.TIENDA) || (Integer.parseInt((String) producto.get(3)) != 0)) { // Si no hay stock, no se creara en caso de la cocina

            ImageView imagenVista = this.cargarImagen((String) producto.get(4)); // Cargar imagen

            Label nombre = new Label((String) producto.get(1)); // Cargar nombre
            nombre.setMinWidth(100);


            double precioNumero = (Double) producto.get(2); // Cargar precio

            String precioCadena = String.valueOf(precioNumero); // Pasar de double a String
            Label precio = new Label("Precio: " + precioCadena + "€"); // Mostrar precio en Label

            Label stock = new Label("Stock: " + producto.get(3)); // Cargar stock

            VBox precioStock = new VBox(precio, stock); //Añadir precio y stock a un contenedor vertical

            // Ajustar posiciones, espaciado, etc.
            precioStock.setSpacing(20);
            precioStock.setAlignment(Pos.CENTER);
            precioStock.setMinWidth(100);

            TextField texto = new TextField(); // Donde se introduce la cantidad para comprar
            texto.setPromptText("Cantidad...");
            texto.setMaxWidth(100);

            texto.textProperty().addListener(new ChangeListener<>() { // Esta hace que solo se puedan introducir numeros. Tampoco permite signo negativo.
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

            // En caso de que un ingrediente tenga poco stock, les aplica un color de fondo.
            if ((pestana == Pestanas.ALMACENINGREDIENTES) && (Integer.parseInt((String) producto.get(3)) <= 5 && (Integer.parseInt((String) producto.get(3)) > 0))) {
                contenedor.setStyle("-fx-background-color: khaki;");
            } else if ((pestana == Pestanas.ALMACENINGREDIENTES) && (Integer.parseInt((String) producto.get(3)) == 0)) {
                contenedor.setStyle("-fx-background-color: burlywood;");
            }


            // Anadir los items al HBOX
            contenedor.getChildren().add(imagenVista);
            contenedor.getChildren().add(nombre);
            contenedor.getChildren().add(precioStock);

            if (!(pestana == Pestanas.ALMACENPRODUCTOS)) { //Solo añade el textField si no es un producto en el almacen.
                contenedor.getChildren().add(texto);
            }

            Button boton = new Button(); // Boton del producto/ingrediente

            switch (pestana) { //Depende del apartado, pone un texto u otro
                case TIENDA:
                    boton.setText("Comprar");
                    break;
                case ALMACENPRODUCTOS:
                    boton.setVisible(false);
                    break;
                case ALMACENINGREDIENTES:
                    boton.setText("Comprar Ingrediente");
                    break;
                case COCINA:
                    boton.setText("Cocinar");
                    break;
            }

            boton.setOnAction(new EventHandler<>() { // Dependiendo de la pestaña, añade una funcionalidad al boton distinta.
                @Override
                public void handle(ActionEvent event) {
                    switch (pestana) {
                        case TIENDA:
                            // Alerta para confirmar compra
                            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                            alerta.setTitle("Confirmacion");
                            alerta.setContentText("Seguro que quieres comprar " + texto.getText() + " " + nombre.getText() + "?");
                            Optional<ButtonType> result = alerta.showAndWait();


                            if (result.filter(buttonType -> buttonType == ButtonType.OK).isPresent()) {
                                if (texto.getText().isBlank()) { //Si el textField esta vacio, se asumira que se compra 1 producto
                                    comprar(boton.getId(), "1"); // llamada al metodo de comprar
                                } else {
                                    comprar(boton.getId(), texto.getText()); // llamada al metodo de comprar
                                }

                            }

                            break;
                        case ALMACENPRODUCTOS:
                            editar(boton.getId()); // llamada al metodo de editar
                            break;
                        case ALMACENINGREDIENTES:
                            if (texto.getText().isBlank()) { //Si el textField esta vacio, se asumira que se compra 1 ingrediente
                                comprarIngrediente(boton.getId(), "1"); // llamada al metodo de comprarIngrediente
                            } else {
                                comprarIngrediente(boton.getId(), texto.getText()); // llamada al metodo de comprarIngrediente
                            }
                            break;
                        case COCINA:
                            cocinar(boton.getId()); // llamada al metodo de cocinar
                            break;
                    }
                }
            });


            boton.setId((String) producto.get(1)); // El ID del boton sera el nombre del producto/ingrediente
            boton.setStyle("-fx-background-color: #ffffff;");

            contenedor.getChildren().add(boton); // Añade el boton al contenedor


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

        for (ArrayList<Object> ingrediente : ingreCociona) { //Por cada ingrediente necesario...
            ImageView imagenVistaIngre = this.cargarImagen((String) ingrediente.get(0)); // Imagen del ingrediente

            Label nombreIngre = new Label((String) ingrediente.get(1)); // Nombre del ingrediente
            Label cantidadIngre = new Label("Necesarios: " + ingrediente.get(2)); // Cantidad necesaria del ingrediente

            VBox contenedor = new VBox(imagenVistaIngre, nombreIngre, cantidadIngre); // Contenedor de los datos del ingediente
            contenedor.setAlignment(Pos.CENTER);

            contenedorIngredientes.add(contenedor); // Guarda el contenedor vertical
        }

        HBox contenedor = new HBox(contenedorProd); // El contenedor horizontal para todos los contenedores verticales
        contenedor.setPrefWidth(999999999);

        contenedor.setAlignment(Pos.CENTER_LEFT);
        contenedor.setSpacing(30); //TODO ajustar dinamicamente el espaciado

        contenedor.getChildren().add(this.cargarImagen("equal.png"));

        for (VBox ingres : contenedorIngredientes) { // Por cada contenedor vertical de ingredientes...
            contenedor.getChildren().add(ingres);
        }

        Button botonCocinar = new Button(); // Boton de cocinar
        botonCocinar.setText("Cocinar");
        botonCocinar.setOnAction(_ -> cocinar(botonCocinar.getId())); // Asigna el metodo de cocinar
        botonCocinar.setStyle("-fx-background-color: #ffffff;");

        Button botonEliminar = new Button(); // Boton de eliminar
        botonEliminar.setText("Eliminar");
        botonEliminar.setOnAction(_ -> eliminar(botonEliminar.getId())); // Asignar el metodo de eliminar
        botonEliminar.setStyle("-fx-background-color: #ffffff;");

        // Asigna al ID del boton el nombre del producto, para uso posterior
        botonCocinar.setId((String) producto.get(1));
        botonEliminar.setId((String) producto.get(1));

        // Añade los botones al contenedor horizontal
        contenedor.getChildren().add(botonCocinar);
        contenedor.getChildren().add(botonEliminar);

        productosCocina.add(contenedor);
    }

    /**
     * Este metodo recibe como parametro el ID de un producto, y retorna los
     * datos de los ingredientes hacen falta para cocinarlos. Realiza una
     * consulta a la BBDD, y por cada linea que reciva, se itera y se añaden
     * los datos a una coleccion, que luego se añaden a una coleccion 2D.
     */
    public ArrayList<ArrayList<Object>> obtenerIngredientes(String id) {
        ArrayList<ArrayList<Object>> tablas = new ArrayList<>(); // La coleccion a retornar
        PreparedStatement ps;

        try {
            // Se realiza la consulta
            ps = conexion.prepareStatement("SELECT I.IMAGEN, N.CANTIDAD, I.NOMBRE FROM PRODUCTOS AS P INNER JOIN NECESITA AS N ON N.PR_ID = P.ID INNER JOIN INGREDIENTES AS I ON N.ING_ID = I.ID WHERE P.ID = ?");
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) { // Por cada linea que la consulta retorna...
                ArrayList<Object> valores = new ArrayList<>(); // Lista que contendra los datos del ingrediente

                valores.add(rs.getString("I.IMAGEN"));
                valores.add(rs.getString("I.NOMBRE"));
                valores.add(rs.getString("N.CANTIDAD"));

                tablas.add(valores); // Se añaden los datos a la coleccion general
            }
        } catch (SQLException e) { // En caso de que haya un error con la conexion...
            System.out.println("Error al consultar los ingredientes");
        }
        return tablas;
    }


    /**
     * El metodo recibe como parametro el nombre de un porducto y una
     * cantidad, y realiza una compra. Resta la cantidad al stock, y tambien
     * ajusta la cartera del usuario inbolucrado
     */
    public void comprar(String nombre, String cantidad) {

        PreparedStatement ps1;
        PreparedStatement ps2;
        ResultSet rs;

        String resultado = "";

        try {
            conexion.setAutoCommit(false); //Nada de lo que se haga se realizara hasta que el commit

            // Realiza una consulta para comprobar el stock del producto
            ps1 = conexion.prepareStatement("SELECT STOCK, PRECIO FROM PRODUCTOS WHERE NOMBRE = ?");
            ps1.setString(1, nombre);
            rs = ps1.executeQuery();

            while (rs.next()) {
                if ((rs.getInt("STOCK") - Integer.parseInt(cantidad) < 0)) { // En caso de que la cantidad deseada sea mayor al stock, se cancela la compra
                    resultado = "No hay Stock suficiente";
                }
                else if (rs.getInt("STOCK") > 0) { // Si el stock es mayor a cero
                    ps2 = conexion.prepareStatement("UPDATE PRODUCTOS SET STOCK = STOCK - ? WHERE NOMBRE = ?");
                    ps2.setString(1, cantidad);
                    ps2.setString(2, nombre);

                    Double precio = rs.getDouble("PRECIO");
                    Cartera cartera = new Cartera();
                    cartera.compra(precio, usuario, cantidad); // Realiza la compra economicamente

                    ps2.executeUpdate();
                    resultado = "Compra finalizada Satisfactoriamente";

                    conexion.commit();
                } else { // La verdad, no se en que caso puede darse este error, no ha pasado nunca y no se si pasara.
                    resultado = "error?";
                }
            }
        } catch (SQLException e) {
            resultado = "Ha habido un error al comprar. Intentelo de nuevo mas tarde";
        } finally {
            // Crea una alerta de confirmacion.
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle(resultado);
            alerta.setContentText(resultado);
            alerta.show();
            try{
                conexion.setAutoCommit(true); //Se vuelve a activar el autocommit
            }
            catch (SQLException e){
                System.out.println("Error extremo. Se cierra la pantalla por razones de seguridad");
                Stage stageActual = (Stage) scrollTienda.getScene().getWindow();
                stageActual.close();
            }
        }
        this.cargar(); // Para actualizar los datos en pantalla
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
    public void comprarIngrediente(String nombre, String cantidad) {
        PreparedStatement ps1;
        PreparedStatement ps2;

        String resultado = "";

        try {
            ps1 = conexion.prepareStatement("SELECT STOCK FROM INGREDIENTES WHERE NOMBRE = ?");
            ps1.setString(1, nombre);

            ps1.executeQuery();

            ps2 = conexion.prepareStatement("UPDATE INGREDIENTES SET STOCK = STOCK + ? WHERE NOMBRE = ?");
            ps2.setString(1, cantidad);
            ps2.setString(2, nombre);
            resultado = "Compra finalizada Satisfactoriamente";
            ps2.executeUpdate();


            PreparedStatement ps3 = conexion.prepareStatement("SELECT PRECIO FROM INGREDIENTES WHERE NOMBRE = ?");
            ps3.setString(1, nombre);
            ResultSet rs1 = ps3.executeQuery();

            Double precio = null;
            while (rs1.next()) {
                precio = rs1.getDouble("PRECIO");
            }

            Usuario usuario1 = ControladorLogin.getUsuario();
            Cartera cartera = new Cartera();
            cartera.compraStockIngredientes(precio, usuario1, cantidad);
        } catch (SQLException e) {
            resultado = "ERROR AL COMPRAR STOCK";
        } finally {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle(resultado);
            alerta.setContentText(resultado);
            alerta.showAndWait();
            this.cargar();
        }

    }

    /**
     * Para el boton cocinar
     *
     * @param nombre: el nombre del producto
     */
    public void cocinar(String nombre) {
        try {
            PreparedStatement ps = conexion.prepareStatement("SELECT ID FROM PRODUCTOS WHERE NOMBRE = ?");
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            Integer idProducto = null;
            if (rs.next()) {
                idProducto = rs.getInt("ID");
            }

            PreparedStatement ps1 = conexion.prepareStatement("SELECT ING_ID, CANTIDAD FROM NECESITA WHERE PR_ID = ?");
            ps1.setInt(1, idProducto);
            HashMap<Integer, Integer> ingredientes = new HashMap<>();
            ResultSet rs1 = ps1.executeQuery();
            while (rs1.next()) {
                ingredientes.put(rs1.getInt("ING_ID"), rs1.getInt("CANTIDAD"));
            }

            for (Map.Entry<Integer, Integer> entry : ingredientes.entrySet()) {
                PreparedStatement ps2 = conexion.prepareStatement("UPDATE INGREDIENTES SET STOCK = STOCK - ? WHERE ID = ?");
                ps2.setInt(1, entry.getValue());
                ps2.setInt(2, entry.getKey());
                ps2.executeUpdate();
            }
            PreparedStatement ps3 = conexion.prepareStatement("UPDATE PRODUCTOS SET STOCK = STOCK + 1 WHERE ID = ?");
            ps3.setInt(1, idProducto);
            ps3.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("La receta de " + nombre + " se ha cocinado correctamente");
            alert.showAndWait();
            this.cargar();

        } catch (SQLException e) {
            throw new IllegalStateException("No se ha podido cocinar la receta");
        }


    }


    /**
     * Para el boton eliminar en cocina
     *
     * @param nombre: el nombre del producto
     */
    public void eliminar(String nombre) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación");
            alert.setContentText("¿Seguro que quiere eliminar esta receta?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.filter(buttonType -> buttonType == ButtonType.OK).isPresent()) {
                PreparedStatement st = conexion.prepareStatement("SELECT ID FROM PRODUCTOS WHERE NOMBRE = ?");
                st.setString(1, nombre);
                ResultSet rs = st.executeQuery();
                String idProducto = null;
                if (rs.next()) {
                    idProducto = rs.getString("ID");
                }

                PreparedStatement st2 = conexion.prepareStatement("DELETE FROM NECESITA WHERE PR_ID = ?");
                st2.setString(1, idProducto);
                st2.executeUpdate();

                PreparedStatement st3 = conexion.prepareStatement("DELETE FROM PRODUCTOS WHERE ID = ?");
                st3.setString(1, idProducto);
                st3.executeUpdate();

                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Mensaje");
                alert1.setContentText("Receta eliminada correctamente");
                alert1.showAndWait();

                cargar();
            }

        } catch (SQLException e) {
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

            AnadirNuevoController recargar = fxmlLoader.getController();
            recargar.setMainController(current);
        }
        catch (IOException e) {
            System.out.println("ERROR");
        }
    }

    @FXML
    public void anadirReceta() {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("anadirReceta-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            stage.setTitle("Panaderia");
            stage.setScene(scene);
            stage.show();

            AnadirRecetaView recargar = fxmlLoader.getController();
            recargar.setMainController(current);
        }
        catch (IOException e) {
            System.out.println("ERROR");
        }
    }


    /**
     * El metodo primero pregunta al usuario si quiere cerrar sesion, y
     * si la respuesta es afirmativa, abre una pantalla de inicio de sesion
     * y cierra la actual.
     */
    @FXML
    public void logout() {
        try {
            // Alerta para preguntar al usuario
            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Confirmacion");
            alerta.setContentText("¿Seguro que quieres cerrar sesion?");
            Optional<ButtonType> result = alerta.showAndWait();

            if (result.filter(buttonType -> buttonType == ButtonType.OK).isPresent()) {
                Stage stageActual = (Stage) scrollTienda.getScene().getWindow(); // Pantalla actual

                // Crea la pantalla de login
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Login.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 400, 400);
                Stage stage = new Stage();
                stage.setTitle("Login");
                stage.setScene(scene);
                stage.show();

                stageActual.close(); // Cerrar pantalla actual
            }
        } catch (IOException a) {
            System.out.println("Tan inutil soy que ni autodestruirme puedo"); // XD
        }
    }

    /**
     * El metodo abre la ventana de edicion, y manda el controlador
     * de la ventana principal para tener acceso.
     */
    @FXML
    public void editarPerfil() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("editar.fxml"));

            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("Editar Perfil");
            stage.setMaxWidth(600);
            stage.setMaxHeight(500);
            stage.setMinWidth(600);
            stage.setMinHeight(500);

            ControladorEditar editar = fxmlLoader.getController();
            editar.inicializar(usuario);
            editar.setMainController(current);

            stage.setScene(scene);
            stage.show();
        } catch (IOException i) {
            System.out.println(":(");
        }
    }


    /**
     * Usado por los metodos que cargan las pestañas, crea las
     * ImageView de los productos para mostrarlas
     *
     * @param ruta: el nombre del archivo
     *                           TODO: aplicar un escalado de verdad. Seguramente no se aplicara un escalado de verdad. Es complicao
     */
    public ImageView cargarImagen(String ruta) {
        ImageView imagenVista;
        Image imagen;
        try {
            imagen = new Image(Objects.requireNonNull(getClass().getResource("/imagenes/" + ruta)).toString()); //Obtiene la imagen de la carpeta imagenes
        } catch (NullPointerException n) {
            // En caso de que no encuentre la imagen, cargara una imagen predeterminada
            imagen = new Image(Objects.requireNonNull(getClass().getResource("/imagenes/missing.png")).toString());
        }
        imagenVista = new ImageView(imagen);
        imagenVista.setFitHeight(100); // Ajustar altura
        imagenVista.setFitWidth(100); // Ajustar anchura

        return imagenVista;
    }

    /**
     * Similar al de arriba, pero retorna un objeto Image para ser usado
     * en un ImageView.
     */
    public Image cargarImage(String ruta) {
        Image imagen;
        try {
            imagen = new Image(Objects.requireNonNull(getClass().getResource("/imagenes/" + ruta)).toString());
        } catch (NullPointerException n) {
            imagen = new Image(Objects.requireNonNull(getClass().getResource("/imagenes/missing.png")).toString());
        }
        return imagen;
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

    public void setCurrentController(ControladorVP controller) {
        this.current = controller;
    }

    public void actualizarFoto(Image fotoNueva) {
        this.fotoPerfil.setImage(fotoNueva);
    }

    public void accederMenuModificarReceta(ActionEvent actionEvent) {

    }
}
