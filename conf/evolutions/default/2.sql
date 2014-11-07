# Tasks schema
 
# --- !Ups

CREATE TABLE usertask (
   login varchar(255) NOT NULL,
   PRIMARY KEY(login)
);


ALTER TABLE task ADD usertask_fk varchar(255);

insert into usertask (login) values ('Anonimo');
insert into usertask (login) values ('Dani');
 
# --- !Downs
ALTER TABLE task DROP usertask_fk;
delete from usertask;
DROP TABLE usertask;