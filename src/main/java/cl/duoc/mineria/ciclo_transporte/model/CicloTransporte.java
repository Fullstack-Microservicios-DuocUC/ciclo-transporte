package cl.duoc.mineria.ciclo_transporte.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ciclos_transporte")
public class CicloTransporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "turno_id", nullable = false)
    private Long turnoId;

    @Column(name = "camion_id", nullable = false)
    private Long camionId;

    @Column(name = "pala_id", nullable = false)
    private Long palaId;

    @Column(name = "palero_id", nullable = false)
    private Long paleroId;

    @Column(name = "material_id")
    private Long materialId;

    @Enumerated(EnumType.STRING)
    @Column(name = "destino")
    private Destino destino;

    @Column(name = "toneladas_cargadas")
    private Double toneladasCargadas;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_ciclo", nullable = false)
    private EstadoCiclo estadoCiclo;

    @Column(name = "fecha_hora_inicio")
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin")
    private LocalDateTime fechaHoraFin;

}
