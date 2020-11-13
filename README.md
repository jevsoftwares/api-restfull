# api-restfull
 Desafio da MilTec para criar uma apirestfull

	De acordo com o cenário do desafio, identifiquei a necessidade de somente duas tabelas, sendo:
ARMAZEM e HISTÓRICO. Certamente em um cenário real teríamos mais tabelas paara identificação 
dos usuários e separação didática dos dados.
	Quanto aos testes, utilizo o JUnit normalmente, mas como meu computador foi para conserto
fiquei com curto prazo para realizar testes unitário pelo mesmo, sendo realizado apenas via debug
no teste rápido.
 
Repositório GitHub: https://github.com/jevsoftwares/api-restfull

 
____________________________________________________________________________________
REQUISITOS:
	1 - TIPO: 	ALCOLICOOS E NÃO ALCOOLICOS 				OK
	2 - SEÇÃO: 	LIMITE 5 1 TIPO DE BEBIDA				OK
	3 - SALDO:	LIMITE 500 ALCOOLICO | 400 NÃO ALCOOLICO POR SEÇÃO	OK
	4 - HISTÓRICO:	ENTRADA E SAÍDA AO REALIZAR ALTERAÇÃO/INCLUSÃO		OK
	5 - CONSULTAS:
		   A - POR SEÇÃO	OK
		   B - POR USUÁRIO	OK
		   C - POR DATA		OK

____________________________________________________________________________________

FERRAMENTAS:
	IDE:		IntelliJ Communiti Edition 2020.2.3
	FRAMEWORK:	Swagger e Spring Boot
____________________________________________________________________________________

BANCO DE DADOS UTILIZADO:
	Server:	MySql Server local.
	Porta:	3306.
	BD:	db_miltec_desafio
	Usuário:root
	Senha:	123456
	
TABELAS E QUERYES PRINCIPAIS:
	CREATE DATABASE db_miltec_desafio


CREATE TABLE armazem(
	id INT PRIMARY KEY,
	codigo VARCHAR(6),
	descricao VARCHAR(60),
	saldo FLOAT,
	alcoolico VARCHAR(1),
 	secao VARCHAR(1))

CREATE TABLE historico(
	id INT PRIMARY KEY,
	id_prod INT,
	codigo VARCHAR(6),
	descricao VARCHAR(60),
	entrada FLOAT,
	saida FLOAT,
	alcoolico VARCHAR(1),
	secao VARCHAR(1),
	data_alt VARCHAR(10),
	hora_alt VARCHAR(8),
	usuario VARCHAR(60)
	)

	