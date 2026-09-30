package com.merito.engenharia.posto_de_gasolina_api.service;

import com.merito.engenharia.posto_de_gasolina_api.dto.BombaRequestDto;
import com.merito.engenharia.posto_de_gasolina_api.entity.Bomba;
import com.merito.engenharia.posto_de_gasolina_api.entity.Combustivel;
import com.merito.engenharia.posto_de_gasolina_api.exception.ConflitoDeNegocioException;
import com.merito.engenharia.posto_de_gasolina_api.exception.RecursoNaoEncontradoException;
import com.merito.engenharia.posto_de_gasolina_api.repository.BombaRepository;
import com.merito.engenharia.posto_de_gasolina_api.repository.CombustivelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/*
* Testes unitários para o serviço de bombas.
* */

class BombaServiceTest {
    private BombaRepository bombaRepository;
    private CombustivelRepository combustivelRepository;
    private BombaService service;

    @BeforeEach
    void configurar() {
        bombaRepository = mock(BombaRepository.class);
        combustivelRepository = mock(CombustivelRepository.class);
        service = new BombaService(bombaRepository, combustivelRepository);
    }

    @Test
    void criarExigeCombustivelExistente() {
        when(combustivelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.criarBomba(new BombaRequestDto("Bomba 1", 99L)));

        verify(bombaRepository, never()).save(org.mockito.ArgumentMatchers.any(Bomba.class));
    }

    @Test
    void reenviarMesmoCombustivelPermiteAtualizarComHistorico() {
        Combustivel combustivel = combustivel(1L);
        Bomba bomba = new Bomba("Nome antigo", combustivel);
        when(bombaRepository.findById(10L)).thenReturn(Optional.of(bomba));
        when(combustivelRepository.findById(1L)).thenReturn(Optional.of(combustivel));
        when(bombaRepository.save(bomba)).thenReturn(bomba);

        var response = service.atualizarBomba(10L, new BombaRequestDto("Nome novo", 1L));

        assertEquals("Nome novo", response.nome());
        assertSame(combustivel, bomba.getCombustivel());
        verify(bombaRepository, never()).existsAbastecimentoByBombaId(10L);
    }

    @Test
    void trocarCombustivelComHistoricoRetornaConflito() {
        Combustivel atual = combustivel(1L);
        Combustivel novo = combustivel(2L);
        Bomba bomba = new Bomba("Bomba 1", atual);
        when(bombaRepository.findById(10L)).thenReturn(Optional.of(bomba));
        when(combustivelRepository.findById(2L)).thenReturn(Optional.of(novo));
        when(bombaRepository.existsAbastecimentoByBombaId(10L)).thenReturn(true);

        assertThrows(ConflitoDeNegocioException.class,
                () -> service.atualizarBomba(10L, new BombaRequestDto("Nome novo", 2L)));

        assertSame(atual, bomba.getCombustivel());
        assertEquals("Bomba 1", bomba.getNome());
        verify(bombaRepository, never()).save(bomba);
    }

    @Test
    void trocarCombustivelSemHistoricoAtualizaVinculo() {
        Combustivel atual = combustivel(1L);
        Combustivel novo = combustivel(2L);
        Bomba bomba = new Bomba("Bomba 1", atual);
        when(bombaRepository.findById(10L)).thenReturn(Optional.of(bomba));
        when(combustivelRepository.findById(2L)).thenReturn(Optional.of(novo));
        when(bombaRepository.save(bomba)).thenReturn(bomba);

        var response = service.atualizarBomba(10L, new BombaRequestDto("Nome novo", 2L));

        assertSame(novo, bomba.getCombustivel());
        assertEquals(2L, response.combustivelId());
        verify(bombaRepository).existsAbastecimentoByBombaId(10L);
    }

    @Test
    void excluirComHistoricoRetornaConflito() {
        Bomba bomba = new Bomba("Bomba 1", combustivel(1L));
        when(bombaRepository.findById(10L)).thenReturn(Optional.of(bomba));
        when(bombaRepository.existsAbastecimentoByBombaId(10L)).thenReturn(true);

        assertThrows(ConflitoDeNegocioException.class, () -> service.deletarBomba(10L));

        verify(bombaRepository, never()).delete(bomba);
    }

    private Combustivel combustivel(Long id) {
        Combustivel combustivel = new Combustivel("Gasolina", new BigDecimal("5.899"));
        combustivel.setId(id);
        return combustivel;
    }
}
