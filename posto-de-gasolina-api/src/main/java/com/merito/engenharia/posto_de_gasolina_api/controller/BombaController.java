package com.merito.engenharia.posto_de_gasolina_api.controller;

import com.merito.engenharia.posto_de_gasolina_api.dto.BombaRequestDto;
import com.merito.engenharia.posto_de_gasolina_api.dto.BombaResponseDto;
import com.merito.engenharia.posto_de_gasolina_api.service.BombaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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
@Tag(name = "Bombas", description = "Bombas vinculadas a um combustível existente.")
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
    @Operation(summary = "Cadastrar bomba", responses = {
            @ApiResponse(responseCode = "201", description = "Bomba criada", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos não permitidos",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Combustível não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public BombaResponseDto criarBomba(@Valid @RequestBody BombaRequestDto requestDto) {
        return bombaService.criarBomba(requestDto);
    }

    // Endpoint para listar todas as bombas disponíveis
    @GetMapping
    @Operation(summary = "Listar bombas", description = "Retorna uma lista vazia quando não há registros.")
    public List<BombaResponseDto> listarBombas() {
        return bombaService.listarBombas();
    }

    // Endpoint para buscar uma bomba pelo ID
    @GetMapping("/{id}")
    @Operation(summary = "Consultar bomba por ID", responses = {
            @ApiResponse(responseCode = "200", description = "Bomba encontrada", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Bomba não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public BombaResponseDto buscarBombaPorId(@PathVariable Long id) {
        return bombaService.buscarBombaPorId(id);
    }

    // Endpoint para atualizar uma bomba existente pelo ID
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar bomba", description = "Enviar nome e combustível. "
            + "A troca de combustível é bloqueada quando há abastecimentos. Reenviar o mesmo combustível permite renomear a bomba.",
            responses = {
            @ApiResponse(responseCode = "200", description = "Bomba atualizada", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos não permitidos",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Bomba ou combustível não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "A bomba possui abastecimentos e não pode trocar de combustível",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public BombaResponseDto atualizarBomba(@PathVariable Long id,
                                           @Valid @RequestBody BombaRequestDto requestDto) {
        return bombaService.atualizarBomba(id, requestDto);
    }

    // Endpoint para excluir uma bomba existente pelo ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir bomba", responses = {
            @ApiResponse(responseCode = "204", description = "Bomba excluída", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bomba não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Há abastecimentos vinculados à bomba",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public void excluirBomba(@PathVariable Long id) {
        bombaService.deletarBomba(id);
    }
}
