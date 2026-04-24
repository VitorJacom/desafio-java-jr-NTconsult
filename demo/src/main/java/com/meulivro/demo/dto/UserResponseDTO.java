package com.meulivro.demo.dto;

import com.meulivro.demo.entities.UserRole;

public record UserResponseDTO(
        Long id,
        String login,
        UserRole role
) {}