package com.meulivro.demo.dto;

public record LivroResponseDTO(
        Long id,
        String titulo,
        String autor,
        Integer anoPublicacao,
        String mensagem
) {}