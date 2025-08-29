package com.example.sgb.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable // Hibernate: garante que não será persistida
@Table(name = "emprestimo_view")
public class EmprestimoView {
    @Id
    @Column(name = "codigoemprestimo")
    private Integer codigoemprestimo;
    @Column(name = "cod_livro")
    private Integer codLivro;
    @Column(name = "cod_usuario")
    private Integer codUsuario;
    @Column(name = "datadeentrega")
    private LocalDate dataDeEntrega;
    @Column(name = "dataderetirada")
    private LocalDate dataDeRetirada;
    @Column(name = "dias_em_atraso")
    private Integer diasEmAtraso;
    @Column(name = "saldo_devedor")
    private BigDecimal saldoDevedor;
    @Column(name = "em_atraso")
    private Boolean emAtraso;
    @Column(name = "nome_livro")
    private String nomeLivro;
    @Column(name = "nome_usuario")
    private String nomeUsuario;
    @Column(name = "data_prevista")
    private LocalDate dataPrevista;

    // Getters e Setters
    public Integer getCodigoemprestimo() { return codigoemprestimo; }
    public void setCodigoemprestimo(Integer codigoemprestimo) { this.codigoemprestimo = codigoemprestimo; }
    public Integer getCodLivro() { return codLivro; }
    public void setCodLivro(Integer codLivro) { this.codLivro = codLivro; }
    public Integer getCodUsuario() { return codUsuario; }
    public void setCodUsuario(Integer codUsuario) { this.codUsuario = codUsuario; }
    public LocalDate getDataDeEntrega() { return dataDeEntrega; }
    public void setDataDeEntrega(LocalDate dataDeEntrega) { this.dataDeEntrega = dataDeEntrega; }
    public LocalDate getDataDeRetirada() { return dataDeRetirada; }
    public void setDataDeRetirada(LocalDate dataDeRetirada) { this.dataDeRetirada = dataDeRetirada; }
    public Integer getDiasEmAtraso() { return diasEmAtraso; }
    public void setDiasEmAtraso(Integer diasEmAtraso) { this.diasEmAtraso = diasEmAtraso; }
    public BigDecimal getSaldoDevedor() { return saldoDevedor; }
    public void setSaldoDevedor(BigDecimal saldoDevedor) { this.saldoDevedor = saldoDevedor; }
    public Boolean getEmAtraso() { return emAtraso; }
    public void setEmAtraso(Boolean emAtraso) { this.emAtraso = emAtraso; }
    public String getNomeLivro() { return nomeLivro; }
    public void setNomeLivro(String nomeLivro) { this.nomeLivro = nomeLivro; }
    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }
    public LocalDate getDataPrevista() { return dataPrevista; }
    public void setDataPrevista(LocalDate dataPrevista) { this.dataPrevista = dataPrevista; }
}
