package Controladores;


import BBDD.ConexionBBDD;
import Modulo.Rol;
import Modulo.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.panaderiadam1b.Main;


import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
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
    private Rol rol;

    private static Usuario usuario;


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



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void login(javafx.event.ActionEvent actionEvent) throws SQLException {

        ArrayList<String> credencialesIntroducidas = new ArrayList<>();
        credencialesIntroducidas.add(txtFldCredencial1.getText());
        credencialesIntroducidas.add(txtFldCredencial2.getText());

        Connection conexion = null;
        conexion = ConexionBBDD.conectar(conexion);
        Statement accion = conexion.createStatement();
        ArrayList<ArrayList<String>> listaDatosUsuarios = new ArrayList<>();
        ResultSet resultado = accion.executeQuery("SELECT USUARIO,CONTRASENA,ROL FROM USUARIO");
        while (resultado.next()) {
            ArrayList<String> datosUsuario = new ArrayList<>();
            datosUsuario.add(resultado.getString("USUARIO"));
            datosUsuario.add(resultado.getString("CONTRASENA"));
            datosUsuario.add(resultado.getString("ROL"));
            listaDatosUsuarios.add(datosUsuario);
        }

        try{
            int revision = this.revisionDeLogin(credencialesIntroducidas, listaDatosUsuarios);

            if(revision != -1){
                FXMLLoader fxmlLoader;
                Scene scene;
                Node node = (Node) actionEvent.getSource();
                Stage currentStage = (Stage) node.getScene().getWindow();
                Stage stage = new Stage();

                String rolUsuario = listaDatosUsuarios.get(revision).get(2);
                rol = Rol.valueOf(rolUsuario);

                switch (rol){
                    case CLIENTE:
                        System.out.println("Iniciado sesion como CLIENTE");
                        usuario = new Usuario(txtFldCredencial1.getText(), rol);

                        fxmlLoader = new FXMLLoader(Main.class.getResource("vista_principal.fxml"));
                        scene = new Scene(fxmlLoader.load(), 1000, 1000);
                        stage.setTitle("Panaderia");
                        stage.setScene(scene);
                        break;

                    case ALMACENERO:
                        System.out.println("Iniciado sesion como ALMACENERO");
                        usuario = new Usuario(txtFldCredencial1.getText(), rol);

                        fxmlLoader = new FXMLLoader(Main.class.getResource("vista_principal.fxml"));
                        scene = new Scene(fxmlLoader.load(), 1000, 1000);
                        stage.setTitle("Panaderia");
                        stage.setScene(scene);
                        break;

                    case PANADERO:
                        System.out.println("Iniciado sesion como PANADERO");
                        usuario = new Usuario(txtFldCredencial1.getText(), rol);

                        fxmlLoader = new FXMLLoader(Main.class.getResource("vista_principal.fxml"));
                        scene = new Scene(fxmlLoader.load(), 1000, 1000);
                        stage.setTitle("Panaderia");
                        stage.setScene(scene);
                        break;
                }

                currentStage.close();
                stage.show();
            }
        }
        catch (IOException e){
            System.out.println("ERROR");
        }

    }

    /**
     * Comprobar si las credenciales introducidas e implementadas en la ArrayList "credenciales" coinciden con alguna de las credenciales de los usuarios del ArrayList "listaDatosUsuario"
     * @return boolean
     */
    private int revisionDeLogin(ArrayList<String> credencialesIntroducidas, ArrayList<ArrayList<String>> listaDatosUsuarios) {
        this.ocultarAvisos();
        //Si alguno de las credenciales introducidas estan vacias:
        if (credencialesIntroducidas.get(0).isBlank() || credencialesIntroducidas.get(1).isBlank()){

            if (credencialesIntroducidas.get(0).isBlank()){
                this.indicarCampoObligatorio(txtCred1Obligatorio);
            }

            if (credencialesIntroducidas.get(1).isBlank()){
                this.indicarCampoObligatorio(txtCred2Obligatorio);
            }

            return -1;
        }
        else {
            //Si las  introducidas son correctas (si estan en la BD ya registradas):
                if (this.encontrarUsuario(listaDatosUsuarios,credencialesIntroducidas) != -1){
                    return this.encontrarUsuario(listaDatosUsuarios,credencialesIntroducidas);
                }
            //En caso contrario:
            this.indicarCredencialesIncorrectas();
            this.vaciarCampos();
            return -1;
        }

    }

    private int encontrarUsuario(ArrayList<ArrayList<String>> listaDatosUsuario, ArrayList<String> credencialesIntroducidas){
        int contador = 0;
        while (contador < listaDatosUsuario.size()){
            if (listaDatosUsuario.get(contador).subList(0,2).equals(credencialesIntroducidas)){
                return contador;
            }
            contador++;
        }
        return -1;
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


    private void indicarCampoObligatorio(Text texto){
        texto.setVisible(true);
    }

    private void indicarCredencialesIncorrectas(){
        txtCredsIncorrectas.setVisible(true);
    }

    private void vaciarCampos(){
        txtFldCredencial1.setText("");
        txtFldCredencial2.setText("");
    }

    private void ocultarAvisos(){
        txtCred1Obligatorio.setVisible(false);
        txtCred2Obligatorio.setVisible(false);
        txtCredsIncorrectas.setVisible(false);
    }

    public static Usuario getUsuario() {
        return usuario;
    }

}
