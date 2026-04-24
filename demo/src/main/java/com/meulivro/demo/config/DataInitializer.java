package com.meulivro.demo.config;

import com.meulivro.demo.entities.Livro;
import com.meulivro.demo.entities.User;
import com.meulivro.demo.entities.UserRole;
import com.meulivro.demo.repositories.LivroRepository;
import com.meulivro.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n⏳ [DataSeeding] Verificando e populando dados iniciais...");

        // 1. Cadastrando o usuário ADMIN inicial
        if (userRepository.findByLogin("admin") == null) {
            String senhaCriptografada = passwordEncoder.encode("admin123");
            User admin = new User("admin", senhaCriptografada, UserRole.ROLE_ADMIN);
            userRepository.save(admin);
            System.out.println("✅ [DataSeeding] Usuário ADMIN criado: (Login: admin | Senha: admin123)");
        } else {
            System.out.println("ℹ️ [DataSeeding] Usuário ADMIN já existe na base.");
        }

        // 2. Cadastrando livros básicos
        List<Livro> livrosIniciais = List.of(
                new Livro("O Senhor dos Anéis", "J.R.R. Tolkien", 1954),
                new Livro("1984", "George Orwell", 1949),
                new Livro("Dom Quixote", "Miguel de Cervantes", 1605),
                new Livro("O Pequeno Príncipe", "Antoine de Saint-Exupéry", 1943)
        );

        int livrosAdicionados = 0;
        for (Livro livro : livrosIniciais) {
            if (livroRepository.findByTitulo(livro.getTitulo()) == null) {
                livroRepository.save(livro);
                livrosAdicionados++;
            }
        }

        if (livrosAdicionados > 0) {
            System.out.println("✅ [DataSeeding] " + livrosAdicionados + " livros iniciais foram adicionados ao acervo.");
        } else {
            System.out.println("ℹ️ [DataSeeding] Os livros iniciais já estão cadastrados.");
        }
        System.out.println("🚀 [API] Aplicação pronta para uso!\n");
    }
}