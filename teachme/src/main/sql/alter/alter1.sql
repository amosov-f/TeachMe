alter table solution change id id int(11);
alter table solution change bean_name solution_text text;
alter table solution add column checker_type text after solution_text;