package com.merito.engenharia.posto_de_gasolina_api.controller;

import com.merito.engenharia.posto_de_gasolina_api.dto.AbastecimentoRequestDto;
import com.merito.engenharia.posto_de_gasolina_api.dto.AbastecimentoResponseDto;
import com.merito.engenharia.posto_de_gasolina_api.service.AbastecimentoService;
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
 * Endpoints do CRUD de abastecimentos.
 */
@RestController
@RequestMapping("/abastecimentos")
public class AbastecimentoController {

    // Injeção de dependência do serviço de abastecimentos
    private final AbastecimentoService abastecimentoService;

    // Construtor para injetar o serviço de abastecimentos
    public AbastecimentoController(AbastecimentoService abastecimentoService) {
        this.abastecimentoService = abastecimentoService;
    }

    // Endpoint para criar um novo abastecimento
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AbastecimentoResponseDto criarAbastecimento(@Valid @RequestBody AbastecimentoRequestDto requestDto) {
        return abastecimentoService.criarAbastecimento(requestDto);
    }

    // Endpoint para listar todos os abastecimentos disponíveis
    @GetMapping
    public List<AbastecimentoResponseDto> listarAbastecimentos() {
        return abastecimentoService.listarAbastecimentos();
    }

    // Endpoint para buscar um abastecimento pelo ID
    @GetMapping("/{id}")
    public AbastecimentoResponseDto buscarAbastecimentoPorId(@PathVariable Long id) {
        return abastecimentoService.buscarAbastecimentoPorId(id);
    }

    // Endpoint para atualizar um abastecimento existente pelo ID
    @PutMapping("/{id}")
    public AbastecimentoResponseDto atualizarAbastecimento(@PathVariable Long id,
                                                           @Valid @RequestBody AbastecimentoRequestDto requestDto) {
        return abastecimentoService.atualizarAbastecimento(id, requestDto);
    }

    // Endpoint para excluir um abastecimento existente pelo ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirAbastecimento(@PathVariable Long id) {
        abastecimentoService.deletarAbastecimento(id);
    }
}
