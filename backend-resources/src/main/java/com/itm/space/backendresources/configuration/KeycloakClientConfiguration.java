package com.itm.space.backendresources.configuration;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.keycloak.OAuth2Constants.CLIENT_CREDENTIALS;
import static org.keycloak.OAuth2Constants.PASSWORD;

@Configuration
public class KeycloakClientConfiguration {
    //Получение значения секрета клиента из файла конфигурации
    @Value("${keycloak.credentials.secret}")
    private String secretKey;
    //Получение идентификатора клиента из файла конфигурации
    @Value("${keycloak.resource}")
    private String clientId;
    // Получение URL сервера аутентификации из файла конфигурации
    @Value("${keycloak.auth-server-url}")
    private String authUrl;
    // Получение имени реальности (realm) из файла конфигурации
    @Value("${keycloak.realm}")
    private String realm;

    // Метод, создающий и возвращающий экземпляр Keycloak
    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()// Начинаем создание клиента Keycloak
                .serverUrl(authUrl)// Устанавливаем URL сервера аутентификации
                .realm(realm) // Устанавливаем имя реальности
                .grantType(CLIENT_CREDENTIALS)// Указываем тип гранта (CLIENT_CREDENTIALS)
                .clientId(clientId)// Устанавливаем идентификатор клиента
                .clientSecret(secretKey)// Устанавливаем секрет клиента
                .build();// Создаем и возвращаем экземпляр Keycloak
    }
}
