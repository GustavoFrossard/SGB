package com.example.sgb.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;
import io.jsonwebtoken.Claims;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        System.out.println("[JwtAuthenticationFilter] Authorization header recebido: " + authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("[JwtAuthenticationFilter] Token extraído: " + token);
            boolean valido = jwtUtil.isTokenValid(token);
            System.out.println("[JwtAuthenticationFilter] Token válido? " + valido);
            if (valido) {
                Claims claims = jwtUtil.extractClaims(token);
                String email = claims.getSubject();
                String perfil = (String) claims.get("perfil");
                System.out.println("[JwtAuthenticationFilter] Email extraído: " + email + ", Perfil: " + perfil);
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + perfil);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email, null, Collections.singletonList(authority));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("[JwtAuthenticationFilter] Token inválido!");
            }
        } else {
            System.out.println("[JwtAuthenticationFilter] Authorization header ausente ou formato inválido.");
        }
        filterChain.doFilter(request, response);
    }
}
