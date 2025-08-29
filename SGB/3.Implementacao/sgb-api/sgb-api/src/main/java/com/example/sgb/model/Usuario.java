package com.example.sgb.model;

import com.example.sgb.model.enums.Perfil;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigologin")
    private Integer codigologin;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private Integer idade;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 100)
    private String telefone;

    @Column(nullable = false, length = 100)
    private String senha;

    @Column(length = 50, nullable = false)
    private String perfil;

    // Getters e setters
    public Integer getCodigologin() { return codigologin; }
    public void setCodigologin(Integer codigologin) { this.codigologin = codigologin; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getPerfil() {
        return perfil;
    }
    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }
}
