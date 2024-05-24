package Modulo;

import BBDD.ConexionBBDD;
import javafx.scene.image.ImageView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Usuario {

    private String usuario;
    private Rol rol;
    private String fotoPerfil;
    private double cartera;

    private Connection conexion;

    public Usuario(String usuario){
        this.usuario = usuario;
        this.cargarDatos();
    }

    public String getNombre() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getFotoPerfil(){
        return fotoPerfil;
    }


    public void cargarDatos(){

        try {
            conexion = ConexionBBDD.conectar(conexion);

            PreparedStatement ps = conexion.prepareStatement("SELECT * FROM USUARIO WHERE USUARIO = ?");
            ps.setString(1, this.getNombre());

            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                this.cartera = rs.getDouble("CARTERA");
                this.fotoPerfil = rs.getString("IMAGEN");
                this.rol = Rol.valueOf(rs.getString("ROL"));
            }
        }
        catch (SQLException e){
            System.out.println("Mal :(");
        }


    }

    public void setNombre(String text) {
        this.usuario = text;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }
}
