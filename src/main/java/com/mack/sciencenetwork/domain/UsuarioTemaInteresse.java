package com.mack.sciencenetwork.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

 @Getter @Setter
public class UsuarioTemaInteresse {
    @NotEmpty
    private Integer idUsuario;
    @NotEmpty
    private Integer idTemaInteresse;


    @Override
    public String toString(){
        return "usuarioTemaInteresse{" + "ID Usuario=" + idUsuario + ",\n\t ID Tema Interesse=" + idTemaInteresse +'}';
    }
}
