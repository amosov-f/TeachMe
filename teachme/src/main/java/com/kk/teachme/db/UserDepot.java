package com.kk.teachme.db;

import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Status;
import com.kk.teachme.model.User;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.List;

public class UserDepot extends AbstractDepot<User> {

    ProblemDepot problemDepot;
    StatusDepot statusDepot;

    @Required
    public void setProblemDepot(ProblemDepot problemDepot) {
        this.problemDepot = problemDepot;
    }

    @Required
    public void setStatusDepot(StatusDepot statusDepot) {
        this.statusDepot = statusDepot;
    }

    @Override
    public int addObject(final User user) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final int update = jdbcTemplate.getJdbcOperations().update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                        PreparedStatement preparedStatement = conn.prepareStatement(
                                "insert into user (login) values (?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        preparedStatement.setString(1, user.getLogin());
                        return preparedStatement;
                    }
                }, keyHolder);
        if (update > 0) {
            int id = keyHolder.getKey().intValue();
            user.setId(id);
            return id;
        }
        return -1;
    }

    @Override
    protected ParameterizedRowMapper<User> getRowMapper() {
        return new ParameterizedRowMapper<User>() {
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                return new User(resultSet.getInt("id"), resultSet.getString("login"));
            }
        };
    }

    @Override
    protected String getQueryForOne() {
        return "select * from user where id = ?";
    }

    public List<Problem> getSolvedProblems(User user) {
        return jdbcTemplate.query("select problem_id from user_problem where user_id = ? and status_id = ?",
                getProblemRowMapper(),
                user.getId(),
                statusDepot.getStatusId(Status.SOLVED)
        );
    }

    private ParameterizedRowMapper<Problem> getProblemRowMapper() {
        return new ParameterizedRowMapper<Problem>() {
            public Problem mapRow(ResultSet resultSet, int i) throws SQLException {
                return problemDepot.getById(resultSet.getInt("problem_id"));
            }
        };
    }

}
