package cl.sprint_rocket_ai.ms_checkpoint.commons.infrastructure;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-checkpoint API")
                        .description("Microservicio de checkpoint para gestión de actividades y recordatorios de desarrollo")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SprintRocket AI")
                                .email("soporte@sprintrocket.ai"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://sprintrocket.ai")));
    }
}
