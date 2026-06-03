package cl.duoc.mineria.ciclo_transporte.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IniciarCicloDTO {

    @NotNull(message = "El ID del turno es obligatorio")
    @Min(value = 1, message = "El ID del turno debe ser mayor que 0")
    private Long turnoId;

    @NotNull(message = "El ID del camión es obligatorio")
    @Min(value = 1, message = "El ID del camión debe ser mayor que 0")
    private Long camionId;

    @NotNull(message = "El ID de la pala es obligatorio")
    @Min(value = 1, message = "El ID de la pala debe ser mayor que 0")
    private Long palaId;

    @NotNull(message = "El ID del palero es obligatorio")
    @Min(value = 1, message = "El ID del palero debe ser mayor que 0")
    private Long paleroId;

    @NotNull(message = "El ID del material a cargar es obligatorio")
    @Min(value = 1, message = "El ID del material debe ser mayor que 0")
    private Long materialId;
}