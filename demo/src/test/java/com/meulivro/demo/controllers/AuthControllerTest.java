package com.meulivro.demo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meulivro.demo.dto.LoginDTO;
import com.meulivro.demo.dto.RegisterDTO;
import com.meulivro.demo.entities.User;
import com.meulivro.demo.entities.UserRole;
import com.meulivro.demo.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void deveRetornar201AoCadastrarNovoUsuario() throws Exception {
        RegisterDTO dto = new RegisterDTO("admin_test", "123456", UserRole.ROLE_ADMIN);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRetornar400AoTentarCadastrarUsuarioExistente() throws Exception {
        RegisterDTO dto = new RegisterDTO("user_test", "senha", UserRole.ROLE_USER);

        // Primeiro cadastro (sucesso)
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // Segundo cadastro com mesmo login (falha)
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400AoTentarCadastrarSemLogin() throws Exception {
        RegisterDTO dto = new RegisterDTO("", "123456", UserRole.ROLE_USER);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400AoTentarCadastrarSemSenha() throws Exception {
        RegisterDTO dto = new RegisterDTO("user_test", "", UserRole.ROLE_USER);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400AoTentarCadastrarSemRole() throws Exception {
        // Passando JSON em formato String direto para simular a ausência do Enum Role
        String jsonPayload = "{\"login\":\"user_test\", \"password\":\"123456\"}";

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveGarantirQueASenhaSejaSalvaComHashBCrypt() throws Exception {
        RegisterDTO dto = new RegisterDTO("hash_test", "minhasenha", UserRole.ROLE_USER);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        User savedUser = (User) userRepository.findByLogin("hash_test");
        
        assertNotNull(savedUser);
        assertNotEquals("minhasenha", savedUser.getPassword());
        assertTrue(savedUser.getPassword().startsWith("$2a$")); // Padrão de início de um hash BCrypt
    }

    @Test
    void deveRetornar200ETokenAoRealizarLoginComSucesso() throws Exception {
        // 1. Cadastra o usuário primeiro
        RegisterDTO registerDTO = new RegisterDTO("login_test", "senha123", UserRole.ROLE_USER);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated());

        // 2. Tenta fazer o login
        LoginDTO loginDTO = new LoginDTO("login_test", "senha123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void deveRetornar403Ou401AoTentarLoginComSenhaIncorreta() throws Exception {
        // 1. Cadastra o usuário
        RegisterDTO registerDTO = new RegisterDTO("login_fail_test", "senha123", UserRole.ROLE_USER);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated());

        // 2. Tenta fazer login com senha errada
        LoginDTO loginDTO = new LoginDTO("login_fail_test", "senha_errada");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isForbidden()); // Spring Security default para bad credentials geralmente é 403 ou 401 dependendo da config
    }
}