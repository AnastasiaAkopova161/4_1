package com.itm.space.backendresources.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "oauth2_auth_code",// Имя схемы безопасности
        type = SecuritySchemeType.OAUTH2,// Тип схемы безопасности (OAuth2)
        flows = @OAuthFlows(// Определяем потоки OAuth
                authorizationCode = @OAuthFlow(// Определяем поток авторизации
                        authorizationUrl = "http://backend-keycloak-auth:8080/auth/realms/ITM/protocol/openid-connect/auth",// URL для авторизации
                        tokenUrl = "http://backend-keycloak-auth:8080/auth/realms/ITM/protocol/openid-connect/token",// URL для получения токена
                        scopes = {
                                @OAuthScope(name = "openid", description = "Read access")// Область "openid" с описанием
                        }
                )
        ),
        in = SecuritySchemeIn.HEADER// Указываем, что схема безопасности будет использоваться в заголовке
)
public class OpenApiConfiguration {

    // Метод, создающий и возвращающий объект OpenAPI
    @Bean
    public OpenAPI publicApi() {
        return new OpenAPI();
    }
}
