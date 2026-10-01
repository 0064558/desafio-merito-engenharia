package com.merito.engenharia.posto_de_gasolina_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/*
 * DTO para respostas de abastecimentos.
 */
public record AbastecimentoResponseDto(
        Long id,
        Long bombaId,
        String bombaNome,
        Long combustivelId,
        String combustivelNome,
        @Schema(description = "Data e hora apresentadas no fuso America/Sao_Paulo.", example = "2026-10-01T14:30:00-03:00")
        OffsetDateTime dataAbastecimento,
        BigDecimal litros,
        @Schema(description = "Preço por litro copiado do combustível no cadastro, preservado após mudanças no preço cadastrado.",
                example = "5.899", accessMode = Schema.AccessMode.READ_ONLY)
        BigDecimal precoLitroAplicado,
        @Schema(description = "Litros multiplicados pelo preço aplicado, arredondados para 2 casas decimais com HALF_UP.",
                example = "118.72", accessMode = Schema.AccessMode.READ_ONLY)
        BigDecimal valorTotal
) {
}
