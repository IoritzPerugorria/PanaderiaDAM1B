package Controladores;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
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
import java.util.Map;

import static BBDD.ConexionBBDD.conectar;
import static BBDD.ConexionBBDD.desconectar;

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
    private List<String> listaElegidos;
    ControladorVP controladorVP;

    public AnadirRecetaView(){
        listaElegidos = new ArrayList<>();
        ingredientes = new HashMap<>();
    }

    public void cargarComboBox(){
        Connection conexion = null;
        conexion = conectar(conexion);

        try{
            PreparedStatement st = conexion.prepareStatement("SELECT NOMBRE FROM INGREDIENTES");
            ResultSet rs = st.executeQuery();
            List<String> lista = new ArrayList<>();
            while(rs.next()){
                lista.add(rs.getString("NOMBRE"));

            }
            ObservableList<String> listaingredientes = FXCollections.observableArrayList(lista);
            cmbBoxIng.setItems(listaingredientes);
            conexion = desconectar(conexion);
        }
        catch(SQLException e){
            throw new IllegalStateException("No se ha podido cargar el combobox");
        }

    }

    @FXML
    public void insertarReceta(){
        String nombre = txtFldNom.getText();
        Double precio = Double.parseDouble(txtFldPrecio.getText());

        Connection conexion = null;
        conexion = conectar(conexion);

        try{
            PreparedStatement st = conexion.prepareStatement("INSERT INTO PRODUCTOS(NOMBRE, PRECIO, STOCK, IMAGEN) VALUES (?, ?, ?, ?)");
            st.setString(1, nombre);
            st.setDouble(2, precio);
            st.setInt(3, 0);
            if(imagen != null){
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

            for (Map.Entry<String, Integer> entry : ingredientes.entrySet()) {
                PreparedStatement st1 = conexion.prepareStatement("SELECT ID FROM INGREDIENTES WHERE NOMBRE = ?");
                st1.setString(1, entry.getKey());
                ResultSet rs1 = st1.executeQuery();
                String idIngrediente = null;
                if(rs1.next()){
                    idIngrediente = rs1.getString("ID");
                }
                PreparedStatement st2 = conexion.prepareStatement("SELECT ID FROM PRODUCTOS WHERE NOMBRE = ?");
                st2.setString(1, nombre);
                ResultSet rs2 = st2.executeQuery();
                String idProducto = null;
                if(rs2.next()){
                    idProducto = rs2.getString("ID");
                }

                PreparedStatement st3 = conexion.prepareStatement("INSERT INTO NECESITA(PR_ID, ING_ID, CANTIDAD) VALUES (?, ? ,?)");
                st3.setString(1, idProducto);
                st3.setString(2, idIngrediente);
                st3.setInt(3, entry.getValue());
                st3.executeUpdate();

            }
            conexion = desconectar(conexion);
        }
        catch(SQLException e){
            throw new IllegalStateException("No se ha podido insertar la receta");
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Receta introducida correctamente");
        alert.showAndWait();
        controladorVP.cargar();
    }


    public void anadirIngredientes(){
        String ingre = cmbBoxIng.getValue();
        Integer cant = Integer.parseInt(txtFldCant.getText());

        ingredientes.put(ingre, cant);

        listaElegidos.add(ingre);
        ObservableList<String> listaingredientes = FXCollections.observableArrayList(listaElegidos);
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
