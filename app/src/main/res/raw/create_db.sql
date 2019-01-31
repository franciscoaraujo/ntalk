create table mensagem_tbl(
	id_mensagem integer primary key autoincrement,
	id_mensagem_servidor integer ,
	id_atendimento integer,
	id_interlocutor_remetente integer,/*aqui eh o id do interlocutor usuario do celular*/
	id_interlocutor_destinatario integer,/*aqui é o id do interlocutor cliente*/
	conteudo varchar (500),
	data_hora_mensagem datetime,
	nome_destinatario varchar(120),
	nome_remetente varchar(120),
	id_mensagem_iteracao integer

);

create table mensagem_interacao_tbl(
	id_mensagem_interacao integer,
	id_mensagem integer,
	recebido boolean,
	lido boolean
);

create table interlocutor_tbl(
	registro integer,
	nome varchar(150),
	usuario_nucleo varchar ,
	id_interlocutor integer
);

create table historico_operador_tbl(
    id_operador_acao integer ,
	id_operador_cadastro integer,
	data_hora_cadastro Date datetime,
	id_operador_alteracao integer,
	id_operador_exclusao integer,
	data_hora_exclusao integer
);

