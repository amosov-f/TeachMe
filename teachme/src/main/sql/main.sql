CREATE DATABASE teachme
  DEFAULT CHARACTER SET utf8;

CREATE TABLE tag (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  name text
);

CREATE TABLE solution (
  id INTEGER PRIMARY KEY,
  solution_text text
  checker_type text
);

CREATE TABLE problem (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  name text,
  statement text
);

CREATE TABLE problem_tag (
  problem_id INTEGER,
  tag_id INTEGER
);

