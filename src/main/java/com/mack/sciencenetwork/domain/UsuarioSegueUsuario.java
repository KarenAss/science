package com.mack.sciencenetwork.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

 @Getter @Setter
public class UsuarioSegueUsuario {
    @NotEmpty
    private Integer idUsuarioSegue;
    @NotEmpty
    private Integer idUsuarioSeguido;


    @Override
    public String toString(){
        return "usuarioSegueUsuario{" + "ID Usuario segue=" + idUsuarioSegue + ",\n\t ID Usuario seguido=" + idUsuarioSeguido +'}';
    }
}
