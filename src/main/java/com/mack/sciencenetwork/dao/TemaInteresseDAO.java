package com.mack.sciencenetwork.dao;

import com.mack.sciencenetwork.domain.Pesquisador;
import com.mack.sciencenetwork.domain.TemaInteresse;
import com.mack.sciencenetwork.domain.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class TemaInteresseDAO {
    public String DRIVER;
    public String URL;
    public String USER;
    public String PASSWORD;

    private final String sqlProcuraTemasInteresse = "select t.idTemaInteresse, t.nomeTemaInteresse , qtdUsuariosTemaInteresse from temaInteresse t inner join usuarioTemaInteresse ut on t.idTemaInteresse = ut.idTemaInteresse where ut.idUsuario = ?";
    private final String sqlInsereTemasUsuario = "insert into usuarioTemaInteresse(idUsuario, idTemaInteresse) VALUES(?, ?);";
    private final String sqlCalculaqtdPessoas = "select count(idUsuario) from usuarioTemaInteresse where idTemaInteresse = ?;";

    private PreparedStatement stmProcuraTemasInteresse;
    private PreparedStatement stmInsereTemasUsuario;
    private PreparedStatement stmDeleteTemas;
    private PreparedStatement stmSelectTemas;
    private PreparedStatement stmAtualizaTemas;
    private PreparedStatement stmCalculaqtdPessoas;

    @Autowired
    public TemaInteresseDAO(@Value("${spring.datasource.driver-class-name}") String DRIVER, @Value("${spring.datasource.url}") String URL, @Value("${spring.datasource.username}") String USER, @Value("${spring.datasource.password}") String PASSWORD){
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
            stmProcuraTemasInteresse = con.prepareStatement(sqlProcuraTemasInteresse);
            stmInsereTemasUsuario = con.prepareStatement(sqlInsereTemasUsuario);
            stmCalculaqtdPessoas = con.prepareStatement(sqlCalculaqtdPessoas);

        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Falha ao preparar statement");
        }
    }

    public List<TemaInteresse> findAll(){
        final String select = "SELECT * FROM temaInteresse";
        List<TemaInteresse> temas = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pst = con.prepareStatement(select);
             ResultSet rs = pst.executeQuery()) {
            while(rs.next()){
                TemaInteresse temaInteresse = temaInteresseMapFull(rs);
                int idTema = temaInteresse.getIdTemaInteresse();
                stmCalculaqtdPessoas.setInt(1, idTema);
                try (ResultSet rs2 = stmCalculaqtdPessoas.executeQuery()) {
                    while (rs2.next()) {
                        temaInteresse.setQtdUsuariosTemaInteresse(qtdPessoasMap(rs2));
                    }
                }

                temas.add(temaInteresse);
            }
            con.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return temas;

    }

    public int cadastrarTemaUsuario(int idUsuario, Usuario usuario){
        int qtdInserts = 0;
        try( Connection con = DriverManager.getConnection(URL, USER, PASSWORD)){

            for(int i = 0; i < usuario.getIdsTemas().size(); i++){
                stmInsereTemasUsuario.setInt(1, idUsuario);
                stmInsereTemasUsuario.setInt(2, usuario.getIdsTemas().get(i));
                qtdInserts += stmInsereTemasUsuario.executeUpdate();

            } con.close();
        }catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return qtdInserts;
    }

    public List<TemaInteresse>buscaTemas(int id) {
        List<TemaInteresse> temas = new java.util.ArrayList<>();

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            stmProcuraTemasInteresse.setInt(1, id);
            try (ResultSet rs = stmProcuraTemasInteresse.executeQuery()) {
                while (rs.next()) {
                    TemaInteresse temaInteresse = temaInteresseMap(rs);
                    int idTema = temaInteresse.getIdTemaInteresse();
                    stmCalculaqtdPessoas.setInt(1, idTema);
                    try (ResultSet rs2 = stmCalculaqtdPessoas.executeQuery()) {
                        while (rs2.next()) {
                            temaInteresse.setQtdUsuariosTemaInteresse(qtdPessoasMap(rs2));
                        }
                    }

                    temas.add(temaInteresse);
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados H2");
        }
        return temas;
    }

    public TemaInteresse temaInteresseMapFull( ResultSet rs) throws SQLException{
        TemaInteresse tema = new TemaInteresse();
        tema.setIdTemaInteresse(rs.getInt("idTemaInteresse"));
        tema.setNomeTemaInteresse(rs.getString("nomeTemaInteresse"));
        tema.setQtdUsuariosTemaInteresse(rs.getInt("qtdUsuariosTemaInteresse"));
        return tema;
    }


    public TemaInteresse temaInteresseMap( ResultSet rs) throws SQLException{
        TemaInteresse tema = new TemaInteresse();
        tema.setIdTemaInteresse(rs.getInt("idTemaInteresse"));
        return tema;
    }

    public int qtdPessoasMap(ResultSet rs ) throws  SQLException{
        return rs.getInt(1);
    }

    public int atualizaTemaUsuario(Usuario usuario){
        int qtdUpdate = 0;
        try( Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {

            stmDeleteTemas = con.prepareStatement("DELETE FROM usuarioTemaInteresse WHERE idUsuario = ?");
            stmAtualizaTemas = con.prepareStatement("insert into usuarioTemaInteresse(idUsuario, idTemaInteresse) VALUES(?, ?)");

            stmDeleteTemas.setInt(1,usuario.getId());
            stmDeleteTemas.executeUpdate();

            for (int i=0; i <usuario.getIdsTemas().size(); i++) {

                stmAtualizaTemas.setInt(1,usuario.getId());
                stmAtualizaTemas.setInt(2,usuario.getIdsTemas().get(i));
                stmAtualizaTemas.executeUpdate();
            }
            
            qtdUpdate = 1;
            con.close();
        }catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return qtdUpdate;
    }
}