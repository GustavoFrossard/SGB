package com.example.sgb.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "livro_disponibilidade")
public class LivroDisponibilidade {
    @Id
    @Column(name = "codigolivro")
    private Integer codigolivro;

    @Column(length = 100, nullable = false)
    private String nome;

    @Column(length = 100, nullable = false)
    private String autor;

    @Column(length = 50, nullable = false)
    private String genero;

    @Column(length = 20, nullable = false)
    private String disponibilidade;

    // Getters
    public Integer getCodigolivro() { return codigolivro; }
    public String getNome() { return nome; }
    public String getAutor() { return autor; }
    public String getGenero() { return genero; }
    public String getDisponibilidade() { return disponibilidade; }
}
