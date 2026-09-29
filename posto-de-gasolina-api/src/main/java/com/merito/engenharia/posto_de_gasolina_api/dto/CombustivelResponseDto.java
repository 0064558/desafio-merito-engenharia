package com.merito.engenharia.posto_de_gasolina_api.dto;

import java.math.BigDecimal;

/*
* DTO para respostas de combustíveis.
* */

public record CombustivelResponseDto(
        Long id,
        String nome,
        BigDecimal precoLitro
) {
}
