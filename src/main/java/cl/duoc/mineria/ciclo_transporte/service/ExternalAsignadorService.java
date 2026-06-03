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
                    .uri("http://127.0.0.1:8087/api/v1/asignador/destino?palaId={palaId}&clasificacionMaterial={material}", 
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
            // 1. Usamos Map<String, Object> y ParameterizedTypeReference para evitar el "raw type"
            Map<String, Object> material = this.webClient.get()
                    .uri("http://127.0.0.1:8086/api/v1/materiales/{id}", materialId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            String clasificacion = "SULFUROS"; // Valor por defecto seguro
            if (material != null) {
                if (material.containsKey("clasificacion")) clasificacion = (String) material.get("clasificacion");
                else if (material.containsKey("nombre")) clasificacion = (String) material.get("nombre");
                else if (material.containsKey("tipo")) clasificacion = (String) material.get("tipo");
            }

            // 2. Con la clasificación lista, llamamos a nuestro método original que consulta al Asignador
            return obtenerDestinoAutomatico(palaId, clasificacion);

        } catch (Exception e) {
            throw new RuntimeException("Error en la cadena de asignación automática: " + e.getMessage());
        }
    }
}