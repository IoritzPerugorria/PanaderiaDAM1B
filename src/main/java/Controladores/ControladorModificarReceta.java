package Controladores;

import BBDD.ConexionBBDD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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
    private TextField TxtfldCantidadIngrediente;
    @FXML
    private VBox ingredientesRecetas;
    private ArrayList<HBox>ingredientes;
    Connection conexion;
    String nombre;



    public void modificarReceta(ActionEvent actionEvent) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

                    try {
                        PreparedStatement datosRecogidosRecetas = conexion.prepareStatement ( "SELECT STOCK FROM INGREDIENTE WHERE NOMBRE = ?" );
                        datosRecogidosRecetas.setString ( 1, valorIngredienteSeleccionado );
                        ResultSet resultadoDatosRecetas = datosRecogidosRecetas.executeQuery ( );
                        resultadoDatosRecetas.next ();
                        TxtfldCantidadIngrediente.setText ( resultadoDatosRecetas.getString ( "STOCK" ) );
                    } catch (SQLException ex) {
                        throw new RuntimeException ( ex );
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



        PreparedStatement recogidaNombresIngredientes = conexion.prepareStatement("SELECT I.NOMBRE,N.ING_ID,N.CANTIDAD FROM NECESITA AS N INNER JOIN INGREDIENTES AS I ON I.ID = N.ING_ID WHERE N.PR_ID = ?");
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

    /**
    private void mostrarObjetosPorPantalla(ListView<String> lista){
        //PARA DETECTAR Y RESPONDER EN BASE A LOS OBJETOS SELECCIONADOS
        lista.setOnMouseClicked(event -> {
            // Obtener el artículo seleccionado
            Object selectedItem = lista.getSelectionModel().getSelectedItem();

            // Acceder a los valores del elemento seleccionado y visualizarlos
            if (selectedItem != null) {
                // Asumiendo que el Item seleccionado es del tipo String
                String selectedValue = (String) selectedItem;

                // Buscar dicho ingrediente:
                try {
                    PreparedStatement datosRecogidosRecetas = conexion.prepareStatement("SELECT * FROM PRODUCTOS WHERE NOMBRE = ?");
                    datosRecogidosRecetas.setString(1, selectedValue);
                    ResultSet resultadoDatos = datosRecogidosRecetas.executeQuery();
                    resultadoDatos.next();
                    nombreReceta.setText(resultadoDatos.getString("NOMBRE"));
                    precioReceta.setText(resultadoDatos.getString("PRECIO"));

                    // PARA VISUALIZAR LOS INGREDIENTES QUE QUIERO MODIFICAR
                    PreparedStatement cantidadIngredientes = conexion.prepareStatement("SELECT NOMBRE,STOCK FROM INGREDIENTES");
                    ResultSet resultadoCantidad = datosRecogidosRecetas.executeQuery();

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    **/
}
