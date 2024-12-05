package com.itm.space.backendresources.controller;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor// Автоматически генерирует конструктор для всех финальных полей
public class UserController {
    private final UserService userService;

    // Метод для создания нового пользователя
    @PostMapping
    @Secured("ROLE_MODERATOR")// Ограничивает доступ к методу для пользователей с ролью "MODERATOR"
    @SecurityRequirement(name = "oauth2_auth_code")// Указывает требование безопасности для Swagger
    public void create(@RequestBody @Valid UserRequest userRequest) {
        userService.createUser(userRequest);// Вызывает сервис для создания пользователя
    }

    // Метод для получения пользователя по ID
    @GetMapping("/{id}")
    @Secured("ROLE_MODERATOR")
    @SecurityRequirement(name = "oauth2_auth_code")
    public UserResponse getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);// Вызывает сервис для получения пользователя по ID
    }

    // Метод для тестирования аутентификации
    @GetMapping("/hello")
    @Secured("ROLE_MODERATOR")
    @SecurityRequirement(name = "oauth2_auth_code")
    public String hello() {
        // Возвращает имя аутентифицированного пользователя из контекста безопасности
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
