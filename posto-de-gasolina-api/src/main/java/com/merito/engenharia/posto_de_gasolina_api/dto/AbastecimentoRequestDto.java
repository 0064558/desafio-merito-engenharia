package com.merito.engenharia.posto_de_gasolina_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/*
 * DTO para requisições de criação ou atualização de abastecimentos.
 */
public record AbastecimentoRequestDto(
        @NotNull
        @Positive
        Long bombaId,

        @NotNull
        OffsetDateTime dataAbastecimento,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 7, fraction = 3)
        BigDecimal litros
) {
}
