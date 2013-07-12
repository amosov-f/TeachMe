package com.kk.teachme.db;


import com.kk.teachme.checker.IntChecker;
import com.kk.teachme.model.Solution;
import com.kk.teachme.model.Tag;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CheckerDepot extends AbstractDepot<Solution>{
    @Override
    public int addObject(Solution solution) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected ParameterizedRowMapper<Solution> getRowMapper() {
        return new ParameterizedRowMapper<Solution>() {
            public Solution mapRow(ResultSet resultSet, int i) throws SQLException {
                return new Solution(resultSet.getInt("id"),
                        resultSet.getString("solution_text"),
                        resultSet.getInt("checker_id")
                );
            }
        };
    }

    @Override
    protected String getQueryForOne() {
        return "select * from solution where id = ?";
    }
    public boolean checkProblem(int problemId, String userAnswer){
        final List<Solution> results = jdbcTemplate.query("select * from solution where id = ?", getRowMapper(), problemId);
        Solution solution = results.get(0);
        IntChecker checker = new IntChecker(solution.getSolution_text());
        return checker.check(userAnswer);
    }
}
