# Posto de Gasolina API

API REST em Java e Spring Boot para o desafio técnico da Mérito Engenharia. O banco PostgreSQL é criado e versionado pelo Flyway; o Hibernate valida o schema existente.

## Requisitos

- JDK 21
- Docker Desktop com Docker Compose
- IntelliJ IDEA ou outro editor Java

O projeto inclui Maven Wrapper, então não é necessário instalar Maven globalmente.

## Configuração local

Na raiz do repositório, copie `.env.example` para `.env` e substitua os valores de senha de exemplo. O Docker Compose lê esse arquivo para configurar o PostgreSQL.

Inicie o banco de desenvolvimento:

```powershell
docker compose up -d postgres
```

Na configuração de execução da aplicação na IDE, defina estas variáveis com os valores correspondentes do `.env`:

- `SPRING_DATASOURCE_URL` — por padrão, `jdbc:postgresql://localhost:5434/posto_gasolina`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

Execute `PostoDeGasolinaApiApplication`. O Flyway aplica as migrações `V1`, `V2` e `V3`; o Hibernate valida as tabelas sem criá-las ou alterá-las.

Para executar pelo terminal, abra a pasta da aplicação e use o Maven Wrapper (`.\mvnw.cmd spring-boot:run` no Windows ou `./mvnw spring-boot:run` no macOS/Linux), definindo as mesmas variáveis de ambiente antes de iniciar.

O banco de desenvolvimento usa a porta `5434` e o volume `postgres_data`, que preserva os dados ao parar ou recriar o container. `docker compose down` mantém o volume. Não use `docker compose down -v` se quiser preservar os dados.

## Testes

Os testes usam o perfil Spring `test` e um banco PostgreSQL isolado, com container, porta e volume próprios. Na raiz do repositório, inicie-o com:

```powershell
docker compose --profile test up -d postgres-test
```

Depois, na pasta `posto-de-gasolina-api`, execute:

```powershell
.\mvnw.cmd test
```

No macOS/Linux, use `./mvnw test`. O teste de contexto conecta ao banco em `localhost:5435` e o Flyway aplica as mesmas migrações. O container de teste usa o volume `postgres_test_data`, separado do volume de desenvolvimento. Os valores locais padrão estão em `.env.example`; se alterar a senha de teste, defina também `TEST_SPRING_DATASOURCE_PASSWORD` para o processo Maven/IDE.

## Combustíveis

O CRUD de combustíveis está implementado:

| Método | Rota | Resultado |
| --- | --- | --- |
| `POST` | `/combustiveis` | Cria e retorna o combustível com ID (`201`) |
| `GET` | `/combustiveis` | Lista combustíveis (`200`) |
| `GET` | `/combustiveis/{id}` | Consulta por ID (`200` ou `404`) |
| `PUT` | `/combustiveis/{id}` | Atualiza nome e preço por litro (`200`, `404`) |
| `DELETE` | `/combustiveis/{id}` | Exclui (`204`, `404` ou `409` se houver bomba vinculada) |

Exemplo de criação, usando JSON com ponto decimal:

```json
{
  "nome": "Gasolina comum",
  "precoLitro": 5.899
}
```

O nome é obrigatório, não pode conter apenas espaços e tem limite de 100 caracteres. O preço por litro deve ser positivo, com até 7 dígitos inteiros e 3 casas decimais. Os erros tratados pela API usam `ProblemDetail`.

## Estado do projeto

Até o momento, o CRUD de combustíveis está concluído. Bombas e abastecimentos serão implementados nas próximas etapas do desafio.
