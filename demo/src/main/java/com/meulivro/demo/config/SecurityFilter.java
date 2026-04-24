package com.meulivro.demo.config;

import com.meulivro.demo.repositories.UserRepository;
import com.meulivro.demo.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        
        if (token != null) {
            var login = tokenService.validateToken(token);
            System.out.println("👉 [SECURITY] Token recebido. Login extraído: '" + login + "'");
            
            if (!login.isEmpty()) {
                UserDetails user = userRepository.findByLogin(login);

                if (user != null) {
                    System.out.println("👉 [SECURITY] Usuário encontrado no banco. Permissões: " + user.getAuthorities());
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    System.out.println("❌ [SECURITY] Usuário '" + login + "' não existe no banco de dados! O banco pode ter sido resetado.");
                }
            } else {
                System.out.println("❌ [SECURITY] O token é inválido, expirou ou a secret key mudou.");
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null)
            return null;
        return authHeader.replace("Bearer ", "");
    }
}