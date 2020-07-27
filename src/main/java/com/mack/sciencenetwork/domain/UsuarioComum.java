package com.mack.sciencenetwork.domain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class UsuarioComum extends Usuario {
    @NotEmpty private String identidade;

    @Override
    public String toString(){
        return "Usuário= "+super.getNome()+"\n\t Email= "+super.getEmail()+"\n\t Identidade= "+identidade+"\n\t Data de Nascimento= "+
                super.getDataNascimento()+"\n\t Total Seguindo= "+super.getTotalSeguindo()+"\n\t Total Seguidores= "+super.getTotalSeguidores()+
                "\n\t Tipo Usuário ="+super.getTipoUsuario()+"\n\t Tema de Interesse= "+super.getTemaInteresse();
    }
}
