package Controladores;

import BBDD.ConexionBBDD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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

    Connection conexion;



    public void modificarReceta(ActionEvent actionEvent) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Para mostrar las recetas de la Base de Datos
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

        //Para detectar y responder en base a los objetos seleccionados
            listaRecetas.setOnMouseClicked(event -> {
                // Obtener el artículo seleccionado
                Object selectedItem = listaRecetas.getSelectionModel().getSelectedItem();

                // Acceder a los valores del elemento seleccionado y visualizarlos
                if (selectedItem != null) {
                    // Assuming the selected item is of type String
                    String selectedValue = (String) selectedItem;

                    // Buscar dicho ingrediente:
                    try {
                        PreparedStatement datosRecogidosRecetas = conexion.prepareStatement("SELECT * FROM PRODUCTOS WHERE NOMBRE = ?");
                        datosRecogidosRecetas.setString(1, selectedValue);
                        ResultSet resultadoDatos = datosRecogidosRecetas.executeQuery();
                        resultadoDatos.next();
                        nombreReceta.setText(resultadoDatos.getString("NOMBRE"));
                        precioReceta.setText(resultadoDatos.getString("PRECIO"));

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Valor seleccionado: " + selectedValue);
                }
            });


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
