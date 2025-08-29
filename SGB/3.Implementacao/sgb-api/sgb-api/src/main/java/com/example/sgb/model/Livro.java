package com.example.sgb.model;

import jakarta.persistence.*;

@Entity
@Table(name = "livro")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigolivro")
    private Integer codigolivro;

    @Column(length = 100, nullable = false)
    private String nome;

    @Column(length = 100, nullable = false)
    private String autor;

    @Column(length = 50, nullable = false)
    private String genero;

    // Getters e Setters
    public Integer getCodigolivro() {
        return codigolivro;
    }
    public void setCodigolivro(Integer codigolivro) {
        this.codigolivro = codigolivro;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }
    public String getGenero() {
        return genero;
    }
    public void setGenero(String genero) {
        this.genero = genero;
    }
}
