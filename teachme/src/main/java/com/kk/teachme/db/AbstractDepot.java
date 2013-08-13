package com.kk.teachme.db;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.util.List;

/**
 * @author akonst
 */

public abstract class AbstractDepot<T> {

    protected SimpleJdbcTemplate simpleJdbcTemplate;

    public abstract int addObject(T t);

    public T getById(int id) {
        final List<T> results = simpleJdbcTemplate.query(getQueryForOne(), getRowMapper(), id);
        if (results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

    protected abstract ParameterizedRowMapper<T> getRowMapper();
    protected abstract String getQueryForOne();

    @Required
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
