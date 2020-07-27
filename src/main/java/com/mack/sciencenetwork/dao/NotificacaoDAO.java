package com.mack.sciencenetwork.dao;

import com.mack.sciencenetwork.domain.Notificacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

@Component
public class NotificacaoDAO {
    public String DRIVER;
    public String URL;
    public String USER;
    public String PASSWORD;

    public final String sqlInsereNotif = "INSERT INTO notificacao (usuarioSeguidorId, usuarioSeguidoId, tipoNotif, dataHoraNotif) VALUES (?, ?, ?, ?)";
    public final String sqlDeletaNotif = "DELETE FROM notificacao WHERE idNotif = ?";
    public final String sqlBuscaNotifUsuario = "SELECT n.idNotif, n.usuarioSeguidorId, n.usuarioSeguidoId, n.tipoNotif, n.dataHoraNotif, u.email, u.nome FROM notificacao n INNER JOIN usuario u ON n.usuarioSeguidoId = u.idUsuario WHERE usuarioSeguidorId = ?";

    private PreparedStatement stmInsereNotif;
    private PreparedStatement stmDeletaNotif;
    private PreparedStatement stmBuscaNotifUsuario;

    @Autowired
    public NotificacaoDAO(@Value("${spring.datasource.driver-class-name}") String DRIVER, @Value("${spring.datasource.url}") String URL, @Value("${spring.datasource.username}") String USER, @Value("${spring.datasource.password}") String PASSWORD){
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

            stmInsereNotif = con.prepareStatement(sqlInsereNotif, Statement.RETURN_GENERATED_KEYS);
            stmDeletaNotif = con.prepareStatement(sqlDeletaNotif, Statement.RETURN_GENERATED_KEYS);
            stmBuscaNotifUsuario = con.prepareStatement(sqlBuscaNotifUsuario, Statement.RETURN_GENERATED_KEYS);

        } catch(
                SQLException e) {
            e.printStackTrace();
            System.out.println("Falha ao preparar statement");
        }
    }

    public int insereNotif(Notificacao notificacao){
        int qtdInserts = 0;
        int chaveGerada = -1;

        try(Connection con = DriverManager.getConnection(URL, USER, PASSWORD)){
            stmInsereNotif.setInt(1,notificacao.getUsuarioSeguidorId());
            stmInsereNotif.setInt(2,notificacao.getUsuarioSeguidoId());
            stmInsereNotif.setString(3,notificacao.getTipoNotif());
            java.sql.Date data = new java.sql.Date((notificacao.getDataHoraNotif().getTime()));
            stmInsereNotif.setDate(4, data);

            qtdInserts = stmInsereNotif.executeUpdate();
            con.close();
            try (ResultSet generatedKeys = stmInsereNotif.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    chaveGerada = (int) generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating notification failed, no ID obtained.");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return chaveGerada;
    }

    public int deletaNotif(int idNotif){
        int qtdDelete =0;
        try(Connection con = DriverManager.getConnection(URL, USER, PASSWORD)){
            stmDeletaNotif.setInt(1,idNotif);
            qtdDelete = stmDeletaNotif.executeUpdate();
            con.close();
        } catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return qtdDelete;
    }

    private Notificacao notificacaoMap(ResultSet rs) throws SQLException {
        Notificacao notificacao = new Notificacao();
        notificacao.setIdNotif(rs.getInt("idNotif"));
        notificacao.setDataHoraNotif(rs.getDate("dataHoraNotif"));
        notificacao.setUsuarioSeguidorId(rs.getInt("usuarioSeguidorId"));
        notificacao.setUsuarioSeguidoId(rs.getInt("usuarioSeguidoId"));
        notificacao.setTipoNotif(rs.getString("tipoNotif"));
        notificacao.setUsuarioSeguidoEmail(rs.getString("email"));
        notificacao.setUsuarioSeguidoNome(rs.getString("nome"));

        return notificacao;
    }

    public List<Notificacao>buscaNotifUsuario(int usuarioSeguidorId) {
        List<Notificacao> notificacoes = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            stmBuscaNotifUsuario.setInt(1, usuarioSeguidorId);
            try (ResultSet rs = stmBuscaNotifUsuario.executeQuery()) {
                while (rs.next()) {
                    Notificacao mensagem = notificacaoMap(rs);
                    notificacoes.add(mensagem);
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados H2");
        }
        return notificacoes;
    }
}