package cl.duoc.mineria.ciclo_transporte.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.mineria.ciclo_transporte.exception.CicloValidationException;
import cl.duoc.mineria.ciclo_transporte.exception.ResourceNotFoundException;
import cl.duoc.mineria.ciclo_transporte.exception.TurnoInvalidoException;
import cl.duoc.mineria.ciclo_transporte.model.CicloTransporte;
import cl.duoc.mineria.ciclo_transporte.model.Destino;
import cl.duoc.mineria.ciclo_transporte.model.EstadoCiclo;
import cl.duoc.mineria.ciclo_transporte.repository.CicloTransporteRepository;

@Service
public class CicloTransporteService {

    private final CicloTransporteRepository cicloRepository;
    private final ExternalValidationService validationService;
    private final ExternalAsignadorService asignadorService;

    public CicloTransporteService(CicloTransporteRepository cicloRepository,
            ExternalValidationService validationService, ExternalAsignadorService asignadorService) {
        this.cicloRepository = cicloRepository;
        this.validationService = validationService;
        this.asignadorService = asignadorService;
    }

    @Transactional(readOnly = true)
    public List<CicloTransporte> obtenerTodosLosCiclos() {
        return cicloRepository.findAll();
    }

    @Transactional
    public CicloTransporte iniciarCiclo(Long turnoId, Long camionId, Long palaId, Long paleroId, Long materialId) {
        
        if (!validationService.verificarTurnoExiste(turnoId)) {
            throw new TurnoInvalidoException(
                    "No se puede iniciar el ciclo: El ID de turno " + turnoId + " no existe en los registros.");
        }

        // 1. Validar Camión (8084)
        if (!validationService.verificarCamionActivo(camionId)) {
            throw new CicloValidationException(
                    "El ID de camión " + camionId + " no está autorizado o está en mantención.");
        }

        // 2. Validar Pala (8083)
        if (!validationService.verificarPalaActiva(palaId)) {
            throw new CicloValidationException("El ID de pala " + palaId + " no está operativa.");
        }

        // 3. Validar Operario (8081)
        if (!validationService.verificarPaleroAutorizado(paleroId)) {
            throw new CicloValidationException("No se puede iniciar el ciclo: El ID de usuario "
                    + paleroId + " no posee el rol de OPERADOR_PALA o no se encuentra activo.");
        }

        // 4. Validar Material (8086)
        if (!validationService.verificarMaterialValido(materialId)) {
            throw new CicloValidationException("El ID de material " + materialId
                    + " no corresponde a ningún tipo mineralógico válido en la faena.");
        }

        CicloTransporte nuevoCiclo = new CicloTransporte();
        nuevoCiclo.setTurnoId(turnoId);
        nuevoCiclo.setCamionId(camionId);
        nuevoCiclo.setPalaId(palaId);
        nuevoCiclo.setPaleroId(paleroId);
        nuevoCiclo.setMaterialId(materialId);
        nuevoCiclo.setEstadoCiclo(EstadoCiclo.EN_TRANSITO);
        nuevoCiclo.setFechaHoraInicio(LocalDateTime.now());

        return cicloRepository.save(nuevoCiclo);
    }

    @Transactional
    public CicloTransporte actualizarEstado(Long cicloId, EstadoCiclo nuevoEstado, Destino destino,
            Double toneladas) {
        CicloTransporte ciclo = cicloRepository.findById(cicloId)
                .orElseThrow(() -> new ResourceNotFoundException("El ciclo con ID " + cicloId + " no existe."));

        ciclo.setEstadoCiclo(nuevoEstado);

        // 💡 SOLUCIÓN: Quitamos el Asignador roto. Si envías el destino manual en el
        // JSON, se respeta.
        if (destino != null) {
            ciclo.setDestino(destino);
        }
        // Fallback de seguridad: Si no envías destino manual pero el estado pasa a
        // tránsito/cargando, el sistema lo calcula solo
        else if (ciclo.getDestino() == null
                && (nuevoEstado == EstadoCiclo.CARGANDO || nuevoEstado == EstadoCiclo.EN_TRANSITO)) {
            if (ciclo.getMaterialId() != null && ciclo.getMaterialId() == 3) { // 3 = Estéril
                ciclo.setDestino(Destino.RELAVES); // ⚠️ Ajustar a RELAVE o RELAVES según tu Enum físico
            } else {
                ciclo.setDestino(Destino.CHANCADO);
            }
        }

        if (toneladas != null) {
            ciclo.setToneladasCargadas(toneladas);
        }

        if (nuevoEstado == EstadoCiclo.COMPLETADO) {
            ciclo.setFechaHoraFin(LocalDateTime.now());
        }

        // Aseguramos el guardado explícito en la base de datos
        return cicloRepository.save(ciclo);
    }

    @Transactional(readOnly = true)
    public List<CicloTransporte> obtenerCiclosPorCamion(Long camionId) {
        return cicloRepository.findByCamionId(camionId);
    }

    @Transactional(readOnly = true)
    public List<CicloTransporte> obtenerCiclosPorTurno(Long turnoId) {
        return cicloRepository.findByTurnoId(turnoId);
    }

    // Convierte el String del Asignador al enum local de forma segura
    private Destino mapearDestinoExterno(String destinoStr) {
        try {
            return Destino.valueOf(destinoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CicloValidationException(
                    "El destino recibido del Asignador no es válido: '" + destinoStr +
                            "'. Valores aceptados: " + Arrays.toString(Destino.values()));
        }
    }
}