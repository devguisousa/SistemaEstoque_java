Sistema de Estoque (API REST)

API REST em Java com Spring Boot para controle de estoque. Cadastra produtos, categorias, fornecedores e clientes, mantém a quantidade de cada produto em cada estoque e registra compras, vendas e transferências entre estoques.

Projeto em desenvolvimento, feito para estudo e portfólio. Veja a seção Status do projeto.

Tecnologias
Java 21
Spring Boot 4.0.1 (Web MVC, Data JPA, Validation, Security)
PostgreSQL
Flyway (dependência incluída para versionar o banco)
JWT com a biblioteca java-jwt (auth0)
Bucket4j (limitação de requisições)
springdoc-openapi (Swagger UI)
Lombok
Maven (com o Maven Wrapper, não precisa instalar o Maven)
Funcionalidades
Usuários: cadastro e login, com senha criptografada com BCrypt.
Produtos: cadastro, listagem paginada, busca por id, atualização, ativação e desativação (o produto não é apagado, só fica inativo).
Categorias: cadastro, listagem paginada, busca, atualização e exclusão.
Fornecedores e clientes: ambos na mesma entidade, separados pelo campo tipo (FORNECEDOR ou CLIENTE). Validação de CPF (11 números), telefone (10 ou 11 números) e e-mail.
Estoques: cada estoque tem um nome, um produto e uma quantidade (nunca negativa).
Compras: entrada de produtos vindos de um fornecedor.
Vendas: saída de produtos para um cliente.
Transferências (em andamento): regras de negócio para mover um produto entre dois estoques.
Listagens paginadas (10 por página por padrão) e com filtros.
Regras de negócio

Compra

Nasce como PENDENTE, com um código gerado automaticamente.
O fornecedor precisa ser do tipo FORNECEDOR.
A data de recebimento não pode ser anterior à data da compra.
Só compras PENDENTE podem ser editadas ou canceladas.
Ao comprar, a quantidade é somada ao estoque e a compra vira COMPRADO.

Venda

Nasce como PENDENTE, com um código gerado automaticamente.
O cliente precisa ser do tipo CLIENTE.
A quantidade não pode ser maior que a disponível no estoque.
Só vendas PENDENTE podem ser editadas ou canceladas.
Ao vender, a quantidade é descontada do estoque e a venda vira VENDIDO.

Transferência

Origem e destino precisam ser estoques diferentes e do mesmo produto.
Estados: PENDENTE, TRANSFERIDO e CANCELADO.
Segurança
Autenticação sem sessão (stateless), com filtro que valida o token JWT (HS256) enviado no cabeçalho Authorization.
Serviço de JWT com validade de 15 minutos e serviço de refresh token de 7 dias, guardado no banco apenas como hash (SHA-256) e com rotação a cada uso (a ligação com o login está nas pendências).
Todas as rotas exigem autenticação, exceto o Swagger e os POST em /auth/**.
Limite de 5 requisições por minuto, por IP, em /auth/login, /auth/cadastrar e /auth/refresh. Ao passar do limite a API responde 429 com o cabeçalho Retry-After.
Segredos (usuário e senha do banco, chave do JWT) ficam em variáveis de ambiente, nunca no código.
Estrutura do projeto

O código é organizado por domínio. Cada pasta reúne tudo de um assunto:

src/main/java
├── auth          # cadastro e login
├── jwt           # geração/validação do token, refresh token e rate limit
├── usuario       # entidade de usuário
├── produto       # Controller, Service, Repository, Request, Response
├── categoria
├── fornecedor    # fornecedores e clientes
├── estoque
├── compra
├── venda
├── transferencia
└── config        # segurança e documentação (OpenAPI)

Dentro de cada domínio, o fluxo é: Controller (recebe a requisição) → Service (regras de negócio) → Repository (acesso ao banco). Os objetos Request e Response são usados para não expor as entidades diretamente.

Endpoints

Documentação interativa: http://localhost:8080/swagger-ui.html (com a aplicação rodando).

Nas rotas protegidas, envie o cabeçalho Authorization: Bearer <token>.

Recurso	Método	Rota
Auth	POST	/auth/cadastrar
Auth	POST	/auth/login
Produtos	POST	/produtos/cadastro
Produtos	GET	/produtos/listar
Produtos	GET	/produtos/buscar/{id}
Produtos	PUT	/produtos/atualizar/{id}
Produtos	PATCH	/produtos/ativar/{id} e /produtos/desativar/{id}
Categorias	POST	/Categoria/cadastrar
Categorias	GET	/Categoria/listar e /Categoria/buscar/{id}
Categorias	PUT	/Categoria/atualizar/{id}
Categorias	DELETE	/Categoria/deletar/{id}
Fornecedores	POST	/fornecedores/cadastrar
Fornecedores	GET	/fornecedores/listar?tipo=FORNECEDOR (filtro opcional)
Fornecedores	GET	/fornecedores/fornecedor/{id} e /fornecedores/cliente/{id}
Fornecedores	PUT	/fornecedores/atualizar/{id}
Fornecedores	PATCH	/fornecedores/ativar/{id} e /fornecedores/desativar/{id}
Estoque	POST	/estoque/cadastrar
Estoque	GET	/estoque/listar e /estoque/buscar/{id}
Estoque	PUT	/estoque/atualizar/{id}
Estoque	DELETE	/estoque/deletar/{id}
Compras	POST	/compras/cadastrar
Compras	GET	/compras/listar?estado=&estoqueId= (filtros opcionais)
Compras	GET	/compras/buscar/{id}
Compras	PUT	/compras/atualizar/{id}
Compras	PATCH	/compras/comprar/{id} e /compras/cancelar/{id}
Vendas	POST	/vendas/cadastrar
Vendas	GET	/vendas/listar?venda= (filtro opcional por estado)
Vendas	GET	/vendas/buscar/{id}
Vendas	PUT	/vendas/atualizar/{id}
Vendas	PATCH	/vendas/vender/{id} e /vendas/cancelar/{id}

As listagens aceitam paginação e ordenação pelos parâmetros page, size e sort.

Como rodar
Pré-requisitos
JDK 21
PostgreSQL
1. Criar o banco

Entre no console do Postgres (sudo -u postgres psql) e rode:

sql
CREATE USER meu_usuario WITH PASSWORD 'minha_senha';
CREATE DATABASE estoque_db OWNER meu_usuario;
2. Configurar as variáveis

Crie um arquivo .env na raiz do projeto (ele já está no .gitignore, então não vai para o GitHub):

properties
DB_USUARIO=meu_usuario
DB_SENHA=minha_senha
JWT_SECRET=uma_chave_longa_e_aleatoria

Para gerar uma chave aleatória: openssl rand -base64 48.

O banco é acessado em jdbc:postgresql://localhost:5432/estoque_db (definido em src/main/resources/application.properties).

3. Executar
bash
git clone https://github.com/devguisousa/SistemaEstoque_java.git
cd SistemaEstoque_java
./mvnw spring-boot:run

A API sobe em http://localhost:8080.

Status do projeto

Pendências conhecidas, que fazem parte do planejamento:

[ ] Ligar a geração do JWT e do refresh token ao login (JwtService e RefreshTokenService já existem) e criar a rota /auth/refresh.
[ ] Versionar o esquema do banco com migrations do Flyway. O projeto usa ddl-auto=validate, então as tabelas precisam existir antes de iniciar.
[ ] Criar o controller de transferências e fazer a transferência mover as quantidades entre os estoques.
[ ] Tratamento global de exceções, para as regras de negócio devolverem um erro claro em vez de erro 500.
[ ] Testes automatizados (hoje existe apenas o teste de carregamento do contexto, que precisa de banco de dados).
Autor

Guilherme

GitHub: devguisousa
LinkedIn: guilherme-fortaleza-de-sousa
