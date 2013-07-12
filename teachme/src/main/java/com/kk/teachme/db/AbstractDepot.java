package com.kk.teachme.db;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author akonst
 */

public abstract class AbstractDepot<T> {

    protected SimpleJdbcTemplate jdbcTemplate;

    public abstract int addObject(T t);

    public T getById(int id) {
        final List<T> results = jdbcTemplate.query(getQueryForOne(), getRowMapper(), id);
        if (results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

    protected abstract ParameterizedRowMapper<T> getRowMapper();
    protected abstract String getQueryForOne();

    @Required
    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
