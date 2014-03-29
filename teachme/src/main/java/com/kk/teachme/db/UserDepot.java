package com.kk.teachme.db;

import com.kk.teachme.model.User;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

public class UserDepot extends AbstractDepot<User> {

    ProblemDepot problemDepot;

    @Override
    public int add(final User user) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final int update = jdbcTemplate.update(
                conn -> {
                    PreparedStatement preparedStatement = conn.prepareStatement(
                            "insert into user (username, first_name, last_name) values (?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    preparedStatement.setString(1, user.getUsername());
                    preparedStatement.setString(2, user.getFirstName());
                    preparedStatement.setString(3, user.getLastName());

                    return preparedStatement;
                },
                keyHolder
        );
        if (update > 0) {
            int id = keyHolder.getKey().intValue();
            user.setId(id);
            return id;
        }
        return -1;
    }

    public boolean contains(String username) {
         // check if user with userLogin exists
         return !jdbcTemplate.query("select * from user where username = ?", getRowMapper(), username).isEmpty();
    }

    public User getByUsername(String username) {
        List<User> userList = jdbcTemplate.query("select * from user where username = ?", getRowMapper(), username);
        if (userList.isEmpty()) {
            return null;
        }
        return userList.get(0);
    }

    @Override
    protected RowMapper<User> getRowMapper() {
        return (resultSet, i) -> new User(
                resultSet.getInt("id"),
                resultSet.getString("username"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name")
        );
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
