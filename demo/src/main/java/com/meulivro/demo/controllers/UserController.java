package com.meulivro.demo.controllers;

import com.meulivro.demo.dto.UserResponseDTO;
import com.meulivro.demo.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema")
public class UserController {

    @Autowired
    private UserRepository repository;

    @Operation(summary = "Lista todos os usuários", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<Object> listarTodos() {
        List<UserResponseDTO> users = repository.findAll().stream()
                .map(u -> new UserResponseDTO(u.getId(), u.getLogin(), u.getRole()))
                .toList();

        if (users.isEmpty()) {
            return ResponseEntity.ok(Map.of("mensagem", "Nenhum usuário cadastrado."));
        }

        return ResponseEntity.ok(users);
    }
}