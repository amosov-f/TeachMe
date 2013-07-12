CREATE TABLE user (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  login text
);

CREATE TABLE user_problem (
   user_id INTEGER,
   problem_id INTEGER,
   status_id INTEGER
);

CREATE TABLE problem_status (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  status text
);

INSERT INTO problem_status (status) values("read");
INSERT INTO problem_status (status) values("solved");

INSERT INTO checker (bean_name) values ("intChecker");
INSERT INTO checker (bean_name) values ("stringChecker");
