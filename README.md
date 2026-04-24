# 📚 MeuLivro API - Gerenciamento de Acervo

Uma API RESTful completa desenvolvida em **Spring Boot 3** para o gerenciamento de um catálogo de livros. O projeto inclui autenticação segura com **JWT**, controle de acesso baseado em perfis (RBAC), banco de dados em memória e documentação interativa.

## 👨‍💻 Autor

Desenvolvido por **Vitor Jacom de Souza** 🚀

Sinta-se à vontade para se conectar comigo:

* 💼 **LinkedIn:** [https://www.linkedin.com/in/VitorJacom/](https://www.linkedin.com/in/VitorJacom/)
* 🐙 **GitHub:** [https://github.com/VitorJacom](https://github.com/VitorJacom)
* ✉️ **E-mail:** [vjds.vitor@gmail.com](mailto:vjds.vitor@gmail.com)

## 🚀 Tecnologias Utilizadas

* **Java 21**
* **Spring Boot 3.5.14** (Web, Data JPA, Security, Validation)
* **H2 Database** (Banco de dados em memória)
* **JSON Web Token (jjwt)** (Autenticação e Autorização)
* **Springdoc OpenAPI (Swagger)** (Documentação da API)
* **Lombok** (Redução de boilerplate)
* **JUnit 5 & MockMvc** (Testes Unitários e de Integração)
* **Maven** (Gerenciador de dependências)

---

## 🏗️ Arquitetura do Projeto

O projeto segue a arquitetura **MVC (Model-View-Controller)** adaptada para APIs REST, dividida em camadas lógicas com responsabilidades únicas:

* **`config/`**: Classes de configuração globais (Segurança Spring Security, Filtros JWT, OpenAPI/Swagger e o Data Seeding inicial).
* **`controllers/`**: Camada de roteamento (Endpoints). Recebe as requisições HTTP, chama as regras de negócio e retorna os DTOs apropriados.
* **`dto/`**: Objetos de Transferência de Dados (*Data Transfer Objects*). Separam as entidades de banco de dados do que é trafegado na rede, validando entradas (`@Valid`).
* **`entities/`**: Classes de domínio mapeadas diretamente para as tabelas do banco de dados (JPA/Hibernate).
* **`repositories/`**: Interfaces de acesso a dados (Spring Data JPA) que abstraem as consultas ao banco (SQL/JPQL).
* **`service/`**: Serviços auxiliares que contém lógicas específicas, como a geração e validação de tokens JWT.

---

## ⚙️ Pré-requisitos para Rodar

Antes de começar, você precisará ter instalado em sua máquina:
* [Java Development Kit (JDK) 21](https://adoptium.net/)
* [Apache Maven](https://maven.apache.org/) (ou usar o Wrapper da IDE)
* Sua IDE favorita (IntelliJ IDEA, Eclipse, VS Code).

---

## 🏃 Como Executar a Aplicação

1.  Clone o repositório para sua máquina local.
2.  Abra o terminal na pasta raiz do projeto (onde está o arquivo `pom.xml`).
3.  Execute o comando Maven para baixar as dependências e rodar a aplicação:
    ```bash
    mvn spring-boot:run
    ```
4.  A aplicação será iniciada na porta padrão `8080`.

---

## 📖 Acesso à Documentação e Banco de Dados

A API possui uma interface gráfica para você testar todos os endpoints facilmente.

* **Documentação Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **JSON do OpenAPI:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)
* **Console do Banco de Dados H2:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
    * *JDBC URL:* `jdbc:h2:mem:testdb`
    * *User Name:* `sa`
    * *Password:* *(deixar em branco)*

---

## 🔑 Credenciais Padrão (Data Seeding)

Para facilitar os testes, a aplicação utiliza um script de "Data Seeding" que popula o banco de dados automaticamente toda vez que a API é iniciada.

**Usuário Administrador pré-cadastrado:**
* **Login:** `admin`
* **Senha:** `admin123`
* **Perfil:** `ROLE_ADMIN`

O acervo também já é populado com **4 livros clássicos** para que você possa testar as rotas de busca e listagem imediatamente.

---

## 🛡️ Fluxo de Uso e Segurança (Como Testar)

A API é protegida. Para criar, editar ou excluir livros, você precisará de um Token JWT.

1.  Acesse o **Swagger UI**.
2.  Abra o endpoint **`POST /auth/login`**.
3.  Insira as credenciais padrão (`admin` / `admin123`) e execute.
4.  Copie o valor do campo `token` da resposta.
5.  Vá até o topo da página do Swagger e clique no botão verde **"Authorize"**.
6.  Cole o seu token na caixa de texto e clique em *Authorize*.
7.  Pronto! Agora você pode testar as rotas protegidas (como `POST /livros` ou `GET /users`).

### 🚦 Regras de Acesso (RBAC)

| Endpoint | Método HTTP | Acesso Permitido |
| :--- | :--- | :--- |
| `/auth/login` | POST | Público |
| `/auth/register` | POST | Público |
| `/livros` (Listar) | GET | Usuários Autenticados (USER ou ADMIN) |
| `/livros/buscar` | GET | Usuários Autenticados (USER ou ADMIN) |
| `/livros` (Criar) | POST | **Apenas ADMIN** |
| `/livros/{id}` (Atualizar) | PUT | **Apenas ADMIN** |
| `/livros/{id}` (Deletar) | DELETE | **Apenas ADMIN** |
| `/users` (Listar) | GET | **Apenas ADMIN** |

---

## 🧪 Testes Automatizados

A qualidade e a segurança da API são garantidas por uma suíte de testes automatizados construída com **JUnit 5** e **Spring Boot Test** (utilizando o `MockMvc`). 

Os testes cobrem cenários de sucesso e falha (Testes de Integração), garantindo o correto funcionamento das regras de negócio, persistência no banco H2 e as restrições de acesso via JWT (RBAC).

### O que está sendo testado?

* **Autenticação (`AuthControllerTest`)**: 
  * Sucesso no cadastro e no login.
  * Validação de campos obrigatórios.
  * Impedimento de cadastro com login duplicado.
  * Verificação do hash seguro da senha (BCrypt).
* **Usuários (`UserControllerTest`)**:
  * Acesso restrito da listagem apenas para perfis `ROLE_ADMIN`.
* **Livros (`LivroControllerTest`)**:
  * Validação de operações CRUD (Criar, Ler, Atualizar e Deletar).
  * Garantia de que requisições sem Token retornem 401 (Unauthorized).
  * Garantia de que perfis `ROLE_USER` recebam 403 (Forbidden) ao tentar realizar mutações (POST, PUT, DELETE).
  * Validação da busca dinâmica (por trecho do título, autor, id ou ano).

### ⚙️ Como Executar os Testes

Você pode rodar toda a suíte de testes diretamente pela linha de comando utilizando o Maven.

**Para rodar todos os testes do projeto:**
```bash
mvn test
```

## 🧠 Justificativa das Decisões Técnicas

Durante o desenvolvimento deste desafio, algumas decisões arquiteturais e de ferramentas foram tomadas visando simplicidade, segurança e escalabilidade:

* **Spring Boot 3 & Java 21:** Escolhidos por representarem o estado da arte do ecossistema Java moderno. O Java 21 traz melhorias de performance e sintaxe (como *Records*, que usei nos DTOs), e o Spring Boot 3 garante suporte atualizado de segurança.
* **H2 Database (In-Memory):** Utilizado para atender ao requisito de persistência simples sem exigir que o avaliador configure um banco de dados local (como PostgreSQL ou MySQL). Isso torna o projeto *plug-and-play*.
* **Padrão DTO (Data Transfer Object):** Decidi separar as Entidades JPA das respostas/requisições da API. Isso evita a exposição de dados sensíveis (como hashes de senhas) e previne vulnerabilidades de *Mass Assignment*.
* **Spring Security + JWT:** O JWT (*JSON Web Token*) é o padrão de mercado para APIs REST *stateless*. Ele permite autenticação escalável e o controle fino de permissões (RBAC) através dos *Claims* do token, sem sobrecarregar o banco de dados com consultas de sessão.
* **ControllerAdvice (ExceptionHandler):** Implementei o tratamento global de exceções para garantir que a API sempre retorne respostas padronizadas em JSON (com mensagens claras de erro) em vez de *stack traces* genéricos do Java.

*Projeto desenvolvido como demonstração técnica de habilidades em Backend com Java e ecossistema Spring.*
