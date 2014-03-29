package com.kk.teachme.db;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

public abstract class AbstractDepot<T> {

    protected JdbcTemplate jdbcTemplate;

    public abstract int add(T t);

    public T get(int id) {
        final List<T> results = jdbcTemplate.query(getQueryForOne(), getRowMapper(), id);
        if (results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

    protected abstract RowMapper<T> getRowMapper();
    protected abstract String getQueryForOne();

    @Required
    public void setJdbcTemplate(JdbcTemplate simpleJdbcTemplate) {
        this.jdbcTemplate = simpleJdbcTemplate;
    }
}
