package com.meulivro.demo.controllers;

import com.meulivro.demo.dto.LivroDTO;
import com.meulivro.demo.dto.LivroRequestDTO;
import com.meulivro.demo.dto.LivroResponseDTO;
import com.meulivro.demo.entities.Livro;
import com.meulivro.demo.repositories.LivroRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/livros")
@Tag(name = "Livros", description = "Gestão do acervo de livros")
public class LivroController {

    @Autowired
    private LivroRepository repository;

    @Operation(summary = "Cadastra um novo livro", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Livro cadastrado") })
    @PostMapping
    public ResponseEntity<Object> cadastrar(@RequestBody @Valid LivroRequestDTO data) {
        try {
            Livro novoLivro = new Livro(data.titulo(), data.autor(), data.anoPublicacao());
            Livro livroSalvo = repository.save(novoLivro);

            LivroResponseDTO response = new LivroResponseDTO(
                    livroSalvo.getId(),
                    livroSalvo.getTitulo(),
                    livroSalvo.getAutor(),
                    livroSalvo.getAnoPublicacao(),
                    "Livro criado com sucesso!");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", "O livro não foi criado: " + e.getMessage()));
        }
    }

    @Operation(summary = "Lista todos os livros", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso") })
    @GetMapping
    public ResponseEntity<Object> listar() {
        List<LivroDTO> livros = repository.findAll().stream()
                .map(livro -> new LivroDTO(livro.getId(), livro.getTitulo(), livro.getAutor(),
                        livro.getAnoPublicacao()))
                .toList();

        if (livros.isEmpty()) {
            return ResponseEntity.ok(Map.of("mensagem", "Nenhum livro encontrado na base de dados."));
        }
        return ResponseEntity.ok(livros);
    }

    @Operation(summary = "Busca personalizada de livros", description = "Busca por trechos do título, nome do autor ou ano exato. Os filtros são opcionais e cumulativos.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    })
    @GetMapping("/buscar")
    public ResponseEntity<Object> buscarPersonalizado(
            @RequestParam(required = false) Long id, // Novo parâmetro opcional
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) Integer ano) {

        List<LivroDTO> livros = repository.buscarPersonalizado(id, titulo, autor, ano).stream()
                .map(livro -> new LivroDTO(livro.getId(), livro.getTitulo(), livro.getAutor(),
                        livro.getAnoPublicacao()))
                .toList();

        if (livros.isEmpty()) {
            return ResponseEntity.ok(Map.of("mensagem", "Nenhum livro encontrado com os filtros informados."));
        }
        return ResponseEntity.ok(livros);
    }

    @Operation(summary = "Atualiza um livro existente", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livro atualizado"),
            @ApiResponse(responseCode = "404", description = "ID não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody @Valid LivroRequestDTO data) {
        Optional<Livro> livroOptional = repository.findById(id);

        if (livroOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Atualização falhou: Livro com ID " + id + " não encontrado."));
        }

        Livro livro = livroOptional.get();
        livro.setTitulo(data.titulo());
        livro.setAutor(data.autor());
        livro.setAnoPublicacao(data.anoPublicacao());

        repository.save(livro);

        LivroResponseDTO response = new LivroResponseDTO(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getAnoPublicacao(),
                "Livro atualizado com sucesso!");

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deleta um livro", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Livro deletado"),
            @ApiResponse(responseCode = "404", description = "ID não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Exclusão falhou: Livro com ID " + id + " não encontrado."));
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build(); // HTTP 204 não deve conter corpo na resposta
    }
}