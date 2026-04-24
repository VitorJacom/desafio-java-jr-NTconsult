US01 ✅: Cadastro de Livros

Como usuário do sistema,
Eu quero cadastrar um novo livro informando título, autor e ano de publicação,
Para que ele seja armazenado na base de dados.

    Critérios de Aceite:

        Endpoint: POST /livros.

        Campos obrigatórios: titulo (String), autor (String) e anoPublicacao (Integer).

        O sistema deve gerar um id (Long) automaticamente.

        Retorno: HTTP 201 (Created) com o objeto criado no corpo da resposta.

US02 ✅: Listagem de Livros

Como usuário do sistema,
Eu quero recuperar a lista de todos os livros cadastrados,
Para que eu possa visualizar o acervo completo.

    Critérios de Aceite:

        Endpoint: GET /livros.

        Retorno: HTTP 200 (OK) contendo uma lista JSON de livros.

        Se não houver livros, retornar uma lista vazia.

US03 ✅: Atualização de Livros

Como usuário do sistema,
Eu quero alterar os dados de um livro existente através do seu ID,
Para que eu possa corrigir informações incorretas.

    Critérios de Aceite:

        Endpoint: PUT /livros/{id}.

        Deve permitir atualizar título, autor ou ano de publicação.

        Retorno: HTTP 200 (OK) com os dados atualizados.

        Retorno: HTTP 404 (Not Found) caso o ID não exista.

US04 ✅: Exclusão de Livros

Como usuário do sistema,
Eu quero remover um livro do catálogo utilizando seu ID,
Para que ele não conste mais no sistema.

    Critérios de Aceite:

        Endpoint: DELETE /livros/{id}.

        Retorno: HTTP 204 (No Content) após exclusão bem-sucedida.

        Retorno: HTTP 404 (Not Found) caso o ID não exista.

US05 ✅: Persistência e Infraestrutura (Técnica)

Como desenvolvedor,
Eu quero configurar o banco de dados H2 e testes automatizados,
Para que a aplicação seja resiliente e fácil de validar.

    Critérios de Aceite:

        Configuração do Spring Data JPA com banco de dados em memória (H2).

        Implementação de testes unitários/integração com JUnit 5.

        Cobertura básica das rotas de CRUD.
        
US06 ✅: Cadastro de Usuários

Como visitante do sistema,
Eu quero me cadastrar informando login, senha e perfil (role),
Para que eu possa acessar as funcionalidades restritas da API.

    Critérios de Aceite:

        Endpoint: POST /auth/register.

        A senha deve ser armazenada utilizando hash (ex: BCrypt).

        Deve permitir definir perfis como ROLE_USER ou ROLE_ADMIN.

        Retorno: HTTP 201 (Created).

US07 ✅: Autenticação (Login) com JWT

Como usuário cadastrado,
Eu quero realizar login com minhas credenciais,
Para que eu receba um token JWT para autenticar minhas requisições.

    Critérios de Aceite:

        Endpoint: POST /auth/login.

        Validar usuário e senha.

        Retorno: HTTP 200 (OK) contendo o token JWT no corpo da resposta.

        O token deve conter o login e as permissões (roles) do usuário.

US08 ✅: Controle de Acesso (RBAC)

Como administrador do sistema,
Eu quero que as operações de escrita em livros sejam restritas,
Para que apenas usuários autorizados gerenciem o acervo.

    Critérios de Aceite:

        POST, PUT e DELETE em /livros exigem ROLE_ADMIN.

        Se um usuário com ROLE_USER tentar acessar, deve retornar HTTP 403 (Forbidden).

        A consulta (GET /livros) deve permanecer pública ou permitida para ROLE_USER.

        Requisições sem token válido devem retornar HTTP 401 (Unauthorized).

US09 ✅: Documentação com Swagger (OpenAPI)

Como desenvolvedor/consumidor da API,
Eu quero uma interface visual que liste todos os endpoints,
Para que eu possa testar a integração de forma simplificada.

    Critérios de Aceite:

        Acessível via /swagger-ui.html ou /swagger-ui/index.html.

        Listar todos os controllers, métodos HTTP e modelos (Livro, User).

        Configurar o Swagger para suportar o envio do Token JWT (Botão "Authorize").

US10 ✅: Segurança nos Testes (Técnica)

Como desenvolvedor,
Eu quero atualizar os testes unitários e de integração,
Para que validem o comportamento da segurança JWT e dos perfis de acesso.

    Critérios de Aceite:

        Testar se rotas protegidas negam acesso sem token.

        Testar se um ROLE_USER é impedido de deletar um livro.

        Garantir que o token gerado é válido e contém as claims corretas.

US11 ✅: População Inicial de Dados (Data Seeding)

Como desenvolvedor,
Eu quero que o sistema cadastre automaticamente um usuário administrador e livros iniciais na primeira execução,
Para que a aplicação esteja pronta para uso e testes imediatamente.

    Critérios de Aceite:

        Criar uma classe de configuração (ex: DataInitializer) que execute no startup da aplicação.

        Verificação de Duplicidade: Antes de inserir, o sistema deve checar se o usuário admin ou os livros já existem (por login ou título).

        Inserir ao menos um usuário com ROLE_ADMIN para permitir o primeiro acesso.

        Inserir uma lista básica de 3 a 5 livros para popular o catálogo inicial.

US12 ✅: Configuração de Ambiente (Application.yml)

Como desenvolvedor,
Eu quero centralizar as configurações de banco de dados, segurança e documentação no arquivo application.yml,
Para que a manutenção e a troca de ambientes sejam simplificadas.

    Critérios de Aceite:

        H2 Database: Configurar URL (jdbc:h2:mem:testdb), console habilitado e credenciais.

        JPA/Hibernate: Configurar ddl-auto: update (para preservar dados entre reinicializações parciais) e show-sql: true.

        Security/JWT: Criar propriedades customizadas para a Secret Key e o tempo de expiração do token.

        Swagger: Definir o caminho customizado da documentação (ex: /api-docs).