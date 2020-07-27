package com.mack.sciencenetwork.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter @Setter @ToString(exclude = {"idsTemas"})
public class Usuario{
     private int id;
    @NotEmpty private String nome;
    @NotEmpty private String email;
    @NotEmpty private String senha;
    @NotNull @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") private Date dataNascimento;
    @NotEmpty private String grauEscolaridade;
    @NotNull private int totalSeguindo;
    @NotNull private int totalSeguidores;
    @NotEmpty private String localFormacao;
    private String tipoUsuario;
    @NotEmpty
    private List<Integer> idsTemas;
    private List<TemaInteresse> temaInteresse;
    private List<Postagem> publicacao;
    private List<Postagem> mensagem;


     @Override
     public String toString(){
         return "Usuario{Nome=" + nome +",\n\t Email=" + email +"\n\t Data de Nascimento="+ dataNascimento
                 +"\n\t Seguidores="+totalSeguidores+"\n\t Seguindo="+totalSeguindo+ "\n\tTipo de Usuário= "+tipoUsuario+"\n\t Tema de Interesse= "+temaInteresse.toString()+"\n\tIds Temas= "+idsTemas+
                 "Publicacoes ="+publicacao+"Mensagens"+mensagem+'}';
     }


}
