# Tasks schema
 
# --- !Ups

ALTER TABLE task ADD enddate date;

# --- !Downs
ALTER TABLE task DROP enddate;