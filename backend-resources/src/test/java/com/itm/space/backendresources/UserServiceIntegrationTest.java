package com.itm.space.backendresources;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.exception.BackendResourcesException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.ws.rs.core.Response;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j// Аннотация для логирования
@ExtendWith(MockitoExtension.class)// Расширяем класс с помощью Mockito
@WithMockUser(username = "mihail", password = "123", authorities = "ROLE_MODERATOR")
public class UserServiceIntegrationTest extends BaseIntegrationTest {
    // для создания заглушки (mock) объекта Keycloak
    @MockBean
    private Keycloak keycloak;

    // Внедрение зависимости MockMvc для тестирования контроллеров
    @Autowired
    private MockMvc mockMvc;
    // ресурс (resource) в Keycloak,
    // который предоставляет операции для работы с определенной областью (realm)
    private RealmResource realmResource;
    // ресурс для работы с пользователями в Keycloak
    private UsersResource usersResource;
    // HTTP-ответ от сервера
    private Response response;
    // для создания и отправки запросов на создание пользователя
    private UserRequest userRequest;
    // ресурс для работы с конкретным пользователем
    private UserResource userResource;
    // представление пользователя
    private UserRepresentation userRepresentation;

    // перед каждым тестом - создаем заглушки и ресурс для работы с конкретным пользователем
    @BeforeEach
    void init() {
        realmResource = mock(RealmResource.class);// Создаем заглушку для RealmResource
        usersResource = mock(UsersResource.class);// Создаем заглушку для UsersResource
        response = mock(Response.class);// Создаем заглушку для Response
        userRequest = new UserRequest("mihail", "mihailjava@gmail.com", "123", "Anastasia", "Akopova");
        userResource = mock(UserResource.class);
        userRepresentation = new UserRepresentation();
        userRepresentation = mock(UserRepresentation.class);
    }

    // 1. Тест на успешное создание пользователя
    @Test
    public void successfulCreateUserByModerator() throws Exception {
        // Настройка поведения заглушек
        when(keycloak.realm(ArgumentMatchers.anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(ArgumentMatchers.any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatusInfo()).thenReturn(Response.Status.CREATED);

        // Выполнение запроса на создание пользователя
        this.mvc.perform(requestWithContent(post("/api/users"), userRequest))
                .andExpect(status().is(200));

        // Проверка, что метод create был вызван
        verify(usersResource).create(any(UserRepresentation.class));
    }

    // 2. Тест на обработку ошибки при создании пользователя с некорректными данными
    /*
        Error: response status is 400
        {
          "password": "Password should be greater than 2 characters long",
          "email": "Email should be valid",
          "username": "Username should be between 2 and 30 characters long"
        }
     */
    @Test
    public void validationErrorWhenCreatedUserByModerator() throws Exception {
        UserRequest request = new UserRequest("m", "", "1", "Mihail", "Akopov");
        mvc.perform(requestWithContent(post("/api/users"),
                        request))
                .andExpect(status().is(400));
    }

    // 3. Тест на обработку ошибки при создании пользователя
    // Например, если уже есть пользователь с таким username
    @Test
    public void createUserByModeratorShouldThrowException() throws Exception {
        when(keycloak.realm(ArgumentMatchers.anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(ArgumentMatchers.any(UserRepresentation.class))).thenReturn(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
        mvc.perform(requestWithContent(post("/api/users"), userRequest))
                .andExpect(status().is(500));
    }

    // 1. Тест на успешное получение пользователя по идентификатору.
    @Test
    public void successfulGetUserByIdByModerator() throws Exception {
        String id = "940ccf47-9589-4c9f-84b2-bf73e93f1f8c";
        when(keycloak.realm(ArgumentMatchers.anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(eq(id))).thenReturn(userResource);

        when(userResource.roles()).thenReturn(mock(RoleMappingResource.class));
        when(userResource.roles().getAll()).thenReturn(mock(MappingsRepresentation.class));

        when(userRepresentation.getFirstName()).thenReturn("Mihail");
        when(userRepresentation.getLastName()).thenReturn("Akopov");
        when(userRepresentation.getEmail()).thenReturn("mihailjava@gmail.com");

        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        mvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk());
    }

    // 2. Тест на обработку ошибки при запросе пользователя с некорректным идентификатором.
    @Test
    public void exceptionGetUserByIdByModerator() throws Exception {
        String id = "940ccf47-9589-4c9f-84b2-bf73e93f1f8c";
        when(keycloak.realm(ArgumentMatchers.anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(realmResource.users().get(eq(id))).thenThrow(new BackendResourcesException("message", HttpStatus.INTERNAL_SERVER_ERROR));
        mvc.perform(get("/api/users/{id}", id))
                .andExpect(status().is(500));
    }

    // 3. Тест на обработку ошибки при запросе пользователя, которого не существует в системе.
    @Test
    public void unsuccessful404GetUserByIdByModerator() throws Exception {
        String id = "a49ae7ef-26df-466c-ade1-8a034b1a1a19";
        when(keycloak.realm(ArgumentMatchers.anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        // Swagger UI на эндпоинте /api/users/a49ae7ef-26df-466c-ade1-8a034b1a1a19
        // возвращает Error: response status is 500 с телом "HTTP 404 Not Found"
        when(realmResource.users().get(eq(id))).thenThrow(new BackendResourcesException("HTTP 404 Not Found", HttpStatus.INTERNAL_SERVER_ERROR));
        mvc.perform(get("/api/users/{id}", id))
                .andExpect(status().is(500))
                .andExpect(content().string(containsString("HTTP 404 Not Found")));
    }
}

