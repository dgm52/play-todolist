# Tasks schema
 
# --- !Ups

ALTER TABLE task ADD enddate date;

# 4ª evolucion con los INSERTS

# --- !Downs
ALTER TABLE task DROP enddate;