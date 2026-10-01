package com.merito.engenharia.posto_de_gasolina_api.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/*
* DTO para requisições de criação ou atualização de combustíveis.
* */

public record CombustivelRequestDto(
        @NotBlank
        @Size(max = 100)
        @Schema(description = "Nome obrigatório, com até 100 caracteres e não composto apenas por espaços.", example = "Gasolina comum")
        String nome,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 7, fraction = 3)
        @Schema(description = "Preço por litro positivo, com até 7 dígitos inteiros e 3 casas decimais.", example = "5.899")
        BigDecimal precoLitro
) {
}
