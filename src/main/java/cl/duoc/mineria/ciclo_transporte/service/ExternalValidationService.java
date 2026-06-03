package cl.duoc.mineria.ciclo_transporte.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class ExternalValidationService {

    private final WebClient webClient;

    public ExternalValidationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    // 🚚 CAMIONES (Puerto 8084)
    public boolean verificarCamionActivo(Long camionId) {
        try {
            Boolean existe = this.webClient.get()
                .uri("http://127.0.0.1:8084/api/v1/camiones/existe/{id}", camionId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
            return existe != null && existe;
        } catch (Exception e) {
            // Imprime la causa real (ej: Connection refused o 404 Not Found)
            System.out.println("⚠️ [Transporte] Error en Camiones (8084): " + e.getMessage());
            return true; 
        }
    }

    // 🏗️ PALAS (Puerto 8083)
    public boolean verificarPalaActiva(Long palaId) {
        try {
            Boolean existe = this.webClient.get()
                .uri("http://127.0.0.1:8083/api/v1/palas/existe/{id}", palaId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
            return existe != null && existe;
        } catch (Exception e) {
            System.out.println("⚠️ [Transporte] Error en Palas (8083): " + e.getMessage());
            return true;
        }
    }

    // 👤 USUARIOS / PALEROS (Puerto 8081)
    public boolean verificarPaleroAutorizado(Long paleroId) {
        try {
            // Invoca al endpoint especializado que valida la existencia y el rol OPERADOR_PALA
            Boolean esPaleroValido = this.webClient.get()
                .uri("http://127.0.0.1:8081/api/v1/usuarios/paleros/{id}", paleroId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
            return esPaleroValido != null && esPaleroValido;
        } catch (WebClientResponseException.BadRequest e) {
            System.out.println("❌ [Validación] El usuario existe pero no cuenta con el rol OPERADOR_PALA.");
            return false;
        } catch (Exception e) {
            System.out.println("⚠️ [Transporte] Error en Usuarios (8081): " + e.getMessage());
            return true; 
        }
    }

    // 💎 MATERIALES (Puerto 8086)
    public boolean verificarMaterialValido(Long materialId) {
        try {
            Boolean existe = this.webClient.get()
                .uri("http://127.0.0.1:8086/api/v1/materiales/existe/{id}", materialId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
            return existe != null && existe;
        } catch (Exception e) {
            System.out.println("⚠️ [Transporte] Error en Materiales (8086): " + e.getMessage());
            return true;
        }
    }
}