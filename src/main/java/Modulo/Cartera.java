package Modulo;

import Controladores.ControladorLogin;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static BBDD.ConexionBBDD.conectar;

public class Cartera {

    private Double monedero;
    private Rol rol;

    /*
    * Método que devuelve el dinero que tiene el usuario en su cartera
    * */
    public Double getMonedero(Usuario usuario){
        Connection conexion = null;
        conexion = conectar(conexion);

        try{
            PreparedStatement st = conexion.prepareStatement("SELECT CARTERA FROM USUARIO WHERE USUARIO = ?");
            st.setString(1, usuario.getUsuario());
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                monedero = rs.getDouble("CARTERA");
            }

            return monedero;

        }
        catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Error al acceder a la cartera");
            alert.showAndWait();
            throw new IllegalStateException("Error al acceder a la cartera");
        }
    }


    /*
    * Método que devuelve el rol del usuario: Cliente, Alamacenero, o Panadero
    * */
    public Rol getRol(Usuario usuario){
        Connection conexion = null;
        conexion = conectar(conexion);

        try{
            PreparedStatement st = conexion.prepareStatement("SELECT ROL FROM USUARIO WHERE USUARIO = ?");
            st.setString(1, usuario.getUsuario());
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                rol = Rol.valueOf(rs.getString("ROL"));
            }
            return rol;

        }
        catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Error al acceder al rol del usuario");
            alert.showAndWait();
            throw new IllegalStateException("Error al rol del usuario");
        }
    }


    /*
    * Método que resta el precio de la compra de la cartera del
    * usuario si es cliente y lo suma al del panadero y el almacenero.
    * Si es cualquiera de los otros dos roles la cartera se mantiene igual
    * */
    public void compra(Double precio, Usuario usuario, String cantidad){
        if(usuario.getRol().equals(Rol.CLIENTE)){
            Double cant = Double.parseDouble(cantidad);
            monedero = getMonedero(usuario) - precio * cant;
            Connection conexion = null;
            conexion = conectar(conexion);

            try{
                PreparedStatement st = conexion.prepareStatement("UPDATE USUARIO SET CARTERA = ? WHERE USUARIO = ? ");
                st.setDouble(1, monedero);
                st.setString(2, usuario.getUsuario());
                st.executeUpdate();

                try{
                    PreparedStatement st1 = conexion.prepareStatement("UPDATE USUARIO SET CARTERA = CARTERA + ? WHERE ROL = ? OR ROL = ?");
                    precio = precio/2;
                    st1.setDouble(1, precio);
                    st1.setString(2, "ALMACENERO");
                    st1.setString(3, "PANADERO");
                    st1.executeUpdate();

                }
                catch(SQLException e){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Error al sumar el precio a la cartera de los trabajadores");
                    alert.showAndWait();
                    throw new IllegalStateException("Error al sumar el precio a la cartera de los trabajadores");
                }

            }
            catch(SQLException e){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Error al restar el precio de la cartera del cliente");
                alert.showAndWait();
                throw new IllegalStateException("Error al restar el precio de la cartera del cliente");
            }
        }
        else if(getRol(usuario).equals(Rol.ALMACENERO) || getRol(usuario).equals(Rol.PANADERO) || getRol(usuario).equals(Rol.ADMINISTRADOR)){
            monedero = monedero;
        }
    }

    public void compraStockIngredientes(Double precio, Usuario usuario, String cantidad){
        if(usuario.getRol().equals(Rol.ALMACENERO) || getRol(usuario).equals(Rol.ADMINISTRADOR)){
            Double cant = Double.parseDouble(cantidad);
            monedero = getMonedero(usuario) - precio * cant;
            Connection conexion = null;
            conexion = conectar(conexion);

            try{
                PreparedStatement st = conexion.prepareStatement("UPDATE USUARIO SET CARTERA = ? WHERE USUARIO = ? ");
                st.setDouble(1, monedero);
                st.setString(2, usuario.getUsuario());
                st.executeUpdate();
                System.out.println("Stock comprado");

            }
            catch(SQLException e){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Error al restar el precio de la cartera del almacenero");
                alert.showAndWait();
                throw new IllegalStateException("Error al restar el precio de la cartera del almacenero");
            }
        }
        else{
            monedero = monedero;
        }
    }


}
