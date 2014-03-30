package com.kk.teachme.db;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.naming.OperationNotSupportedException;
import java.util.List;

public abstract class AbstractDepot<T> {

    protected JdbcTemplate jdbcTemplate;

    public abstract int add(T t) throws OperationNotSupportedException;

    public T get(int id) {
        final List<T> results = jdbcTemplate.query(getQueryForOne(), getRowMapper(), id);
        return results.size() == 0 ? null : results.get(0);
    }

    public boolean contains(int id) {
        return !jdbcTemplate.query(getQueryForOne(), getRowMapper(), id).isEmpty();
    }

    protected abstract RowMapper<T> getRowMapper();
    protected abstract String getQueryForOne();

    @Required
    public void setJdbcTemplate(JdbcTemplate simpleJdbcTemplate) {
        this.jdbcTemplate = simpleJdbcTemplate;
    }
}
