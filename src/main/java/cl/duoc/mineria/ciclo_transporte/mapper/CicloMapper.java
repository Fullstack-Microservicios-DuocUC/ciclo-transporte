package cl.duoc.mineria.ciclo_transporte.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import cl.duoc.mineria.ciclo_transporte.dto.IniciarCicloDTO;
import cl.duoc.mineria.ciclo_transporte.model.CicloTransporte;
import cl.duoc.mineria.ciclo_transporte.model.EstadoCiclo;

@Component
public class CicloMapper {

    public CicloTransporte toEntity(IniciarCicloDTO dto) {

        if (dto==null) return null;

        CicloTransporte ciclo = new CicloTransporte();

        ciclo.setCamionId(dto.getCamionId() != null ? dto.getCamionId() : 0);
        ciclo.setPalaId(dto.getPalaId() != null ? dto.getPalaId() : 0);
        ciclo.setPaleroId(dto.getPaleroId() != null ? dto.getPaleroId() : 0);

        ciclo.setEstadoCiclo(EstadoCiclo.EN_TRANSITO);
        ciclo.setFechaHoraInicio(LocalDateTime.now());

        return ciclo;
    }

}
