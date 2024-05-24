package Controladores;

import BBDD.ConexionBBDD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ControladorModificarReceta implements Initializable {

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
    private int ID_ING;
    private int ID_PR;
    Connection conexion;

    public ControladorModificarReceta(){
        nombreReceta = new TextField();
        precioReceta = new TextField();
        txtfldCantidadIngrediente = new TextField();

    }


    public void modificarReceta(ActionEvent actionEvent) {

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
                        PreparedStatement ordenIdentificarIngrediente = conexion.prepareStatement("SELECT ID FROM INGREDIENTES WHERE NOMBRE = ?");
                        ordenIdentificarIngrediente.setString ( 1, valorIngredienteSeleccionado );
                        ResultSet resultadoDatos = ordenIdentificarIngrediente.executeQuery ( );
                        resultadoDatos.next ( );
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

    // METODOS RELACIONADOS CON LA ACCESIBILIDAD DE NODOS
    private void deshabilitarNodosEscritura(){
        nombreReceta.setDisable(true);
        precioReceta.setDisable(true);
        txtfldCantidadIngrediente.setDisable(true);
    }

    private void habilitarNodosEscrituraNombrePrecio(){
        nombreReceta.setDisable(false);
        precioReceta.setDisable(false);
    }

    private void deshabilitarNodoEscrituraIngredientes(){
        txtfldCantidadIngrediente.setDisable(true);
        txtfldCantidadIngrediente.setText("");
    }

    private void habilitarNodoEscrituraIngredientes(){
        txtfldCantidadIngrediente.setDisable(false);
    }


}
