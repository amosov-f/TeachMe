package com.kk.teachme.db;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.naming.OperationNotSupportedException;

public abstract class AbstractDepot<T> {

    protected JdbcTemplate jdbcTemplate;

    public abstract int add(T t) throws OperationNotSupportedException;

    public T get(int id) {
        try {
            return jdbcTemplate.queryForObject(getSelectQuery() + " WHERE id = ?", getRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean contains(int id) {
        return get(id) != null;
    }

    private String getSelectQuery() {
        return "SELECT * FROM " + getTableName();
    }

    protected abstract RowMapper<T> getRowMapper();

    protected abstract String getTableName();

    @Required
    public void setJdbcTemplate(JdbcTemplate simpleJdbcTemplate) {
        this.jdbcTemplate = simpleJdbcTemplate;
    }
}
