CREATE DATABASE teachme
  DEFAULT CHARACTER SET utf8;

USE teachme;

CREATE TABLE config (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  variable text,
  value int
);

CREATE TABLE tag (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  name text
);

CREATE TABLE solution (
  id INTEGER PRIMARY KEY,
  solution_text text,
  checker_id int
);

CREATE TABLE problem (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  name text,
  statement text,
  figures text,
  complexity INTEGER,
  in_mind boolean
);

CREATE TABLE problem_tag (
  problem_id INTEGER,
  tag_id INTEGER
);

CREATE TABLE checker (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  bean_name text
);

CREATE TABLE user_problem (
   user_id INTEGER,
   problem_id INTEGER,
   status_id INTEGER,
   attempts INTEGER
);

CREATE TABLE status (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  status text
);

CREATE TABLE admin (
   id INTEGER
);

INSERT INTO status (status) values ("read");
INSERT INTO status (status) values ("solved");
INSERT INTO status (status) values ("attempted");

INSERT INTO checker (bean_name) values ("intChecker");
INSERT INTO checker (bean_name) values ("stringChecker");