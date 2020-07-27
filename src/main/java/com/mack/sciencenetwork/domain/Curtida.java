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
public class Curtida {
    @NotNull
    private int idUsuario;
    @NotNull
    private int idPostagem;

    @Override
    public String toString(){
        return "curtida{" + "ID Usuario=" + idUsuario + ",\n\t ID Postagem=" + idPostagem + '}';
    }
}
