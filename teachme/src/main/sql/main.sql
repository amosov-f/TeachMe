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
  figures text
);

CREATE TABLE problem_tag (
  problem_id INTEGER,
  tag_id INTEGER
);

CREATE TABLE checker (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  bean_name text
);

CREATE TABLE user (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  username varchar(255),
  firstName varchar(255),
  lastName varchar(255)
);

CREATE TABLE user_problem (
   user_id INTEGER,
   problem_id INTEGER,
   status_id INTEGER,
   attempts INTEGER
);

CREATE TABLE problem_status (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  status text
);


INSERT INTO problem_status (status) values("read");
INSERT INTO problem_status (status) values("solved");

INSERT INTO checker (bean_name) values ("intChecker");
INSERT INTO checker (bean_name) values ("stringChecker");

# add indexes






