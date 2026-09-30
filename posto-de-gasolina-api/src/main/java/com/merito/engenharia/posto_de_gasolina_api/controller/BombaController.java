package com.merito.engenharia.posto_de_gasolina_api.controller;

import com.merito.engenharia.posto_de_gasolina_api.dto.BombaRequestDto;
import com.merito.engenharia.posto_de_gasolina_api.dto.BombaResponseDto;
import com.merito.engenharia.posto_de_gasolina_api.service.BombaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
* Controlador para gerenciar as operações relacionadas às bombas de combustível.
*/

@RestController
@RequestMapping("/bombas")
public class BombaController {
    // Injeção de dependência do serviço de bombas
    private final BombaService bombaService;

    // Construtor para injetar o serviço de bombas
    public BombaController(BombaService bombaService) {
        this.bombaService = bombaService;
    }

    // Endpoint para criar uma nova bomba
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BombaResponseDto criarBomba(@Valid @RequestBody BombaRequestDto requestDto) {
        return bombaService.criarBomba(requestDto);
    }

    // Endpoint para listar todas as bombas disponíveis
    @GetMapping
    public List<BombaResponseDto> listarBombas() {
        return bombaService.listarBombas();
    }

    // Endpoint para buscar uma bomba pelo ID
    @GetMapping("/{id}")
    public BombaResponseDto buscarBombaPorId(@PathVariable Long id) {
        return bombaService.buscarBombaPorId(id);
    }

    // Endpoint para atualizar uma bomba existente pelo ID
    @PutMapping("/{id}")
    public BombaResponseDto atualizarBomba(@PathVariable Long id,
                                           @Valid @RequestBody BombaRequestDto requestDto) {
        return bombaService.atualizarBomba(id, requestDto);
    }

    // Endpoint para excluir uma bomba existente pelo ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirBomba(@PathVariable Long id) {
        bombaService.deletarBomba(id);
    }
}
