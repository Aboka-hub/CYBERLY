package cb.empty.cyberly.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cyberlyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CYBERLY API")
                        .version("1.0")
                        .description("""
                                API для системы мониторинга безопасности аккаунтов.

                                **Аутентификация:** Bearer JWT токен.
                                Получите токен через `POST /user/login` и передавайте его в заголовке:
                                `Authorization: Bearer <token>`

                                **Защита от брутфорса:** после 5 неудачных попыток входа аккаунт
                                блокируется автоматически (HTTP 423 Locked).
                                """)
                )
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT токен из /user/login")
                        )
                );
    }
}
