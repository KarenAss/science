package com.mack.sciencenetwork.domain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class Pesquisador extends Usuario{
    @NotEmpty private String inicioPesquisa;
    @NotEmpty private String cidade;
    @NotEmpty private String estado;
    @NotEmpty private String instituicaoPesquisa;
    @NotEmpty private String linkLattes;



    @Override
    public String toString(){
        return "Usuario{Nome=" + super.getNome()+",\n\t Email=" + super.getEmail() +"\n\t Data de Nascimento="+
                super.getDataNascimento() +"\n\t Seguidores="+super.getTotalSeguidores()+"\n\t Seguindo="+
                super.getTotalSeguindo()+"\n\t Cidade= "+cidade+"\n\t Estado="+estado+"\n\t Início de Pesquisa="+inicioPesquisa+
                "\n\t Instituição de Pesquisa="+instituicaoPesquisa+"\n\tLink Lattes= "+linkLattes+"\n\tTipo de Usuário= "+super.getTipoUsuario()+
                "\n\t Tema de Interesse= "+super.getTemaInteresse()+"\n\tLocal de Formação= "+super.getLocalFormacao()+'}';
    }
}
