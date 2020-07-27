package com.mack.sciencenetwork.controller;

import com.mack.sciencenetwork.dao.*;
import com.mack.sciencenetwork.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

@RestController
public class RestApi {
    @Autowired
    private UsuarioDAO usuarioDao;
    @Autowired
    private TemaInteresseDAO temaDao;
    @Autowired
    private MensagemDAO mensagemDao;
    @Autowired
    private PublicacaoDAO publicacaoDao;
    @Autowired
    private NotificacaoDAO notificacaoDAO;
    @Autowired
    private CurtidaDAO curtidaDAO;

    @GetMapping("/listarUsuarios")
    public List<Usuario> obterUsuarios(){
        return usuarioDao.findAll();
    }

    @GetMapping(value="/buscaPorEmail/email={email}")
    public List<Usuario> buscaPorEmail(@PathVariable String email){
        return usuarioDao.buscaPorEmail(email);
    }

    @GetMapping(value="/verificaEmail/email={email}")
    public Boolean verificaEmail(@PathVariable String email){
        if(usuarioDao.buscaPorEmail(email).size() == 1){
            return true;
        }
        return false;
    }

    @GetMapping("/listarTemas")
    public List<TemaInteresse> obterTemas(){
        return temaDao.findAll();
    }

    @GetMapping(value="/buscarMembro/nome={nome}")
    public List<Usuario> buscaPorNome(@PathVariable String nome){
        return usuarioDao.buscaPorNome(nome);
    }

    @PostMapping(value = "/cadastrarUsuarioComum")
    public String cadastrarUsuario(@Valid @RequestBody UsuarioComum usuario){

        if (usuarioDao.verificaIdentidade(usuario.getIdentidade())){
            return "Identidade já está cadastrada no sistema";
        }

        int chaveGerada = usuarioDao.cadastrarUsuarioComum(usuario);
        temaDao.cadastrarTemaUsuario(chaveGerada, usuario);


        if (chaveGerada != -1){
            return "Cadastrado com sucesso";
        }
        return "Registro não cadastrado";
    }

    @PostMapping(value = "/cadastrarPesquisador")
    public String cadastrarUsuario(@Valid @RequestBody Pesquisador pesquisador){
        if(usuarioDao.buscaPorEmail(pesquisador.getEmail()).size() == 1){
            return "Email já cadastrado no sistema";
        }

        int chaveGerada = usuarioDao.cadastrarPesquisador(pesquisador);
        temaDao.cadastrarTemaUsuario(chaveGerada, pesquisador);

        if (chaveGerada != -1){
            return "Cadastrado com sucesso";
        }
        return "Registro não cadastrado";
    }

    @PostMapping(value="/atualizaUsuarioComum")
    public String atualizaUsuarioComum(@Valid @RequestBody UsuarioComum usuario){
        int id = usuarioDao.atualizaUsuarioComum(usuario);

        int a = temaDao.atualizaTemaUsuario(usuario);
        if(a==0 || id !=1){
            return "Falha ao atualizar os dados!";
        }
        else{
            return "Dados Atualizados!";
        }

    }

    @PostMapping(value="/atualizaPesquisador")
    public String atualizaPesquisador(@Valid @RequestBody Pesquisador pesquisador){
        int id = usuarioDao.atualizaPesquisador(pesquisador);
        int a = temaDao.atualizaTemaUsuario(pesquisador);
        if(a==0 || id !=1){
            return "Falha ao atualizar os dados!";
        }
        else{
            return "Dados Atualizados!";
        }
    }
    @GetMapping(value = "/login/{email}/{senha}")
    public boolean login(@PathVariable String email, @PathVariable String senha){
        List<Usuario> usuarios = usuarioDao.buscaPorEmail(email);
        if(usuarios.isEmpty()){
            return false;
        }
        Usuario usuario = usuarios.get(0);
        if(usuario.getSenha().equalsIgnoreCase(senha)){
            return true;
        }
        else{
            return false;
        }

    }

    @PostMapping(value = "/curte/{idUsuario}/{idPub}")
    public boolean login(@PathVariable Integer idUsuario, @PathVariable Integer idPub){
        if(usuarioDao.curte(idUsuario,idPub)){
            return true;
        }
        else{
            return false;
        }

    }

    @PostMapping(value = "/inserirMensagem")
    public String inserirMensagem(@Valid @RequestBody Postagem mensagem){
        int chaveGerada = mensagemDao.insereMsg(mensagem);
        if (chaveGerada != -1){
            List<Usuario> usuarios = usuarioDao.findAll();
            Usuario usuario = new Usuario();
            for(Usuario u : usuarios){
                if(u.getId() == mensagem.getIdUsuarioAutor()){
                    usuario = u;
                }
            }
            List<Usuario> seguidores = usuarioDao.listaSeguidores(usuario.getId());
            for(Usuario seguidor : seguidores){
                Notificacao notificacao = new Notificacao();
                notificacao.setUsuarioSeguidorId(seguidor.getId());
                notificacao.setUsuarioSeguidoId(usuario.getId());
                notificacao.setUsuarioSeguidoEmail(usuario.getEmail());
                notificacao.setUsuarioSeguidoNome(usuario.getNome());
                notificacao.setTipoNotif("Mensagem");
                notificacao.setDataHoraNotif(mensagem.getDataHoraPostagem());
                notificacaoDAO.insereNotif(notificacao);
            }
            return "Inserido com sucesso";
        }
        return "Não Inserido";
    }

    @PostMapping(value="/atualizaMensagem")
    public String atualizaMensagem(@Valid @RequestBody Postagem mensagem){
        int id = mensagemDao.atualizaMensagem(mensagem);

        if(id==0){
            return "Falha ao atualizar os dados!";
        }
        else{
            List<Usuario> usuarios = usuarioDao.findAll();
            Usuario usuario = new Usuario();
            for(Usuario u : usuarios){
                if(u.getId() == mensagem.getIdUsuarioAutor()){
                    usuario = u;
                }
            }
            List<Usuario> seguidores = usuarioDao.listaSeguidores(usuario.getId());
            for(Usuario seguidor : seguidores){
                Notificacao notificacao = new Notificacao();
                notificacao.setUsuarioSeguidorId(seguidor.getId());
                notificacao.setUsuarioSeguidoId(usuario.getId());
                notificacao.setUsuarioSeguidoEmail(usuario.getEmail());
                notificacao.setUsuarioSeguidoNome(usuario.getNome());
                notificacao.setTipoNotif("Mensagem");
                notificacao.setDataHoraNotif(mensagem.getDataHoraPostagem());
                notificacaoDAO.insereNotif(notificacao);
            }
            return "Dados Atualizados!";
        }

    }

    @PostMapping(value = "/deletaMensagem")
    public String deletaMensagem(@Valid @RequestBody Postagem mensagem){
        int id = mensagemDao.deletaMensagem(mensagem);
        if(id==0){
            return "Falha ao deletar a Mensagem!";
        }
        else{
            return "Mensagem Deletada!";
        }
    }

    @PostMapping(value = "/inserirPublicacao")
    public String inserirPublicacao(@Valid @RequestBody Postagem publicacao){


        int chaveGerada = publicacaoDao.inserePub(publicacao);

        if (chaveGerada != -1){
            List<Usuario> usuarios = usuarioDao.findAll();
            Usuario usuario = new Usuario();
            for(Usuario u : usuarios){
                if(u.getId() == publicacao.getIdUsuarioAutor()){
                    usuario = u;
                }
            }
            List<Usuario> seguidores = usuarioDao.listaSeguidores(usuario.getId());
            for(Usuario seguidor : seguidores){
                Notificacao notificacao = new Notificacao();
                notificacao.setUsuarioSeguidorId(seguidor.getId());
                notificacao.setUsuarioSeguidoId(usuario.getId());
                notificacao.setUsuarioSeguidoEmail(usuario.getEmail());
                notificacao.setUsuarioSeguidoNome(usuario.getNome());
                notificacao.setTipoNotif("Publicacao");
                notificacao.setDataHoraNotif(publicacao.getDataHoraPostagem());
                notificacaoDAO.insereNotif(notificacao);
            }
            return "Inserido com sucesso";
        }
        return "Não Inserido";
    }

    @PostMapping(value="/atualizaPublicacao")
    public String atualizaPublicacao(@Valid @RequestBody Postagem publicacao){
        int id = publicacaoDao.atualizaPublicacao(publicacao);

        if(id==0){
            return "Falha ao atualizar a publicação!";
        }
        else{
            List<Usuario> usuarios = usuarioDao.findAll();
            Usuario usuario = new Usuario();
            for(Usuario u : usuarios){
                if(u.getId() == publicacao.getIdUsuarioAutor()){
                    usuario = u;
                }
            }
            List<Usuario> seguidores = usuarioDao.listaSeguidores(usuario.getId());
            for(Usuario seguidor : seguidores){
                Notificacao notificacao = new Notificacao();
                notificacao.setUsuarioSeguidorId(seguidor.getId());
                notificacao.setUsuarioSeguidoId(usuario.getId());
                notificacao.setUsuarioSeguidoEmail(usuario.getEmail());
                notificacao.setUsuarioSeguidoNome(usuario.getNome());
                notificacao.setTipoNotif("Publicacao");
                notificacao.setDataHoraNotif(publicacao.getDataHoraPostagem());
                notificacaoDAO.insereNotif(notificacao);
            }
            return "Publicação atualizada!";
        }
    }

    @PostMapping(value = "/deletaPublicacao")
    public String deletaPublicacao(@Valid @RequestBody Postagem publicacao){
        int id = publicacaoDao.deletaMensagem(publicacao);
        if(id==0){
            return "Falha ao deletar a Publicação!";
        }
        else{
            return "Publicação deletada!";
        }
    }

    @GetMapping("/listarMensagens")
    public List<Postagem> buscarMensagem(){
        return mensagemDao.findAll();
    }

    @GetMapping("/listarPublicacoes")
    public List<Postagem> buscarPublicacao(){
        return publicacaoDao.findAll();
    }

    @GetMapping(value = "/seguidores/{id}")
    public List<Usuario> listarSeguidores(@PathVariable Integer id) {
        List<Usuario> usuarios = usuarioDao.listaSeguidores(id);
        return usuarios;
    }
    @GetMapping(value = "/seguindo/{id}")
    public List<Usuario> listarSeguindo(@PathVariable Integer id){
        List<Usuario> usuarios = usuarioDao.listaSeguindo(id);
        return usuarios;
    }
    @PostMapping(value = "/seguir/{usuario}/{seguindo}")
    public String seguirUsuario(@PathVariable Integer usuario, @PathVariable Integer seguindo){
        boolean seg = usuarioDao.seguir(usuario,seguindo);
        if(seg==false){
            return "Falha ao realizar operação!";
        }
        else{
            return "Operação realizada!";
        }
    }

    @GetMapping("/notificacoes/{usuarioSeguidorId}")
    public List<Notificacao> listarNotificacoesUsuario (@PathVariable Integer usuarioSeguidorId){
        List<Notificacao> notificacoes = notificacaoDAO.buscaNotifUsuario(usuarioSeguidorId);
        return notificacoes;
    }

    @PostMapping("/notificacoes/delete/{idNotif}")
    public String deletaNotificacao(@PathVariable Integer idNotif){
        if(notificacaoDAO.deletaNotif(idNotif) == 0){
            return "Falha ao realizar operação!";
        }
        return "Operação realizada!";
    }

    @GetMapping("/curtidas/{usuarioBuscadoId}")
    public List<Curtida> listarCurtidasUsuario (@PathVariable Integer usuarioBuscadoId){
        List<Curtida> curtidas = curtidaDAO.buscaCurtidasUsuario(usuarioBuscadoId);
        return curtidas;
    }
}