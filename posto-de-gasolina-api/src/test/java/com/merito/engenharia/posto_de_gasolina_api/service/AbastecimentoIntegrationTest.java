package com.merito.engenharia.posto_de_gasolina_api.service;

import com.jayway.jsonpath.JsonPath;
import com.merito.engenharia.posto_de_gasolina_api.entity.Bomba;
import com.merito.engenharia.posto_de_gasolina_api.entity.Combustivel;
import com.merito.engenharia.posto_de_gasolina_api.repository.BombaRepository;
import com.merito.engenharia.posto_de_gasolina_api.repository.CombustivelRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
* Testes de integração para o serviço de abastecimentos.
*/

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AbastecimentoIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CombustivelRepository combustivelRepository;

    @Autowired
    private BombaRepository bombaRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void criaConsultaEditaEExcluiPreservandoOPrecoAplicado() throws Exception {
        // Exercita o fluxo completo com PostgreSQL e verifica o preço histórico após mudar o cadastro.
        Combustivel gasolina = combustivelRepository.save(new Combustivel("Gasolina", new BigDecimal("5.899")));
        Bomba primeiraBomba = bombaRepository.save(new Bomba("Bomba 1", gasolina));
        Combustivel diesel = combustivelRepository.save(new Combustivel("Diesel", new BigDecimal("6.199")));
        Bomba segundaBomba = bombaRepository.save(new Bomba("Bomba 2", diesel));

        String respostaCriacao = mockMvc.perform(post("/abastecimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicao(primeiraBomba.getId(), "2026-09-28T19:30:00-03:00", "20.125")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valorTotal").value(118.72))
                .andExpect(jsonPath("$.precoLitroAplicado").value(5.899))
                .andExpect(jsonPath("$.bombaNome").value("Bomba 1"))
                .andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(respostaCriacao, "$.id")).longValue();

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/abastecimentos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataAbastecimento").value("2026-09-28T19:30:00-03:00"));
        mockMvc.perform(get("/abastecimentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));

        combustivelRepository.findById(gasolina.getId()).orElseThrow()
                .setPrecoLitro(new BigDecimal("7.000"));
        entityManager.flush();
        entityManager.clear();
        mockMvc.perform(put("/abastecimentos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicao(primeiraBomba.getId(), "2026-09-29T12:00:00-03:00", "21.000")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precoLitroAplicado").value(5.899))
                .andExpect(jsonPath("$.valorTotal").value(123.88));

        mockMvc.perform(put("/abastecimentos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicao(primeiraBomba.getId(), "2026-09-29T16:00:00Z", "21.000")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataAbastecimento").value("2026-09-29T13:00:00-03:00"))
                .andExpect(jsonPath("$.precoLitroAplicado").value(5.899))
                .andExpect(jsonPath("$.valorTotal").value(123.88));

        mockMvc.perform(put("/abastecimentos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicao(segundaBomba.getId(), "2026-09-29T12:00:00-03:00", "21.000")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bombaId").value(segundaBomba.getId()))
                .andExpect(jsonPath("$.precoLitroAplicado").value(6.199))
                .andExpect(jsonPath("$.valorTotal").value(130.18));

        mockMvc.perform(delete("/abastecimentos/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/abastecimentos/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void rejeitaReferenciasInvalidasTotalZeradoECamposCalculadosNaEntrada() throws Exception {
        // Entradas inválidas devem falhar antes de persistir um abastecimento incorreto.
        Combustivel combustivel = combustivelRepository.save(new Combustivel("Gasolina", new BigDecimal("0.001")));
        Bomba bomba = bombaRepository.save(new Bomba("Bomba 1", combustivel));

        mockMvc.perform(post("/abastecimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicao(99999L, "2026-09-28T19:30:00-03:00", "1.000")))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/abastecimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicao(bomba.getId(), "2026-09-28T19:30:00-03:00", "0.001")))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/abastecimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bombaId": %d, "dataAbastecimento": "2026-09-28T19:30:00-03:00",
                                 "litros": 10.000, "valorTotal": 0.01}
                                """.formatted(bomba.getId())))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/abastecimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicao(bomba.getId(), "2026-09-28T19:30:00", "1.000")))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/abastecimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicao(bomba.getId(), "2026-09-28T19:30:00-03:00", "1.0001")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void arredondaMeioCentavoParaCima() throws Exception {
        // 1,000 L × R$ 0,005/L deve resultar em R$ 0,01 pelo HALF_UP.
        Combustivel combustivel = combustivelRepository.save(new Combustivel("Gasolina", new BigDecimal("0.005")));
        Bomba bomba = bombaRepository.save(new Bomba("Bomba 1", combustivel));

        mockMvc.perform(post("/abastecimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicao(bomba.getId(), "2026-09-28T19:30:00-03:00", "1.000")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valorTotal").value(0.01));
    }

    @Test
    void historicoBloqueiaExclusoesAteRemoverOAbastecimento() throws Exception {
        // O vínculo protege bomba e combustível; após remover o filho, ambos podem ser excluídos.
        Combustivel gasolina = combustivelRepository.save(new Combustivel("Gasolina", new BigDecimal("5.899")));
        Bomba bomba = bombaRepository.save(new Bomba("Bomba 1", gasolina));
        String respostaCriacao = mockMvc.perform(post("/abastecimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicao(bomba.getId(), "2026-09-28T19:30:00-03:00", "10.000")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long abastecimentoId = ((Number) JsonPath.read(respostaCriacao, "$.id")).longValue();

        mockMvc.perform(delete("/bombas/{id}", bomba.getId())).andExpect(status().isConflict());
        mockMvc.perform(delete("/combustiveis/{id}", gasolina.getId())).andExpect(status().isConflict());

        mockMvc.perform(delete("/abastecimentos/{id}", abastecimentoId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/bombas/{id}", bomba.getId())).andExpect(status().isNoContent());
        mockMvc.perform(delete("/combustiveis/{id}", gasolina.getId())).andExpect(status().isNoContent());
    }

    @Test
    void referenciaInexistenteNaEdicaoNaoAlteraORegistro() throws Exception {
        // Um PUT inválido não pode modificar a bomba, o preço ou o total já gravados.
        Combustivel gasolina = combustivelRepository.save(new Combustivel("Gasolina", new BigDecimal("5.899")));
        Bomba bomba = bombaRepository.save(new Bomba("Bomba 1", gasolina));
        String respostaCriacao = mockMvc.perform(post("/abastecimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicao(bomba.getId(), "2026-09-28T19:30:00-03:00", "10.000")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(respostaCriacao, "$.id")).longValue();

        mockMvc.perform(put("/abastecimentos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicao(99999L, "2026-09-29T12:00:00-03:00", "20.000")))
                .andExpect(status().isNotFound());

        entityManager.clear();
        mockMvc.perform(get("/abastecimentos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bombaId").value(bomba.getId()))
                .andExpect(jsonPath("$.litros").value(10.000))
                .andExpect(jsonPath("$.precoLitroAplicado").value(5.899))
                .andExpect(jsonPath("$.valorTotal").value(58.99));
    }

    private String requisicao(Long bombaId, String data, String litros) {
        return """
                {"bombaId": %d, "dataAbastecimento": "%s", "litros": %s}
                """.formatted(bombaId, data, litros);
    }
}
