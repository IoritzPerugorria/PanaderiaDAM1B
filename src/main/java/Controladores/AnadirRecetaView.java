package Controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static BBDD.ConexionBBDD.conectar;

public class AnadirRecetaView {

    @FXML
    private TextField txtFldNom;
    @FXML
    private TextField txtFldPrecio;
    @FXML
    private TextField txtFldCant;
    @FXML
    private ComboBox<String> cmbBoxIng;
    @FXML
    private ListView lstViewIng;
    private HashMap<String, Integer> ingredientes;
    private String imagen;

    public void cargarComboBox(){
        Connection conexion = null;
        conexion = conectar(conexion);

        try{
            PreparedStatement st = conexion.prepareStatement("SELECT NOMBRE FROM INGREDIENTES");
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                cmbBoxIng.setValue(rs.getString("NOMBRE"));
            }
        }
        catch(SQLException e){
            throw new IllegalStateException("No se ha podido cargar el combobox");
        }

    }

    public void insertarReceta(){

    }

    public void anadirIngredientes(){
        String ingre = cmbBoxIng.getValue();
        Integer cant = Integer.parseInt(txtFldCant.getText());
        ingredientes.put(ingre, cant);
        List<String> lista = new ArrayList<>();
        lista.add(ingre);
        ObservableList<String> listaingredientes = FXCollections.observableArrayList(lista);
        lstViewIng.setItems(listaingredientes);
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
            testFile = Files.move(testFile, Paths.get("C:\\Users\\AlumTA\\Desktop\\PROG\\Proyectos intelliJ\\PanaderiaDAM1B\\src\\main\\resources\\imagenes\\" + imagen));
        }
    }

}
