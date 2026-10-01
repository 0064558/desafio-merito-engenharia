package com.merito.engenharia.posto_de_gasolina_api.controller;

import com.merito.engenharia.posto_de_gasolina_api.dto.AbastecimentoRequestDto;
import com.merito.engenharia.posto_de_gasolina_api.dto.AbastecimentoResponseDto;
import com.merito.engenharia.posto_de_gasolina_api.service.AbastecimentoService;
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
 * Endpoints do CRUD de abastecimentos.
 */
@RestController
@RequestMapping("/abastecimentos")
@Tag(name = "Abastecimentos", description = "Registros com litros, preço aplicado e total calculado pelo backend.")
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
    @Operation(summary = "Cadastrar abastecimento", description = "Enviar bomba, data com fuso e litros. "
            + "O preço atual do combustível é copiado e o total é calculado com HALF_UP para 2 casas decimais. "
            + "Enviar preço aplicado ou total retorna 400; um total arredondado para zero também é rejeitado.", responses = {
            @ApiResponse(responseCode = "201", description = "Abastecimento criado", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, campos não permitidos ou total arredondado para zero",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Bomba não encontrada",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public AbastecimentoResponseDto criarAbastecimento(@Valid @RequestBody AbastecimentoRequestDto requestDto) {
        return abastecimentoService.criarAbastecimento(requestDto);
    }

    // Endpoint para listar todos os abastecimentos disponíveis
    @GetMapping
    @Operation(summary = "Listar abastecimentos", description = "Retorna uma lista vazia quando não há registros.")
    public List<AbastecimentoResponseDto> listarAbastecimentos() {
        return abastecimentoService.listarAbastecimentos();
    }

    // Endpoint para buscar um abastecimento pelo ID
    @GetMapping("/{id}")
    @Operation(summary = "Consultar abastecimento por ID", responses = {
            @ApiResponse(responseCode = "200", description = "Abastecimento encontrado", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Abastecimento não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public AbastecimentoResponseDto buscarAbastecimentoPorId(@PathVariable Long id) {
        return abastecimentoService.buscarAbastecimentoPorId(id);
    }

    // Endpoint para atualizar um abastecimento existente pelo ID
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar abastecimento", description = "Enviar bomba, data com fuso e litros. "
            + "Mantendo a mesma bomba, o preço original é preservado e o total é recalculado pelos litros. "
            + "Ao trocar a bomba, utiliza-se o preço atual do combustível da nova bomba. "
            + "Alterar somente a data mantém preço e total. Não há histórico de preços por período; "
            + "transferir um registro antigo para outra bomba pode alterar seu total.", responses = {
            @ApiResponse(responseCode = "200", description = "Abastecimento atualizado", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, campos não permitidos ou total arredondado para zero",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Abastecimento ou bomba não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public AbastecimentoResponseDto atualizarAbastecimento(@PathVariable Long id,
                                                           @Valid @RequestBody AbastecimentoRequestDto requestDto) {
        return abastecimentoService.atualizarAbastecimento(id, requestDto);
    }

    // Endpoint para excluir um abastecimento existente pelo ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir abastecimento", responses = {
            @ApiResponse(responseCode = "204", description = "Abastecimento excluído", content = @Content),
            @ApiResponse(responseCode = "404", description = "Abastecimento não encontrado",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public void excluirAbastecimento(@PathVariable Long id) {
        abastecimentoService.deletarAbastecimento(id);
    }
}
