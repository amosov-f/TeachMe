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

CREATE TABLE user (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  username text,
  first_name text,
  last_name text
);

CREATE TABLE user_problem (
   user_id INTEGER,
   problem_id INTEGER,
   status_id INTEGER,
   attempts INTEGER
);

#TODO rename table to status
CREATE TABLE problem_status (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  status text
);

CREATE TABLE admin (
   id INTEGER
);


INSERT INTO problem_status (status) values("read");
INSERT INTO problem_status (status) values("solved");

INSERT INTO checker (bean_name) values ("intChecker");
INSERT INTO checker (bean_name) values ("stringChecker");

# add indexes





select *
from user_problem join problem_status on user_problem.status_id = problem_status.id
                  join problem_tag on user_problem.problem_id = problem_tag.problem_id
where problem_status.status in ('read','solved','attempted')
and user_problem.problem_id in (select problem_id from problem_tag where tag_id = 51)
and user_problem.problem_id in (select problem_id from problem_tag where tag_id = 66)

-- for unsolved
select user_problem.problem_id, user_problem.status_id, user_problem.attempts
from user_problem join problem_status on user_problem.status_id = problem_status.id
                  join problem_tag on user_problem.problem_id = problem_tag.problem_id
where user_id = 2
and user_problem.problem_id in (select problem_id from problem_tag where tag_id = 51)
and user_problem.problem_id in (select problem_id from problem_tag where tag_id = 66)
and problem_status.status in ('read','attempted')
UNION
select problem.id, -1, 0 from problem where id not in (select problem_id from user_problem where user_id = 2)
and problem.id in (select problem_id from problem_tag where tag_id = 51)
and problem.id in (select problem_id from problem_tag where tag_id = 66);

-- for unsolved PDD in_mind
select user_problem.problem_id, user_problem.status_id, user_problem.attempts
from user_problem join problem_status on user_problem.status_id = problem_status.id
                  join problem_tag on user_problem.problem_id = problem_tag.problem_id
                  join problem on user_problem.problem_id = problem.id
where user_id = 2
and tag_id = 73
and problem_status.status in ('read','attempted')
and problem.in_mind = 1
UNION
select problem.id, -1, 0 from problem where id not in (select problem_id from user_problem where user_id = 2)
and problem.id in (select problem_id from problem_tag where tag_id = 73)
and in_mind = 1
limit 10 offset 20;

--offset -- сколько пропустил от начала


where problem_status.status in ('read','solved','attempted')
and user_problem.problem_id in (select problem_id from problem_tag where tag_id = 51)
and user_problem.problem_id in (select problem_id from problem_tag where tag_id = 66)


problem_tag.tag_id in (51,66);



SELECT problem_id, attempts FROM user_problem
WHERE user_id = 2
AND problem_id IN (SELECT id FROM problem WHERE in_mind = false)
UNION
SELECT id, NULL FROM problem
WHERE id NOT IN (SELECT problem_id FROM user_problem WHERE user_id = 2)
AND id IN (SELECT id FROM problem WHERE in_mind = false)
limit 20 offset 0;

