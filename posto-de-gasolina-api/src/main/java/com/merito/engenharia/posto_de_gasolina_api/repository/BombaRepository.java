package com.merito.engenharia.posto_de_gasolina_api.repository;

import com.merito.engenharia.posto_de_gasolina_api.entity.Bomba;
import org.springframework.data.jpa.repository.JpaRepository;

/*
* Repositório para a entidade Bomba.
*/

public interface BombaRepository extends JpaRepository<Bomba, Long> {
    // Verifica se existe alguma bomba associada a um determinado combustível pelo ID do combustível.
    boolean existsByCombustivel_Id(Long combustivelId);
}
