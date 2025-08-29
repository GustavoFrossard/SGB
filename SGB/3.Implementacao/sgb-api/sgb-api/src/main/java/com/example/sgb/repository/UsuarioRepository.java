package com.example.sgb.repository;

import com.example.sgb.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findById(Integer id);
    boolean existsById(Integer id);
    List<Usuario> findAll();
    void deleteById(Integer id);
    List<Usuario> findByPerfil(String perfil);
}
