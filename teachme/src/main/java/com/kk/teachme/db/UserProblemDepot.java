package com.kk.teachme.db;

import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Status;
import com.kk.teachme.model.Tag;
import com.kk.teachme.model.UserProblem;
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
    StatusDepot statusDepot;
    ProblemDepot problemDepot;
    SimpleJdbcTemplate jdbcTemplate;

    public void addObject(final UserProblem userProblem) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final int update = jdbcTemplate.getJdbcOperations().update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                        PreparedStatement preparedStatement =
                                conn.prepareStatement("insert into user_problem (problem_id, user_id, status_id) values(?,?,?)"
                                        , Statement.RETURN_GENERATED_KEYS);
                        preparedStatement.setInt(1, userProblem.getProblem().getId());
                        preparedStatement.setInt(2, userProblem.getUser_id());
                        preparedStatement.setInt(3, statusDepot.getStatusId(userProblem.getStatus()));
                        return preparedStatement;
                    }
                }, keyHolder);

    }


    public List<UserProblem> getAllUserProblems(int userId) {
        return jdbcTemplate.query("select * from user_problem where user_id=?",
                getRowMapper(),
                userId);
    }

    public List<UserProblem> getUnsolvedProblems(int userId) {
        return jdbcTemplate.query("select * from user_problem where user_id=? and status_id !=?",
                getRowMapper(),
                userId,
                statusDepot.getStatusId(Status.SOLVED));
    }
    public List<UserProblem> getSolvedProblems(int userId) {
        return jdbcTemplate.query("select problem_id from user_problem where user_id = ? and status_id = ?",
                getRowMapper(),
                userId,
                statusDepot.getStatusId(Status.SOLVED)
        );
    }

    public List<UserProblem> getProblemsByTag(int userId, Tag tag) {
        return jdbcTemplate.query("select * from user_problem where user_id=? inner join (select * from problem_tag where tag_id = ?)  on user_problem.problem_id = problem_tag.problem_id",
        getRowMapper(),
        userId,
        tag.getId());
    }



    protected ParameterizedRowMapper<UserProblem> getRowMapper() {
        return new ParameterizedRowMapper<UserProblem>() {
            public UserProblem mapRow(ResultSet resultSet, int i) throws SQLException {
                Problem problem = problemDepot.getById(resultSet.getInt("problem_id"));
                Status status = statusDepot.getById(resultSet.getInt("status_id"));
                return new UserProblem(problem,
                        resultSet.getInt("user_id"),
                        status);

            }
        };
    }

    @Required
    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Required
    public void setStatusDepot(StatusDepot statusDepot) {
        this.statusDepot = statusDepot;
    }

    @Required
    public void setProblemDepot(ProblemDepot problemDepot) {
        this.problemDepot = problemDepot;
    }

}
