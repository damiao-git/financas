# Monexa API

Backend da Monexa, uma API de planejamento financeiro pessoal focada em substituir uma planilha de contas mensais, fluxo de caixa, dividas e amortizacoes.

## Stack

- Java 21
- Spring Boot
- Spring Security + JWT
- Spring Data JPA
- MySQL
- Docker Compose
- Swagger/OpenAPI

## Como rodar

```bash
docker compose up -d
./mvnw spring-boot:run
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

## Autenticacao

Criar usuario:

```http
POST /auth/register
```

Login:

```http
POST /auth/login
```

Use o token retornado nas demais chamadas:

```http
Authorization: Bearer <token>
```

## Fluxo principal para testar o MVP

1. Criar usuario e fazer login.
2. Criar categorias.
3. Criar receitas, despesas recorrentes e dividas.
4. Gerar recorrencias para os proximos meses.
5. Consultar contas mensais.
6. Registrar pagamentos e amortizacoes.
7. Consultar fluxo mensal e projecao multi-mes.

## Endpoints principais

### Perfil

```http
GET /usuarios/me
PUT /usuarios/me
DELETE /usuarios/me
```

### Categorias

```http
GET /categorias
GET /categorias/{id}
POST /categorias
PUT /categorias/{id}
DELETE /categorias/{id}
```

### Receitas

```http
GET /receitas
GET /receitas?ano=2026&mes=9
GET /receitas/{id}
POST /receitas
PUT /receitas/{id}
DELETE /receitas/{id}
```

Receitas recorrentes podem ser usadas como modelo para gerar meses futuros.

### Despesas

```http
GET /despesas
GET /despesas/{id}
POST /despesas
PUT /despesas/{id}
DELETE /despesas/{id}
```

Despesas recorrentes podem gerar contas mensais futuras.

### Contas mensais

```http
GET /contas-mensais
GET /contas-mensais?ano=2026&mes=9
GET /contas-mensais/{id}
POST /contas-mensais
PUT /contas-mensais/{id}
DELETE /contas-mensais/{id}
```

### Pagamentos

```http
GET /contas-mensais/{contaMensalId}/pagamentos
POST /contas-mensais/{contaMensalId}/pagamentos
PUT /pagamentos/{id}
DELETE /pagamentos/{id}
```

Registrar pagamento marca a conta mensal como paga. Remover o ultimo pagamento ativo volta a conta para pendente.

### Dividas

```http
GET /dividas
GET /dividas?status=ATIVA
GET /dividas/{id}
POST /dividas
PUT /dividas/{id}
DELETE /dividas/{id}
```

### Amortizacoes

```http
GET /dividas/{dividaId}/amortizacoes
POST /dividas/{dividaId}/amortizacoes
GET /amortizacoes?ano=2026&mes=9
PUT /amortizacoes/{id}
DELETE /amortizacoes/{id}
```

Registrar amortizacao reduz o saldo atual da divida. Remover uma amortizacao reverte o saldo.

### Planejamento

Gerar receitas recorrentes, contas mensais de despesas recorrentes e parcelas de dividas:

```http
POST /planejamento/gerar-recorrencias?anoInicial=2026&mesInicial=9&quantidadeMeses=12
```

### Fluxo de caixa

Fluxo mensal:

```http
GET /fluxo-caixa?ano=2026&mes=9
```

Projecao multi-mes:

```http
GET /fluxo-caixa/projecao?anoInicial=2026&mesInicial=9&quantidadeMeses=12&saldoInicial=1000
```

Retorna receitas, despesas previstas, contas pagas, contas pendentes, amortizacoes, saldo do mes e caixa acumulado.

## Status do MVP

O MVP backend cobre:

- usuario/autenticacao
- receitas por competencia
- despesas e categorias
- contas mensais e pagamentos
- dividas e amortizacoes
- geracao de recorrencias
- fluxo de caixa mensal
- projecao multi-mes com caixa acumulado

Proximos passos naturais:

- testes unitarios e de integracao dos services principais
- eventos/marcos da projecao
- cenarios financeiros
- frontend web
