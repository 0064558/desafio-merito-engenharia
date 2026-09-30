package com.merito.engenharia.posto_de_gasolina_api.dto;

/*
 * DTO para respostas de bombas.
 */
public record BombaResponseDto(
        Long id,
        String nome,
        Long combustivelId,
        String combustivelNome
) {
}
