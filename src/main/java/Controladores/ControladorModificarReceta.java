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
    private TextField nombreReceta;
    @FXML
    private TextField precioReceta;
    @FXML
    private VBox ingredientesRecetas;
    private ArrayList<HBox>ingredientes;

    Connection conexion;



    public void modificarReceta(ActionEvent actionEvent) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //PARA MOSTRAR LAS RECETAS DE LA BASE DE DATOS
        try {
            conexion = ConexionBBDD.conectar(conexion);
            PreparedStatement recogidaNombresRecetas = conexion.prepareStatement("SELECT NOMBRE FROM PRODUCTOS");
            ResultSet resultadoNombres = recogidaNombresRecetas.executeQuery();
            ArrayList<String> listaNombresRecetas = new ArrayList<>();
            while (resultadoNombres.next()){
                listaNombresRecetas.add(resultadoNombres.getString("NOMBRE"));
            }
            for (int i = 0; i < listaNombresRecetas.size(); i++) {
                listaRecetas.getItems().add(listaNombresRecetas.get(i));
            }

        //PARA DETECTAR Y RESPONDER EN BASE A LOS OBJETOS SELECCIONADOS
            listaRecetas.setOnMouseClicked(event -> {
                // Obtener el artículo seleccionado
                Object selectedItem = listaRecetas.getSelectionModel().getSelectedItem();

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
                        PreparedStatement cantidadIngredientes = conexion.prepareStatement("SELECT * FROM INGREDIENTES");
                        ResultSet resultadoCantidad = datosRecogidosRecetas.executeQuery();
                        while (resultadoCantidad.next()){
                            // POR CADA INGREDIENTE, GENERA UNA SERIE DE NODOS QUE INDICAN EL NOMBRE Y LA CANTIDAD (STOCK) DE DICHO INGREDIENTE:
                            HBox ingrediente = new HBox();

                            Label ingredienteNombre = new Label();
                            ingredienteNombre.setText(resultadoCantidad.getString("NOMBRE"));

                            TextField ingredientePrecio = new TextField();
                            ingredientePrecio.setText(resultadoCantidad.getString("PRECIO"));

                            ingrediente.getChildren().add(ingredienteNombre);
                            ingrediente.getChildren().add(ingredientePrecio);
                        }

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
