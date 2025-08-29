package com.example.sgb.controller;

import com.example.sgb.model.Usuario;
import com.example.sgb.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
import com.example.sgb.model.enums.Perfil;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public List<Usuario> listarTodos(@RequestParam(required = false) String perfil) {
        if (perfil != null && !perfil.isBlank()) {
            return usuarioRepository.findByPerfil(perfil);
        } else {
            return usuarioRepository.findAll();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<?> criar(@RequestBody Usuario usuario, Authentication authentication) {
        if (!isPerfilValido(usuario.getPerfil())) {
            return ResponseEntity.badRequest().body("Perfil inválido. Valores aceitos: USUARIO, BIBLIOTECARIO");
        }
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado em outro usuário.");
        }
        if (temRestricaoBibliotecario(authentication, usuario.getPerfil())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("BIBLIOTECARIO só pode criar usuários com perfil USUARIO");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        Usuario salvo = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<?> atualizar(@PathVariable Integer id, @RequestBody Usuario usuario,
            Authentication authentication) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (!isPerfilValido(usuario.getPerfil())) {
            return ResponseEntity.badRequest().body("Perfil inválido. Valores aceitos: USUARIO, BIBLIOTECARIO");
        }
        // Verifica se o email já existe em outro usuário
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioExistente.isPresent() && !usuarioExistente.get().getCodigologin().equals(id)) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado em outro usuário.");
        }
        if (temRestricaoBibliotecario(authentication, usuario.getPerfil())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("BIBLIOTECARIO só pode editar usuários com perfil USUARIO");
        }
        usuario.setCodigologin(id);
        Usuario atualizado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<?> deletar(@PathVariable Integer id, Authentication authentication) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Usuario usuario = usuarioOpt.get();
        if (authentication != null && usuario.getEmail().equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Você não pode excluir a si mesmo.");
        }
        if (temRestricaoBibliotecario(authentication, usuario.getPerfil())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("BIBLIOTECARIO só pode excluir usuários com perfil USUARIO");
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Regra: BIBLIOTECARIO só pode manipular usuários com perfil USUARIO
    private boolean temRestricaoBibliotecario(Authentication authentication, String perfilDoAlvo) {
        if (authentication == null)
            return false;
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_BIBLIOTECARIO"))
                && !"USUARIO".equals(perfilDoAlvo);
    }

    private boolean isPerfilValido(String perfil) {
        for (Perfil p : Perfil.values()) {
            System.out.println("Perfil encontrado: " + p.name());
            if (p.name().equals(perfil))
                return true;
        }
        return false;
    }
}
