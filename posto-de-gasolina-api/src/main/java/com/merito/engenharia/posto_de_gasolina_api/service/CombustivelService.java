package com.merito.engenharia.posto_de_gasolina_api.service;

import com.merito.engenharia.posto_de_gasolina_api.dto.CombustivelRequestDto;
import com.merito.engenharia.posto_de_gasolina_api.dto.CombustivelResponseDto;
import com.merito.engenharia.posto_de_gasolina_api.entity.Combustivel;
import com.merito.engenharia.posto_de_gasolina_api.exception.RecursoNaoEncontradoException;
import com.merito.engenharia.posto_de_gasolina_api.repository.CombustivelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Serviço contendo a lógica de negócio para CRIAR, LISTAR, ATUALIZAR e EXCLUIR combustíveis.
 */

@Service
public class CombustivelService {
    // Injeção de dependência do repositório de combustíveis
    private final CombustivelRepository combustivelRepository;

    // Construtor para injetar o repositório de combustíveis
    public CombustivelService(CombustivelRepository combustivelRepository) {
        this.combustivelRepository = combustivelRepository;
    }

    // Método para criar um novo combustível
    @Transactional
    public CombustivelResponseDto criarCombustivel(CombustivelRequestDto requestDto) {
        // Cria uma nova entidade Combustivel a partir do DTO de requisição
        Combustivel combustivel = new Combustivel(requestDto.nome(), requestDto.precoLitro());

        // Salva a entidade no banco de dados
        combustivel = combustivelRepository.save(combustivel);

        // Retorna o DTO de resposta contendo os dados do combustível criado
        return new CombustivelResponseDto(combustivel.getId(), combustivel.getNome(), combustivel.getPrecoLitro());
    }

    // Método para listar todos os combustíveis disponíveis
    @Transactional(readOnly = true)
    public List<CombustivelResponseDto> listarCombustiveis() {
        // Recupera todos os combustíveis do banco de dados
        List<Combustivel> combustiveis = combustivelRepository.findAll();

        // Mapeia a lista de entidades Combustivel para uma lista de DTOs de resposta CombustivelResponseDto
        return combustiveis.stream()
                .map(combustivel -> new CombustivelResponseDto(combustivel.getId(), combustivel.getNome(), combustivel.getPrecoLitro()))
                .toList();
    }

    // Método para buscar um combustível pelo ID
    @Transactional(readOnly = true)
    public CombustivelResponseDto buscarCombustivelPorId(Long id) {
        // Recupera o combustível pelo ID do banco de dados
        Combustivel combustivel = combustivelRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Combustível não encontrado com o ID: " + id));

        // Retorna o DTO de resposta contendo os dados do combustível encontrado
        return new CombustivelResponseDto(combustivel.getId(), combustivel.getNome(), combustivel.getPrecoLitro());
    }

    // Método para atualizar um combustível existente pelo ID
    @Transactional
    public CombustivelResponseDto atualizarCombustivel(Long id, CombustivelRequestDto requestDto) {
        // Recupera o combustível pelo ID do banco de dados
        Combustivel combustivel = combustivelRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Combustível não encontrado com o ID: " + id));

        // Atualiza os dados do combustível com os valores do DTO de requisição
        combustivel.setNome(requestDto.nome());
        combustivel.setPrecoLitro(requestDto.precoLitro());

        // Salva as alterações no banco de dados
        combustivel = combustivelRepository.save(combustivel);

        // Retorna o DTO de resposta contendo os dados do combustível atualizado
        return new CombustivelResponseDto(combustivel.getId(), combustivel.getNome(), combustivel.getPrecoLitro());
    }
}
