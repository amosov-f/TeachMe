package com.kk.teachme.db;

import com.kk.teachme.model.Solution;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SolutionDepot {
    CheckerDepot checkerDepot;
    SimpleJdbcTemplate jdbcTemplate;

    public boolean check(int problemId, String userAnswer) {
        Solution solution = getSolution(problemId);
        if (solution == null) throw new IllegalStateException();
        return solution.ckeck(userAnswer);
    }

    private Solution getSolution(int problemId) {
        List<Solution> solutions = jdbcTemplate.query("select * from solution where id = ?", new ParameterizedRowMapper<Solution>() {
            @Override
            public Solution mapRow(ResultSet resultSet, int i) throws SQLException {
                return new Solution(resultSet.getString("solution_text"), checkerDepot.getChecker(resultSet.getInt("checker_id")));
            }
        }, problemId);
        if (solutions.size() != 1) throw new IllegalStateException();
        return solutions.get(0);
    }

    @Required
    public void setCheckerDepot(CheckerDepot checkerDepot) {
        this.checkerDepot = checkerDepot;
    }

    @Required
    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
