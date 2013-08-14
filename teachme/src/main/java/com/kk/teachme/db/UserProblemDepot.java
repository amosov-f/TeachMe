package com.kk.teachme.db;

import com.kk.teachme.model.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserProblemDepot {
    UserDepot userDepot;
    ProblemDepot problemDepot;
    StatusDepot statusDepot;
    TagDepot tagDepot;

    SimpleJdbcTemplate simpleJdbcTemplate;

    public void addObject(final User user, final UserProblem userProblem) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final int update = simpleJdbcTemplate.getJdbcOperations().update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                        PreparedStatement preparedStatement =
                                conn.prepareStatement("insert into user_problem (user_id, problem_id, status_id) values(?, ?, ?)"
                                        , Statement.RETURN_GENERATED_KEYS);
                        preparedStatement.setInt(1, user.getId());
                        preparedStatement.setInt(2, userProblem.getProblem().getId());
                        preparedStatement.setInt(3, statusDepot.getStatusId(userProblem.getStatus()));
                        return preparedStatement;
                    }
                }, keyHolder);

    }

    public boolean setStatus(User user, Problem problem, Status status) {
        List<UserProblem> problemList = simpleJdbcTemplate.query(
                "select * from user_problem where user_id = ? and problem_id = ?",
                getRowMapper(),
                user.getId(),
                problem.getId()
        );
        if (problemList.size() == 0 && status != null && status != Status.NEW) {
            addObject(user, new UserProblem(problem, status));
            return true;
        }
        simpleJdbcTemplate.update(
                "update user_problem set status_id = ? where user_id = ? and problem_id = ?",
                statusDepot.getStatusId(status),
                user.getId(),
                problem.getId()
        );

        return false;
    }

    public Status getStatus(User user, Problem problem) {
        List<UserProblem> userProblemList = simpleJdbcTemplate.query(
                "select * from user_problem where user_id = ? and problem_id = ?",
                getRowMapper(),
                user.getId(),
                problem.getId()
        );

        if (userProblemList.isEmpty()) {
            return Status.NEW;
        }

        return userProblemList.get(0).getStatus();
    }

    public List<UserProblem> getAllUserProblems(User user) {
        List<UserProblem> userProblems = simpleJdbcTemplate.query("select problem_id, status_id " +
                "from user_problem where user_id = ?",
                getRowMapper(),
                user.getId());
        List<Problem> allProblems = problemDepot.getAllProblems();

        List<UserProblem> allUserProblems = new ArrayList<UserProblem>();

        for (Problem problem : allProblems) {
            boolean flag = false;
            for (UserProblem userProblem : userProblems) {
                if (userProblem.getProblem().getId() == problem.getId()) {
                    allUserProblems.add(new UserProblem(problem, userProblem.getStatus()));
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                allUserProblems.add(new UserProblem(problem, Status.NEW));
            }
        }

        return allUserProblems;
    }

    public List<UserProblem> getUnsolvedProblems(User user) {
        List<UserProblem> userProblems = simpleJdbcTemplate.query("select problem_id, status_id " +
                "from user_problem where user_id = ?",
                getRowMapper(),
                user.getId());
        List<Problem> allProblems = problemDepot.getAllProblems();

        List<UserProblem> unsolvedUserProblems = new ArrayList<UserProblem>();

        for (Problem problem : allProblems) {
            boolean flag = false;
            for (UserProblem userProblem : userProblems) {
                if (userProblem.getProblem().getId() == problem.getId()) {
                    flag = true;
                    if (userProblem.getStatus() != Status.SOLVED) {
                        unsolvedUserProblems.add(new UserProblem(problem, userProblem.getStatus()));
                    }
                    break;
                }
            }
            if (flag) {
                continue;
            }
            unsolvedUserProblems.add(new UserProblem(problem, Status.NEW));
        }

        return unsolvedUserProblems;
    }

    public List<UserProblem> getReadProblems(User user) {
        return simpleJdbcTemplate.query(
                "select problem_id, status_id from user_problem where user_id = ? and status_id = ?",
                getRowMapper(),
                user.getId(),
                statusDepot.getStatusId(Status.READ)
        );
    }

    public List<UserProblem> getSolvedProblems(User user) {
        return simpleJdbcTemplate.query("select problem_id, status_id " +
                "from user_problem where user_id = ? and status_id = ?",
                getRowMapper(),
                user.getId(),
                statusDepot.getStatusId(Status.SOLVED)
        );
    }

    public List<UserProblem> getByTag(User user, Tag tag) {

        List<UserProblem> userProblems = simpleJdbcTemplate.query("select up.problem_id, up.status_id " +
                "from user_problem up inner join problem_tag pt on pt.problem_id = up.problem_id " +
                "where up.user_id = ? and pt.tag_id = ?",
                getRowMapper(),
                user.getId(),
                tag.getId());
        List<Problem> problems = problemDepot.getByTag(tag);

        List<UserProblem> resultUserProblems = new ArrayList<UserProblem>();

        for (Problem problem : problems) {
            boolean flag = false;
            for (UserProblem userProblem : userProblems) {
                if (userProblem.getProblem().getId() == problem.getId()) {
                    resultUserProblems.add(new UserProblem(problem, userProblem.getStatus()));
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                resultUserProblems.add(new UserProblem(problem, Status.NEW));
            }
        }

        return resultUserProblems;
    }

    public List<UserProblem> getByTagList(User user, List<Tag> tags) {

        if (tags == null || tags.isEmpty()) {
            return new ArrayList<UserProblem>();
        }

        List<UserProblem> userProblemsBy1 = simpleJdbcTemplate.query("select up.problem_id, up.status_id " +
                "from user_problem up inner join problem_tag pt on pt.problem_id = up.problem_id " +
                "where up.user_id = ? and pt.tag_id = ?",
                getRowMapper(),
                user.getId(),
                tags.get(0).getId());
        List<Problem> problems = problemDepot.getByTagList(tags);

        List<UserProblem> userProblems = new ArrayList<UserProblem>();
        for (UserProblem userProblem : userProblemsBy1) {
            if (userProblem.getProblem().getTags().containsAll(tags)) {
                userProblems.add(userProblem);
            }
        }

        List<UserProblem> resultUserProblems = new ArrayList<UserProblem>();

        for (Problem problem : problems) {
            boolean flag = false;
            for (UserProblem userProblem : userProblems) {
                if (userProblem.getProblem().getId() == problem.getId()) {
                    resultUserProblems.add(new UserProblem(problem, userProblem.getStatus()));
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                resultUserProblems.add(new UserProblem(problem, Status.NEW));
            }
        }

        return resultUserProblems;

    }

    protected ParameterizedRowMapper<UserProblem> getRowMapper() {
        return new ParameterizedRowMapper<UserProblem>() {
            public UserProblem mapRow(ResultSet resultSet, int i) throws SQLException {
                return new UserProblem(
                        problemDepot.getById(resultSet.getInt("problem_id")),
                        statusDepot.getById(resultSet.getInt("status_id")));
            }
        };
    }
    @Required
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Required
    public void setUserDepot(UserDepot userDepot) {
        this.userDepot = userDepot;
    }

    @Required
    public void setProblemDepot(ProblemDepot problemDepot) {
        this.problemDepot = problemDepot;
    }

    @Required
    public void setStatusDepot(StatusDepot statusDepot) {
        this.statusDepot = statusDepot;
    }

    @Required
    public void setTagDepot(TagDepot tagDepot) {
        this.tagDepot = tagDepot;
    }

}
