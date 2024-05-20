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
        try {
            conexion = ConexionBBDD.conectar(conexion);
            PreparedStatement recogidaNombresRecetas = conexion.prepareStatement("SELECT NOMBRE FROM PRODUCTOS");
            ResultSet resultado = recogidaNombresRecetas.executeQuery();
            ArrayList<String> listaNombresRecetas = new ArrayList<>();
            while (resultado.next()){
                listaNombresRecetas.add(resultado.getString("NOMBRE"));
            }
            for (int i = 0; i < listaNombresRecetas.size(); i++) {
                listaRecetas.getItems().add(listaNombresRecetas.get(i));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
