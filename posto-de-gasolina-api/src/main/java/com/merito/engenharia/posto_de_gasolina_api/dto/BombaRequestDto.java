package com.merito.engenharia.posto_de_gasolina_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/*
 * DTO para requisições de criação ou atualização de bombas.
 */
public record BombaRequestDto(
        @NotBlank
        @Size(max = 100)
        String nome,

        @NotNull
        @Positive
        Long combustivelId
) {
}
