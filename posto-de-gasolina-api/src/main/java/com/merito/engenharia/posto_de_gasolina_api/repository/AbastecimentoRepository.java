package com.merito.engenharia.posto_de_gasolina_api.repository;

import com.merito.engenharia.posto_de_gasolina_api.entity.Abastecimento;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * Repositório para a entidade Abastecimento.
 */
public interface AbastecimentoRepository extends JpaRepository<Abastecimento, Long> {
}
