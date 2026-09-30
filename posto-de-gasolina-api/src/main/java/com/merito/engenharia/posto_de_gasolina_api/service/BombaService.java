package com.merito.engenharia.posto_de_gasolina_api.service;

import com.merito.engenharia.posto_de_gasolina_api.dto.BombaRequestDto;
import com.merito.engenharia.posto_de_gasolina_api.dto.BombaResponseDto;
import com.merito.engenharia.posto_de_gasolina_api.entity.Bomba;
import com.merito.engenharia.posto_de_gasolina_api.entity.Combustivel;
import com.merito.engenharia.posto_de_gasolina_api.exception.ConflitoDeNegocioException;
import com.merito.engenharia.posto_de_gasolina_api.exception.RecursoNaoEncontradoException;
import com.merito.engenharia.posto_de_gasolina_api.repository.BombaRepository;
import com.merito.engenharia.posto_de_gasolina_api.repository.CombustivelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
* Serviço para gerenciamento de bombas.
*/

@Service
public class BombaService {
    // Injeção de dependência do repositório de bombas e combustíveis
    private final BombaRepository bombaRepository;
    private final CombustivelRepository combustivelRepository;

    // Construtor para injetar os repositórios de bombas e combustíveis
    public BombaService(BombaRepository bombaRepository, CombustivelRepository combustivelRepository) {
        this.bombaRepository = bombaRepository;
        this.combustivelRepository = combustivelRepository;
    }

    // Método para criar uma nova bomba
    @Transactional
    public BombaResponseDto criarBomba(BombaRequestDto requestDto) {
        // Busca o combustível associado à bomba pelo ID fornecido no DTO de requisição
        Combustivel combustivel = buscarCombustivel(requestDto.combustivelId());

        // Cria uma nova entidade Bomba a partir do DTO de requisição e do combustível encontrado
        Bomba bomba = bombaRepository.save(new Bomba(requestDto.nome(), combustivel));

        // Retorna o DTO de resposta contendo os dados da bomba criada
        return paraResponseDto(bomba);
    }

    // Método para listar todas as bombas disponíveis
    @Transactional(readOnly = true)
    public List<BombaResponseDto> listarBombas() {
        // Recupera todas as bombas do banco de dados e mapeia para uma lista de DTOs de resposta BombaResponseDto
        return bombaRepository.findAll().stream()
                .map(this::paraResponseDto)
                .toList();
    }

    // Método para buscar uma bomba pelo ID
    @Transactional(readOnly = true)
    public BombaResponseDto buscarBombaPorId(Long id) {
        return paraResponseDto(buscarBomba(id));
    }

    // Método para atualizar uma bomba existente
    @Transactional
    public BombaResponseDto atualizarBomba(Long id, BombaRequestDto requestDto) {
        // Busca a bomba pelo ID fornecido
        Bomba bomba = buscarBomba(id);

        // Busca o combustível associado à bomba pelo ID fornecido no DTO de requisição
        Combustivel combustivel = buscarCombustivel(requestDto.combustivelId());

        // Verifica se o combustível da bomba está sendo alterado
        if (!bomba.getCombustivel().getId().equals(combustivel.getId())) {
            // Verifica se há abastecimentos associados à bomba antes de permitir a troca de combustível
            if (bombaRepository.existsAbastecimentoByBombaId(id)) {
                throw new ConflitoDeNegocioException(
                        "Não é possível trocar o combustível da bomba, pois há abastecimentos associados a ela.");
            }
            // Atualiza o combustível da bomba
            bomba.setCombustivel(combustivel);
        }

        // Atualiza o nome da bomba
        bomba.setNome(requestDto.nome());

        // Salva as alterações no banco de dados e retorna o DTO de resposta contendo os dados da bomba atualizada
        return paraResponseDto(bombaRepository.save(bomba));
    }

    // Método para excluir uma bomba existente
    @Transactional
    public void deletarBomba(Long id) {
        // Busca a bomba pelo ID fornecido
        Bomba bomba = buscarBomba(id);

        // Verifica se há abastecimentos associados à bomba antes de permitir a exclusão
        if (bombaRepository.existsAbastecimentoByBombaId(id)) {
            throw new ConflitoDeNegocioException(
                    "Não é possível excluir a bomba, pois há abastecimentos associados a ela.");
        }

        // Exclui a bomba do banco de dados
        bombaRepository.delete(bomba);
    }

    // Método auxiliar para buscar uma bomba pelo ID, lançando uma exceção se não encontrada
    private Bomba buscarBomba(Long id) {
        return bombaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Bomba não encontrada com o ID: " + id));
    }

    // Método auxiliar para buscar um combustível pelo ID, lançando uma exceção se não encontrado
    private Combustivel buscarCombustivel(Long id) {
        return combustivelRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Combustível não encontrado com o ID: " + id));
    }

    // Método auxiliar para converter uma entidade Bomba em um DTO de resposta BombaResponseDto
    private BombaResponseDto paraResponseDto(Bomba bomba) {
        Combustivel combustivel = bomba.getCombustivel();
        return new BombaResponseDto(bomba.getId(), bomba.getNome(), combustivel.getId(), combustivel.getNome());
    }
}
