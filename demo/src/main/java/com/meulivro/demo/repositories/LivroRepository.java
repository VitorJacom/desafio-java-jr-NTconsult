package com.meulivro.demo.repositories;

import com.meulivro.demo.entities.Livro;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    Livro findByTitulo(String titulo);

    @Query("SELECT l FROM Livro l WHERE " +
            "(:id IS NULL OR l.id = :id) AND " + // Novo filtro por ID
            "(:titulo IS NULL OR LOWER(l.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))) AND " +
            "(:autor IS NULL OR LOWER(l.autor) LIKE LOWER(CONCAT('%', :autor, '%'))) AND " +
            "(:ano IS NULL OR l.anoPublicacao = :ano)")
    List<Livro> buscarPersonalizado(
            @Param("id") Long id,
            @Param("titulo") String titulo,
            @Param("autor") String autor,
            @Param("ano") Integer ano);
}