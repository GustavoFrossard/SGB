package com.example.sgb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.sgb.model.Usuario;
import com.example.sgb.repository.UsuarioRepository;
import com.example.sgb.service.UsuarioDetailsService;

@Configuration 
@EnableWebSecurity 
@EnableMethodSecurity 
public class SecurityConfig {
    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }

    @Bean 
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/hello").permitAll() 
                .requestMatchers("/admin/**").hasRole("ADMIN") 
                .anyRequest().authenticated() 
            )
            .addFilterBefore(jwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class) // Filtro JWT para todas as rotas
            .addFilterBefore(jsonAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class) // Login via JSON
            .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)); // Stateless
        return http.build();
    }

    @Bean 
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*")); 
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); 
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }

    // Cria o filtro customizado para autenticação via JSON
    public JsonAuthenticationFilter jsonAuthenticationFilter(AuthenticationManager authenticationManager) {
        JsonAuthenticationFilter filter = new JsonAuthenticationFilter(authenticationManager);
        filter.setAuthenticationSuccessHandler(successHandler());
        filter.setAuthenticationFailureHandler(failureHandler());
        return filter;
    }

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private JwtUtil jwtUtil;

    
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElse(null);
            String perfil = usuario != null ? usuario.getPerfil() : "USER";
            HashMap<String, Object> claims = new HashMap<>();
            claims.put("perfil", perfil);
            String token = jwtUtil.generateToken(authentication.getName(), claims);
            HashMap<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("user", authentication.getName());
            result.put("userId", usuario != null ? usuario.getCodigologin() : null);
            result.put("perfil", perfil);
            result.put("token", token);
            new ObjectMapper().writeValue(response.getWriter(), result);
        };
    }

   
    public AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Status 401
            response.setContentType("application/json"); // Tipo JSON
            response.setCharacterEncoding("UTF-8"); // Encoding
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error"); // Status de erro
            result.put("message", "Credenciais inválidas"); // Mensagem de erro
            new ObjectMapper().writeValue(response.getWriter(), result); // Escreve resposta JSON
        };
    }

    
    public static class JsonAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
        private final ObjectMapper objectMapper = new ObjectMapper(); // Para ler JSON
        public JsonAuthenticationFilter(AuthenticationManager authenticationManager) {
            super("/login"); // Intercepta requisições para /login
            setAuthenticationManager(authenticationManager); // Define o AuthenticationManager
        }
        @Override
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
                throws AuthenticationException, IOException, ServletException {
            // Só processa se for POST e Content-Type application/json
            if ("POST".equalsIgnoreCase(request.getMethod()) &&
                request.getContentType() != null &&
                request.getContentType().contains("application/json")) {
                @SuppressWarnings("unchecked") // Suprime o warning de conversão não verificada
                Map<String, String> creds = (Map<String, String>) objectMapper.readValue(request.getInputStream(), Map.class); // Lê email e senha do JSON
                String email = creds.get("email"); // Pega o email
                String password = creds.get("password"); // Pega a senha
                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password); // Cria token de autenticação
                return this.getAuthenticationManager().authenticate(authRequest); // Autentica
            }
            
            return super.attemptAuthentication(request, response);
        }
        @Override
        protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
            super.successfulAuthentication(request, response, chain, authResult); // Usa handler de sucesso definido
        }
        @Override
        protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
            super.unsuccessfulAuthentication(request, response, failed); // Usa handler de falha definido
        }
        /*@Override
        protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
            // Só ativa o filtro para POST com Content-Type application/json
            return "POST".equalsIgnoreCase(request.getMethod()) &&
                   request.getContentType() != null &&
                   request.getContentType().contains("application/json");
        }*/
    }

    // Filtro JWT para validar token em cada request
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil) {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public org.springframework.security.authentication.AuthenticationManager authenticationManager(UsuarioDetailsService usuarioDetailsService, PasswordEncoder passwordEncoder) {
        org.springframework.security.authentication.dao.DaoAuthenticationProvider provider = new org.springframework.security.authentication.dao.DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new org.springframework.security.authentication.ProviderManager(provider);
    }
}
