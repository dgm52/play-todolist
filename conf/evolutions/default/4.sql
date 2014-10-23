# --- !Ups
INSERT INTO task (usertask_fk, label, enddate) values ('Anonimo', 'Tarea 1000', '2014-10-15');

# --- !Downs
DELETE FROM task;