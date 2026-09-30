package com.merito.engenharia.posto_de_gasolina_api.service;

import com.merito.engenharia.posto_de_gasolina_api.dto.BombaRequestDto;
import com.merito.engenharia.posto_de_gasolina_api.entity.Combustivel;
import com.merito.engenharia.posto_de_gasolina_api.exception.ConflitoDeNegocioException;
import com.merito.engenharia.posto_de_gasolina_api.exception.RecursoNaoEncontradoException;
import com.merito.engenharia.posto_de_gasolina_api.repository.CombustivelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
* Testes de integração para o serviço de bombas.
*/

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BombaIntegrationTest {
    @Autowired
    private BombaService bombaService;

    @Autowired
    private CombustivelRepository combustivelRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void criaConsultaAlteraEExcluiBombaNoPostgresql() {
        Combustivel gasolina = combustivelRepository.save(new Combustivel("Gasolina", new BigDecimal("5.899")));
        Combustivel diesel = combustivelRepository.save(new Combustivel("Diesel", new BigDecimal("6.199")));

        var criada = bombaService.criarBomba(new BombaRequestDto("Bomba 1", gasolina.getId()));
        assertEquals(gasolina.getId(), criada.combustivelId());
        assertEquals("Gasolina", bombaService.buscarBombaPorId(criada.id()).combustivelNome());
        assertEquals(1, bombaService.listarBombas().size());

        var atualizada = bombaService.atualizarBomba(criada.id(), new BombaRequestDto("Bomba 2", diesel.getId()));
        assertEquals("Bomba 2", atualizada.nome());
        assertEquals(diesel.getId(), atualizada.combustivelId());

        bombaService.deletarBomba(criada.id());
        assertThrows(RecursoNaoEncontradoException.class, () -> bombaService.buscarBombaPorId(criada.id()));
    }

    @Test
    void historicoBloqueiaTrocaDeCombustivelEExclusao() {
        Combustivel gasolina = combustivelRepository.save(new Combustivel("Gasolina", new BigDecimal("5.899")));
        Combustivel diesel = combustivelRepository.save(new Combustivel("Diesel", new BigDecimal("6.199")));
        var bomba = bombaService.criarBomba(new BombaRequestDto("Bomba 1", gasolina.getId()));

        jdbcTemplate.update("""
                INSERT INTO abastecimentos (bomba_id, data_abastecimento, litros, preco_litro_aplicado, valor_total)
                VALUES (?, CURRENT_TIMESTAMP, 10.000, 5.899, 58.99)
                """, bomba.id());

        assertThrows(ConflitoDeNegocioException.class,
                () -> bombaService.atualizarBomba(bomba.id(), new BombaRequestDto("Bomba 2", diesel.getId())));
        assertThrows(ConflitoDeNegocioException.class, () -> bombaService.deletarBomba(bomba.id()));

        var renomeada = bombaService.atualizarBomba(bomba.id(), new BombaRequestDto("Bomba 2", gasolina.getId()));
        assertEquals("Bomba 2", renomeada.nome());
        assertEquals(gasolina.getId(), renomeada.combustivelId());
    }
}
