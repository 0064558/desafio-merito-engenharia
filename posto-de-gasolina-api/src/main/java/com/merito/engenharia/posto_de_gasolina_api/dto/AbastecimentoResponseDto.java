package com.merito.engenharia.posto_de_gasolina_api.dto;

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
        OffsetDateTime dataAbastecimento,
        BigDecimal litros,
        BigDecimal precoLitroAplicado,
        BigDecimal valorTotal
) {
}
