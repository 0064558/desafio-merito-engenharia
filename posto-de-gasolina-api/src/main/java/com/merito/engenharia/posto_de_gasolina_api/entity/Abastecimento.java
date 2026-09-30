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

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/*
 * Entidade que representa um abastecimento realizado em uma bomba.
 */
@Entity
@Table(name = "abastecimentos")
public class Abastecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "bomba_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_abastecimentos_bombas")
    )
    private Bomba bomba;

    @Column(name = "data_abastecimento", nullable = false)
    private OffsetDateTime dataAbastecimento;

    @Column(name = "litros", nullable = false, precision = 10, scale = 3)
    private BigDecimal litros;

    @Column(name = "preco_litro_aplicado", nullable = false, precision = 10, scale = 3)
    private BigDecimal precoLitroAplicado;

    @Column(name = "valor_total", nullable = false, precision = 16, scale = 2)
    private BigDecimal valorTotal;

    protected Abastecimento() {
        // Construtor protegido para uso do JPA.
    }

    public Abastecimento(Bomba bomba, OffsetDateTime dataAbastecimento, BigDecimal litros,
                         BigDecimal precoLitroAplicado, BigDecimal valorTotal) {
        this.bomba = bomba;
        this.dataAbastecimento = dataAbastecimento;
        this.litros = litros;
        this.precoLitroAplicado = precoLitroAplicado;
        this.valorTotal = valorTotal;
    }

    public Long getId() {
        return id;
    }

    public Bomba getBomba() {
        return bomba;
    }

    public void setBomba(Bomba bomba) {
        this.bomba = bomba;
    }

    public OffsetDateTime getDataAbastecimento() {
        return dataAbastecimento;
    }

    public void setDataAbastecimento(OffsetDateTime dataAbastecimento) {
        this.dataAbastecimento = dataAbastecimento;
    }

    public BigDecimal getLitros() {
        return litros;
    }

    public void setLitros(BigDecimal litros) {
        this.litros = litros;
    }

    public BigDecimal getPrecoLitroAplicado() {
        return precoLitroAplicado;
    }

    public void setPrecoLitroAplicado(BigDecimal precoLitroAplicado) {
        this.precoLitroAplicado = precoLitroAplicado;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}
