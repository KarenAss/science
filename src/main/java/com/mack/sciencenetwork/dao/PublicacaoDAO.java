package com.mack.sciencenetwork.dao;

import com.mack.sciencenetwork.domain.Postagem;
import com.mack.sciencenetwork.domain.TemaInteresse;
import com.mack.sciencenetwork.domain.UsuarioComum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.List;

@Component
public class PublicacaoDAO {
    public String DRIVER;
    public String URL;
    public String USER;
    public String PASSWORD;

    public final String sqlInserePub = "INSERT INTO postagem (dataHoraPostagem, idUsuarioAutor, qtdCurtidasPostagem, tipoPostagem,"+
            "tituloPubliCientifica,localPubliCientifica,anoPubliCientifica, resumoPubliCientifica, palavrasChavePubliCientifica,"+
            "urlPubliCientifica) values(?,?,?,?,?,?,?,?,?,?)";
    public final String sqlAtualizaPub = "UPDATE postagem SET dataHoraPostagem =?, tituloPubliCientifica = ?, "+
            "localPubliCientifica = ?, anoPubliCientifica =?, resumoPubliCientifica = ?, "+
            "palavrasChavePubliCientifica = ?, urlPubliCientifica = ? WHERE idPostagem = ?";
    public final String sqlDeletaPub = "DELETE FROM postagem WHERE idPostagem = ?";
    public final String sqlProcuraPublicacao = "SELECT idPostagem, dataHoraPostagem, idUsuarioAutor, tipoPostagem, qtdCurtidasPostagem,tituloPubliCientifica, localPubliCientifica, anoPubliCientifica, resumoPubliCientifica, PalavrasChavePubliCientifica, urlPubliCientifica FROM postagem WHERE idUsuarioAutor =?";
    public final String sqlDeletaCurtidas = "DELETE FROM usuarioCurtePostagem WHERE idPostagem=?";

    private PreparedStatement stmInserePub;
    private PreparedStatement stmAtualizaPub;
    private PreparedStatement stmDeletaPub;
    private PreparedStatement stmProcuraPublicacao;
    private PreparedStatement stmDeletaCurtidas;

    @Autowired
    public PublicacaoDAO(@Value("${spring.datasource.driver-class-name}") String DRIVER, @Value("${spring.datasource.url}") String URL, @Value("${spring.datasource.username}") String USER, @Value("${spring.datasource.password}") String PASSWORD){
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

            stmInserePub = con.prepareStatement(sqlInserePub, Statement.RETURN_GENERATED_KEYS);
            stmAtualizaPub = con.prepareStatement(sqlAtualizaPub, Statement.RETURN_GENERATED_KEYS);
            stmDeletaPub = con.prepareStatement(sqlDeletaPub, Statement.RETURN_GENERATED_KEYS);
            stmProcuraPublicacao = con.prepareStatement(sqlProcuraPublicacao, Statement.RETURN_GENERATED_KEYS);
            stmDeletaCurtidas = con.prepareStatement(sqlDeletaCurtidas, Statement.RETURN_GENERATED_KEYS);

        } catch(
                SQLException e) {
            e.printStackTrace();
            System.out.println("Falha ao preparar statement");
        }
    }
    public int inserePub(Postagem publicacao){
        int qtdInserts = 0;
        int chaveGerada = -1;
        try( Connection con = DriverManager.getConnection(URL, USER, PASSWORD)){
            java.sql.Date data = new java.sql.Date((publicacao.getDataHoraPostagem().getTime()));
            stmInserePub.setDate(1, data);
            stmInserePub.setInt(2,publicacao.getIdUsuarioAutor());
            stmInserePub.setInt(3,0);
            stmInserePub.setString(4,"Publicacao");
            stmInserePub.setString(5,publicacao.getTituloPubliCientifica());
            stmInserePub.setString(6, publicacao.getLocalPubliCientifica());
            java.sql.Date data1 = new java.sql.Date((publicacao.getAnoPubliCientifica().getTime()));
            stmInserePub.setDate(7, data1);
            stmInserePub.setString(8,publicacao.getResumoPubliCientifica());
            stmInserePub.setString(9,publicacao.getPalavrasChavePubliCientifica());
            stmInserePub.setString(10,publicacao.getUrlPubliCientifica());

            qtdInserts = stmInserePub.executeUpdate();
            con.close();
            try (ResultSet generatedKeys = stmInserePub.getGeneratedKeys()) {
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


    public int atualizaPublicacao(Postagem publicacao){
        int qtdUpdate = 0;
        try(Connection con = DriverManager.getConnection(URL, USER, PASSWORD)){

            java.sql.Date data = new java.sql.Date((publicacao.getDataHoraPostagem().getTime()));
            stmAtualizaPub.setDate(1, data);
            stmAtualizaPub.setString(2,publicacao.getTituloPubliCientifica());
            stmAtualizaPub.setString(3,publicacao.getLocalPubliCientifica());
            java.sql.Date data1 = new java.sql.Date((publicacao.getAnoPubliCientifica().getTime()));
            stmAtualizaPub.setDate(4, data1);
            stmAtualizaPub.setString(5,publicacao.getResumoPubliCientifica());
            stmAtualizaPub.setString(6,publicacao.getPalavrasChavePubliCientifica());
            stmAtualizaPub.setString(7,publicacao.getUrlPubliCientifica());
            stmAtualizaPub.setInt(8,publicacao.getIdPostagem());

            qtdUpdate = stmAtualizaPub.executeUpdate();
            con.close();

        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return qtdUpdate;
    }


    public int deletaMensagem(Postagem publicacao){
        int qtdDelete =0;
        int qtd =0;
        try(Connection con = DriverManager.getConnection(URL, USER, PASSWORD)){
            stmDeletaCurtidas.setInt(1,publicacao.getIdPostagem());
            stmDeletaPub.setInt(1, publicacao.getIdPostagem());


            qtd = stmDeletaCurtidas.executeUpdate();
            qtdDelete = stmDeletaPub.executeUpdate();
            con.close();

        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return qtdDelete;
    }


    public Postagem publicacaoMap(ResultSet rs) throws SQLException {
        Postagem publicacao = new Postagem();
        publicacao.setIdPostagem(rs.getInt("idPostagem"));
        publicacao.setDataHoraPostagem(rs.getDate("dataHoraPostagem"));
        publicacao.setIdUsuarioAutor(rs.getInt("idUsuarioAutor"));
        publicacao.setQtdCurtidasPostagem(rs.getInt("qtdCurtidasPostagem"));
        publicacao.setTipoPostagem(rs.getString("tipoPostagem"));
        publicacao.setTituloPubliCientifica(rs.getString("tituloPubliCientifica"));
        publicacao.setLocalPubliCientifica(rs.getString("localPubliCientifica"));
        publicacao.setAnoPubliCientifica(rs.getDate("anoPubliCientifica"));
        publicacao.setResumoPubliCientifica(rs.getString("resumoPubliCientifica"));
        publicacao.setPalavrasChavePubliCientifica(rs.getString("palavrasChavePubliCientifica"));
        publicacao.setUrlPubliCientifica(rs.getString("urlPubliCientifica"));

        return publicacao;
    }

    public List<Postagem> findAll() {
        final String sql = "SELECT * FROM postagem";
        List<Postagem> publicacoes = new java.util.ArrayList<>();
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {

                Postagem publicacao = publicacaoMap(rs);
                if(publicacao.getTipoPostagem().equalsIgnoreCase("Publicacao")) {
                    publicacoes.add(publicacao);
                }
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return publicacoes;
    }

    public List<Postagem>buscaPublicacao(int id) {
        List<Postagem> publicacoes = new java.util.ArrayList<>();

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            stmProcuraPublicacao.setInt(1, id);
            try (ResultSet rs = stmProcuraPublicacao.executeQuery()) {
                while (rs.next()) {
                    Postagem publicacao = publicacaoMap(rs);
                    if(publicacao.getTipoPostagem().equalsIgnoreCase("Publicacao")) {
                        publicacoes.add(publicacao);
                    }
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados H2");
        }
        return publicacoes;
    }
}
