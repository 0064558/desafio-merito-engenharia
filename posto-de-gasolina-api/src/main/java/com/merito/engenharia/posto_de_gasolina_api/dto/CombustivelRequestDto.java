package com.merito.engenharia.posto_de_gasolina_api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/*
* DTO para requisições de criação ou atualização de combustíveis.
* */

public record CombustivelRequestDto(
        @NotBlank
        @Size(max = 100)
        String nome,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 7, fraction = 3)
        BigDecimal precoLitro
) {
}
