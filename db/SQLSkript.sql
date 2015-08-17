create schema egt;

create table egt.graph 
(
  graph_nr int GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) primary key,
  name varchar(250),
  svg_text clob
);

insert into egt.graph (name) values ('test');
