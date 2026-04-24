package com.meulivro.demo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meulivro.demo.dto.LivroDTO;
import com.meulivro.demo.dto.LivroRequestDTO;
import com.meulivro.demo.entities.Livro;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.meulivro.demo.repositories.LivroRepository livroRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCadastrarLivroComMensagemDeSucesso() throws Exception {
        LivroRequestDTO dto = new LivroRequestDTO("O Hobbit", "J.R.R. Tolkien", 1937);

        mockMvc.perform(post("/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensagem").value("Livro criado com sucesso!"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveRetornar403AoTentarCadastrarSendoUser() throws Exception {
        LivroDTO dto = new LivroDTO(null, "Livro Proibido", "Autor X", 2024);

        mockMvc.perform(post("/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveListarLivrosRetornando200ParaUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/livros"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar200AtualizarLivroExistenteSendoAdmin() throws Exception {
        Livro livroSalvo = livroRepository.save(new com.meulivro.demo.entities.Livro("Antigo", "Autor Antigo", 2000));
        LivroDTO dto = new LivroDTO(null, "Novo Titulo", "Novo Autor", 2024);

        mockMvc.perform(MockMvcRequestBuilders.put("/livros/" + livroSalvo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Novo Titulo"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar404AoAtualizarIdInexistente() throws Exception {
        LivroDTO dto = new LivroDTO(null, "Titulo", "Autor", 2024);

        mockMvc.perform(MockMvcRequestBuilders.put("/livros/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar204AoDeletarLivroExistenteSendoAdmin() throws Exception {
        Livro livroSalvo = livroRepository.save(new com.meulivro.demo.entities.Livro("A Deletar", "Autor", 2000));

        mockMvc.perform(MockMvcRequestBuilders.delete("/livros/" + livroSalvo.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveRetornar403AoTentarDeletarSendoUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/livros/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveBuscarLivroPorTrechoDoTitulo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/livros/buscar")
                .param("titulo", "Senhor")) // "Senhor" deve achar "O Senhor dos Anéis" cadastrado no Data Seeding
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("O Senhor dos Anéis"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveBuscarLivroPorAnoEAutor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/livros/buscar")
                .param("autor", "orwell") // Ignorando maiúsculas
                .param("ano", "1949"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("1984"))
                .andExpect(jsonPath("$[0].autor").value("George Orwell"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveBuscarLivroPorIdNaBuscaPersonalizada() throws Exception {
        // Primeiro salva um livro para garantir que o ID exista
        Livro livro = livroRepository.save(new Livro("Livro por ID", "Autor Teste", 2024));

        mockMvc.perform(MockMvcRequestBuilders.get("/livros/buscar")
                .param("id", livro.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Livro por ID"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveRetornarMensagemQuandoNenhumFiltroDerMatch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/livros/buscar")
                .param("titulo", "LivroInexistente2050xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Nenhum livro encontrado com os filtros informados."));
    }
}