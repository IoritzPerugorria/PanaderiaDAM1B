package Controladores;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static BBDD.ConexionBBDD.conectar;

public class AnadirNuevoController {

    @FXML
    private TextField txtFldNom;
    @FXML
    private TextField txtFldCant;
    @FXML
    private TextField txtFldPrec;

    @FXML
    protected void anadirNuevo(){
        Connection conexion = null;
        conexion = conectar(conexion);

        String nombre = txtFldNom.getText();
        int cant = Integer.parseInt(txtFldCant.getText());
        int precio = Integer.parseInt(txtFldPrec.getText());

        try{
            PreparedStatement st = conexion.prepareStatement("INSERT INTO INGREDIENTES(NOMBRE, PRECIO, STOCK) VALUES (?, ?, ?)");
            st.setString(1, nombre);
            st.setInt(2, precio);
            st.setInt(3, cant);
            st.executeUpdate();
        }
        catch(SQLException e){
            throw new IllegalStateException("Error al insertar Ingrediente");
        }
    }
}
