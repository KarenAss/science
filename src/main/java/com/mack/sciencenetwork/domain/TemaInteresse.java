package com.mack.sciencenetwork.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter @Setter
public class TemaInteresse {
    @NotNull
    private Integer idTemaInteresse;
    @NotEmpty
    private String nomeTemaInteresse;
    @NotEmpty
    private Integer qtdUsuariosTemaInteresse;


    @Override
    public String toString(){
        return "temaInteresse{" + "IDTemaInteresse=" + idTemaInteresse + ",\n\t Nome tema interesse=" + nomeTemaInteresse +"\n\t Quantidade de usuarios interessados="+ qtdUsuariosTemaInteresse+'}';
    }

}
