package com.merito.engenharia.posto_de_gasolina_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/*
 * DTO para requisições de criação ou atualização de abastecimentos.
 */
public record AbastecimentoRequestDto(
        @NotNull
        @Positive
        @Schema(description = "ID positivo de uma bomba existente.", example = "1")
        Long bombaId,

        @NotNull
        @Schema(description = "Data e hora em ISO 8601 com deslocamento de fuso obrigatório (-03:00 ou Z).",
                format = "date-time", example = "2026-10-01T14:30:00-03:00")
        OffsetDateTime dataAbastecimento,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 7, fraction = 3)
        @Schema(description = "Quantidade positiva, com até 7 dígitos inteiros e 3 casas decimais. "
                + "O valor total calculado deve permanecer positivo após o arredondamento.", example = "20.125")
        BigDecimal litros
) {
}
