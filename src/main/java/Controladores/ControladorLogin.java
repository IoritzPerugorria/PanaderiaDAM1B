package Controladores;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ControladorLogin {
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

    public void acceso(){
        if (txtFldCredencial1 == null || txtFldCredencial2 == null){

        }
    }

    private void indicarCampoObligatorio(Text texto){
        texto.setText("* Este campo es obligatorio");
    }



}
