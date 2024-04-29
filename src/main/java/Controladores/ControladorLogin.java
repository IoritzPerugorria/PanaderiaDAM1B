package Controladores;


import BBDD.ConexionBBDD;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ControladorLogin implements Initializable {
    @FXML
    private Text txtCredencialesIncorrectas;
    @FXML
    private Text txtUsrObligatorio;
    @FXML
    private Text txtCtrObligatorio;
    @FXML
    private TextField txtFldCredencial1;
    @FXML
    private PasswordField txtFldCredencial2;
    @FXML
    private Button btnLogin;


    public ControladorLogin(){
        txtFldCredencial1 = new TextField();
        txtFldCredencial2 = new PasswordField();
        txtCredencialesIncorrectas = new Text();
        txtUsrObligatorio = new Text();
        txtCtrObligatorio = new Text();
    }

    //Conexion con la Base de Datos de la Panadería
    public Connection conexionBBDD() throws SQLException {
        Connection conexion = null;
        conexion = ConexionBBDD.conectar(conexion);
        return conexion;
    }

    public void desconexionBBDD(Connection conexion) throws SQLException {
        conexion.close();
    }

    /**
     * Metodo que define si un usuario tiene acceso o no a la panaderia que devuelve true o false
     *
     * Dependiendo de los resultados, se mostraran unos mensajes de error u otros.
     *
     * El método crea una variable que consiste en un Array de Arrays donde cada Array se corresponde
     * con las credenciales de cada uno de los usuarios registrados (Nombre de usuario y contraseña).
     * Para crear dicho Array necesitamos acceder previamente a la BBDD.
     */

    public boolean accesoUsuario() throws SQLException {
        ArrayList<ArrayList<String>> listaDatosUsuario = new ArrayList<>();

        Connection conexion = this.conexionBBDD();
        Statement accion = conexion.createStatement();
        ResultSet resultado = accion.executeQuery("SELECT USUARIO,CONTRASENA FROM USUARIO");

        while (resultado.next()) {

        }

        if (txtFldCredencial1 == null || txtFldCredencial2 == null){

        }

        this.desconexionBBDD(conexion);
        return false;
    }



    private void indicarCampoObligatorio(Text texto){
        texto.setText("* Este campo es obligatorio");
    }

    private void indicarCredencialesIncorrectas(){
        this.txtCredencialesIncorrectas.setText("* Usuario o contraseña incorrectos");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
