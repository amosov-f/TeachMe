create index user_status_index
on user_problem(user_id, status_id);

create unique index user_problem_index
on user_problem(user_id, problem_id);