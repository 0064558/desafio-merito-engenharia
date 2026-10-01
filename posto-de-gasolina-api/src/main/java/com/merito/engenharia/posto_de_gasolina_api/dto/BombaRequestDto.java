package com.merito.engenharia.posto_de_gasolina_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

/*
 * DTO para requisições de criação ou atualização de bombas.
 */
public record BombaRequestDto(
        @NotBlank
        @Size(max = 100)
        @Schema(description = "Nome obrigatório, com até 100 caracteres e não composto apenas por espaços.", example = "Bomba 1")
        String nome,

        @NotNull
        @Positive
        @Schema(description = "ID positivo de um combustível existente.", example = "1")
        Long combustivelId
) {
}
