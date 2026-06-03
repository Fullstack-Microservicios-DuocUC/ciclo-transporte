package cl.duoc.mineria.ciclo_transporte.dto;

import lombok.Data;

@Data
public class DestinoAsignadoDTO {
    
    // Aquí es donde aterrizará la palabra "CHANCADOR" o "RELAVE" que nos envíe el Asignador
    private String destinoAsignado; 
    
    private Long palaId;
    private Boolean rutaHabilitada;
    
}