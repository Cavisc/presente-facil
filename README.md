# 🎁 Presente Fácil
**Um sistema para gestão de listas de sugestões de presentes**

### 👨‍💻 Autores:
**Alexandre Augusto Niess Ferreira**

**Carlos Vinícius de Souza Coelho**

**Lucas Araujo Barduino Rodrigues**

**Marcos Paulo Miranda Pereira**

## TP2

### 📹 Link para o vídeo de demonstração: [Clique aqui](https://youtu.be/2OLQLJTdwI8)

Este projeto foi desenvolvido como parte do Trabalho Prático 2 (TP2) da disciplina de AED3, utilizando conceitos de persistência em arquivos, indexação com Árvore B+, e arquitetura MVC. O trabalho teve como objetivo a implementação da parte de um sistema de gestão de listas de presentes, na qual usuários podem cadastrar produtos e utilizá-los em suas listas de presentes (aniversário, natal, casamento etc.).

O código permite que usuários cadastrem e gerenciem produtos, que podem ser compartilhadas com outras pessoas através de um código GTIN-13. Cada produto pode ser usado em várias listas e cada lista pode possuir vários produtos (N:N). É possível consultar um produto pelo seu código GTIN-13 e ver em quais listas sua e/ou de outros usuários ele é usado.

Classes que foram criadas:

Classe Product: classe que representa a entidade produto. Possui métodos e atributos específicos, além dos métodos estipulados pela interface Generic.

Classe ProductList: classe que representa a cardinalidade N:N de produto e lista de presentes. Possui métodos e atributos específicos, além dos métodos estipulados pela interface Generic.

Classe PairGtin13Id: classe que representa o par gtin id de um índice secundário indireto usado no CRUD de Product. Possui métodos e atributos específicos, além dos métodos estipulados pela interface GenericExtensibleHashTable.

Classe ProductDAO: classe responsável pelas operações de CRUD da classe Product e seus índices. Se comunica com a classe DAO.

Classe ProductListDAO: classe responsável pelas operações de CRUD da classe ProductList e seus índices. Se comunica com a classe DAO.

Classe ProductController: é responsável pela gerência dos fluxos de Product. Faz comunicação com ProductDAO, Product, ProductView, ProductList e ProductListDAO.

Classe ProductView: essa classe tem a função de mostrar e receber as informações usadas em fluxos de leitura, alteração e exclusão do Product e ProdcutList, além de alguns menus. Se comunica com a classe InputScanner.

- Há um CRUD de produtos (que estende a classe ArquivoIndexado, acrescentando Tabelas Hash Extensíveis e Árvores B+ como índices diretos e indiretos conforme necessidade) que funciona corretamente? SIM
- Há um CRUD da entidade de associação ListaProduto (que estende a classe ArquivoIndexado, acrescentando Tabelas Hash Extensíveis e Árvores B+ como índices diretos e indiretos conforme necessidade) que funciona corretamente? SIM
- A visão de produtos está corretamente implementada e permite consultas as listas em que o produto aparece (apenas quantidade no caso de lista de outras pessoas)? SIM
- A visão de listas funciona corretamente e permite a gestão dos produtos na lista? SIM
- A integridade do relacionamento entre listas e produtos está mantida em todas as operações? SIM
- O trabalho compila corretamente? SIM
- O trabalho está completo e funcionando sem erros de execução? SIM
- O trabalho é original e não a cópia de um trabalho de outro grupo? SIM


## TP1

### 📹 Link para o vídeo de demonstração: [Clique aqui](https://youtu.be/eIU2AnrRZWI)

Este projeto foi desenvolvido como parte do Trabalho Prático 1 (TP1) da disciplina de AED3, utilizando conceitos de persistência em arquivos, indexação com Árvore B+, e arquitetura MVC. O trabalho teve como objetivo a implementação de um sistema de gestão de listas de presentes, no qual usuários podem se cadastrar e criar listas de sugestões de presentes para diferentes ocasiões (aniversário, natal, casamento etc.).

O código permite que usuários cadastrem e gerenciem listas de presentes, que podem ser compartilhadas com outras pessoas através de um código único. Cada lista pertence a um usuário, que pode possuir múltiplas listas, e cada lista é identificada por um código compartilhável (NanoID), que permite que outras pessoas visualizem essa lista sem precisar acessar diretamente o usuário.

Classes que foram criadas:

Classe Main: é o ponto de início do programa. É responsável por chamar a classe MainController.

Classe MainController: gerencia o fluxo inicial do programa. Faz contato com o as classes LoginController, UserController, LoginView e User.

Classe LoginController: gerencia o fluxo de login e register do User. Se comunica com as classes LoginView, UserDAO, User e Encryption.

Classe UserController: é responsável pelos fluxos que um User logado pode seguir. Se comunica com GiftListController, UserDAO, User e UserView.

Classe GiftListController: é responsável pela gerência dos fluxos que podem acontecer nas GiftLists de um User. Faz comunicação com GiftListDAO, GiftList e GiftListView.

Classe LoginView: é responsável por mostrar e receber informações usadas nos fluxos de login e register. Se comunica com a classe InputScanner.

Classe UserView: essa classe tem a função de mostrar e receber as informações usadas em fluxos de leitura, alteração e exclusão do User logado, além de alguns menus. Se comunica com a classe InputScanner.

Classe GiftListView: é a classe responsável por mostrar e receber as informações utilizadas nos fluxos de criação, alteração e exclusão de uma GiftList do User logado. Se comunica com as classes InputScanner e DateFormatter.

Interface Generic: interface genérica com alguns métodos que serão usados nos modelos do sistema.

Classe User: classe que representa a entidade usuário. Possui métodos e atributos específicos, além dos métodos estipulados pela interface Generic. Se comunica com a classe Encryption.

Classe GiftList: classe que representa a entidade lista de presentes. Possui métodos e atributos específicos, além dos métodos estipulados pela interface Generic. Se comunica com a classe NanoID.

Classe BPlusTree: classe que presenta a árvore B+. Código criado pelo professor Marcos Kutova.

Classe ExtensibleHashTable: classe que representa a tabela hash extensível. Código criado pelo professor Marcos Kutova.

Interface GenericBPlusTree: interface genérica com alguns métodos que serão usados em alguns índices do sistema.

Classe GenericExtensibleHashTable: interface genérica com alguns métodos que serão usados em alguns índices do sistema.

Classe PairIdAddress: classe que representa o par id endereço de um índice secundário direto usado nos CRUDs. Possui métodos e atributos específicos, além dos métodos estipulados pela interface GenericExtensibleHashTable.

Classe PairEmailId: classe que representa o par email id de um índice secundário indireto usado no CRUD de User. Possui métodos e atributos específicos, além dos métodos estipulados pela interface GenericExtensibleHashTable.

Classe PairNanoIdId: classe que representa o par nanoid id de um índice secundário indireto usado no CRUD de GiftList. Possui métodos e atributos específicos, além dos métodos estipulados pela interface GenericExtensibleHashTable.

Classe PairIdId: classe que representa o par id id de um índice secundário indireto. Possui métodos e atributos específicos, além dos métodos estipulados pela interface GenericBPlusTree.

Classe PairNameId: classe que representa o par name id de um índice secundário indireto. Possui métodos e atributos específicos, além dos métodos estipulados pela interface GenericBPlusTree.

Classe DAO: classe responsável pelas operações de CRUD nos BDs e nos índices.

Classe UserDAO: classe responsável pelas operações de CRUD da classe User e seus índices. Se comunica com a classe DAO.

Classe GiftListDAO: classe responsável pelas operações de CRUD da classe GiftList e seus índices. Se comunica com a classe DAO.

Classe NanoID: classe responsável por gerar os nanoids.

Classe InputScanner: classe responsável por ler a entrada do usuário.

Classe Encryption: classe responsável por fazer o hash da senha e comparar os hashs para login.

Classe DateFormatter: classe responsável por formatar as datas para String ou LocalDate.

A experiência geral de realizar o TP foi tranquila pois o professor ajudou bastante nos códigos auxiliares que eram uma parte muito importante para a implementação de todos os requisitos. Como resultado geral, acreditamos que o trabalho apresentado desempenhe de forma satisfatória nos requisitos pedidos, sendo assim acreditamos que os resultados tenham sim sido alcançados.

- Há um CRUD de usuários (que estende a classe ArquivoIndexado, acrescentando Tabelas Hash Extensíveis e Árvores B+ como índices diretos e indiretos conforme necessidade) que funciona corretamente? SIM
- Há um CRUD de listas (que estende a classe ArquivoIndexado, acrescentando Tabelas Hash Extensíveis e Árvores B+ como índices diretos e indiretos conforme necessidade) que funciona corretamente? SIM
- As listas de presentes estão vinculadas aos usuários usando o idUsuario como chave estrangeira? SIM
- Há uma árvore B+ que registre o relacionamento 1:N entre usuários e listas? SIM
- Há um CRUD de usuários (que estende a classe ArquivoIndexado, acrescentando Tabelas Hash Extensíveis e Árvores B+ como índices diretos e indiretos conforme necessidade)? SIM
- Há uma visualização das listas de outras pessoas por meio de um código NanoID? SIM
- O trabalho compila corretamente? SIM
- O trabalho está completo e funcionando sem erros de execução? SIM
- O trabalho é original e não a cópia de um trabalho de outro grupo? SIM
