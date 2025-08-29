package com.example.sgb.repository;

import com.example.sgb.model.LivroDisponibilidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroDisponibilidadeRepository extends JpaRepository<LivroDisponibilidade, Integer> {
    List<LivroDisponibilidade> findByGenero(String genero);
    List<LivroDisponibilidade> findByDisponibilidade(String disponibilidade);
    List<LivroDisponibilidade> findByGeneroAndDisponibilidade(String genero, String disponibilidade);
}
