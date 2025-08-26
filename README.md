
# Título do Projeto

Projeto pessoal para testes de conhecimento

O objetivo principal desse projeto é criar um sistema que gerencia as finanças de uma casa, desde as categorias até as despesas. 

### O MVP será:  

- Cadastro de categorias de despesas (Alimentação, Transporte, Lazer, etc).

- Cadastro de despesas (valor, dia do vencimento, descrição, categoria).

- Listagem das despesas por mês.

- Cálculo do total de despesas no mês.

- (Opcional) Meta mensal e aviso se ultrapassar.

### As melhorias seguem uma ordem lógica:

- Tratar possíveis exceções com uma Exception personalizada

- Padronizar o código com as melhores práticas

- Inserir toda a parte de segurança com login e senha (Spring Security)

- Relatórios (média de consumo, minimo, máximo, etc)

- Etc...

### Lista dos Endpoints

```
GET /categoria -> Buscar todas as categorias
GET /categoria/{id} -> Buscar categoria por id
POST /categoria -> Cadastrar uma nova categoria
PUT /categoria/{id} -> Atualizar uma categoria por id
DELETE /categoria/{id} -> Deletar uma categoria por id
GET /despesa -> Buscar todas as despesas
GET /despesa/{id} -> Buscar despesa por id
POST /despesa -> Cadastrar uma nova despesa
PUT /despesa/{id} -> Atualizar uma despesa por id
DELETE /despesa/{id} -> Deletar uma despesa por id

```

Caso queira baixar pra testar, fique a vontade

O Swagger do projeto está funcional também no endpoint abaixo: 

/swagger-ui/index.html#/
