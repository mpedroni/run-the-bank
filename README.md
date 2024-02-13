# run-the-bank

Este repositório contém a implementação de um sistema bancário simples, que permite a criação de clientes, contas, depósitos e transferências entre contas. 

## Tecnologias utilizadas

- Java 21
- Spring Boot 3
- RabbitMQ
- H2 Database

## Como executar

Para executar a aplicação, basta clonar o repositório e executar a aplicação usando o IntelliJ IDEA ou usando o seguinte comando:
    
```shell
./gradlew bootRun 
```
Na raiz do projeto há um arquivo `docker-compose.yml`, que contém as configurações para subir um container RabbitMQ, necessário para o funcionamento correto da aplicação. O arquivo `docker-compose.yml` também contém as configurações para subir um container da aplicação em si. Para desenvolvimento, este container pode ser desabilitado. Para subir os containers, basta executar o seguinte comando:

```shell
docker-compose up -d
```

## Sobre o design e arquitetura da aplicação

A aplicação foi desenvolvida utilizando o padrão [Transaction Script](https://martinfowler.com/eaaCatalog/transactionScript.html), que organiza as regras de negócios em _procedures_, sendo que cada _procedure_ é responsável por uma operação específica. A aplicação também utiliza o padrão [Ports and Adapters](https://alistair.cockburn.us/hexagonal-architecture/), que separa as regras de negócio da lógica de infraestrutura e garante um alto nível de testabilidade, permitindo que os comportamentos dos componentes de domínio sejam testados de maneira isolada.

A escolha do padrão Transaction Script se deu pelo fato de que a aplicação é simples e não possui regras de negócio complexas. O padrão Transaction Script é mais simples e direto, o que facilita a compreensão e manutenção do código. Porém, caso a aplicação cresça e as regras de negócio se tornem mais complexas, é possível migrar para o padrão [Domain Model](https://martinfowler.com/eaaCatalog/domainModel.html), que organiza as regras de negócio em objetos de domínio, agrupando em um único objeto o estado e comportamento do objeto de domínio, aumentando a coesão e a possibilidade de reuso das classes.

A aplicação possui uma integração com o RabbitMQ, de modo que as transações financeiras são enfileiradas e processadas assincronamente. Dessa forma, toda nova transação realizada é enviada para uma fila no RabbitMQ e um listener, configurado na própria aplicação, consome as mensagens da fila e processa as transações.

## Contratos de API

### Criar cliente
`POST /customers`
```json
{
  "name": "John Doe",
  "document": 12365478902,
  "address": "Zurich, Switzerland",
  "password": "Password@1234"
}
```

### Criar empresa
`POST /companies`
```json
{
  "name": "ACME Inc.",
  "document": 12365478902,
  "address": "Zurich, Switzerland",
  "password": "Password@1234"
}
```

### Criar conta
`POST /accounts`
```json
{
  "clientId": "{UUID}",
  "agency": 1234
}
```

### Realizar depósito
`POST /deposits`

```json
{
  "accountId": "{UUID}",
  "amount": 100.00
}
```

### Realizar transferência
`POST /transfers`

```json
{
  "payerAccountId": "{UUID}",
  "payeeAccountId": "{UUID}",
  "amount": 100.00
}
```

### Cancelar transferência
`PATCH /transactions/cancel`

```json
{
  "transactionId": "{UUID}"
}
```

## Possíveis melhorias e implementações futuras

- Implementar endpoints para consultar clientes e empresas, bem como suas respectivas contas.
- Criar _value-objects_ para abstrair alguns conceitos de domínio, como CPF, CNPJ, endereço, valores monetários, etc. 
  - Essa abstração facilita a compreensão e evita a repetição de código, além de melhorar a testabilidade do código referente a esses conceitos.
- Agregar os erros de validação da requisição em um único objeto de resposta.
  - Atualmente, os erros de validação da requisição são retornados conforme o erro ocorre. Isso piora a experiência do usuário, pois ele não sabe todos os campos que necessitam de correção de uma vez.
- Exigir uma senha forte e criar um hash da senha antes de salvá-la no banco, além de criar um mecanismo de autenticação.
- Criar rotas para registrar e consultar agências.
- Usar algum mecanismo de lock distribuído (por exemplo, o Redis ou ZooKeeper) para processar transações de débito de uma determinada conta uma de cada vez.
  - Isso evita que duas transações de débito sejam processadas simultaneamente, o que poderia levar a inconsistências no saldo da conta.
- Criar uma fila de _dead letter_ para transações.
  - Isso permite que transações que falharam sejam reprocessadas posteriormente.
- Testar a integração com o RabbitMQ e criar testes _end-to-end_ para garantir que as operações de depósito e transferência estão funcionando corretamente.
  - É necessário testar se tanto o _publisher_ quanto o _listener_ estão funcionando corretamente.
- Utilizar uma chave de idempotência para transações.
  - Isso evita que uma transação seja processada mais de uma vez.
- Melhorar a estratégia de _logging_.
  - Atualmente, a aplicação faz _logging_ apenas para fins de depuração. Seria interessante adicionar _logging_ para monitoramento e auditoria.
- Adicionar notificações assíncronas.
  - Após cada transferência, ambos os clientes envolvidos na transação são notificados sobre a transação, porém atualmente essas notificações são enviadas de maneira síncrona.
- Criar uma DSL comum para os testes, especialmente para os testes de integração e _end-to-end_.
  - Isso facilita a criação e manutenção dos testes, além de melhorar a legibilidade.
