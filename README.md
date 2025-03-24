# Projeto de API de Gerenciamento de Loja de Eletrônicos

## Descrição

Esta API foi desenvolvida para simular o controle interno de uma loja de eletrônicos. O sistema é composto por microsserviços responsáveis pelo cadastro de clientes, produtos, pedidos e gerenciamento de entrega, a comunicação entre eles ocorre via OpenFeign e Kafka. A API apenas simula o gerenciamento interno da loja.

## Desenvolvedor

Elias Mathias Sand: [elias.coder1@gmail.com](mailto:elias.coder1@gmail.com) - [GitHub](https://github.com/EliasNote) - [LinkedIn](https://www.linkedin.com/in/elias-mathias-sand-243398234/)

## Arquitetura

A aplicação é composta pelos seguintes microsserviços:

- **Server** : Faz a descoberta dos microsserviços
- **Gateway** : Centraliza todas as requisições para a porta 8080
- **Clients**: Gerencia o cadastro e informações dos clientes
- **Products**: Gerencia o cadastro e informações dos produtos
- **Orders**: Gerencia os pedidos realizados pelos clientes
- **Delivery**: Gerencia os status de entrega dos pedidos

### Principais Tecnologias Utilizadas

- **Java** com **Spring Boot**
- **Eureka** para descoberta de serviços
- **Actuator** para monitoramento e gerenciamento
- **OpenFeign** para comunicação entre os microsserviços
- **Apache Kafka** para mensageria
- **Docker** e **Docker Compose** para orquestração dos microsserviços

### Funcionalidades

- Cadastro, edição, e remoção de clientes e produtos;
- Criação de pedido com um cliente e produto;
- Envio do pedido via kafka quando o mesmo estiver sendo processado;
- Atualização de status do pedido para enviado ou cancelado, remoção do pedido e armazenamento;
- Consulta dos dados;

## Instalação

### Pré-requisito

- Docker Desktop instalado na máquina.

### Via Docker

```bash
git clone https://github.com/EliasNote/commerce.git
cd commerce
docker compose up
```

<!-- Após executar o comando, aguarde a criação dos containers e acesse os serviços conforme descrito abaixo -->

### Via Repositório

```bash
git clone https://github.com/EliasNote/commerce.git
cd commerce
docker-compose -f docker-compose-kafka.yml up
```

#### Etapas de Inicialização

1. Executar o container do Kafka no Docker Desktop;
2. Server;
3. Microsserviços clients, products, orders e delivery;
4. Gateway.

E pronto, o projeto já está em funcionamento

## Acessos Importantes

_Obs. Para acessos com o Swagger, Actuator e H2 é necessário utilizar a porta aleatória gerada no Eureka Server!_

- Endereço para acesso do Eureka Server: http://localhost:8761
- Endereço para acesso do Actuator: http://localhost:{porta}/actuator
- Endereço para acesso do Swagger: http://localhost:{porta}/docs-ui.html
- Endereço para acesso do Kafdrop: http://localhost:19000

**H2 API Clients**

- Endereço: http://localhost:{porta}/h2-clients
- JDBC URL: `jdbc:h2:file:./clients`;
- User Name: `root`;
- Password: `1234`;

**H2 API Products**

- Endereço: http://localhost:{porta}/h2-products
- JDBC URL: `jdbc:h2:file:./products`;
- User Name: `root`;
- Password: `1234`;

**H2 API Orders**

- Endereço: http://localhost:{porta}/h2-orders
- JDBC URL: `jdbc:h2:file:./orders`;
- User Name: `root`;
- Password: `1234`;

**H2 API Delivery**

- Endereço: http://localhost:{porta}/h2-delivery
- JDBC URL: `jdbc:h2:file:./delivery`;
- User Name: `root`;
- Password: `1234`;

## Endpoints

### Clients

| Rota                                                                                       | Método | Descrição                             |
| ------------------------------------------------------------------------------------------ | :----: | ------------------------------------- |
| `http://localhost:8080/api/v1/clients`                                                     |  POST  | Cadastrar um cliente                  |
| `http://localhost:8080/api/v1/clients`                                                     |  GET   | Buscar todos os clientes              |
| `http://localhost:8080/api/v1/clients/name/{name}`                                         |  GET   | Buscar clientes pelo nome             |
| `http://localhost:8080/api/v1/clients/cpf/{cpf}`                                           |  GET   | Buscar um cliente pelo CPF            |
| `http://localhost:8080/api/v1/clients/date?afterDate={yyyy-MM-dd}&beforeDate={yyyy-MM-dd}` |  GET   | Buscar clientes pela data de cadastro |
| `http://localhost:8080/api/v1/clients/edit/{cpf}`                                          | PATCH  | Editar dados de um cliente pelo CPF   |
| `http://localhost:8080/api/v1/clients/delete/{cpf}`                                        | DELETE | Excluir um cliente pelo CPF           |

### Products

#### Categorias de produtos para cadastro

```
COMPUTERS,
SMARTPHONES,
HEADPHONES,
MOUSES,
KEYBOARDS,
SCREENS
```

| Rota                                                                                        | Método | Descrição                                        |
| ------------------------------------------------------------------------------------------- | :----: | ------------------------------------------------ |
| `http://localhost:8080/api/v1/products`                                                     |  POST  | Cadastrar um produto                             |
| `http://localhost:8080/api/v1/products`                                                     |  GET   | Buscar por todos os produtos                     |
| `http://localhost:8080/api/v1/products/title/{title}`                                       |  GET   | Buscar por um produto pelo título                |
| `http://localhost:8080/api/v1/products/supplier/{supplier}`                                 |  GET   | Buscar produtos por nome de fornecedor           |
| `http://localhost:8080/api/v1/products/category/{category}`                                 |  GET   | Buscar produtos por categoria                    |
| `http://localhost:8080/api/v1/products/sku/{sku}`                                           |  GET   | Buscar um produto por código SKU                 |
| `http://localhost:8080/api/v1/products/actived`                                             |  GET   | Buscar por todos os produtos ativos              |
| `http://localhost:8080/api/v1/products/disabled`                                            |  GET   | Buscar por todos os produtos desativados         |
| `http://localhost:8080/api/v1/products/date?afterDate={yyyy-MM-dd}&beforeDate={yyyy-MM-dd}` |  GET   | Buscar produtos pela data de cadastro            |
| `http://localhost:8080/api/v1/products/edit/{sku}`                                          | PATCH  | Editar dados de um produto pelo código SKU       |
| `http://localhost:8080/api/v1/products/status/{sku}`                                        | PATCH  | Alterar o status do produto pelo código SKU      |
| `http://localhost:8080/api/v1/products/sku/{sku}/add/{quantity}`                            | PATCH  | Adicionar quantidade de produtos pelo código SKU |
| `http://localhost:8080/api/v1/products/sku/{sku}/sub/{quantity}`                            | PATCH  | Remover quantidade de produtos pelo código SKU   |
| `http://localhost:8080/api/v1/products/delete/{sku}`                                        | DELETE | Excluir um produto pelo código SKU               |

### Orders

| Rota                                                                                      | Método | Descrição                                                                               |
| ----------------------------------------------------------------------------------------- | :----: | --------------------------------------------------------------------------------------- |
| `http://localhost:8080/api/vi/orders`                                                     |  POST  | Cadastrar um pedido                                                                     |
| `http://localhost:8080/api/vi/orders`                                                     |  GET   | Buscar por todos os pedidos                                                             |
| `http://localhost:8080/api/vi/orders/sku/{sku}`                                           |  GET   | Buscar pedidos por código SKU                                                           |
| `http://localhost:8080/api/vi/orders/cpf/{cpf}`                                           |  GET   | Buscar pedidos por CPF                                                                  |
| `http://localhost:8080/api/vi/orders/date?afterDate={yyyy-MM-dd}&beforeDate={yyyy-MM-dd}` |  GET   | Buscar pedidos pela data de cadastro                                                    |
| `http://localhost:8080/api/vi/orders/processing/{id}`                                     | PATCH  | Atualizar o status do pedido para processando e enviar para o microsserviço de delivery |
| `http://localhost:8080/api/vi/orders/delete/{id}`                                         | DELETE | Deletar um pedido pelo id                                                               |
| `http://localhost:8080/api/vi/orders/delete/processing`                                   | DELETE | Deletar todos os pedidos com status de processando                                      |

### Delivery

#### Status dos pedidos para entrega

```
PROCESSING,
SHIPPED,
CANCELED
```

| Rota                                                                                          | Método | Descrição                                            |
| --------------------------------------------------------------------------------------------- | :----: | ---------------------------------------------------- |
| `http://localhost:8080/api/vi/deliveries`                                                     |  GET   | Buscar por todas as entregas                         |
| `http://localhost:8080/api/vi/deliveries/id/{id}`                                             |  GET   | Buscar entrega por id                                |
| `http://localhost:8080/api/vi/deliveries/shipped`                                             |  GET   | Buscar por todos os pedidos entregues                |
| `http://localhost:8080/api/vi/deliveries/processing`                                          |  GET   | Buscar por todos os pedidos em processamento         |
| `http://localhost:8080/api/vi/deliveries/canceled`                                            |  GET   | Buscar por todos os pedidos cancelados               |
| `http://localhost:8080/api/vi/deliveries/date?afterDate={yyyy-MM-dd}&beforeDate={yyyy-MM-dd}` |  GET   | Buscar entregas pela data de cadastro do pedido      |
| `http://localhost:8080/api/vi/deliveries/cancel/{id}`                                         | PATCH  | Alterar o status de entrega do pedido para cancelado |
| `http://localhost:8080/api/vi/deliveries/shipped/{id}`                                        | PATCH  | Alterar o status de entrega do pedido para entregue  |
| `http://localhost:8080/api/vi/deliveries/delete/canceled`                                     | DELETE | Deleta todos os pedidos com status de cancelado      |

## Contribuição

Contribuições são bem-vindas!

1. Faça um fork do repositório.
2. Crie uma branch para sua feature: `git checkout -b minha-feature`.
3. Envie um pull request descrevendo as alterações.
