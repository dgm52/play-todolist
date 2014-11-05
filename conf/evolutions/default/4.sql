# --- !Ups

CREATE SEQUENCE category_id_seq;
CREATE TABLE category (
   id integer NOT NULL DEFAULT nextval('category_id_seq'),
   name varchar(255),
   user varchar(255) REFERENCES usertask(login)
);

ALTER TABLE task ADD categorytask_fk varchar(255);
ALTER TABLE task ADD FOREIGN KEY (categorytask_fk) REFERENCES category(name);

insert into category (name, user) values ('Inbox', 'Dani');
 
# --- !Downs

ALTER TABLE task DROP categorytask_fk;
DROP TABLE category;
DROP SEQUENCE category_id_seq;