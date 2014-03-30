CREATE TABLE checker (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  bean_name text
);

alter table solution change checker_name checker_id integer;
