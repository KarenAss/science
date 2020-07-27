package com.mack.sciencenetwork.dao;

import com.mack.sciencenetwork.domain.Curtida;
import com.mack.sciencenetwork.domain.Notificacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

@Component
public class CurtidaDAO {
    public String DRIVER;
    public String URL;
    public String USER;
    public String PASSWORD;

    public final String sqlBuscaCurtidasUsuario = "SELECT * FROM usuarioCurtePostagem where idUsuario = ?";

    private PreparedStatement stmBuscaCurtidasUsuario;

    @Autowired
    public CurtidaDAO(@Value("${spring.datasource.driver-class-name}") String DRIVER, @Value("${spring.datasource.url}") String URL, @Value("${spring.datasource.username}") String USER, @Value("${spring.datasource.password}") String PASSWORD){
        this.DRIVER = DRIVER;
        this.URL = URL;
        this.USER = USER;
        this.PASSWORD = PASSWORD;

        try{
            Class.forName(DRIVER);
        }catch(ClassNotFoundException e){
            e.printStackTrace();
            System.out.println("Driver do banco de dados não encontrado");
        }

        try{
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            stmBuscaCurtidasUsuario = con.prepareStatement(sqlBuscaCurtidasUsuario, Statement.RETURN_GENERATED_KEYS);
        } catch(
                SQLException e) {
            e.printStackTrace();
            System.out.println("Falha ao preparar statement");
        }
    }

    private Curtida curtidaMap(ResultSet rs) throws SQLException {
        Curtida curtida = new Curtida();
        curtida.setIdUsuario(rs.getInt("idUsuario"));
        curtida.setIdPostagem(rs.getInt("idPostagem"));
        return curtida;
    }

    public List<Curtida> buscaCurtidasUsuario(int idUsuarioBuscado){
        List<Curtida> curtidas = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            stmBuscaCurtidasUsuario.setInt(1, idUsuarioBuscado);
            try (ResultSet rs = stmBuscaCurtidasUsuario.executeQuery()) {
                while (rs.next()) {
                    Curtida curtida = curtidaMap(rs);
                    curtidas.add(curtida);
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados H2");
        }
        return curtidas;
    }
}
