package com.merito.engenharia.posto_de_gasolina_api.controller;

import com.merito.engenharia.posto_de_gasolina_api.dto.CombustivelRequestDto;
import com.merito.engenharia.posto_de_gasolina_api.dto.CombustivelResponseDto;
import com.merito.engenharia.posto_de_gasolina_api.service.CombustivelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/*
 * Controlador para gerenciar as operações relacionadas a combustíveis.
 * */

@RestController
@RequestMapping("/combustiveis")
public class CombustivelController {
    // Injeção de dependência do serviço de combustíveis
    private final CombustivelService combustivelService;

    // Construtor para injetar o serviço de combustíveis
    public CombustivelController(CombustivelService combustivelService) {
        this.combustivelService = combustivelService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CombustivelResponseDto criarCombustivel(@Valid @RequestBody CombustivelRequestDto requestDto) {
        return combustivelService.criarCombustivel(requestDto);
    }
}
