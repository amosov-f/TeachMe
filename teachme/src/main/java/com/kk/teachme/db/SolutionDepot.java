package com.kk.teachme.db;

import com.kk.teachme.checker.Checker;
import com.kk.teachme.model.Solution;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class SolutionDepot {

    CheckerDepot checkerDepot;

    JdbcTemplate jdbcTemplate;

    public Checker.SolveStatus check(int problemId, String userAnswer) {
        Solution solution = getSolution(problemId);
        if (solution == null) {
            throw new IllegalStateException();
        }
        return solution.check(userAnswer);
    }

    public void addSolution(int problemId, String solution, int checkerId) {
        jdbcTemplate.update(
                "insert into solution (id, solution_text, checker_id) values (?, ?, ?)",
                problemId,
                solution,
                checkerId
        );
    }

    public void setSolution(int problemId, String solution, int checkerId) {
        jdbcTemplate.update(
                "update solution set solution_text = ?, checker_id = ? where id = ?",
                solution,
                checkerId,
                problemId
        );
    }

    public Solution getSolution(int problemId) {
        List<Solution> solutions = jdbcTemplate.query(
                "select * from solution where id = ?",
                (resultSet, i) -> new Solution(
                        resultSet.getString("solution_text"),
                        checkerDepot.get(resultSet.getInt("checker_id"))
                ),
                problemId
        );
       /* if (solutions.size() != 1) {
            throw new IllegalStateException();
        }*/
        return solutions.get(0);
    }

    public int getCheckerId(int problemId) {
        List<Integer> checkerIds = jdbcTemplate.query(
                "select * from solution where id = ?",
                (resultSet, i) -> resultSet.getInt("checker_id"),
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
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
