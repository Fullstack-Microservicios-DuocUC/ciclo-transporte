package cl.duoc.mineria.ciclo_transporte.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // 1. Exponemos de forma manual el Builder por si ExternalValidationService lo necesita directamente
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    // 2. Creamos el WebClient usando el Bean que definimos arriba
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}