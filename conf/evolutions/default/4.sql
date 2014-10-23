# --- !Ups
INSERT INTO task (usertask_fk, label, enddate) values ('Dani', 'Tarea 1000', '2014-10-15');

# --- !Downs
DELETE FROM task;