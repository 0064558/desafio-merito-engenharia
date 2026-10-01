package com.merito.engenharia.posto_de_gasolina_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Posto de Gasolina API",
        version = "1.0",
        description = "API do desafio Mérito Engenharia para gerenciar combustíveis, bombas e abastecimentos. "
                + "O PUT recebe todos os campos editáveis. Valores monetários são calculados no backend; "
                + "erros são retornados no formato ProblemDetail."
))
public class OpenApiConfig {
}
