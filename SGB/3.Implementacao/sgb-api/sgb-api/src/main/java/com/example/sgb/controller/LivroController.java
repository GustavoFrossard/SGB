package com.example.sgb.controller;

import com.example.sgb.model.Livro;
import com.example.sgb.model.LivroDisponibilidade;
import com.example.sgb.model.enums.Genero;
import com.example.sgb.repository.LivroRepository;
import com.example.sgb.repository.LivroDisponibilidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/livros")
public class LivroController {
    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private LivroDisponibilidadeRepository livroDisponibilidadeRepository;

    @GetMapping
    public List<LivroDisponibilidade> listarTodos(@RequestParam(required = false) String genero,
                                                  @RequestParam(required = false) String disponibilidade) {
        if (StringUtils.hasText(genero) && StringUtils.hasText(disponibilidade)) {
            return livroDisponibilidadeRepository.findByGeneroAndDisponibilidade(genero, disponibilidade);
        } else if (StringUtils.hasText(genero)) {
            return livroDisponibilidadeRepository.findByGenero(genero);
        } else if (StringUtils.hasText(disponibilidade)) {
            return livroDisponibilidadeRepository.findByDisponibilidade(disponibilidade);
        } else {
            return livroDisponibilidadeRepository.findAll();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivroDisponibilidade> buscarPorId(@PathVariable Integer id) {
        Optional<LivroDisponibilidade> livro = livroDisponibilidadeRepository.findById(id);
        return livro.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    @PostMapping
    public ResponseEntity<Livro> criar(@RequestBody Livro livro) {

        if (!isGeneroValido(livro.getGenero())) {
            return ResponseEntity.badRequest().body(null);
        }
        Livro salvo = livroRepository.save(livro);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<Livro> atualizar(@PathVariable Integer id, @RequestBody Livro livro) {
        if (!livroRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (!isGeneroValido(livro.getGenero())) {
            return ResponseEntity.badRequest().body(null);
        }
        livro.setCodigolivro(id);
        Livro atualizado = livroRepository.save(livro);
        return ResponseEntity.ok(atualizado);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        try {
            livroRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean isGeneroValido(String genero) {
        for (Genero g : Genero.values()) {
            if (g.name().equals(genero))
                return true;
        }
        return false;
    }
}
