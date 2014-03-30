alter table problem_tag
add constraint uc_problem_tag unique (problem_id, tag_id);