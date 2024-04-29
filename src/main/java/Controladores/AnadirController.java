package Controladores;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static BBDD.ConexionBBDD.conectar;

public class AnadirController {

    @FXML
    private ComboBox<String> cmbIng;
    @FXML
    private TextField cantIng;

    @FXML
    private ComboBox<String> cmbPrd;
    @FXML
    private TextField cantPrd;


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
        }
        catch(SQLException e){
            throw new IllegalStateException("Error al insertar Ingrediente");
        }
    }

    @FXML
    protected void anadirProducto(){
        Connection conexion = null;
        conexion = conectar(conexion);

        String nom = cmbPrd.getValue();
        int cant = Integer.parseInt(cantPrd.getText());

        try{
            PreparedStatement st = conexion.prepareStatement("UPDATE PRODUCTOS SET STOCK = STOCK + ? WHERE NOMBRE = ?");
            st.setInt(1, cant);
            st.setString(2, nom);
            st.executeUpdate();

            cmbPrd.setValue("");
            cantPrd.clear();
        }
        catch(SQLException e){
            throw new IllegalStateException("Error al insertar Ingrediente");
        }
    }

}
