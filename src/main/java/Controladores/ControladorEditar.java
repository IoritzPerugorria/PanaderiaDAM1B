package Controladores;

import BBDD.ConexionBBDD;
import Modulo.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

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
    private ImageView imagenPerfil;
    @FXML
    private Label mensaje;

    private Usuario usuario;

    private Connection conexion;

    public void inicializar(Usuario usuario, Image laImagen){
        this.conexion = ConexionBBDD.conectar(conexion);
        this.usuario = usuario;
        this.nombre.setText(usuario.getNombre());
        this.rol.setText(usuario.getRol().toString());

        this.imagenPerfil = new ImageView(laImagen);
    }

    public void inicializar(Usuario usuario){
        this.conexion = ConexionBBDD.conectar(conexion);
        this.usuario = usuario;
        this.nombre.setText(usuario.getNombre());
        this.rol.setText(usuario.getRol().toString());


        Image imagen;
        try {
            imagen = new Image(Objects.requireNonNull(getClass().getResource("/imagenes/" + usuario.getFotoPerfil())).toString());
        } catch (NullPointerException n) {
            imagen = new Image(Objects.requireNonNull(getClass().getResource("/imagenes/missing.png")).toString());
        }

        imagenPerfil = new ImageView(imagen);
        System.out.println(imagenPerfil.getImage());
    }

    @FXML
    public void cambiarImagen() {
        System.out.println("bleh");
    }

    public void actualizar() {
        String resultado = "";

        try{
            PreparedStatement ps1 = conexion.prepareStatement("SELECT CONTRASENA FROM USUARIO WHERE USUARIO = ?");
            ps1.setString(1, usuario.getNombre());

            ResultSet rs = ps1.executeQuery();

            while (rs.next()){
                int respuesta = this.comprobarContrasena(rs.getString("CONTRASENA"));
                if (respuesta == 0){
                    PreparedStatement ps2 = conexion.prepareStatement("UPDATE USUARIO SET CONTRASENA = ? WHERE USUARIO = ?");

                    ps2.setString(1, contrasenaNueva.getText());
                    ps2.setString(2, usuario.getNombre());
                    ps2.executeUpdate();
                    resultado = "La contraseña se ha actualizado correctamente";
                }
                else if (respuesta == 1){
                    resultado = "La contraseña es incorrecta";
                }
                else if (respuesta == 2){
                    resultado = "Las contraseñas no coinciden";
                }
                else if (respuesta == 3){
                    resultado = "Rellene todos los campos de las contraseñas";
                }
            }

            if(!(nombre.getText().isBlank()) && !Objects.equals(nombre.getText(), usuario.getNombre())){
                PreparedStatement ps3 = conexion.prepareStatement("UPDATE USUARIO SET USUARIO = ? WHERE USUARIO = ?");
                ps3.setString(1, nombre.getText());
                ps3.setString(2, usuario.getNombre());
                ps3.executeUpdate();
                usuario.setNombre(nombre.getText());
                resultado = "Nombre de usuario actualizado";
            }

            mensaje.setText(resultado);
        }
        catch (SQLException e){
            mensaje.setText("No se han podido actualizar los datos. Intentelo de nuevo mas tarde.");
        }
    }

    public int comprobarContrasena(String contrasena){
        if (contrasenaActual.getText().isBlank() || contrasenaNueva.getText().isBlank() || contrasenaConfirmar.getText().isBlank()){
            return 3;
        }
        else{
            if (contrasena.equals(contrasenaActual.getText())){
                if (contrasenaConfirmar.getText().equals(contrasenaNueva.getText())){
                    return 0;
                }
                else{
                    return 2;
                }
            }
            else{
                return 1;
            }
        }
    }
}
