package Controladores;

import Modulo.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ControladorEditar {
    @FXML
    private TextField nombre;
    @FXML
    private PasswordField contrasenaActual;
    @FXML
    private PasswordField contrasenaNueva;
    @FXML
    private PasswordField contrasenaConfirmar;
    @FXML
    private Label rol;
    @FXML
    private ImageView imagen;

    public void inicializar(Usuario usuario){
        this.nombre.setText(usuario.getNombre());
        this.rol.setText(usuario.getRol().toString());

        this.imagen = new ImageView();
    }

    public void inicializar(Usuario usuario, Image imagen){
        this.nombre.setText(usuario.getNombre());
        this.rol.setText(usuario.getRol().toString());

        this.imagen = new ImageView(imagen);
    }

    @FXML
    public void cambiarImagen(ActionEvent actionEvent) {
        System.out.println("bleh");
    }

    public void actualizar(ActionEvent actionEvent) {
        System.out.println("yipii");
    }
}
