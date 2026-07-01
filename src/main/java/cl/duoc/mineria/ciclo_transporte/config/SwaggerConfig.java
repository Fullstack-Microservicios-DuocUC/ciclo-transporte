package cl.duoc.mineria.ciclo_transporte.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("API de Ciclos de Transporte")
                    .version("1.0.0")
                    .description("Microservicio para la gestión de los ciclos de transporte de material, desde la carga hasta la descarga."));
    }
}