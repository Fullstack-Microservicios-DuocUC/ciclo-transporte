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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/ciclos-transporte")
public class CicloTransporteController {

    private final CicloTransporteService cicloService;

    public CicloTransporteController(CicloTransporteService cicloService) {
        this.cicloService = cicloService;
    }

    @GetMapping
    public ResponseEntity<List<CicloTransporte>> listarTodos() {
        return ResponseEntity.ok(cicloService.obtenerTodosLosCiclos());
    }

    @PostMapping("/iniciar")
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
    public ResponseEntity<List<CicloTransporte>> listarPorCamion(@PathVariable Long camionId) {
        return ResponseEntity.ok(cicloService.obtenerCiclosPorCamion(camionId));
    }

    @GetMapping("/turno/{turnoId}")
    public ResponseEntity<List<CicloTransporte>> listarPorTurno(@PathVariable Long turnoId) {
        return ResponseEntity.ok(cicloService.obtenerCiclosPorTurno(turnoId));
    }
}