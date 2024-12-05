package com.itm.space.backendresources;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.keycloak.admin.client.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc// Автоматическая настройка MockMvc для тестирования контроллеров
public class UserControllerIntegrationTest extends BaseIntegrationTest {

    // Внедрение MockMvc для выполнения тестовых запросов к контроллеру
    @Autowired
    private MockMvc mockMvc;

    // для преобразования объектов Java в JSON и обратно
    @Autowired
    private ObjectMapper objectMapper;

    // заглушка для UserService
    @MockBean
    private UserService userService;

    // ресурс для работы с конкретным пользователем
    @MockBean
    private UserResource userResource;

    @BeforeEach// Метод, который будет выполнен перед каждым тестом
    public void setup(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        System.out.println("Запущен : " + testName);
    }

    @AfterEach// Метод, который будет выполнен после каждого теста
    public void afterTestStatus(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        System.out.println("Завершен : " + testName);
    }

    // Тест на успешное создание пользователя
    @Test
    @DisplayName("Тест создания нового пользователя")
    @WithMockUser(roles = "MODERATOR") // имитируем аутентификацию пользователя с ролью "MODERATOR"
    public void testCreateUser() throws Exception {
        // некий пользователь, который будет создаваться
        UserRequest userRequest = new UserRequest("someUserName", "someusername@test.com", "somePassword", "Ivan", "Ivanov");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users") // URL для создания пользователя
                        .contentType(MediaType.APPLICATION_JSON) // тип контента - JSON
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk()); // ожидаем статус 200 OK
    }

    @Test
    @DisplayName("Тест создания нового пользователя - неавторизованный пользователь")
    @WithAnonymousUser // неавторизованный пользователь
    public void testCreateUserNotAuth() throws Exception {
        // некий пользователь, который будет создаваться
        UserRequest userRequest = new UserRequest("someUserName", "someusername@test.com", "somePassword", "Ivan", "Ivanov");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users") // POST-запрос на "/api/users"
                        .contentType(MediaType.APPLICATION_JSON) // тип контента - JSON
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized()); // ожидаем статус 401 Unauthorized
    }

    // Тест на обработку ошибки при создании пользователя с некорректными данными
    @Test
    @DisplayName("Тест на обработку ошибки при создании пользователя с некорректными данными")
    @WithMockUser(roles = "MODERATOR")// Имитация пользователя с ролью "MODERATOR"
    public void testCreateUserWithInvalidData() throws Exception {
        // Создаем пользователя с некорректными данными
        UserRequest userRequest = new UserRequest("a", "", "1", "Anastasia", "Akopova");

        // Выполнение POST-запроса на создание пользователя
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()); // Ожидаем статус 400 Bad Request
    }

    // тест получения пользователя по ID - можно использовать @ParameterizedTest
    // с заранее созданными перечисленными ID пользователей
    // или генерировать UUIDs с помощью UUID.randomUUID()
    @ParameterizedTest
    @DisplayName("Тест получения пользователя по ID")
    @WithMockUser(roles = "MODERATOR") // имитируем аутентификацию пользователя с ролью "MODERATOR"
    @ValueSource(strings = {"123e4567-e89b-12d3-a456-426614174000", "123e4567-e89b-12d3-a456-426614174001",
            "123e4567-e89b-12d3-a456-426614174002"}) // заранее созданные ID пользователя - корректные форматы UUID
    public void testGetUserByValidId(String userId) throws Exception {
        // Выполняем GET-запрос на получение пользователя по ID
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", userId))
                .andExpect(MockMvcResultMatchers.status().isOk()); // ожидаем статус 200 OK
    }

    @ParameterizedTest
    @DisplayName("Тест получения пользователя по ID - неавторизованный пользователь")
    @WithAnonymousUser // неавторизованный пользователь
    @ValueSource(strings = {"123e4567-e89b-12d3-a456-426614174000", "123e4567-e89b-12d3-a456-426614174001",
            "123e4567-e89b-12d3-a456-426614174002"}) // заранее созданные ID пользователя - корректные форматы UUID
    public void testGetUserByValidIdNotAuth(String userId) throws Exception {
        // Выполняем GET-запрос на получение пользователя по ID
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", userId))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized()); // ожидаем статус 401 Unauthorized
    }

    // тест получения пользователя по ID - некорректные форматы ID - можно использовать @ParameterizedTest
    // с заранее созданными перечисленными ID пользователей
    // или генерировать UUIDs с помощью UUID.randomUUID()
    @ParameterizedTest
    @DisplayName("Тест получения пользователя по ID - некорректный формат ID")
    @WithMockUser(roles = "MODERATOR") // имитируем аутентификацию пользователя с ролью "MODERATOR"
    @ValueSource(strings = {"12-12-12", "0123", "364-254"}) // некорректный формат ID
    public void testGetUserByIdWithInvalidId(String userId) throws Exception {
        // Выполняем GET-запрос на получение пользователя по некорректному ID
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", userId))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()); // ожидаем статус 400 Bad Request
    }

    // тест получения страницы приветствия
    @Test
    @DisplayName("Тест получения страницы приветствия пользователем с ролью MODERATOR")
    @WithMockUser(roles = "MODERATOR") // имитируем аутентификацию пользователя с ролью "MODERATOR"
    public void testHello() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/hello")) // GET-запрос на "/api/users/hello"
                .andExpect(MockMvcResultMatchers.status().isOk()); // ожидаем статус 200 OK
    }

    // тест получения страницы приветствия - анонимный пользователь
    @Test
    @DisplayName("Тест получения страницы приветствия анонимным пользователем")
    @WithAnonymousUser // имитируем анонимного пользователя
    public void testHelloNoAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/hello")) // GET-запрос на "/api/users/hello"
                .andExpect(MockMvcResultMatchers.status().isUnauthorized()); // ожидаем статус 401 Unauthorized
    }
}
