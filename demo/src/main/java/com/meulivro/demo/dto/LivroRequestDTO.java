package com.meulivro.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LivroRequestDTO(
        @NotBlank(message = "O título é obrigatório") String titulo,
        @NotBlank(message = "O autor é obrigatório") String autor,
        @NotNull(message = "O ano de publicação é obrigatório") Integer anoPublicacao
) {}