package com.mack.sciencenetwork.dao;

import com.mack.sciencenetwork.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class UsuarioDAO {
    public String DRIVER;
    public String URL;
    public String USER;
    public String PASSWORD;

    @Autowired
    TemaInteresseDAO temaDao;
    @Autowired
    PublicacaoDAO publicacaoDao;
    @Autowired
    MensagemDAO mensagemDAO;

    private final String sqlCadastrarPesquisador ="insert into usuario(email, senha, localFormacao,dataNascimento, grauEscolaridade, totalSeguidores, totalSeguido, tipoUsuario,inicioPesquisa, cidade, estado,instituicaoPesquisa,linkLattes, nome) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final String sqlCadastrarUsuarioComum = "insert into usuario(email, senha, localFormacao,dataNascimento, grauEscolaridade, totalSeguidores, totalSeguido, tipoUsuario, identidade, nome) VALUES(?,?,?,?,?,?,?,?,?,?)";

    private final String sqlVerificaEmail = "SELECT * FROM usuario WHERE email = ?";

    private final String sqlVerificaIdentidade = "SELECT * FROM usuario WHERE identidade = ?";

    private final String sqlProcuraPorNome = "SELECT * FROM usuario WHERE nome LIKE ? ";
    private final String sqlProcuraTemasInteresse = "select t.nomeTemaInteresse from temaInteresse t inner join usuarioTemaInteresse ut on t.idTemaInteresse = ut.idTemaInteresse where ut.idUsuario = ?";
    private final String sqlAtualizaUsuarioComum = "UPDATE usuario SET email =?, nome =?, senha = ?, localFormacao = ?, dataNascimento = ?, grauEscolaridade=?, identidade =? WHERE idUsuario =?";
    private final String sqlAtualizaPesquisador = "\n" +
            "UPDATE usuario SET email =?, nome =?, senha = ?, dataNascimento =?, cidade = ?, estado =?, instituicaoPesquisa=?, inicioPesquisa=?,grauEscolaridade =?, localFormacao=?, linkLattes =? WHERE idUsuario = ?";
    private final String sqlVerificaLogin = "SELECT email, senha FROM Usuario WHERE email = ? AND senha =?";
    public final String sqlCurte = "INSERT INTO usuarioCurtePostagem(idUsuario, idPostagem) VALUES(?,?)";
    public final String sqlProcuraCurtida = "SELECT idPostagem, idUsuario FROM usuarioCurtePostagem WHERE idUsuario=? AND idPostagem =?";
    public final String sqlDescurte = "DELETE FROM usuarioCurtePostagem WHERE idUsuario=? AND idPostagem =?";
    private final String sqlCalculaQtdCurtidas = "SELECT COUNT(idPostagem) FROM usuarioCurtePostagem WHERE idPostagem = ?";
    public final String sqlAtualizaQtdCuritdas = "UPDATE postagem SET qtdCurtidasPostagem =? WHERE idPostagem = ?";
    public final String sqlWordCloud = "SELECT nomeTemaInteresse FROM temaInteresse WHERE idTemaInteresse IN (SELECT idTemaInteresse FROM usuarioTemaInteresse WHERE idUsuario = 1)";
    public final String sqlSegue = "INSERT into usuarioSegueUsuario(idUsuarioSegue, idUsuarioSeguido) VALUES(?,?)";
    public final String sqlProcuraSeguida = "SELECT idUsuarioSegue, idUsuarioSeguido FROM usuarioSegueUsuario WHERE idUsuarioSegue=? AND idUsuarioSeguido =?";
    public final String sqlCancelado = "DELETE FROM usuarioSegueUsuario WHERE idUsuarioSegue=? AND idUsuarioSeguido=?";
    private final String sqlCalculaSeguidores = "select count(idUsuarioSeguido) from usuarioSegueUsuario where idUsuarioSeguido = ?"; /*por quantas pessoas o usuario x é seguido*/
    private final String sqlCalculaSeguindo = "select count(idUsuarioSeguido) from usuarioSegueUsuario where idUsuarioSegue = ?";  /*quantas pessoas o usuario x segue*/
    private final String sqlListaSeguidores = "select * from usuario where idUsuario IN (select idUsuarioSegue from usuarioSegueUsuario where idUsuarioSeguido =?)";
    private final String sqlListaSeguindo = "select * from usuario where idUsuario IN (select idUsuarioSeguido from usuarioSegueUsuario where idUsuarioSegue =?)";

    private PreparedStatement stmCadastrarPesquisador;
    private PreparedStatement stmCadastrarUsuarioComum;
    private PreparedStatement stmVerificaEmail;
    private PreparedStatement stmVerificaIdentidade;
    private PreparedStatement stmProcuraPorNome;
    private PreparedStatement stmAtualizaUsuarioComum;
    private PreparedStatement stmAtualizaPesquisador;
    private PreparedStatement stmProcuraTemasInteresse;
    private PreparedStatement stmVerificaLogin;
    private PreparedStatement stmCurte;
    private PreparedStatement stmProcuraCurtida;
    private PreparedStatement stmDescurte;
    private PreparedStatement stmCalculaQtdCurtidas;
    private PreparedStatement stmAtualizaQtdCuritdas;
    private PreparedStatement stmWordCloud;
    private PreparedStatement stmSegue;
    private PreparedStatement stmProcuraSeguida;
    private PreparedStatement stmCancelado;
    private PreparedStatement stmCalculaSeguidores;
    private PreparedStatement stmCalculaSeguindo;
    private PreparedStatement stmListaSeguidores;
    private PreparedStatement stmListaSeguindo;

    @Autowired
    public UsuarioDAO(@Value("${spring.datasource.driver-class-name}") String DRIVER, @Value("${spring.datasource.url}") String URL, @Value("${spring.datasource.username}") String USER, @Value("${spring.datasource.password}") String PASSWORD){
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
            stmCadastrarPesquisador = con.prepareStatement(sqlCadastrarPesquisador, Statement.RETURN_GENERATED_KEYS);
            stmCadastrarUsuarioComum = con.prepareStatement(sqlCadastrarUsuarioComum, Statement.RETURN_GENERATED_KEYS);
            stmVerificaEmail = con.prepareStatement(sqlVerificaEmail);
            stmVerificaIdentidade = con.prepareStatement(sqlVerificaIdentidade);
            stmProcuraPorNome = con.prepareStatement(sqlProcuraPorNome);
            stmAtualizaUsuarioComum = con.prepareStatement(sqlAtualizaUsuarioComum);
            stmAtualizaPesquisador = con.prepareStatement(sqlAtualizaPesquisador);
            stmProcuraTemasInteresse = con.prepareStatement(sqlProcuraTemasInteresse);
            stmCurte = con.prepareStatement(sqlCurte);
            stmDescurte = con.prepareStatement(sqlDescurte);
            stmProcuraCurtida= con.prepareStatement(sqlProcuraCurtida);
            stmCalculaQtdCurtidas = con.prepareStatement(sqlCalculaQtdCurtidas);
            stmAtualizaQtdCuritdas = con.prepareStatement(sqlAtualizaQtdCuritdas);
            stmWordCloud = con.prepareStatement(sqlWordCloud);
            stmSegue = con.prepareStatement(sqlSegue);
            stmProcuraSeguida = con.prepareStatement(sqlProcuraSeguida);
            stmCancelado = con.prepareStatement(sqlCancelado);
            stmCalculaSeguidores = con.prepareStatement(sqlCalculaSeguidores);
            stmCalculaSeguindo = con.prepareStatement(sqlCalculaSeguindo);
            stmListaSeguidores = con.prepareStatement(sqlListaSeguidores);
            stmListaSeguindo = con.prepareStatement(sqlListaSeguindo);

        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Falha ao preparar statement");
        }


    }

    public List<Usuario> findAll() {
        final String sql = "SELECT * FROM usuario";
        List<Usuario> usuarios = new java.util.ArrayList<>();
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                String valor = rs.getString(10);


                if (valor.indexOf("Pesquisador")!=-1){
                    Pesquisador pesquisador = pesquisadorMap(rs);
                    pesquisador.setTotalSeguidores(calculaSeguidor(pesquisador.getId()));
                    pesquisador.setTotalSeguindo(CalculaSeguindo(pesquisador.getId()));
                    pesquisador.setTemaInteresse(temaDao.buscaTemas(pesquisador.getId()));
                    pesquisador.setPublicacao(publicacaoDao.buscaPublicacao(pesquisador.getId()));
                    pesquisador.setMensagem(mensagemDAO.buscaMensagem(pesquisador.getId()));
                    usuarios.add(pesquisador);
                }
                else if (valor.indexOf("Usuario comum")!=-1) {
                    Usuario usuario = usuarioMap(rs);
                    usuario.setTotalSeguidores(calculaSeguidor(usuario.getId()));
                    usuario.setTotalSeguindo(CalculaSeguindo(usuario.getId()));
                    usuario.setTemaInteresse(temaDao.buscaTemas(usuario.getId()));
                    usuario.setPublicacao(publicacaoDao.buscaPublicacao(usuario.getId()));
                    usuario.setMensagem(mensagemDAO.buscaMensagem(usuario.getId()));
                    usuarios.add(usuario);
                }
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return usuarios;
    }


    public int cadastrarUsuarioComum(UsuarioComum usuario){
        int qtdInserts = 0;
        int chaveGerada = -1;
        try( Connection con = DriverManager.getConnection(URL, USER, PASSWORD)){
            stmCadastrarUsuarioComum.setString(1, usuario.getEmail());
            stmCadastrarUsuarioComum.setString(2, usuario.getSenha());
            stmCadastrarUsuarioComum.setString(3, usuario.getLocalFormacao());
            java.sql.Date data = new java.sql.Date(usuario.getDataNascimento().getTime());
            stmCadastrarUsuarioComum.setDate(4, data);
            stmCadastrarUsuarioComum.setString(5, usuario.getGrauEscolaridade());
            stmCadastrarUsuarioComum.setInt(6, usuario.getTotalSeguidores());
            stmCadastrarUsuarioComum.setInt(7, usuario.getTotalSeguindo());
            stmCadastrarUsuarioComum.setString(8, "Usuario comum");
            stmCadastrarUsuarioComum.setString(9, usuario.getIdentidade());
            stmCadastrarUsuarioComum.setString(10, usuario.getNome());

            qtdInserts = stmCadastrarUsuarioComum.executeUpdate();
            con.close();
            try (ResultSet generatedKeys = stmCadastrarUsuarioComum.getGeneratedKeys()) {
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

    public int cadastrarPesquisador(Pesquisador pesquisador){
        int qtdInserts = 0;
        int chaveGerada = -1;
        try( Connection con = DriverManager.getConnection(URL, USER, PASSWORD)){
            stmCadastrarPesquisador.setString(1, pesquisador.getEmail());
            stmCadastrarPesquisador.setString(2, pesquisador.getSenha());
            stmCadastrarPesquisador.setString(3, pesquisador.getLocalFormacao());
            java.sql.Date data = new java.sql.Date(pesquisador.getDataNascimento().getTime());
            stmCadastrarPesquisador.setDate(4, data);
            stmCadastrarPesquisador.setString(5, pesquisador.getGrauEscolaridade());
            stmCadastrarPesquisador.setInt(6, pesquisador.getTotalSeguidores());
            stmCadastrarPesquisador.setInt(7, pesquisador.getTotalSeguindo());
            stmCadastrarPesquisador.setString(8, "Pesquisador");
            stmCadastrarPesquisador.setString(9, pesquisador.getInicioPesquisa());
            stmCadastrarPesquisador.setString(10, pesquisador.getCidade());
            stmCadastrarPesquisador.setString(11, pesquisador.getEstado());
            stmCadastrarPesquisador.setString(12, pesquisador.getInstituicaoPesquisa());
            stmCadastrarPesquisador.setString(13, pesquisador.getLinkLattes());
            stmCadastrarPesquisador.setString(14, pesquisador.getNome());

            qtdInserts = stmCadastrarPesquisador.executeUpdate();
            con.close();

            try (ResultSet generatedKeys = stmCadastrarPesquisador.getGeneratedKeys()) {
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


    public int atualizaUsuarioComum(UsuarioComum usuario){
        int qtdUpdate = 0;
        try(Connection con = DriverManager.getConnection(URL, USER, PASSWORD)){
            stmAtualizaUsuarioComum.setString(1,usuario.getEmail());
            stmAtualizaUsuarioComum.setString(2,usuario.getNome());
            stmAtualizaUsuarioComum.setString(3,usuario.getSenha());
            stmAtualizaUsuarioComum.setString(4,usuario.getLocalFormacao());
            java.sql.Date data = new java.sql.Date(usuario.getDataNascimento().getTime());
            stmAtualizaUsuarioComum.setDate(5,data);
            stmAtualizaUsuarioComum.setString(6,usuario.getGrauEscolaridade());
            stmAtualizaUsuarioComum.setString(7,usuario.getIdentidade());
            stmAtualizaUsuarioComum.setLong(8,usuario.getId());

            qtdUpdate = stmAtualizaUsuarioComum.executeUpdate();
            con.close();

        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return qtdUpdate;
    }

    public int atualizaPesquisador(Pesquisador pesquisador){
        int qtdUpdate = 0;
        try(Connection con = DriverManager.getConnection(URL, USER, PASSWORD)){
            stmAtualizaPesquisador.setString(1,pesquisador.getEmail());
            stmAtualizaPesquisador.setString(2,pesquisador.getNome());
            stmAtualizaPesquisador.setString(3,pesquisador.getSenha());
            java.sql.Date data = new java.sql.Date(pesquisador.getDataNascimento().getTime());
            stmAtualizaPesquisador.setDate(4,data);
            stmAtualizaPesquisador.setString(5,pesquisador.getCidade());
            stmAtualizaPesquisador.setString(6,pesquisador.getEstado());
            stmAtualizaPesquisador.setString(7,pesquisador.getInstituicaoPesquisa());
            stmAtualizaPesquisador.setString(8,pesquisador.getInicioPesquisa());
            stmAtualizaPesquisador.setString(9,pesquisador.getGrauEscolaridade());
            stmAtualizaPesquisador.setString(10,pesquisador.getLocalFormacao());
            stmAtualizaPesquisador.setString(11,pesquisador.getLinkLattes());
            stmAtualizaPesquisador.setInt(12,pesquisador.getId());
            qtdUpdate = stmAtualizaPesquisador.executeUpdate();
            con.close();
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return qtdUpdate;
    }

    public boolean curte(Integer idUser, Integer idPost){
        int qtdUpdate = 0;
        try( Connection con = DriverManager.getConnection(URL, USER, PASSWORD))
        {
            stmProcuraCurtida.setInt(1, idUser);
            stmProcuraCurtida.setInt(2, idPost);
            ResultSet procuraCurtiaRS = stmProcuraCurtida.executeQuery();

            if(procuraCurtiaRS.next()) {
                stmDescurte.setInt(1, idUser);
                stmDescurte.setInt(2, idPost);
                qtdUpdate = stmDescurte.executeUpdate();
            } else {
                stmCurte.setInt(1, idUser);
                stmCurte.setInt(2, idPost);
                qtdUpdate = stmCurte.executeUpdate();
            }

            if (qtdUpdate>0){
                stmCalculaQtdCurtidas.setInt(1, idPost);
                ResultSet novasCurtidasRS = stmCalculaQtdCurtidas.executeQuery();
                if(novasCurtidasRS.next()) {
                    stmAtualizaQtdCuritdas.setInt(1, novasCurtidasRS.getInt("COUNT(idPostagem)"));
                    stmAtualizaQtdCuritdas.setInt(2, idPost);
                    stmAtualizaQtdCuritdas.executeUpdate();
                    return true;
                }
            }con.close();
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return false;
    }


    public List<Usuario> buscaPorEmail(String email){
        List<Usuario> usuarios = new java.util.ArrayList<>();
        try( Connection con = DriverManager.getConnection(URL, USER, PASSWORD))
        {
            stmVerificaEmail.setString(1, email);

            try( ResultSet rs = stmVerificaEmail.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = usuarioMap(rs);
                    if(usuario.getTipoUsuario().equalsIgnoreCase("Pesquisador")){
                        Pesquisador pesquisador = pesquisadorMap(rs);
                        pesquisador.setTotalSeguidores(calculaSeguidor(pesquisador.getId()));
                        pesquisador.setTotalSeguindo(CalculaSeguindo(pesquisador.getId()));
                        pesquisador.setTemaInteresse(temaDao.buscaTemas(pesquisador.getId()));
                        pesquisador.setPublicacao(publicacaoDao.buscaPublicacao(pesquisador.getId()));
                        pesquisador.setMensagem(mensagemDAO.buscaMensagem(pesquisador.getId()));
                        usuarios.add(pesquisador);


                    }
                    else{
                        UsuarioComum comum = usuarioComumMap(rs);
                        comum.setTotalSeguidores(calculaSeguidor(comum.getId()));
                        comum.setTotalSeguindo(CalculaSeguindo(comum.getId()));
                        comum.setTemaInteresse(temaDao.buscaTemas(comum.getId()));
                        comum.setMensagem(mensagemDAO.buscaMensagem(comum.getId()));
                        usuarios.add(comum);
                    }
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return usuarios;
    }

    public Boolean verificaIdentidade(String identidade){
        List<UsuarioComum> usuarios = new java.util.ArrayList<>();
        try( Connection con = DriverManager.getConnection(URL, USER, PASSWORD))
        {
            stmVerificaIdentidade.setString(1, identidade);

            try( ResultSet rs = stmVerificaIdentidade.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(usuarioComumMap(rs));
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        if(usuarios.size()==1){
            return true;
        }
        return false;
    }

    public List<Usuario> buscaPorNome(String nome) {
        List<Usuario> usuarios = new java.util.ArrayList<>();

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            stmProcuraPorNome.setString(1, "%" + nome + "%");
            try (ResultSet rs = stmProcuraPorNome.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = usuarioMap(rs);
                    usuario.setTotalSeguidores(calculaSeguidor(usuario.getId()));
                    usuario.setTotalSeguindo(CalculaSeguindo(usuario.getId()));
                    usuario.setTemaInteresse(temaDao.buscaTemas(usuario.getId()));
                    usuarios.add(usuario);
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados H2");
        }
        return usuarios;
    }


    public Usuario usuarioMap(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("idUsuario"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setDataNascimento(rs.getDate("dataNascimento"));
        usuario.setGrauEscolaridade(rs.getString("grauEscolaridade"));
        usuario.setTotalSeguindo(rs.getInt("totalSeguido"));
        usuario.setTotalSeguidores(rs.getInt("totalSeguidores"));
        usuario.setLocalFormacao(rs.getString("localFormacao"));
        usuario.setTipoUsuario(rs.getString("tipoUsuario"));


        return usuario;
    }

    public Pesquisador pesquisadorMap(ResultSet rs) throws SQLException {
        Pesquisador pesquisador = new Pesquisador();
        pesquisador.setId(rs.getInt("idUsuario"));
        pesquisador.setNome(rs.getString("nome"));
        pesquisador.setEmail(rs.getString("email"));
        pesquisador.setSenha(rs.getString("senha"));
        pesquisador.setLocalFormacao(rs.getString("localFormacao"));
        pesquisador.setDataNascimento(rs.getDate("dataNascimento"));
        pesquisador.setGrauEscolaridade(rs.getString("grauEscolaridade"));
        pesquisador.setTotalSeguindo(rs.getInt("totalSeguido"));
        pesquisador.setTotalSeguidores(rs.getInt("totalSeguidores"));
        pesquisador.setInicioPesquisa(rs.getString("inicioPesquisa"));
        pesquisador.setCidade(rs.getString("cidade"));
        pesquisador.setEstado(rs.getString("estado"));
        pesquisador.setInstituicaoPesquisa(rs.getString("instituicaoPesquisa"));
        pesquisador.setLinkLattes(rs.getString("linkLattes"));
        pesquisador.setTipoUsuario(rs.getString("tipoUsuario"));

        return pesquisador;
    }

    public UsuarioComum usuarioComumMap(ResultSet rs) throws SQLException {
        UsuarioComum usuario = new UsuarioComum();
        usuario.setId(rs.getInt("idUsuario"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setDataNascimento(rs.getDate("dataNascimento"));
        usuario.setGrauEscolaridade(rs.getString("grauEscolaridade"));
        usuario.setTotalSeguindo(rs.getInt("totalSeguido"));
        usuario.setTotalSeguidores(rs.getInt("totalSeguidores"));
        usuario.setLocalFormacao(rs.getString("localFormacao"));
        usuario.setIdentidade(rs.getString("identidade"));
        usuario.setTipoUsuario(rs.getString("tipoUsuario"));

        return usuario;
    }

    public boolean logar(String email, String senha){
        boolean verifica = false;

        try(Connection con = DriverManager.getConnection(URL, USER, PASSWORD)){
            stmVerificaLogin.setString(1,email);
            stmVerificaLogin.setString(2,senha);
            ResultSet rs = stmVerificaLogin.executeQuery();

            if (rs.next()){
                verifica = true;
                System.out.println("True");
            }
            else {
                verifica = false;
                System.out.println("False");
            }
            con.close();
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return verifica;
    }
    public TemaInteresse temaMap(ResultSet rs) throws SQLException {
        TemaInteresse tema = new TemaInteresse();
        tema.setIdTemaInteresse(rs.getInt("idTemaInteresse"));
        tema.setNomeTemaInteresse(rs.getString("nomeTemaInteresse"));
        tema.setQtdUsuariosTemaInteresse(rs.getInt("qtdUsuariosTemaInteresse"));
        return tema;
    }

    public List<TemaInteresse> buscaWordCloud(Integer id) throws SQLException{
        List<TemaInteresse> temas = new ArrayList();
        try(Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            ResultSet rs = stmWordCloud.executeQuery()){
                while (rs.next()) {
                        TemaInteresse tema = temaMap(rs);
                        temas.add(tema);

                }}


        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return temas;
    }

    public boolean seguir(Integer idUsuarioSeguidor, Integer idUsuarioSeguido){
        int qtdUpdate = 0;
        int qtdCancelado =0;
        int qtdSeguidores =0;
        int i = 0;
        try( Connection con = DriverManager.getConnection(URL, USER, PASSWORD))
        {
            stmProcuraSeguida.setInt(1,idUsuarioSeguidor);
            stmProcuraSeguida.setInt(2,idUsuarioSeguido);
            ResultSet rs = stmProcuraSeguida.executeQuery();
            if (rs.next()) {
                stmCancelado.setInt(1, idUsuarioSeguidor);
                stmCancelado.setInt(2, idUsuarioSeguido);
                qtdCancelado = stmCancelado.executeUpdate();
                return true;
            }else{
                stmSegue.setInt(1,idUsuarioSeguidor);
                stmSegue.setInt(2,idUsuarioSeguido);
                qtdUpdate = stmSegue.executeUpdate();
                return true;

            }



        }

        catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return false;
    }


    public int calculaSeguidor(Integer idUsuarioSeguido)throws SQLException {
        int qtdSeguidores = 0;
        int i = 0;
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            stmCalculaSeguidores.setInt(1, idUsuarioSeguido);
            ResultSet rs = stmCalculaSeguidores.executeQuery();
            while (rs.next()) {
                qtdSeguidores = qtdSeguidoresMap(rs);
            }

            con.close();
            return qtdSeguidores;
        }
    }
        public int CalculaSeguindo(int idUsuarioSeg)throws SQLException {
            int seguindo = 0;
            try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
                stmCalculaSeguindo.setInt(1, idUsuarioSeg);
                ResultSet rs = stmCalculaSeguindo.executeQuery();
                while (rs.next()) {
                    seguindo = qtdSeguidoresMap(rs);
                }

                con.close();
                return seguindo;
            }

        catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados");
        }
        return 0;
    }
    public int qtdSeguidoresMap(ResultSet rs ) throws  SQLException{
        return rs.getInt(1);
    }

    public List<Usuario> listaSeguidores(int id) {
        List<Usuario> usuarios = new java.util.ArrayList<>();

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            stmListaSeguidores.setInt(1, id);
            try (ResultSet rs = stmListaSeguidores.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = usuarioMap(rs);
                    usuario.setTotalSeguidores(calculaSeguidor(usuario.getTotalSeguidores()));
                    usuario.setTotalSeguindo(CalculaSeguindo(usuario.getTotalSeguindo()));
                    usuario.setTemaInteresse(temaDao.buscaTemas(usuario.getId()));
                    usuarios.add(usuario);
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados H2");
        }
        return usuarios;
    }

    public List<Usuario> listaSeguindo(int id) {
        List<Usuario> usuarios = new java.util.ArrayList<>();

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            stmListaSeguindo.setInt(1, id);
            try (ResultSet rs = stmListaSeguindo.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = usuarioMap(rs);
                    usuario.setTotalSeguidores(calculaSeguidor(usuario.getTotalSeguidores()));
                    usuario.setTotalSeguindo(CalculaSeguindo(usuario.getTotalSeguindo()));
                    usuario.setTemaInteresse(temaDao.buscaTemas(usuario.getId()));
                    usuarios.add(usuario);
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Falha na conexao com o banco de dados H2");
        }
        return usuarios;
    }
}
