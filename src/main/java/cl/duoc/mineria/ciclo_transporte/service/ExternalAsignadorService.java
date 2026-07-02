package cl.duoc.mineria.ciclo_transporte.service;

import cl.duoc.mineria.ciclo_transporte.dto.DestinoAsignadoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.core.ParameterizedTypeReference;
import java.util.Map;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class ExternalAsignadorService {

    private final WebClient webClient;

    public String obtenerDestinoAutomatico(Long palaId, String clasificacionMaterial) {
        try {
            // 1. Hacemos la "llamada" al microservicio Asignador (que está en el puerto 8087)
            DestinoAsignadoDTO respuesta = this.webClient.get()
                    .uri("http://asignador/api/v1/asignador/destino?palaId={palaId}&clasificacionMaterial={material}", 
                         palaId, clasificacionMaterial)
                    .retrieve()
                    .bodyToMono(DestinoAsignadoDTO.class) // 2. Atrapamos la respuesta en la "cajita" que creamos en el Paso 1
                    .block(); // 3. El programa se pausa un milisegundo aquí esperando la respuesta

            // 4. Si todo salió bien, sacamos el destino de la cajita ("CHANCADOR" o "RELAVE") y lo devolvemos
            if (respuesta != null && respuesta.getDestinoAsignado() != null) {
                return respuesta.getDestinoAsignado();
            }
            throw new RuntimeException("El Asignador no devolvió un destino válido.");
            
        } catch (Exception e) {
            throw new RuntimeException("Error consultando al Asignador (Puerto 8087): " + e.getMessage());
        }
    }

    public String obtenerDestinoPorMaterialId(Long palaId, Long materialId) {
        try {
            Map<String, Object> material = this.webClient.get()
                    .uri("http://materiales/api/v1/materiales/{id}", materialId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (material == null || !material.containsKey("clasificacion")) {
                throw new IllegalArgumentException("El material " + materialId + " no tiene una clasificación geológica válida.");
            }

            String clasificacion = (String) material.get("clasificacion");
            return obtenerDestinoAutomatico(palaId, clasificacion);

        } catch (Exception e) {
            throw new RuntimeException("Error en la cadena de asignación automática: " + e.getMessage());
        }
    }
}