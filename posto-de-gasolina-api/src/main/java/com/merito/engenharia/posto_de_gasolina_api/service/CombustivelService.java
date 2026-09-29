package com.merito.engenharia.posto_de_gasolina_api.service;

import com.merito.engenharia.posto_de_gasolina_api.dto.CombustivelRequestDto;
import com.merito.engenharia.posto_de_gasolina_api.dto.CombustivelResponseDto;
import com.merito.engenharia.posto_de_gasolina_api.entity.Combustivel;
import com.merito.engenharia.posto_de_gasolina_api.repository.CombustivelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
* Serviço contendo a lógica de negócio para CRIAR, LISTAR, ATUALIZAR e EXCLUIR combustíveis.
* */

@Service
public class CombustivelService {
    // Injeção de dependência do repositório de combustíveis
    private final CombustivelRepository combustivelRepository;

    // Construtor para injetar o repositório de combustíveis
    public CombustivelService(CombustivelRepository combustivelRepository) {
        this.combustivelRepository = combustivelRepository;
    }

    // Método para criar um novo combustível a partir de um DTO de requisição
    @Transactional
    public CombustivelResponseDto criarCombustivel(CombustivelRequestDto requestDto) {
        // Cria uma nova entidade Combustivel a partir do DTO de requisição
        Combustivel combustivel = new Combustivel(requestDto.nome(), requestDto.precoLitro());

        // Salva a entidade no banco de dados
        combustivel = combustivelRepository.save(combustivel);

        // Retorna o DTO de resposta contendo os dados do combustível criado
        return new CombustivelResponseDto(combustivel.getId(), combustivel.getNome(), combustivel.getPrecoLitro());
    }
}
