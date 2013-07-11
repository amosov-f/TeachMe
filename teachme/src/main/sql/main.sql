CREATE DATABASE teachme
  DEFAULT CHARACTER SET utf8;


CREATE TABLE tag (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  name text
);

CREATE TABLE statement (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  bean_name text
);

CREATE TABLE problem (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  name text,
  situation text,
  statement_id integer
);

CREATE TABLE problem_tag (
  problem_id INTEGER,
  tag_id INTEGER
);

