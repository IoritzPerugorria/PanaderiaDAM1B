package Controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
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
import java.util.Objects;

import static BBDD.ConexionBBDD.conectar;
import static BBDD.ConexionBBDD.desconectar;

public class AnadirNuevoController {

    @FXML
    private TextField txtFldNom;
    @FXML
    private TextField txtFldCant;
    @FXML
    private TextField txtFldPrec;
    private String imagen;
    ControladorVP controladorVP;

    /**
     * Método que inserta en la BBDD
     * un ingrediente nuevo con los
     * datos introducidos en pantalla
     * */
    @FXML
    protected void anadirNuevo(){
        Connection conexion = null;
        conexion = conectar(conexion);
        if(txtFldNom.getText().isEmpty() || txtFldCant.getText().isEmpty() || txtFldPrec.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Todos los campos deben estar rellenados");
        }
        else{
            String nombre = txtFldNom.getText();
            int cant = Integer.parseInt(txtFldCant.getText());
            Double precio = Double.parseDouble(txtFldPrec.getText());


            try{
                PreparedStatement st = conexion.prepareStatement("INSERT INTO INGREDIENTES(NOMBRE, PRECIO, STOCK, IMAGEN) VALUES (?, ?, ?, ?)");
                st.setString(1, nombre);
                st.setDouble(2, precio);
                st.setInt(3, cant);

                if(imagen != null){  //si se ha seleccionado bien la imagen, intenta introducir su nombre de archivo a la base de datos
                    try{
                        st.setString(4, imagen);
                        st.executeUpdate();
                    }
                    catch(SQLException e){
                        st.setString(4, null);  //si no consigue introducirla, ese atributo se vuelve null
                        st.executeUpdate();
                        throw new IllegalStateException("Error al insertar imagen");
                    }
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Ingrediente insertado correctamente");
                alert.showAndWait();
                controladorVP.cargar();
                conexion = desconectar(conexion);
            }
            catch(SQLException e){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Error al insertar ingrediente");
                alert.showAndWait();
                throw new IllegalStateException("Error al insertar ingrediente");
            }
        }

    }

    /**
     * Método que muestra el explorador
     * permitiendo seleccionar una imagen
     * y una vez seleccionada, la copia a
     * la carpeta imagenes del proyecto
     * */
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
            Path testFile = Path.of(archivo.getPath());
            File dest = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\imagenes\\" + imagen);
            Path pathdest = Path.of(dest.getPath());
            if(Path.of(archivo.getPath()) != Path.of(dest.getPath())){
                testFile = Files.copy(testFile, pathdest);
            }
        }
    }

    public void setMainController(ControladorVP controller){
        this.controladorVP = controller;
    }


}
