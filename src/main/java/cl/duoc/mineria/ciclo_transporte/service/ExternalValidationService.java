package cl.duoc.mineria.ciclo_transporte.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import cl.duoc.mineria.ciclo_transporte.exception.ServicioExternoNoDisponibleException;

@Service
public class ExternalValidationService {

    private final WebClient webClient;

    public ExternalValidationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    // CAMIONES (Puerto 8084)
    public boolean verificarCamionActivo(Long camionId) {
        try {
            Boolean existe = this.webClient.get()
                .uri("http://camiones/api/v1/camiones/existe/{id}", camionId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
            return existe != null && existe;
        } catch (WebClientResponseException.NotFound e) {
            return false; // el recurso no existe: respuesta legítima
        } catch (Exception e) {
            // Timeout, conexión rechazada, 5xx, etc: el servicio no puede confirmar -> rechazar la operación
            throw new ServicioExternoNoDisponibleException(
                "No se pudo validar el camión " + camionId + ": " + e.getMessage());
        }
    }

    // PALAS (Puerto 8083)
    public boolean verificarPalaActiva(Long palaId) {
        try {
            Boolean existe = this.webClient.get()
                .uri("http://palas/api/v1/palas/existe/{id}", palaId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
            return existe != null && existe;
        } catch (Exception e) {
            System.out.println("[Transporte] Error en Palas (8083): " + e.getMessage());
            return true;
        }
    }

    // USUARIOS / PALEROS (Puerto 8081)
    public boolean verificarPaleroAutorizado(Long paleroId) {
        try {
            // Invoca al endpoint especializado que valida la existencia y el rol OPERADOR_PALA
            Boolean esPaleroValido = this.webClient.get()
                .uri("http://usuarios/api/v1/usuarios/paleros/{id}", paleroId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
            return esPaleroValido != null && esPaleroValido;
        } catch (WebClientResponseException.BadRequest e) {
            System.out.println("[Validación] El usuario existe pero no cuenta con el rol OPERADOR_PALA.");
            return false;
        } catch (Exception e) {
            System.out.println("[Transporte] Error en Usuarios (8081): " + e.getMessage());
            return true; 
        }
    }

    // MATERIALES (Puerto 8086)
    public boolean verificarMaterialValido(Long materialId) {
        try {
            Boolean existe = this.webClient.get()
                .uri("http://materiales/api/v1/materiales/existe/{id}", materialId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
            return existe != null && existe;
        } catch (Exception e) {
            System.out.println("[Transporte] Error en Materiales (8086): " + e.getMessage());
            return true;
        }
    }
}