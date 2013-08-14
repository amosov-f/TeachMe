package com.kk.teachme.db;

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

    @Override
    public int addObject(final User user) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final int update = simpleJdbcTemplate.getJdbcOperations().update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                        PreparedStatement preparedStatement = conn.prepareStatement(
                                "insert into user (login) values (?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        preparedStatement.setString(1, user.getUsername());
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

    public boolean checkIfExists(String userLogin){
         // check if user with userLogin exists
         return !simpleJdbcTemplate.query("select * from user where login = ?", getRowMapper(), userLogin).isEmpty();
    }

    public User getByLogin(String login) {
        List<User> userList = simpleJdbcTemplate.query("select * from user where login = ?", getRowMapper(), login);
        if (userList.isEmpty()) {
            return null;
        }
        return userList.get(0);
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

    @Required
    public void setProblemDepot(ProblemDepot problemDepot) {
        this.problemDepot = problemDepot;
    }
}
