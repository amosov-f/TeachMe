package com.kk.teachme.db;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ConfigDepot {

    SimpleJdbcTemplate simpleJdbcTemplate;

    public void addVariable(String variable, int value) {
        simpleJdbcTemplate.update(
                "insert into config (variable, value) values (?, ?)",
                variable,
                value
        );
    }

    public void setValue(String variable, int value) {
        simpleJdbcTemplate.update(
                "update config set value = ? where variable = ?",
                value,
                variable
        );
    }

    public Integer getValue(String variable) {

        final List<Integer> values = simpleJdbcTemplate.query("select value from config where variable = ?",
            new ParameterizedRowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getInt(1);
            }
        }, variable);

        if (values.size() == 0) {
            return null;
        }
        return values.get(0);

    }

    @Required
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

}
