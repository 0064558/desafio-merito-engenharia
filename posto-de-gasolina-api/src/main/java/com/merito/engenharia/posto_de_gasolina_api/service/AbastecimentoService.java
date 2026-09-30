package com.merito.engenharia.posto_de_gasolina_api.service;

import com.merito.engenharia.posto_de_gasolina_api.dto.AbastecimentoRequestDto;
import com.merito.engenharia.posto_de_gasolina_api.dto.AbastecimentoResponseDto;
import com.merito.engenharia.posto_de_gasolina_api.entity.Abastecimento;
import com.merito.engenharia.posto_de_gasolina_api.entity.Bomba;
import com.merito.engenharia.posto_de_gasolina_api.entity.Combustivel;
import com.merito.engenharia.posto_de_gasolina_api.exception.RecursoNaoEncontradoException;
import com.merito.engenharia.posto_de_gasolina_api.exception.RequisicaoInvalidaException;
import com.merito.engenharia.posto_de_gasolina_api.repository.AbastecimentoRepository;
import com.merito.engenharia.posto_de_gasolina_api.repository.BombaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.List;

/*
 * Serviço com as operações e regras de cálculo dos abastecimentos.
 */
@Service
public class AbastecimentoService {
    // O fuso horário de São Paulo é usado para apresentar as datas de abastecimento na API.
    private static final ZoneId FUSO_SAO_PAULO = ZoneId.of("America/Sao_Paulo");

    // Injeção de dependência do repositório de abastecimentos e bombas
    private final AbastecimentoRepository abastecimentoRepository;
    private final BombaRepository bombaRepository;

    // Construtor para injetar os repositórios de abastecimentos e bombas
    public AbastecimentoService(AbastecimentoRepository abastecimentoRepository, BombaRepository bombaRepository) {
        this.abastecimentoRepository = abastecimentoRepository;
        this.bombaRepository = bombaRepository;
    }

    // Método para criar um novo abastecimento
    @Transactional
    public AbastecimentoResponseDto criarAbastecimento(AbastecimentoRequestDto requestDto) {
        // Busca a bomba associada ao abastecimento pelo ID fornecido no DTO de requisição
        Bomba bomba = buscarBomba(requestDto.bombaId());

        // Copia o preço atual para que mudanças futuras no combustível não alterem este registro.
        BigDecimal precoAplicado = bomba.getCombustivel().getPrecoLitro();

        // Calcula o valor total do abastecimento com base na quantidade de litros e no preço aplicado
        BigDecimal total = calcularTotal(requestDto.litros(), precoAplicado);

        // Cria uma nova entidade Abastecimento a partir do DTO de requisição
        Abastecimento abastecimento = new Abastecimento(
                bomba, requestDto.dataAbastecimento(), requestDto.litros(), precoAplicado, total);

        // Salva a entidade no banco de dados e retorna o DTO de resposta contendo os dados do abastecimento criado
        return paraResponseDto(abastecimentoRepository.save(abastecimento));
    }

    // Método para listar todos os abastecimentos
    @Transactional(readOnly = true)
    public List<AbastecimentoResponseDto> listarAbastecimentos() {
        return abastecimentoRepository.findAll().stream()
                .map(this::paraResponseDto)
                .toList();
    }

    // Método para buscar um abastecimento pelo ID
    @Transactional(readOnly = true)
    public AbastecimentoResponseDto buscarAbastecimentoPorId(Long id) {
        return paraResponseDto(buscarAbastecimento(id));
    }

    // Método para atualizar um abastecimento existente
    @Transactional
    public AbastecimentoResponseDto atualizarAbastecimento(Long id, AbastecimentoRequestDto requestDto) {
        // Busca o abastecimento existente pelo ID
        Abastecimento abastecimento = buscarAbastecimento(id);

        // Busca a bomba associada ao abastecimento pelo ID fornecido no DTO de requisição
        Bomba bomba = buscarBomba(requestDto.bombaId());

        // Verifica se houve troca de bomba comparando os IDs das bombas
        boolean trocouBomba = !abastecimento.getBomba().getId().equals(bomba.getId());

        if (trocouBomba) {
            // Só a troca efetiva de bomba usa o preço atual do novo combustível.
            abastecimento.setBomba(bomba);
            abastecimento.setPrecoLitroAplicado(bomba.getCombustivel().getPrecoLitro());
        }

        // Se houve troca de bomba ou a quantidade de litros foi alterada, recalcula o valor total
        if (trocouBomba || requestDto.litros().compareTo(abastecimento.getLitros()) != 0) {
            // Na mesma bomba, uma correção de litros preserva o preço aplicado original.
            abastecimento.setValorTotal(calcularTotal(requestDto.litros(), abastecimento.getPrecoLitroAplicado()));
        }

        // Atualiza os campos do abastecimento com os valores do DTO de requisição
        abastecimento.setLitros(requestDto.litros());
        abastecimento.setDataAbastecimento(requestDto.dataAbastecimento());

        // Salva as alterações no banco de dados e retorna o DTO de resposta contendo os dados do abastecimento atualizado
        return paraResponseDto(abastecimentoRepository.save(abastecimento));
    }

    // Método para deletar um abastecimento existente
    @Transactional
    public void deletarAbastecimento(Long id) {
        abastecimentoRepository.delete(buscarAbastecimento(id));
    }

    // Métodos auxiliares privados para buscar entidades e calcular valores

    private Abastecimento buscarAbastecimento(Long id) {
        return abastecimentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Abastecimento não encontrado com o ID: " + id));
    }

    private Bomba buscarBomba(Long id) {
        return bombaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Bomba não encontrada com o ID: " + id));
    }

    private BigDecimal calcularTotal(BigDecimal litros, BigDecimal precoAplicado) {
        // Calcula o valor total do abastecimento com arredondamento para duas casas decimais
        BigDecimal total = litros.multiply(precoAplicado).setScale(2, RoundingMode.HALF_UP);

        // Verifica se o valor total é maior que zero após o arredondamento
        if (total.signum() <= 0) {
            throw new RequisicaoInvalidaException(
                    "O valor total do abastecimento deve ser maior que zero após o arredondamento.");
        }

        // Retorna o valor total calculado
        return total;
    }

    // Método para converter um abastecimento em um DTO de resposta
    private AbastecimentoResponseDto paraResponseDto(Abastecimento abastecimento) {
        Bomba bomba = abastecimento.getBomba();
        Combustivel combustivel = bomba.getCombustivel();
        return new AbastecimentoResponseDto(
                abastecimento.getId(),
                bomba.getId(),
                bomba.getNome(),
                combustivel.getId(),
                combustivel.getNome(),
                // O banco guarda o instante; a API o apresenta no horário de São Paulo.
                abastecimento.getDataAbastecimento().atZoneSameInstant(FUSO_SAO_PAULO).toOffsetDateTime(),
                abastecimento.getLitros(),
                abastecimento.getPrecoLitroAplicado(),
                abastecimento.getValorTotal());
    }
}
