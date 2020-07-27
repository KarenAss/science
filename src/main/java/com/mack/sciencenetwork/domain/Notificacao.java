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
public class Notificacao {

    private Integer idNotif;
    @NotNull
    private Integer usuarioSeguidorId;
    @NotNull
    private Integer usuarioSeguidoId;
    private String usuarioSeguidoEmail;
    private String usuarioSeguidoNome;
    @NotEmpty
    private String tipoNotif;
    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") private Date dataHoraNotif;

    @Override
    public String toString(){
        return "notificacao{" + "ID Notificacao=" + idNotif + ",\n\t ID Usuario segue=" + usuarioSeguidorId + ",\n\t ID Usuario seguido=" + usuarioSeguidoId + ",\n\t E-Mail Usuario seguido" + usuarioSeguidoEmail + ",\n\t Nome Usuario seguido" + usuarioSeguidoNome + ",\n\t Tipo notificação=" + tipoNotif + ",\n\t Data e hora=" + dataHoraNotif + '}';
    }
}