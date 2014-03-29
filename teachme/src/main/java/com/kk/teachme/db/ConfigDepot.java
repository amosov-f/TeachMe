package com.kk.teachme.db;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class ConfigDepot {

    JdbcTemplate jdbcTemplate;

    public void addVariable(String variable, int value) {
        jdbcTemplate.update(
                "insert into config (variable, value) values (?, ?)",
                variable,
                value
        );
    }

    public void setValue(String variable, int value) {
        jdbcTemplate.update(
                "update config set value = ? where variable = ?",
                value,
                variable
        );
    }

    public Integer getValue(String variable) {

        final List<Integer> values = jdbcTemplate.query(
                "select value from config where variable = ?",
                (resultSet, i) -> resultSet.getInt(1),
                variable
        );

        if (values.size() == 0) {
            return null;
        }
        return values.get(0);
    }

    @Required
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
