package com.merito.engenharia.posto_de_gasolina_api.controller;

import com.merito.engenharia.posto_de_gasolina_api.dto.CombustivelRequestDto;
import com.merito.engenharia.posto_de_gasolina_api.dto.CombustivelResponseDto;
import com.merito.engenharia.posto_de_gasolina_api.service.CombustivelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * Controlador para gerenciar as operações relacionadas a combustíveis.
 * */

@RestController
@RequestMapping("/combustiveis")
@Tag(name = "Combustíveis", description = "Cadastro de tipos de combustível e preço por litro.")
public class CombustivelController {
    // Injeção de dependência do serviço de combustíveis
    private final CombustivelService combustivelService;

    // Construtor para injetar o serviço de combustíveis
    public CombustivelController(CombustivelService combustivelService) {
        this.combustivelService = combustivelService;
    }

    // Endpoint para criar um novo combustível
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar combustível", responses = {
            @ApiResponse(responseCode = "201", description = "Combustível criado", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos não permitidos",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public CombustivelResponseDto criarCombustivel(@Valid @RequestBody CombustivelRequestDto requestDto) {
        return combustivelService.criarCombustivel(requestDto);
    }

    // Endpoint para listar todos os combustíveis disponíveis
    @GetMapping
    @Operation(summary = "Listar combustíveis", description = "Retorna uma lista vazia quando não há registros.")
    public List<CombustivelResponseDto> listarCombustiveis() {
        return combustivelService.listarCombustiveis();
    }

    // Endpoint para buscar um combustível pelo ID
    @GetMapping("/{id}")
    @Operation(summary = "Consultar combustível por ID", responses = {
            @ApiResponse(responseCode = "200", description = "Combustível encontrado", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Combustível não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public CombustivelResponseDto buscarCombustivelPorId(@PathVariable Long id) {
        return combustivelService.buscarCombustivelPorId(id);
    }

    // Endpoint para atualizar um combustível existente pelo ID
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar combustível", description = "Enviar nome e preço por litro. "
            + "Alterar o preço não modifica abastecimentos já registrados.", responses = {
            @ApiResponse(responseCode = "200", description = "Combustível atualizado", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos não permitidos",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Combustível não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public CombustivelResponseDto atualizarCombustivel(@PathVariable Long id,
                                                       @Valid @RequestBody CombustivelRequestDto requestDto) {
        return combustivelService.atualizarCombustivel(id, requestDto);
    }

    // Endpoint para excluir um combustível existente pelo ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir combustível", responses = {
            @ApiResponse(responseCode = "204", description = "Combustível excluído", content = @Content),
            @ApiResponse(responseCode = "404", description = "Combustível não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Há bombas vinculadas ao combustível",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public void excluirCombustivel(@PathVariable Long id) {
        combustivelService.deletarCombustivel(id);
    }
}
