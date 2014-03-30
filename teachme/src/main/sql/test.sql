# add indexes

select *
from user_problem join status on user_problem.status_id = status.id
                  join problem_tag on user_problem.problem_id = problem_tag.problem_id
where status.status in ('read','solved','attempted')
and user_problem.problem_id in (select problem_id from problem_tag where tag_id = 51)
and user_problem.problem_id in (select problem_id from problem_tag where tag_id = 66);

#-- for unsolved
select user_problem.problem_id, user_problem.status_id, user_problem.attempts
from user_problem join status on user_problem.status_id = status.id
                  join problem_tag on user_problem.problem_id = problem_tag.problem_id
where user_id = 2
and user_problem.problem_id in (select problem_id from problem_tag where tag_id = 51)
and user_problem.problem_id in (select problem_id from problem_tag where tag_id = 66)
and status.status in ('read','attempted')
UNION
select problem.id, -1, 0 from problem where id not in (select problem_id from user_problem where user_id = 2)
and problem.id in (select problem_id from problem_tag where tag_id = 51)
and problem.id in (select problem_id from problem_tag where tag_id = 66);

-- for unsolved PDD in_mind
select user_problem.problem_id, user_problem.status_id, user_problem.attempts
from user_problem join status on user_problem.status_id = status.id
                  join problem_tag on user_problem.problem_id = problem_tag.problem_id
                  join problem on user_problem.problem_id = problem.id
where user_id = 2
and tag_id = 73
and status.status in ('read','attempted')
and problem.in_mind = 1
UNION
select problem.id, -1, 0 from problem where id not in (select problem_id from user_problem where user_id = 2)
and problem.id in (select problem_id from problem_tag where tag_id = 73)
and in_mind = 1
limit 10 offset 20;

#--offset -- сколько пропустил от начала


#where problem_status.status in ('read','solved','attempted')
#and user_problem.problem_id in (select problem_id from problem_tag where tag_id = 51)
#and user_problem.problem_id in (select problem_id from problem_tag where tag_id = 66)


#problem_tag.tag_id in (51,66);



SELECT problem_id, attempts FROM user_problem
WHERE user_id = 2
AND problem_id IN (SELECT id FROM problem WHERE in_mind = false)
UNION
SELECT id, NULL FROM problem
WHERE id NOT IN (SELECT problem_id FROM user_problem WHERE user_id = 2)
AND id IN (SELECT id FROM problem WHERE in_mind = false)
limit 20 offset 0;