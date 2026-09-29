package com.merito.engenharia.posto_de_gasolina_api.repository;

import com.merito.engenharia.posto_de_gasolina_api.entity.Combustivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
* Repository para a entidade Combustivel.
* */

@Repository
public interface CombustivelRepository extends JpaRepository<Combustivel, Long> {
}
