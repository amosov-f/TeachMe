package com.kk.teachme.db;

import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Status;
import com.kk.teachme.model.User;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatusDepot {

    SimpleJdbcTemplate jdbcTemplate;

    public boolean setStatus(User user, Problem problem, Status status) {
        jdbcTemplate.update(
                "insert into user_problem values (?, ?, ?)",
                user.getId(),
                problem.getId(),
                getStatusId(status)
        );

        return false;
    }

    public Status getStatus(User user, Problem problem) {
        int status_id = jdbcTemplate.queryForInt(
                "select status_id from user_problem where user_id = ? and problem_id = ?",
                user.getId(),
                problem.getId()
        );

        Status result = jdbcTemplate.query(
                "select status from problem_status where id = ?",
                getStatusRowMapper(),
                status_id
        ).get(0);

        return result == null ? Status.NEW : result;
    }

    public int getStatusId(Status status) {
        return jdbcTemplate.query(
                "select id from problem_status where status = ?",
                getIdRowMapper(),
                status.toString().toLowerCase()
        ).get(0);
    }

    private ParameterizedRowMapper<Status> getStatusRowMapper() {
        return new ParameterizedRowMapper<Status>() {
            public Status mapRow(ResultSet resultSet, int i) throws SQLException {
                return Status.valueOf(resultSet.getString("status").toUpperCase());
            }
        };
    }

    private ParameterizedRowMapper<Integer> getIdRowMapper() {
        return new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt("id");
            }
        };
    }

    @Required
    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
