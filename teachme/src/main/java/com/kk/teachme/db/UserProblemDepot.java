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

    SimpleJdbcTemplate jdbcTemplate;

    public void addObject(final UserProblem userProblem) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final int update = jdbcTemplate.getJdbcOperations().update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                        PreparedStatement preparedStatement =
                                conn.prepareStatement("insert into user_problem (problem_id, status_id) values(?,?)"
                                        , Statement.RETURN_GENERATED_KEYS);
                        preparedStatement.setInt(1, userProblem.getProblem().getId());
                        preparedStatement.setInt(2, statusDepot.getStatusId(userProblem.getStatus()));
                        return preparedStatement;
                    }
                }, keyHolder);

    }


    public List<Problem> getAllUserProblems(int userId) {
        return jdbcTemplate.query("select problem_id, name, statement from user_problem " +
                " inner join problem on user_problem.problem_id = problem.id" +
                " where user_id = ? ",
                getRowMapper(),
                userId);
    }

    public List<Problem> getUnsolvedProblems(int userId) {
        return jdbcTemplate.query("select problem_id, name, statement from user_problem " +
                "inner join problem on user_problem.problem_id = problem.id" +
                " where user_id = ? and status_id != ? ",
                getRowMapper(),
                userId,
                statusDepot.getStatusId(Status.SOLVED));
    }

    public List<Problem> getSolvedProblems(int userId) {
        return jdbcTemplate.query("select problem_id, name, statement from user_problem " +
                "inner join problem on user_problem.problem_id = problem.id " +
                "where user_id = ? and status_id = ? ",
                getRowMapper(),
                userId,
                statusDepot.getStatusId(Status.SOLVED)
        );
    }

    public List<Problem> getProblemsByTag(int userId, Tag tag) {

        return jdbcTemplate.query("select user_problem.problem_id, problem.name, problem.statement from user_problem " +
                "inner join problem on problem.id=user_problem.problem_id " +
                "inner join problem_tag on problem.id=problem_tag.problem_id" +
                "where user_id=? and tag_id=?",
                getRowMapper(),
                userId,
                tag.getId());
    }



    protected ParameterizedRowMapper<Problem> getRowMapper() {
        return new ParameterizedRowMapper<Problem>() {
            public Problem mapRow(ResultSet resultSet, int i) throws SQLException {
                return new Problem(resultSet.getInt("problem_id"),
                        resultSet.getString("name"),
                        resultSet.getString("statement")
                );
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

}


