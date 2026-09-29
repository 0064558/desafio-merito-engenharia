package com.merito.engenharia.posto_de_gasolina_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/*
 * Entidade que representa uma bomba de combustível no sistema.
 * Cada bomba está associada a um tipo específico de combustível.
 */

@Entity
@Table(name = "bombas")
public class Bomba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    // Relacionamento Many-to-One com a entidade Combustivel.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "combustivel_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_bombas_combustiveis")
    )
    private Combustivel combustivel;

    protected Bomba() {
        // Construtor protegido para uso do JPA.
    }

    public Bomba(String nome, Combustivel combustivel) {
        this.nome = nome;
        this.combustivel = combustivel;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Combustivel getCombustivel() {
        return combustivel;
    }

    public void setCombustivel(Combustivel combustivel) {
        this.combustivel = combustivel;
    }
}
