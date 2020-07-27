package com.mack.sciencenetwork.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

 @Getter @Setter
public class UsuarioCurtePostagem {
    @NotEmpty
    private Integer idUsuario;
    @NotEmpty
    private Integer idPostagem;


    @Override
    public String toString(){
        return "temaInteresse{" + "ID Usuario=" + idUsuario + ",\n\t ID Postagem=" + idPostagem +'}';
    }
}
