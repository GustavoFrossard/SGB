package com.example.sgb.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "emprestimo")
public class Emprestimo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigoemprestimo")
    private Integer codigoemprestimo;

    @ManyToOne
    @JoinColumn(name = "cod_livro", referencedColumnName = "codigolivro", nullable = false)
    private Livro livro;

    @ManyToOne
    @JoinColumn(name = "cod_usuario", referencedColumnName = "codigologin", nullable = false)
    private Usuario usuario;

    @Column
    private LocalDate datadeentrega;

    @Column
    private LocalDate dataderetirada;

    @Column
    private LocalDate data_prevista;

    // Getters e Setters
    public Integer getCodigoemprestimo() { return codigoemprestimo; }
    public void setCodigoemprestimo(Integer codigoemprestimo) { this.codigoemprestimo = codigoemprestimo; }

    public Livro getLivro() { return livro; }
    public void setLivro(Livro livro) { this.livro = livro; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDate getDatadeentrega() { return datadeentrega; }
    public void setDatadeentrega(LocalDate datadeentrega) { this.datadeentrega = datadeentrega; }

    public LocalDate getDataderetirada() { return dataderetirada; }
    public void setDataderetirada(LocalDate dataderetirada) { this.dataderetirada = dataderetirada; }

    public LocalDate getData_prevista() { return data_prevista; }
    public void setData_prevista(LocalDate data_prevista) { this.data_prevista = data_prevista; }
}
