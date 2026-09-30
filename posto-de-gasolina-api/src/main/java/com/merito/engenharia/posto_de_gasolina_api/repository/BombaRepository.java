package com.merito.engenharia.posto_de_gasolina_api.repository;

import com.merito.engenharia.posto_de_gasolina_api.entity.Bomba;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/*
* Repositório para a entidade Bomba.
*/

public interface BombaRepository extends JpaRepository<Bomba, Long> {
    // Verifica se existe alguma bomba associada a um determinado combustível pelo ID do combustível.
    boolean existsByCombustivel_Id(Long combustivelId);

    // Verifica se existe algum abastecimento associado a uma determinada bomba pelo ID da bomba.
    @Query(value = "SELECT EXISTS (SELECT 1 FROM abastecimentos WHERE bomba_id = :bombaId)", nativeQuery = true)
    boolean existsAbastecimentoByBombaId(@Param("bombaId") Long bombaId);
}
