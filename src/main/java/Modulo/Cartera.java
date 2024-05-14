package Modulo;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static BBDD.ConexionBBDD.conectar;

public class Cartera {

    private double monedero;
    private Rol rol;

    /*
    * Método que devuelve el dinero que tiene el usuario en su cartera
    * */
    public Double getMonedero(String usuario){
        Connection conexion = null;
        conexion = conectar(conexion);

        try{
            PreparedStatement st = conexion.prepareStatement("SELECT CARTERA FROM USUARIOS WHERE NOMBRE = ?");
            st.setString(1, usuario);
            ResultSet rs = st.executeQuery();
            monedero = rs.getDouble("CARTERA");
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
    public Rol getRol(String usuario){
        Connection conexion = null;
        conexion = conectar(conexion);

        try{
            PreparedStatement st = conexion.prepareStatement("SELECT ROL FROM USUARIOS WHERE NOMBRE = ?");
            st.setString(1, usuario);
            ResultSet rs = st.executeQuery();
            rol = Rol.valueOf(rs.getString("ROL"));
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
    public void compra(Double precio, String usuario, String cantidad){
        if(getRol(usuario).equals(Rol.CLIENTE)){
            Double cant = Double.parseDouble(cantidad);
            monedero = getMonedero(usuario) - precio * cant;
            Connection conexion = null;
            conexion = conectar(conexion);

            try{
                PreparedStatement st = conexion.prepareStatement("UPDATE USUARIOS SET CARTERA = ? WHERE NOMBRE = ? ");
                st.setDouble(1, monedero);
                st.setString(2, usuario);
                st.executeUpdate();

                try{
                    PreparedStatement st1 = conexion.prepareStatement("UPDATE USUARIOS SET CARTERA = CARTERA + ? WHERE ROL = ALMACENERO OR ROL = PANADERO");
                    precio = precio/2;
                    st.setDouble(1, precio);
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
        else if(getRol(usuario).equals(Rol.ALMACENERO) || getRol(usuario).equals(Rol.PANADERO)){
            monedero = monedero;
        }
    }

    public void compraStockIngredientes(Double precio, String usuario, String cantidad){
        if(getRol(usuario).equals(Rol.ALMACENERO)){
            Double cant = Double.parseDouble(cantidad);
            monedero = getMonedero(usuario) - precio * cant;
            Connection conexion = null;
            conexion = conectar(conexion);

            try{
                PreparedStatement st = conexion.prepareStatement("UPDATE USUARIOS SET CARTERA = ? WHERE NOMBRE = ? ");
                st.setDouble(1, monedero);
                st.setString(2, usuario);
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
