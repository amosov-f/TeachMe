package com.kk.teachme.db;

import com.kk.teachme.model.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserProblemDepot {
    UserDepot userDepot;
    ProblemDepot problemDepot;
    StatusDepot statusDepot;
    TagDepot tagDepot;

    SimpleJdbcTemplate jdbcTemplate;

    public void addObject(final UserProblem userProblem, final User user) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final int update = jdbcTemplate.getJdbcOperations().update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                        PreparedStatement preparedStatement =
                                conn.prepareStatement("insert into user_problem (user_id, problem_id, status_id) values(?, ?, ?)"
                                        , Statement.RETURN_GENERATED_KEYS);
                        preparedStatement.setInt(1, user.getId());
                        preparedStatement.setInt(2, userProblem.getProblem().getId());
                        preparedStatement.setInt(3, statusDepot.getStatusId(userProblem.getStatus()));
                        return preparedStatement;
                    }
                }, keyHolder);

    }

    public boolean addUserProblem(int userId, int problemId) {
        List<UserProblem> problemList =
                jdbcTemplate.query("select * from user_problem " +
                "where user_id = ? and problem_id = ?",
                getRowMapper(),
                userId,
                problemId);
        if (problemList.size() < 1) {
            addObject(new UserProblem(
                    problemDepot.getById(problemId)
            ),
                    userDepot.getById(userId)
            );
            return true;
        }
        return false;
    }

    public List<UserProblem> getAllUserProblems(int userId) {
        return jdbcTemplate.query("select problem_id, status_id " +
                "from user_problem where user_id = ?",
                getRowMapper(),
                userId);
    }

    public List<UserProblem> getUnsolvedProblems(int userId) {
        return jdbcTemplate.query("select problem_id, status_id " +
                "from user_problem where user_id = ? and status_id != ?",
                getRowMapper(),
                userId,
                statusDepot.getStatusId(Status.SOLVED));
    }

    public List<UserProblem> getSolvedProblems(int userId) {
        return jdbcTemplate.query("select problem_id, status_id " +
                "from user_problem where user_id = ? and status_id = ?",
                getRowMapper(),
                userId,
                statusDepot.getStatusId(Status.SOLVED)
        );
    }

    public List<UserProblem> getProblemsByTag(int userId, int tagId) {

        List<Problem> problemList = problemDepot.getByTag(tagDepot.getById(tagId));

        List<UserProblem> userProblems = jdbcTemplate.query("select up.problem_id, up.status_id " +
                "from user_problem up inner join problem_tag pt on pt.problem_id = up.problem_id " +
                "where up.user_id = ? and pt.tag_id = ?",
                getRowMapper(),
                userId,
                tagId);

        List<UserProblem> resultUserProblemList = new ArrayList<UserProblem>();

        for (Problem problem : problemList) {

            UserProblem newUserProblem = new UserProblem(problem);
            resultUserProblemList.add(newUserProblem);

            for (UserProblem userProblem : userProblems) {
                if (userProblem.getProblem().getId() == newUserProblem.getProblem().getId()) {
                    newUserProblem.setStatus(userProblem.getStatus());
                    break;
                }
            }

        }

        return resultUserProblemList;

    }


    public List<UserProblem> getProblemsByTagIdList(int userId, List<Integer> tagIdList) {

        List<Tag> tagList = new ArrayList<Tag>();
        for (Integer tagId : tagIdList) {
            tagList.add(tagDepot.getById(tagId));
        }
        return getProblemsByTagList(userId, tagList);

    }

    public List<UserProblem> getProblemsByTagList(int userId, List<Tag> tagList) {

        if (tagList == null || tagList.isEmpty()) {
            return new ArrayList<UserProblem>();
        }

        List<Problem> problemList = problemDepot.getByTagList(tagList);

        List<UserProblem> userProblems = jdbcTemplate.query("select up.problem_id, up.status_id " +
                "from user_problem up inner join problem_tag pt on pt.problem_id = up.problem_id " +
                "where up.user_id = ? and pt.tag_id = ?",
                getRowMapper(),
                userId,
                tagList.get(0).getId());

        List<UserProblem> userProblemList = new ArrayList<UserProblem>();
        for (UserProblem userProblem : userProblems) {
            if (userProblem.getProblem().getTags().containsAll(tagList)) {
                userProblemList.add(userProblem);
            }
        }

        List<UserProblem> resultUserProblemList = new ArrayList<UserProblem>();

        for (Problem problem : problemList) {

            UserProblem newUserProblem = new UserProblem(problem);
            resultUserProblemList.add(newUserProblem);

            for (UserProblem userProblem : userProblems) {
                if (userProblem.getProblem().getId() == newUserProblem.getProblem().getId()) {
                    newUserProblem.setStatus(userProblem.getStatus());
                    break;
                }
            }

        }

        return resultUserProblemList;

    }

    protected ParameterizedRowMapper<UserProblem> getRowMapper() {
        return new ParameterizedRowMapper<UserProblem>() {
            public UserProblem mapRow(ResultSet resultSet, int i) throws SQLException {
                return new UserProblem(
                        problemDepot.getById(resultSet.getInt("problem_id")),
                        statusDepot.getById(resultSet.getInt("status_id")));
            }
        };
    }
    @Required
    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Required
    public void setUserDepot(UserDepot userDepot) {
        this.userDepot = userDepot;
    }

    @Required
    public void setProblemDepot(ProblemDepot problemDepot) {
        this.problemDepot = problemDepot;
    }

    @Required
    public void setStatusDepot(StatusDepot statusDepot) {
        this.statusDepot = statusDepot;
    }

    @Required
    public void setTagDepot(TagDepot tagDepot) {
        this.tagDepot = tagDepot;
    }

}