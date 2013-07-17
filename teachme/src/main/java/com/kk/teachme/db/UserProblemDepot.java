package com.kk.teachme.db;

import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Status;
import com.kk.teachme.model.Tag;
import com.kk.teachme.model.UserProblem;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserProblemDepot {
    StatusDepot statusDepot;
    ProblemDepot problemDepot;
    SimpleJdbcTemplate jdbcTemplate;

    public int addObject(final UserProblem userProblem) {
         return -1;
    }


    public List<UserProblem> getAllUserProblems(int userId)   {
        return  new ArrayList<UserProblem>();
    }

    public List<UserProblem> getUnsolvedProblems(int userId)   {
        return  new ArrayList<UserProblem>();
    }

    public List<UserProblem> getProblemsByTag(int userId, Tag tag)   {
        return  new ArrayList<UserProblem>();
    }



    protected ParameterizedRowMapper<UserProblem> getRowMapper() {
        return new ParameterizedRowMapper<UserProblem>() {
            public UserProblem mapRow(ResultSet resultSet, int i) throws SQLException {
                Problem problem = problemDepot.getById(resultSet.getInt("problem_id"));
                //todo change status
                Status status = (Status)  resultSet.getObject("status");
                return new UserProblem(problem,
                        resultSet.getInt("user_id"),
                        status);

            }
        };
    }

    @Required
    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Required
    public void setStatusDepot(StatusDepot statusDepot) {
        this.statusDepot = statusDepot;
    }
    @Required
    public void setProblemDepot(ProblemDepot problemDepot) {
        this.problemDepot = problemDepot;
    }

}
