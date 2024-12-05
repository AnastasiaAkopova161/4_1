package com.itm.space.backendresources.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity// Аннотация, включающая веб-безопасность
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    // Метод, создающий и возвращающий объект SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)// Отключаем защиту CSRF
                .authorizeHttpRequests(requests -> requests// Настраиваем авторизацию для HTTP-запросов
                        .anyRequest().permitAll())// Разрешаем доступ ко всем запросам
                .oauth2ResourceServer()// Настраиваем сервер ресурсов OAuth2
                .jwt()// Указываем, что будем использовать JWT для аутентификации
                .jwtAuthenticationConverter(SecurityConfiguration::convertJwtToken);// Указываем метод для преобразования JWT в токен аутентификации
        return http.build();// Строим и возвращаем цепочку фильтров безопасности
    }

    // Метод для преобразования JWT в токен аутентификации
    private static JwtAuthenticationToken convertJwtToken(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();// Создаем коллекцию для хранения авторизаций
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(jwt, authorities);// Создаем токен аутентификации
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");//Получаем доступ к данным о ролях из JWT
        List<String> roles = (List<String>) realmAccess.get("roles");// Извлекаем роли из данных о доступе
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));// Добавляем каждую роль в коллекцию авторизаций
        }
        return new JwtAuthenticationToken(jwt, authorities, authenticationToken.getName());// Возвращаем токен аутентификации с ролями
    }
}
