CREATE DATABASE teachme
  DEFAULT CHARACTER SET utf8;

CREATE TABLE tag (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  name text
);

CREATE TABLE checker (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  bean_name text
);

CREATE TABLE problem (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  situation text,
  checker_id
);

CREATE TABLE problem_tag {
  problem_id INTEGER,
  tag_id INTEGER
};

