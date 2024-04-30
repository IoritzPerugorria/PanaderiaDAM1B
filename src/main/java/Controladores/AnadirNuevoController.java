package Controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private String imagen;

    @FXML
    protected void anadirNuevo(){
        Connection conexion = null;
        conexion = conectar(conexion);

        String nombre = txtFldNom.getText();
        int cant = Integer.parseInt(txtFldCant.getText());
        Double precio = Double.parseDouble(txtFldPrec.getText());

        try{
            PreparedStatement st = conexion.prepareStatement("INSERT INTO INGREDIENTES(NOMBRE, PRECIO, STOCK) VALUES (?, ?, ?)");
            st.setString(1, nombre);
            st.setDouble(2, precio);
            st.setInt(3, cant);
            st.executeUpdate();

            if(imagen != null){
                try{
                    PreparedStatement st1 = conexion.prepareStatement("UPDATE INGREDIENTES SET IMAGEN = ? WHERE NOMBRE = ?");
                    st1.setString(1, imagen);
                    st1.setString(2, txtFldNom.getText());
                    st1.executeUpdate();
                }
                catch(SQLException e){
                    throw new IllegalStateException("Error al insertar imagen");
                }
            }
        }
        catch(SQLException e){
            throw new IllegalStateException("Error al insertar Ingrediente");
        }
    }

    @FXML
    protected void cargarImagen(ActionEvent event) throws IOException {
        FileChooser cargador = new FileChooser();
        cargador.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos png", "*.png")
        );

        Node node = (Node) event.getSource();
        File archivo = cargador.showOpenDialog(node.getScene().getWindow());


        if(archivo != null){
            imagen = archivo.getName();
            Path testFile = Files.createFile(Paths.get(archivo.getPath()));
            testFile = Files.move(testFile, Paths.get("C:\\Users\\AlumTA\\Desktop\\PROG\\Proyectos intelliJ\\PanaderiaDAM1B\\src\\main\resources\\imagenes\\" + imagen));
        }

    }


}
