package com.mack.sciencenetwork.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

 @Getter @Setter
public class Postagem {

    private Integer idPostagem;
    @NotNull @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") private Date dataHoraPostagem;
    @NotNull
    private Integer idUsuarioAutor;
    @NotNull
    private Integer qtdCurtidasPostagem;
    @NotEmpty
    private String tipoPostagem;
    private String conteudoMensagem;
    private String tituloPubliCientifica;
    private String localPubliCientifica;
     @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") private Date anoPubliCientifica;
    private String resumoPubliCientifica;
    private String palavrasChavePubliCientifica;
    private String urlPubliCientifica;


    @Override
    public String toString(){
        return "postagem{" + "IDPostagem=" + idPostagem + ",\n\t Data e hora da Postagem=" + dataHoraPostagem +"\n\t Id Usuario autor="+ idUsuarioAutor+"\n\t Quantidade de curtidas="+ qtdCurtidasPostagem+"\n\t Tipo de postagem="+tipoPostagem
                +"\n\t Conteudo Postagem="+conteudoMensagem+"\n\t Titulo Publicação Cientifica="+tituloPubliCientifica+"\n\t Local Publicação cientifica="+localPubliCientifica +"\n\t Ano da publicação="+anoPubliCientifica+"\n\t Resumo Publicação cientifica="+resumoPubliCientifica+
                "\n\t Palavras Chave das Publicações Cientificas="+ palavrasChavePubliCientifica+"\n\t URL da publicação cientifica="+urlPubliCientifica+'}';
    }
}
