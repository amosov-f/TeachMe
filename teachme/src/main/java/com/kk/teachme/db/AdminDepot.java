package com.kk.teachme.db;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

public class AdminDepot {

    JdbcTemplate jdbcTemplate;

    public void addAdmin(int adminId) {
        jdbcTemplate.update("insert into admin (id) values (?)", adminId);
    }

    public boolean contains(int adminId) {
        return !jdbcTemplate.queryForList("select * from admin where id = " + adminId).isEmpty();

    }

    @Required
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}

