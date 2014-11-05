# --- !Ups

CREATE SEQUENCE category_id_seq;
CREATE TABLE category (
   id integer NOT NULL DEFAULT nextval('category_id_seq'),
   name varchar(255),
   user varchar(255) REFERENCES usertask(login)
);

 
# --- !Downs

DROP TABLE category;
DROP SEQUENCE category_id_seq;