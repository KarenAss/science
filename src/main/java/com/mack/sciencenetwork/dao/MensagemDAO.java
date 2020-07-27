package com.mack.sciencenetwork.dao;

import com.mack.sciencenetwork.domain.Pesquisador;
import com.mack.sciencenetwork.domain.Postagem;
import com.mack.sciencenetwork.domain.Usuario;
import com.mack.sciencenetwork.domain.UsuarioComum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.List;

@Component
public class MensagemDAO {
    public String DRIVER;
    public String URL;
    public String USER;
    public String PASSWORD;

    public final String sqlInsereMsg = "INSERT INTO postagem (dataHoraPostagem, idUsuarioAutor, qtdCurtidasPostagem, tipoPostagem,"+
            "conteudoMensagem) values(?,?,?,?,?)";
    public final String sqlAtualizaMensagem = "UPDATE postagem SET dataHoraPostagem =?, conteudoMensagem = ? WHERE idPostagem = ?";
    public final String sqlDeletaMensagem = "DELETE FROM postagem WHERE idPostagem = ?";
    public final String sqlProcuraMensagem = "SELECT idPostagem, qtdCurtidasPostagem, conteudoMensagem,dataHoraPostagem, idUsuarioAutor, tipoPostagem FROM postagem WHERE idUsuarioAutor =?";
    public final String sqlDeletaCurtidas = "DELETE FROM usuarioCurtePostagem WHERE idPostagem=?";

    private PreparedStatement stmInsereMsg;
    private PreparedStatement stmAtualizaMensagem;
    private PreparedStatement stmDeletaMensagem;
    private PreparedStatement stmProcuraMensagem;
    private PreparedStatement stmDeletaCurtidas;

    @Autowired
    public MensagemDAO(@Value("${spring.datasource.driver-class-name}") String DRIVER, @Value("${spring.datasource.url}") String URL, @Value("${spring.datasource.username}") String USER, @Value("${spring.datasource.password}") String PASSWORD){
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

            stmInsereMsg = con.prepareStatement(sqlInsereMsg, Statement.RETURN_GENERATED_KEYS);
            stmAtualizaMensagem = con.prepareStatement(sqlAtualizaMensagem, Statement.RETURN_GENERATED_KEYS);
            stmDeletaMensagem = con.prepareStatement(sqlDeletaMensagem, Statement.RETURN_GENERATED_KEYS);
            stmProcuraMensagem = con.prepareStatement(sqlProcuraMensagem, Statement.RETURN_GENERATED_KEYS);
            stmDeletaCurtidas = con.prepareStatement(sqlDeletaCurtidas, Statement.RETURN_GENERATED_KEYS);
        } catch(
        SQLException e) {
                e.printStackTrace();
                System.out.println("Falha ao preparar statement");
            }
    }

    public int insereMsg(Postagem postagem){
        int qtdInserts = 0;
        int chaveGerada = -1;
        try( Connection con = DriverManager.getConnection(URL, USER, PASSWORD)){
            java.sql.Date data = new java.sql.Date((postagem.getDataHoraPostagem().getTime()));
            stmInsereMsg.setDate(1, data);
            stmInsereMsg.setInt(2,postagem.getIdUsuarioAutor());
            stmInsereMsg.setInt(3,0);
            stmInsereMsg.setString(4,"Mensagem");
            stmInsereMsg.setString(5,postagem.getConteudoMensagem());

            qtdInserts = stmInsereMsg.executeUpdate();
            con.close();
            try (ResultSet generatedKeys = stmInsereMsg.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    chaveGerada = (int) generatedKeys.getLong(1);
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return chaveGerada;
    }

    public int atualizaMensagem(Postagem mensagem){
        int qtdUpdate = 0;
        try(Connection con = DriverManager.getConnection(URL, USER, PASSWORD)){

            java.sql.Date data = new java.sql.Date((mensagem.getDataHoraPostagem().getTime()));
            stmAtualizaMensagem.setDate(1, data);
            stmAtualizaMensagem.setString(2,mensagem.getConteudoMensagem());
            stmAtualizaMensagem.setInt(3,mensagem.getIdPostagem());

            qtdUpdate = stmAtualizaMensagem.executeUpdate();
            con.close();

        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return qtdUpdate;
    }


    public int deletaMensagem(Postagem mensagem){
        int qtdDelete =0;
        int qtd =0;
        try(Connection con = DriverManager.getConnection(URL, USER, PASSWORD)){

            stmDeletaCurtidas.setInt(1,mensagem.getIdPostagem());
            stmDeletaMensagem.setInt(1, mensagem.getIdPostagem());

            qtd = stmDeletaCurtidas.executeUpdate();
            qtdDelete = stmDeletaMensagem.executeUpdate();
            con.close();

        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return qtdDelete;
    }

    public Postagem mensagemMap(ResultSet rs) throws SQLException {
        Postagem mensagem = new Postagem();
        mensagem.setIdPostagem(rs.getInt("idPostagem"));
        mensagem.setDataHoraPostagem(rs.getDate("dataHoraPostagem"));
        mensagem.setIdUsuarioAutor(rs.getInt("idUsuarioAutor"));
        mensagem.setQtdCurtidasPostagem(rs.getInt("qtdCurtidasPostagem"));
        mensagem.setTipoPostagem(rs.getString("tipoPostagem"));
        mensagem.setConteudoMensagem(rs.getString("conteudoMensagem"));


        return mensagem;
    }

    public List<Postagem> findAll() {
        final String sql = "SELECT * FROM postagem";
        List<Postagem> mensagens = new java.util.ArrayList<>();
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {

                    Postagem mensagem = mensagemMap(rs);
                    if(mensagem.getTipoPostagem().equalsIgnoreCase("Mensagem")){
                        mensagens.add(mensagem);
                    }


            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return mensagens;
    }

    public List<Postagem>buscaMensagem(int id) {
        List<Postagem> mensagens = new java.util.ArrayList<>();

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            stmProcuraMensagem.setInt(1, id);
            try (ResultSet rs = stmProcuraMensagem.executeQuery()) {
                while (rs.next()) {
                    Postagem mensagem = mensagemMap(rs);
                    if(mensagem.getTipoPostagem().equalsIgnoreCase("Mensagem")){
                        mensagens.add(mensagem);
                    }
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados H2");
        }
        return mensagens;
    }
}
