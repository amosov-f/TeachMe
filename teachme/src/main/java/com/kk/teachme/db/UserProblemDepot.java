package com.kk.teachme.db;

import com.kk.teachme.model.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProblemDepot {
    UserDepot userDepot;
    ProblemDepot problemDepot;
    StatusDepot statusDepot;
    TagDepot tagDepot;

    SimpleJdbcTemplate simpleJdbcTemplate;

    private void addObject(final int userId, final UserProblem userProblem) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final int update = simpleJdbcTemplate.getJdbcOperations().update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                        PreparedStatement preparedStatement =
                                conn.prepareStatement("insert into user_problem (user_id, problem_id, status_id, attempts) values(?, ?, ?, ?)"
                                        , Statement.RETURN_GENERATED_KEYS);
                        preparedStatement.setInt(1, userId);
                        preparedStatement.setInt(2, userProblem.getProblem().getId());
                        preparedStatement.setInt(3, statusDepot.getStatusId(userProblem.getStatus()));
                        preparedStatement.setInt(4, userProblem.getRawAttempts());
                        return preparedStatement;
                    }
                }, keyHolder);

    }

    private void alterObject(final int userId, final UserProblem userProblem) {
        simpleJdbcTemplate.update(
                "update user_problem set status_id = ?, attempts = ? where user_id = ? and problem_id = ?",
                statusDepot.getStatusId(userProblem.getStatus()),
                userProblem.getRawAttempts(),
                userId,
                userProblem.getProblem().getId()
        );
    }

    public boolean addUserProblem(int userId, int problemId) {
        List<UserProblem> userProblems = simpleJdbcTemplate.query("select problem_id, attempts " +
                "from user_problem where user_id = ? and problem_id = ?",
                getRowMapper(),
                userId,
                problemId
        );
        if (userProblems.isEmpty()) {
            addObject(userId, new UserProblem(problemDepot.getById(problemId), 0));
            return true;
        }
        return false;
    }

    public UserProblem getByIds(int userId, int problemId) {
        List<UserProblem> userProblems = simpleJdbcTemplate.query(
                "select problem_id, attempts from user_problem where user_id = ? and problem_id = ?",
                getRowMapper(),
                userId,
                problemId
        );

        if (userProblems.isEmpty()) {
            return null;
        }

        return userProblems.get(0);
    }


 /*   public boolean setStatus(User user, Problem problem, Status status) {
        List<UserProblem> problemList = jdbcTemplate.query(
                "select * from user_problem where user_id = ? and problem_id = ?",
                getRowMapper(),
                user.getId(),
                problem.getId()
        );
        if (problemList.size() == 0 && status != null && status != Status.NEW) {
            addObject(user, new UserProblem(problem, status));
            return true;
        }
        jdbcTemplate.update(
                "update user_problem set status_id = ? where user_id = ? and problem_id = ?",
                statusDepot.getStatusId(status),
                user.getId(),
                problem.getId()
        );

        return false;
    } */

    public UserProblem attempt(int userId, int problemId, boolean solved) {

        List<UserProblem> userProblems = simpleJdbcTemplate.query("select problem_id, attempts " +
                "from user_problem where user_id = ? and problem_id = ?",
                getRowMapper(),
                userId,
                problemId
        );

        UserProblem userProblem;
        if (userProblems.isEmpty()) {
            userProblem = new UserProblem(problemDepot.getById(problemId));
            addObject(userId, userProblem);
        } else {
            userProblem = userProblems.get(0);
        }

        userProblem.attempt(solved);
        alterObject(userId, userProblem);

        return userProblem;

    }

    public Status getStatus(int userId, int problemId) {
        List<UserProblem> userProblems = simpleJdbcTemplate.query("select problem_id, attempts " +
                "from user_problem where user_id = ? and problem_id = ?",
                getRowMapper(),
                userId,
                problemId
        );

        if (userProblems.isEmpty()) {
            return Status.NEW;
        }

        return userProblems.get(0).getStatus();
    }

    public List<UserProblem> getAllUserProblems(int userId) {

        List<UserProblem> userProblems = simpleJdbcTemplate.query("select problem_id, attempts " +
                "from user_problem where user_id = ?",
                getRowMapper(),
                userId);
        List<Problem> allProblems = problemDepot.getAllProblems();

        List<UserProblem> allUserProblems = new ArrayList<UserProblem>();

        for (Problem problem : allProblems) {
            boolean flag = false;
            for (UserProblem userProblem : userProblems) {
                if (userProblem.getProblem().getId() == problem.getId()) {
                    allUserProblems.add(new UserProblem(problem, userProblem.getRawAttempts()));
                    flag = true;
                    break;
                }
            }
            if (flag == false) {
                allUserProblems.add(new UserProblem(problem));
            }
        }

        return allUserProblems;
    }


    public List<UserProblem> getUnsolvedProblems(int userId) {
        List<UserProblem> userProblems = simpleJdbcTemplate.query(
                "select problem_id, attempts from user_problem where user_id = ?",
                getRowMapper(),
                userId
        );
        List<Problem> allProblems = problemDepot.getAllProblems();

        List<UserProblem> unsolvedUserProblems = new ArrayList<UserProblem>();

        for (Problem problem : allProblems) {
            boolean flag = false;
            for (UserProblem userProblem : userProblems) {
                if (userProblem.getProblem().getId() == problem.getId()) {
                    flag = true;
                    if (userProblem.getStatus() != Status.SOLVED) {
                        unsolvedUserProblems.add(new UserProblem(problem, userProblem.getRawAttempts()));
                    }
                    break;
                }
            }
            if (flag == false) {
                unsolvedUserProblems.add(new UserProblem(problem));
            }
        }

        return unsolvedUserProblems;
    }

    public List<UserProblem> getSolvedProblems(int userId) {
        return simpleJdbcTemplate.query("select problem_id, attempts " +
                "from user_problem where user_id = ? and attempts > 0",
                getRowMapper(),
                userId
        );
    }

    public List<UserProblem> getReadProblems(int userId) {
        return simpleJdbcTemplate.query("select problem_id, attempts " +
                "from user_problem where user_id = ? and attempts <= 0",
                getRowMapper(),
                userId
        );
    }

    public List<UserProblem> getAttemptedProblems(int userId) {
        return simpleJdbcTemplate.query("select problem_id, attempts " +
                "from user_problem where user_id = ? and attempts < 0",
                getRowMapper(),
                userId
        );
    }

    public List<UserProblem> getByTag(int userId, Tag tag) {

        List<UserProblem> userProblems = simpleJdbcTemplate.query("select up.problem_id, up.attempts " +
                "from user_problem up inner join problem_tag pt on pt.problem_id = up.problem_id " +
                "where up.user_id = ? and pt.tag_id = ?",
                getRowMapper(),
                userId,
                tag.getId());
        List<Problem> problems = problemDepot.getByTag(tag);

        List<UserProblem> resultUserProblems = new ArrayList<UserProblem>();

        for (Problem problem : problems) {
            boolean flag = false;
            for (UserProblem userProblem : userProblems) {
                if (userProblem.getProblem().getId() == problem.getId()) {
                    resultUserProblems.add(new UserProblem(problem, userProblem.getRawAttempts()));
                    flag = true;
                    break;
                }
            }
            if (flag == false) {
                resultUserProblems.add(new UserProblem(problem));
            }
        }

        return resultUserProblems;
    }

    public List<UserProblem> getByTagList(int userId, List<Tag> tags) {

        if (tags == null || tags.isEmpty()) {
            return new ArrayList<UserProblem>();
        }

        List<UserProblem> userProblemsBy1 = simpleJdbcTemplate.query("select up.problem_id, up.attempts " +
                "from user_problem up inner join problem_tag pt on pt.problem_id = up.problem_id " +
                "where up.user_id = ? and pt.tag_id = ?",
                getRowMapper(),
                userId,
                tags.get(0).getId());
        List<Problem> problems = problemDepot.getByTagList(tags);

        List<UserProblem> userProblems = new ArrayList<UserProblem>();
        for (UserProblem userProblem : userProblemsBy1) {
            if (userProblem.getProblem().getTags().containsAll(tags)) {
                userProblems.add(userProblem);
            }
        }

        List<UserProblem> resultUserProblems = new ArrayList<UserProblem>();

        for (Problem problem : problems) {
            boolean flag = false;
            for (UserProblem userProblem : userProblems) {
                if (userProblem.getProblem().getId() == problem.getId()) {
                    resultUserProblems.add(new UserProblem(problem, userProblem.getRawAttempts()));
                    flag = true;
                    break;
                }
            }
            if (flag == false) {
                resultUserProblems.add(new UserProblem(problem));
            }
        }

        return resultUserProblems;

    }

    public List<UserProblem> getByFilters(int userId, List<Tag> tags, String filter, boolean inMind, int from, int to) {
        String query =
                "SELECT problem_id, attempts FROM user_problem\n" +
                "WHERE user_id = " + userId + "\n" +
                getTagsQuery("problem_id", tags) + "\n" +
                getStatusQuery(filter) + "\n" +
                getInMindQuery("problem_id", inMind) + "\n";
        if (filter == null || filter.isEmpty() || filter.equals("unsolved")) {
            query +=
                    "UNION\n" +
                    "SELECT id, NULL FROM problem\n" +
                    "WHERE id NOT IN (SELECT problem_id FROM user_problem WHERE user_id = " + userId + ")\n" +
                    getTagsQuery("id", tags) + "\n" +
                    getInMindQuery("id", inMind) + "\n";
        }
        query += "LIMIT " + (to - from) + " OFFSET " + from;

        final List<Integer> ids = new ArrayList<Integer>();
        final Map<Integer, UserProblem> id2userProblem = new HashMap<Integer, UserProblem>();
        simpleJdbcTemplate.getJdbcOperations().query(query, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                ids.add(resultSet.getInt("problem_id"));
                id2userProblem.put(
                        resultSet.getInt("problem_id"),
                        new UserProblem((Integer)resultSet.getObject("attempts"))
                );
            }
        });

        for (Problem problem : problemDepot.getByIds(ids)) {
            id2userProblem.get(problem.getId()).setProblem(problem);
        }

        List<UserProblem> result = new ArrayList<UserProblem>();
        result.addAll(id2userProblem.values());

        return result;
    }

    private String getStatusQuery(String filter) {
        if ("unsolved".equals(filter)) {
            return "AND attempts <= 0";
        }
        if ("solved".equals(filter))  {
            return "AND attempts > 0";
        }
        if ("attempted".equals(filter)) {
            return "AND attempts < 0";
        }
        return "";
    }

    private String getInMindQuery(String field, boolean inMind) {
        if (inMind) {
            return "AND " + field + " IN (SELECT id FROM problem WHERE in_mind = true)";
        }
        return "";
    }

    private String getTagsQuery(String field, List<Tag> tags) {
        if (tags == null) {
            return "";
        }
        String result = "";
        for (Tag tag : tags) {
            result += "AND " + field + " IN (SELECT problem_id FROM problem_tag WHERE tag_id = " + tag.getId() + ")\n";
        }
        return result;
    }

    protected ParameterizedRowMapper<UserProblem> getRowMapper() {
        return new ParameterizedRowMapper<UserProblem>() {
            public UserProblem mapRow(ResultSet resultSet, int i) throws SQLException {
                return new UserProblem(
                        problemDepot.getById(resultSet.getInt("problem_id")),
                        resultSet.getInt("attempts")
                );
            }
        };
    }

    @Required
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
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