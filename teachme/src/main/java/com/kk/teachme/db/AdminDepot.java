package com.kk.teachme.db;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

//TODO
public class AdminDepot {

    JdbcTemplate jdbcTemplate;

    public void addAdmin(int adminId) {
        //see addSolution in SolutionDepot
    }

    @Required
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
