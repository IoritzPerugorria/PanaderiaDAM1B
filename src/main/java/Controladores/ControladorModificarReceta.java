package Controladores;
import BBDD.ConexionBBDD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.panaderiadam1b.Main;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;
public class ControladorModificarReceta implements Initializable {
    @FXML
    private Label lblIngredientes;
    @FXML
    private Button btnModificar;
    @FXML
    private ListView<String> listaRecetas;
    @FXML
    private ListView<String> listaIngredientes;
    @FXML
    private TextField nombreReceta;
    @FXML
    private TextField precioReceta;
    @FXML
    private TextField txtfldCantidadIngrediente;
    @FXML
    private int ID_ING;
    private int ID_PR;
    Connection conexion;
    public ControladorModificarReceta(){
        this.nombreReceta = new TextField();
        this.precioReceta = new TextField();
        this.txtfldCantidadIngrediente = new TextField();
        this.btnModificar = new Button();
        this.lblIngredientes = new Label("(Ninguno seleccionado)");
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //PRIMERO DESHABILITAREMOS LOS NODOS HASTA QUE NO SE SELECCIONE UNA RECETA
        this.deshabilitarNodosEscritura();

        //PARA MOSTRAR LAS RECETAS DE LA BASE DE DATOS
        try {
            conexion = ConexionBBDD.conectar ( conexion );
            this.rellenarListaRecetas ();
            //PARA DETECTAR Y RESPONDER EN BASE A LOS OBJETOS SELECCIONADOS
            listaRecetas.setOnMouseClicked ( event -> {
                        // Obtener el artículo seleccionado
                        Object recetaSeleccionada = listaRecetas.getSelectionModel ( ).getSelectedItem ( );
                        // Acceder a los valores del elemento seleccionado y visualizarlos
                        if ( recetaSeleccionada != null ) {
                            // Asumiendo que el Item seleccionado es del tipo String
                            String valorRecetaSeleccionada = (String) recetaSeleccionada;


                            /**
                             AL SELECCIONAR UNA RECETA, HABILITAREMOS LOS NODOS "nombreReceta" Y "precioReceta",
                             PERO DESACTIVAREMOS POR EN ESE MOMENTO EL NODO "txtfldCantidadIngrediente",
                             PORQUE EN DICHA RECETA TODAVIA NO SE HA SELECCIONADO NINGUN INGREDIENTE
                             **/
                            this.habilitarNodosEscrituraNombrePrecio();
                            this.deshabilitarNodoEscrituraIngredientes();


                            // Buscar dicho ingrediente:
                            try {
                                PreparedStatement datosRecogidosRecetas = conexion.prepareStatement ( "SELECT * FROM PRODUCTOS WHERE NOMBRE = ?" );
                                datosRecogidosRecetas.setString ( 1, valorRecetaSeleccionada );
                                ResultSet resultadoDatos = datosRecogidosRecetas.executeQuery ( );
                                resultadoDatos.next ( );
                                nombreReceta.setText ( resultadoDatos.getString ( "NOMBRE" ) );
                                precioReceta.setText ( resultadoDatos.getString ( "PRECIO" ) );
                                this.rellenarListaIngredientes();
                            }
                            catch (SQLException e) {
                                throw new RuntimeException ( e );
                            }
                        }
                    }
            );
            listaIngredientes.setOnMouseClicked ( event -> {
                Object ingredienteSeleccionado = listaIngredientes.getSelectionModel ( ).getSelectedItem ( );

                if ( ingredienteSeleccionado != null ) {
                    String valorIngredienteSeleccionado = (String) ingredienteSeleccionado;
                    /** EL NODO "txtfldCantidadIngrediente" UNICAMENTE SE HABILITARÁ CUANDO SE HAYA SELECCIONADO UN INGREDIENTE **/
                    this.habilitarNodoEscrituraIngredientes();

                    try {
                        PreparedStatement ordenIdentificarIngrediente = conexion.prepareStatement("SELECT ID,NOMBRE FROM INGREDIENTES WHERE NOMBRE = ?");
                        ordenIdentificarIngrediente.setString ( 1, valorIngredienteSeleccionado );
                        ResultSet resultadoDatos = ordenIdentificarIngrediente.executeQuery ( );
                        resultadoDatos.next ( );
                        lblIngredientes.setText("(" + resultadoDatos.getString("NOMBRE") + "):");
                        ID_ING = resultadoDatos.getInt ( "ID" );

                        PreparedStatement ordenIdentificarProducto = conexion.prepareStatement("SELECT ID FROM PRODUCTOS WHERE NOMBRE = ?");
                        ordenIdentificarProducto.setString ( 1, nombreReceta.getText());
                        ResultSet resultadoIdentificarProducto = ordenIdentificarProducto.executeQuery ( );
                        resultadoIdentificarProducto.next ( );
                        ID_PR = resultadoIdentificarProducto.getInt ( "ID" );

                        PreparedStatement ordenCantidadNecesariaIngredientes = conexion.prepareStatement ( "SELECT CANTIDAD FROM NECESITA WHERE ING_ID = ? AND PR_ID = ?");
                        ordenCantidadNecesariaIngredientes.setInt ( 1, ID_ING );
                        ordenCantidadNecesariaIngredientes.setInt ( 2, ID_PR );
                        ResultSet resultadoCantidadIngredientes = ordenCantidadNecesariaIngredientes.executeQuery ( );
                        resultadoCantidadIngredientes.next ( );
                        txtfldCantidadIngrediente.setText(resultadoCantidadIngredientes.getString("CANTIDAD"));

                    } catch (SQLException ex) {
                        throw  new RuntimeException ( ex );
                    }
                }
            } );
        } catch (SQLException e) {
            throw new RuntimeException ( e );
        }
    }
    private void rellenarListaRecetas() throws SQLException {
        PreparedStatement recogidaNombresRecetas = conexion.prepareStatement("SELECT NOMBRE FROM PRODUCTOS ");
        ResultSet resultadoNombres = recogidaNombresRecetas.executeQuery();
        ArrayList<String> listaNombresRecetas = new ArrayList<>();
        while (resultadoNombres.next()){
            listaNombresRecetas.add(resultadoNombres.getString("NOMBRE"));
        }
        for (int i = 0; i < listaNombresRecetas.size(); i++) {
            listaRecetas.getItems().add(listaNombresRecetas.get(i));
        }
    }
    private void rellenarListaIngredientes() throws SQLException {
        listaIngredientes.getItems().removeAll(listaIngredientes.getItems());

        PreparedStatement aux1 = conexion.prepareStatement ( "SELECT ID FROM PRODUCTOS WHERE NOMBRE = ?");
        aux1.setString (1, nombreReceta.getText());
        ResultSet resultadoIngredientes = aux1.executeQuery();
        resultadoIngredientes.next();
        int IDproducto = resultadoIngredientes.getInt(1);

        PreparedStatement recogidaNombresIngredientes = conexion.prepareStatement("SELECT I.NOMBRE,N.ING_ID,N.PR_ID FROM NECESITA AS N INNER JOIN INGREDIENTES AS I ON I.ID = N.ING_ID WHERE N.PR_ID = ?");
        recogidaNombresIngredientes.setInt ( 1, IDproducto );
        ResultSet resultadoNombresIngredientes = recogidaNombresIngredientes.executeQuery();
        ArrayList<String> listaNombresIngredientes = new ArrayList<>();
        while (resultadoNombresIngredientes.next()){
            listaNombresIngredientes.add(resultadoNombresIngredientes.getString("I.NOMBRE"));
        }
        for (String listaNombresIngrediente : listaNombresIngredientes) {
            listaIngredientes.getItems().add(listaNombresIngrediente);
        }
    }

    // Metodo confirmación modificación receta:
    public void modificarReceta(ActionEvent actionEvent) {
        try {
            // SI EL CAMPO RECETA Y EL CAMPO PRECIO ESTAN RELLENADOS
            if ((!nombreReceta.getText().isBlank()) && (!precioReceta.getText().isBlank())){
                String nombre = nombreReceta.getText();
                double precio = Integer.parseInt(precioReceta.getText());

                // SI EL CAMPO INGREDIENTES NO SE HA RELLENADO, SOLO SE ACTUALIZARAN EL NOMBRE Y EL PRECIO
                if (txtfldCantidadIngrediente.getText().isBlank()){
                    this.actualizarProducto(nombre,precio);
                }
                // EN EL CASO DE QUE EL CAMPO INGREDIENTES SÍ SE HA RELLENADO, SE ACTUALIZARAN EL NOMBRE, EL PRECIO, Y LA CANTIDAD NECESARIA DE UN INGREDIENTE
                else {
                    int cantidadIngredientes = Integer.parseInt(txtfldCantidadIngrediente.getText());
                    // SI LA CANTIDAD DEL INGREDIENTE NÓ ES NEGATIVA, SE PROCEDERÁ CON LA OPERACIÓN INDICADA PREVIAMENTE
                    if (cantidadIngredientes >= 0){
                        this.actualizarProducto(nombre,precio);
                        this.actualizarIngrediente(cantidadIngredientes);
                    }
                    else {
                        // SI LA CANTIDAD DEL INGREDIENTE SÍ ES NEGATIVA, SALTARÁ OTRO MENSAJE DE ERROR Y SE CANCELARA LA MODIFICACI'ON.
                        Alert alerta = new Alert(Alert.AlertType.ERROR);
                        alerta.setTitle("ERROR");
                        alerta.setHeaderText("Modificación incorrecta");
                        alerta.setContentText("La cantidad de ingredientes no puede ser negativa");
                        alerta.showAndWait();
                    }
                }
            }
            // EN CASO DE QUE LOS CAMPOS NOMBRE Y PRECIOS ESTEN SIN RELLENAR, MOSTAREMOS UN MENSAJE EN CADA UNA DE LAS POSIBILIDADES
            else {
                if (nombreReceta.getText().isBlank() && precioReceta.getText().isBlank()){
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setTitle("ERROR");
                    alerta.setHeaderText("Modificación incorrecta");
                    alerta.setContentText("El campo del Nombre y Precio no pueden quedar vacias.");
                    alerta.showAndWait();
                }
                else if (nombreReceta.getText().isBlank()){
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setTitle("ERROR");
                    alerta.setHeaderText("Modificación incorrecta");
                    alerta.setContentText("El campo del Nombre no puede quedar vacia.");
                    alerta.showAndWait();
                }
                else {
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setTitle("ERROR");
                    alerta.setHeaderText("Modificación incorrecta");
                    alerta.setContentText("El campo del Precio no puede quedar vacia.");
                    alerta.showAndWait();
                }
            }
        }


        // EN CASO DE QUE EL CAMPO DE PRECIO O DE CANTIDAD DE INGREDIENTES SE HAYA ESCRITO CON UN FORMATO INCORRECTO
        catch (NumberFormatException e){
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("ERROR");
            alerta.setHeaderText("Modificación incorrecta");
            alerta.setContentText("Campo de Precio o Ingredientes no valido.");
            alerta.showAndWait();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // MÉTODO PARA SALIR DEL MENÚ DE MODIFICACIÓN
    @FXML
    public void salirDeLaPantalla(ActionEvent actionEvent) throws IOException {
        Node node = (Node) actionEvent.getSource();
        Stage currentStage = (Stage) node.getScene().getWindow();
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("vista_principal.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 900);
        stage.setTitle("Panaderia");
        stage.setScene(scene);
        currentStage.close();
        stage.show();
    }

    // METODOS DE ACTUALIZACIÓN
    private void actualizarProducto(String nombre,double precio) throws SQLException {
        PreparedStatement ordenActualizacionProducto = conexion.prepareStatement("UPDATE PRODUCTOS SET NOMBRE = ?, PRECIO = ? WHERE ID = ?");
        ordenActualizacionProducto.setString(1,nombre);
        ordenActualizacionProducto.setDouble (2,precio);
        ordenActualizacionProducto.setInt (3,ID_PR);
        ordenActualizacionProducto.execute();
    }

    private void actualizarIngrediente(int cantidad) throws SQLException{
        PreparedStatement ordenActualizacionIngrediente = conexion.prepareStatement("UPDATE NECESITA SET CANTIDAD = ? WHERE PR_ID = ? AND ING_ID = ?");
        ordenActualizacionIngrediente.setInt(1,cantidad);
        ordenActualizacionIngrediente.setInt(2,ID_PR);
        ordenActualizacionIngrediente.setInt(3,ID_ING);
        ordenActualizacionIngrediente.execute();
    }

    // METODOS RELACIONADOS CON LA ACCESIBILIDAD DE NODOS
    private void deshabilitarNodosEscritura(){
        nombreReceta.setDisable(true);
        precioReceta.setDisable(true);
        txtfldCantidadIngrediente.setDisable(true);
        btnModificar.setDisable(true);
        lblIngredientes.setText("(Ninguno seleccionado)");
    }

    private void habilitarNodosEscrituraNombrePrecio(){
        nombreReceta.setDisable(false);
        precioReceta.setDisable(false);
        btnModificar.setDisable(false);
    }

    private void deshabilitarNodoEscrituraIngredientes(){
        txtfldCantidadIngrediente.setDisable(true);
        txtfldCantidadIngrediente.setText("");
    }

    private void habilitarNodoEscrituraIngredientes(){
        txtfldCantidadIngrediente.setDisable(false);
    }

}