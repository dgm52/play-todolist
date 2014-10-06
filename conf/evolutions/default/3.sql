# Tasks schema
 
# --- !Ups

ALTER TABLE task ADD enddate timestamp;

# --- !Downs
ALTER TABLE task DROP usertask_fk;