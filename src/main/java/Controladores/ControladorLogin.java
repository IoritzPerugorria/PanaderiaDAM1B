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
    private Text txtCredsIncorrectas;
    @FXML
    private Text txtCred1Obligatorio;
    @FXML
    private Text txtCred2Obligatorio;
    @FXML
    private TextField txtFldCredencial1;
    @FXML
    private PasswordField txtFldCredencial2;
    @FXML
    private Button btnLogin;


    public ControladorLogin(){
        txtFldCredencial1 = new TextField();
        txtFldCredencial2 = new PasswordField();
        txtCredsIncorrectas = new Text("* Usuario o contraseña incorrectos");
        txtCred1Obligatorio = new Text("* Este campo es obligatorio");
        txtCred2Obligatorio = new Text("* Este campo es obligatorio");

        txtCredsIncorrectas.setVisible(false);
        txtCred1Obligatorio.setVisible(false);
        txtCred2Obligatorio.setVisible(false);

    }

    //Conexion con la Base de Datos de la Panadería
    public Connection conexionBBDD() throws SQLException {
        Connection conexion = null;
        conexion = ConexionBBDD.conectar(conexion);
        return conexion;
    }

    /**
     * Metodo que define si un usuario tiene acceso o no a la panaderia que devuelve true o false
     * <p>
     * Dependiendo de los resultados, se mostraran unos mensajes de error u otros.
     *
     * El método crea una variable que consiste en un Array de Arrays donde cada Array se corresponde
     * con las credenciales de cada uno de los usuarios registrados (Nombre de usuario y contraseña).
     * Para crear dicho Array necesitamos acceder previamente a la BBDD.
     */

    public boolean accesoUsuario() throws SQLException {
        //ArrayList de las credenciales de cada uno de los usuarios registrados en la BBDD
        ArrayList<ArrayList<String>> listaDatosUsuario = new ArrayList<>();

        Connection conexion = this.conexionBBDD();
        Statement accion = conexion.createStatement();
        ResultSet resultado = accion.executeQuery("SELECT USUARIO,CONTRASENA FROM USUARIO");

        while (resultado.next()) {
            ArrayList<String> datosUsuario = new ArrayList<>();
            datosUsuario.add(resultado.getString("USUARIO"));
            datosUsuario.add(resultado.getString("CONTRASENA"));
            listaDatosUsuario.add(datosUsuario);
        }

        //ArrayList de las credenciales introducidas por el usuario.
        ArrayList<String> credencialesIntroducidas = new ArrayList<>();
        credencialesIntroducidas.add(txtFldCredencial1.getText());
        credencialesIntroducidas.add(txtFldCredencial2.getText());

        //Se comprueba si las credenciales son correctas
        boolean acceso = this.revisionDeLogin(credencialesIntroducidas, listaDatosUsuario);


        conexion.close();
        return acceso;
    }


    /**
     * Comprobar si las credenciales introducidas e implementadas en la ArrayList "credenciales" coinciden con alguna de las credenciales de los usuarios del ArrayList "listaDatosUsuario"
     * @return boolean
     */
    private boolean revisionDeLogin(ArrayList<String> credencialesIntroducidas, ArrayList<ArrayList<String>> listaDatosUsuario) {

        //Si alguno de las credenciales introducidas estan vacias:
        if (txtFldCredencial1 == null || txtFldCredencial2 == null){
            if (txtFldCredencial1 == null){
                this.indicarCampoObligatorio(txtCred1Obligatorio);
            }
            if (txtFldCredencial2 == null){
                this.indicarCampoObligatorio(txtCred2Obligatorio);
            }
            this.vaciarCampos();
            return false;
        }
        else {
            //Si las credenciales introducidas son correctas (si estan en la BD ya registradas):
            if (listaDatosUsuario.contains(credencialesIntroducidas)){
                return true;
            }
            //En caso contrario:
            else {
                this.indicarCredencialesIncorrectas();
                this.vaciarCampos();
                return false;
            }
        }



    }

    private void indicarCampoObligatorio(Text texto){
        txtCredsIncorrectas.setVisible(false);
        texto.setVisible(true);
    }

    private void indicarCredencialesIncorrectas(){
        txtCred1Obligatorio.setVisible(false);
        txtCred2Obligatorio.setVisible(false);
        txtCredsIncorrectas.setVisible(true);
    }

    private void vaciarCampos(){
        txtFldCredencial1.setText("");
        txtFldCredencial2.setText("");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
