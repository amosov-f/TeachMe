package com.kk.teachme.db;

import com.kk.teachme.checker.SolveStatus;
import com.kk.teachme.model.Solution;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SolutionDepot {
    CheckerDepot checkerDepot;
    SimpleJdbcTemplate simpleJdbcTemplate;

    public SolveStatus check(int problemId, String userAnswer) {
        Solution solution = getSolution(problemId);
        if (solution == null) {
            throw new IllegalStateException();
        }
        return solution.check(userAnswer);
    }

    public void addSolution(int problemId, String solution, int checkerId) {
        simpleJdbcTemplate.update(
                "insert into solution (id, solution_text, checker_id) values (?, ?, ?)",
                problemId,
                solution,
                checkerId
        );
    }

    public void setSolution(int problemId, String solution, int checkerId) {
        simpleJdbcTemplate.update(
                "update solution set solution_text = ?, checker_id = ? where id = ?",
                solution,
                checkerId,
                problemId
        );
    }

    public Solution getSolution(int problemId) {
        List<Solution> solutions = simpleJdbcTemplate.query(
                "select * from solution where id = ?",
                new ParameterizedRowMapper<Solution>() {
                    @Override
                    public Solution mapRow(ResultSet resultSet, int i) throws SQLException {
                        return new Solution(
                                resultSet.getString("solution_text"),
                                checkerDepot.getChecker(resultSet.getInt("checker_id"))
                        );
                    }
                },
                problemId
        );
        if (solutions.size() != 1) {
            throw new IllegalStateException();
        }
        return solutions.get(0);
    }

    public int getCheckerId(int problemId) {
        List<Integer> checkerIds = simpleJdbcTemplate.query(
                "select * from solution where id = ?",
                new ParameterizedRowMapper<Integer>() {
                    @Override
                    public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getInt("checker_id");
                    }
                },
                problemId
        );
        if (checkerIds.size() != 1) {
            throw new IllegalStateException();
        }
        return checkerIds.get(0);
    }

    @Required
    public void setCheckerDepot(CheckerDepot checkerDepot) {
        this.checkerDepot = checkerDepot;
    }

    @Required
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
