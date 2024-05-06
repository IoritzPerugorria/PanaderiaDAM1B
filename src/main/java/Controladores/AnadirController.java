package Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static Modulo.ConexionBBDD.conectar;

public class AnadirController {

    @FXML
    private ComboBox<String> cmbIng;
    @FXML
    private TextField cantIng;

//    @FXML
//    private ComboBox<String> cmbPrd;
//    @FXML
//    private TextField cantPrd;


    protected void itemsCombo(){
        Connection conexion = null;
        conexion = conectar(conexion);

        try{
            PreparedStatement st = conexion.prepareStatement("SELECT NOMBRE FROM INGREDIENTES");
            ResultSet rs = st.executeQuery();

            List<String> lista = new ArrayList<>();
            while(rs.next()){
                lista.add(rs.getString("NOMBRE"));
            }
            ObservableList<String> combo = FXCollections.observableArrayList(lista);

            for(String cmb: combo){
                cmbIng.getItems().add(cmb);
            }

        }
        catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Error al crear el combobox");
            alert.showAndWait();
            throw new IllegalStateException("Error al crear el combobox");
        }
    }

    @FXML
    protected void anadirIngrediente(){
        Connection conexion = null;
        conexion = conectar(conexion);

        String nom = cmbIng.getValue();
        int cant = Integer.parseInt(cantIng.getText());

        try{
            PreparedStatement st = conexion.prepareStatement("UPDATE INGREDIENTES SET STOCK = STOCK + ? WHERE NOMBRE = ? ");
            st.setInt(1, cant);
            st.setString(2, nom);
            st.executeUpdate();

            cmbIng.setValue("");
            cantIng.clear();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Se ha añadido al stock del ingrediente correctamente");
            alert.showAndWait();
        }
        catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Error al añadir al stock");
            alert.showAndWait();
            throw new IllegalStateException("Error al insertar Ingrediente");
        }
    }

//    @FXML
//    protected void anadirProducto(){
//        Connection conexion = null;
//        conexion = conectar(conexion);
//
//        String nom = cmbPrd.getValue();
//        int cant = Integer.parseInt(cantPrd.getText());
//
//        try{
//            PreparedStatement st = conexion.prepareStatement("UPDATE PRODUCTOS SET STOCK = STOCK + ? WHERE NOMBRE = ?");
//            st.setInt(1, cant);
//            st.setString(2, nom);
//            st.executeUpdate();
//
//            cmbPrd.setValue("");
//            cantPrd.clear();
//        }
//        catch(SQLException e){
//            throw new IllegalStateException("Error al insertar Ingrediente");
//        }
//    }

}
