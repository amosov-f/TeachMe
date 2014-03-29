package com.kk.teachme.db;

import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Status;
import com.kk.teachme.model.Tag;
import com.kk.teachme.model.UserProblem;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProblemDepot {

    UserDepot userDepot;
    ProblemDepot problemDepot;
    StatusDepot statusDepot;
    TagDepot tagDepot;

    JdbcTemplate jdbcTemplate;

    private void addObject(final int userId, final UserProblem userProblem) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                conn -> {
                    PreparedStatement preparedStatement = conn.prepareStatement(
                            "insert into user_problem (user_id, problem_id, status_id, attempts) values(?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    preparedStatement.setInt(1, userId);
                    preparedStatement.setInt(2, userProblem.getProblem().getId());
                    preparedStatement.setInt(3, statusDepot.getStatusId(userProblem.getStatus()));
                    preparedStatement.setInt(4, userProblem.getRawAttempts());
                    return preparedStatement;
                },
                keyHolder
        );
    }

    private void alterObject(final int userId, final UserProblem userProblem) {
        jdbcTemplate.update(
                "update user_problem set status_id = ?, attempts = ? where user_id = ? and problem_id = ?",
                statusDepot.getStatusId(userProblem.getStatus()),
                userProblem.getRawAttempts(),
                userId,
                userProblem.getProblem().getId()
        );
    }

    public boolean addUserProblem(int userId, int problemId) {
        List<UserProblem> userProblems = jdbcTemplate.query("select problem_id, attempts " +
                "from user_problem where user_id = ? and problem_id = ?",
                getRowMapper(),
                userId,
                problemId
        );
        if (userProblems.isEmpty()) {
            addObject(userId, new UserProblem(problemDepot.get(problemId), 0));
            return true;
        }
        return false;
    }

    public UserProblem getByIds(int userId, int problemId) {
        List<UserProblem> userProblems = jdbcTemplate.query(
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

    public UserProblem attempt(int userId, int problemId, boolean solved) {

        List<UserProblem> userProblems = jdbcTemplate.query("select problem_id, attempts " +
                "from user_problem where user_id = ? and problem_id = ?",
                getRowMapper(),
                userId,
                problemId
        );

        UserProblem userProblem;
        if (userProblems.isEmpty()) {
            userProblem = new UserProblem(problemDepot.get(problemId));
            addObject(userId, userProblem);
        } else {
            userProblem = userProblems.get(0);
        }

        userProblem.attempt(solved);
        alterObject(userId, userProblem);

        return userProblem;

    }

    public Status getStatus(int userId, int problemId) {
        List<UserProblem> userProblems = jdbcTemplate.query("select problem_id, attempts " +
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

        List<UserProblem> userProblems = jdbcTemplate.query(
                "select problem_id, attempts from user_problem where user_id = ?",
                getRowMapper(),
                userId
        );
        List<Problem> allProblems = problemDepot.getAllProblems();

        List<UserProblem> allUserProblems = new ArrayList<>();

        for (Problem problem : allProblems) {
            boolean flag = false;
            for (UserProblem userProblem : userProblems) {
                if (userProblem.getProblem().getId() == problem.getId()) {
                    allUserProblems.add(new UserProblem(problem, userProblem.getRawAttempts()));
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                allUserProblems.add(new UserProblem(problem));
            }
        }

        return allUserProblems;
    }


    public List<UserProblem> getUnsolvedProblems(int userId) {
        List<UserProblem> userProblems = jdbcTemplate.query(
                "select problem_id, attempts from user_problem where user_id = ?",
                getRowMapper(),
                userId
        );
        List<Problem> allProblems = problemDepot.getAllProblems();

        List<UserProblem> unsolvedUserProblems = new ArrayList<>();

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
            if (!flag) {
                unsolvedUserProblems.add(new UserProblem(problem));
            }
        }

        return unsolvedUserProblems;
    }

    public List<UserProblem> getSolvedProblems(int userId) {
        return jdbcTemplate.query("select problem_id, attempts " +
                "from user_problem where user_id = ? and attempts > 0",
                getRowMapper(),
                userId
        );
    }

    public List<UserProblem> getReadProblems(int userId) {
        return jdbcTemplate.query("select problem_id, attempts " +
                "from user_problem where user_id = ? and attempts <= 0",
                getRowMapper(),
                userId
        );
    }

    public List<UserProblem> getAttemptedProblems(int userId) {
        return jdbcTemplate.query("select problem_id, attempts " +
                "from user_problem where user_id = ? and attempts < 0",
                getRowMapper(),
                userId
        );
    }

    public List<UserProblem> getByTag(int userId, Tag tag) {

        List<UserProblem> userProblems = jdbcTemplate.query("select up.problem_id, up.attempts " +
                "from user_problem up inner join problem_tag pt on pt.problem_id = up.problem_id " +
                "where up.user_id = ? and pt.tag_id = ?",
                getRowMapper(),
                userId,
                tag.getId());
        List<Problem> problems = problemDepot.getByTag(tag);

        List<UserProblem> resultUserProblems = new ArrayList<>();

        for (Problem problem : problems) {
            boolean flag = false;
            for (UserProblem userProblem : userProblems) {
                if (userProblem.getProblem().getId() == problem.getId()) {
                    resultUserProblems.add(new UserProblem(problem, userProblem.getRawAttempts()));
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                resultUserProblems.add(new UserProblem(problem));
            }
        }

        return resultUserProblems;
    }

    public List<UserProblem> getByTagList(int userId, List<Tag> tags) {

        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }

        List<UserProblem> userProblemsBy1 = jdbcTemplate.query(
                "select up.problem_id, up.attempts " +
                "from user_problem up inner join problem_tag pt on pt.problem_id = up.problem_id " +
                "where up.user_id = ? and pt.tag_id = ?",
                getRowMapper(),
                userId,
                tags.get(0).getId()
        );
        List<Problem> problems = problemDepot.getByTagList(tags);

        List<UserProblem> userProblems = new ArrayList<>();
        for (UserProblem userProblem : userProblemsBy1) {
            if (userProblem.getProblem().getTags().containsAll(tags)) {
                userProblems.add(userProblem);
            }
        }

        List<UserProblem> resultUserProblems = new ArrayList<>();

        for (Problem problem : problems) {
            boolean flag = false;
            for (UserProblem userProblem : userProblems) {
                if (userProblem.getProblem().getId() == problem.getId()) {
                    resultUserProblems.add(new UserProblem(problem, userProblem.getRawAttempts()));
                    flag = true;
                    break;
                }
            }
            if (!flag) {
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

        final List<Integer> ids = new ArrayList<>();
        final Map<Integer, UserProblem> id2userProblem = new HashMap<>();
        jdbcTemplate.query(query, (RowCallbackHandler) resultSet -> {
            ids.add(resultSet.getInt("problem_id"));
            id2userProblem.put(
                    resultSet.getInt("problem_id"),
                    new UserProblem((Integer)resultSet.getObject("attempts"))
            );
        });

        for (Problem problem : problemDepot.getByIds(ids)) {
            id2userProblem.get(problem.getId()).setProblem(problem);
        }

        List<UserProblem> result = new ArrayList<>();
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
        return (resultSet, i) -> new UserProblem(
                problemDepot.get(resultSet.getInt("problem_id")),
                resultSet.getInt("attempts")
        );
    }

    @Required
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
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