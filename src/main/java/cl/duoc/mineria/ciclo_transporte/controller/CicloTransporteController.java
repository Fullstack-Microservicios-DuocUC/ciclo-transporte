package cl.duoc.mineria.ciclo_transporte.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.mineria.ciclo_transporte.dto.ActualizarEstadoDTO;
import cl.duoc.mineria.ciclo_transporte.dto.IniciarCicloDTO;
import cl.duoc.mineria.ciclo_transporte.model.CicloTransporte;
import cl.duoc.mineria.ciclo_transporte.service.CicloTransporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/ciclos-transporte")
@Tag(name = "Gestión de Ciclos de Transporte", description = "Operaciones para registrar y seguir el ciclo de vida de los viajes de transporte de material.")
public class CicloTransporteController {

    private final CicloTransporteService cicloService;

    public CicloTransporteController(CicloTransporteService cicloService) {
        this.cicloService = cicloService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los ciclos de transporte", description = "Obtiene una lista completa de todos los ciclos de transporte registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de ciclos obtenida con éxito")
    public ResponseEntity<List<CicloTransporte>> listarTodos() {
        return ResponseEntity.ok(cicloService.obtenerTodosLosCiclos());
    }

    @PostMapping("/iniciar")
    @Operation(summary = "Iniciar un nuevo ciclo de transporte", description = "Registra el inicio de un nuevo ciclo, validando la disponibilidad de camión, pala y palero.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ciclo iniciado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o validación de negocio fallida (ej. camión no disponible)")
    })
    public ResponseEntity<CicloTransporte> iniciarViaje(@Valid @RequestBody IniciarCicloDTO dto) {
        CicloTransporte guardado = cicloService.iniciarCiclo(
            dto.getTurnoId(),
            dto.getCamionId(), 
            dto.getPalaId(), 
            dto.getPaleroId(),
            dto.getMaterialId()
        );
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    @PutMapping("/estado")
    @Operation(summary = "Actualizar el estado de un ciclo", description = "Modifica el estado de un ciclo de transporte existente (ej. EN_CARGA, EN_TRANSITO, COMPLETADO).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado del ciclo actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Ciclo no encontrado con el ID proporcionado")
    })
    public ResponseEntity<CicloTransporte> actualizarEstado(@Valid @RequestBody ActualizarEstadoDTO dto) {
        CicloTransporte actualizado = cicloService.actualizarEstado(
            dto.getId(),
            dto.getNuevoEstado(),
            dto.getDestino(),
            dto.getToneladas()
        );
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/camion/{camionId}")
    @Operation(summary = "Listar ciclos por camión", description = "Obtiene el historial de ciclos de transporte para un camión específico.")
    @ApiResponse(responseCode = "200", description = "Historial de ciclos del camión obtenido con éxito")
    public ResponseEntity<List<CicloTransporte>> listarPorCamion(@PathVariable Long camionId) {
        return ResponseEntity.ok(cicloService.obtenerCiclosPorCamion(camionId));
    }

    @GetMapping("/turno/{turnoId}")
    @Operation(summary = "Listar ciclos por turno", description = "Obtiene todos los ciclos de transporte que se realizaron durante un turno específico.")
    @ApiResponse(responseCode = "200", description = "Ciclos de transporte del turno obtenidos con éxito")
    public ResponseEntity<List<CicloTransporte>> listarPorTurno(@PathVariable Long turnoId) {
        return ResponseEntity.ok(cicloService.obtenerCiclosPorTurno(turnoId));
    }
}