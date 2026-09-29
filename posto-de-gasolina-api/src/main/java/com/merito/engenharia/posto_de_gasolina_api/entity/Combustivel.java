package com.merito.engenharia.posto_de_gasolina_api.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

/*
* Entidade que representa um combustível no sistema.
* */

@Entity
@Table(name = "combustiveis")
public class Combustivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "preco_litro", nullable = false, precision = 10, scale = 3)
    private BigDecimal precoLitro;

    protected Combustivel() {
        // Construtor protegido para uso do JPA
    }

    public Combustivel(String nome, BigDecimal precoLitro) {
        this.nome = nome;
        this.precoLitro = precoLitro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPrecoLitro() {
        return precoLitro;
    }

    public void setPrecoLitro(BigDecimal precoLitro) {
        this.precoLitro = precoLitro;
    }
}
