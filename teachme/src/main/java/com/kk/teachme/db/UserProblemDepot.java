package com.kk.teachme.db;

import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Status;
import com.kk.teachme.model.Tag;
import com.kk.teachme.model.UserProblem;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
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
    TagDepot tagDepot;

    JdbcTemplate jdbcTemplate;

    private void add(final int userId, final UserProblem userProblem) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                conn -> {
                    PreparedStatement preparedStatement = conn.prepareStatement(
                            "insert into user_problem (user_id, problem_id, attempts) values (?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    preparedStatement.setInt(1, userId);
                    preparedStatement.setInt(2, userProblem.getProblem().getId());
                    preparedStatement.setInt(3, userProblem.getRawAttempts());
                    return preparedStatement;
                },
                keyHolder
        );
    }

    private void alterObject(final int userId, final UserProblem userProblem) {
        jdbcTemplate.update(
                "update user_problem set attempts = ? where user_id = ? and problem_id = ?",
                userProblem.getRawAttempts(),
                userId,
                userProblem.getProblem().getId()
        );
    }

    public boolean addUserProblem(int userId, int problemId) {
        List<UserProblem> userProblems = jdbcTemplate.query(
                "SELECT problem_id, attempts FROM user_problem WHERE user_id = ? AND problem_id = ?",
                getRowMapper(),
                userId,
                problemId
        );
        if (userProblems.isEmpty()) {
            add(userId, new UserProblem(problemDepot.get(problemId), 0));
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
            add(userId, userProblem);
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

    public int getSolvedProblemsCount(int userId) {
        return jdbcTemplate.queryForInt("SELECT COUNT(*) FROM user_problem WHERE user_id = ? AND attempts > 0", userId);
    }

    public List<UserProblem> getByFilters(int userId, List<Tag> tags, String filter, boolean inMind, int from, int to) {
        String query = filtersQuery(userId, tags, filter, inMind) + limitQuery(from, to);

        List<Integer> ids = new ArrayList<>();
        Map<Integer, UserProblem> id2userProblem = new HashMap<>();
        jdbcTemplate.query(query, (RowCallbackHandler) resultSet -> {
            int problemId = resultSet.getInt("problem_id");
            ids.add(problemId);
            id2userProblem.put(problemId, new UserProblem(resultSet.getInt("attempts")));
        });

        for (Problem problem : problemDepot.get(ids)) {
            id2userProblem.get(problem.getId()).setProblem(problem);
        }

        return new ArrayList<>(id2userProblem.values());
    }

    private static String statusQuery(String filter) {
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

    private static String inMindQuery(String field, boolean inMind) {
        if (inMind) {
            return "AND " + field + " IN (SELECT id FROM problem WHERE in_mind = true)";
        }
        return "";
    }

    private static String filtersQuery(int userId, List<Tag> tags, String filter, boolean inMind) {
        String query =
                "(SELECT problem_id, attempts FROM user_problem\n" +
                        "WHERE user_id = " + userId + "\n" +
                        tagsQuery("problem_id", tags) + "\n" +
                        statusQuery(filter) + "\n" +
                        inMindQuery("problem_id", inMind) + ")\n";
        if (filter == null || filter.isEmpty() || filter.equals("unsolved")) {
            query +=
                    "UNION\n" +
                            "(SELECT id, NULL FROM problem\n" +
                            "WHERE id NOT IN (SELECT problem_id FROM user_problem WHERE user_id = " + userId + ")\n" +
                            tagsQuery("id", tags) + "\n" +
                            inMindQuery("id", inMind) + ")\n";
        }
        return query;
    }

    public static String limitQuery(int from, int to) {
        return "LIMIT " + (to - from) + " OFFSET " + from + "\n";
    }


    private static String tagsQuery(String field, List<Tag> tags) {
        if (tags == null) {
            return "";
        }
        String result = "";
        for (Tag tag : tags) {
            result += "AND " + field + " IN (SELECT problem_id FROM problem_tag WHERE tag_id = " + tag.getId() + ")\n";
        }
        return result;
    }

    public UserProblem getEasierProblem(int userId, int problemId, List<Tag> tags, boolean inMind) {
        int complexity = problemDepot.get(problemId).getComplexity();

        String query = "SELECT * FROM (" + filtersQuery(userId, tags, "unsolved", inMind) + ") AS filter\n";
        query += "WHERE problem_id IN (SELECT id FROM problem WHERE complexity < " + complexity + ")\n";
        query += limitQuery(0, 1);

        try {
            return jdbcTemplate.queryForObject(query, getRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UserProblem getSimilarProblem(int userId, int problemId, List<Tag> tags, boolean inMind) {
        int complexity = problemDepot.get(problemId).getComplexity();

        String query = "SELECT * FROM (" + filtersQuery(userId, tags, "unsolved", inMind) + ") AS filter\n";
        query += "WHERE problem_id IN (SELECT id FROM problem WHERE complexity = " + complexity + " AND id <> " + problemId + ")\n";
        query += limitQuery(0, 1);

        try {
            return jdbcTemplate.queryForObject(query, getRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UserProblem getHarderProblem(int userId, int problemId, List<Tag> tags, boolean inMind) {
        int complexity = problemDepot.get(problemId).getComplexity();

        String query = "SELECT * FROM (" + filtersQuery(userId, tags, "unsolved", inMind) + ") AS filter\n";
        query += "WHERE problem_id IN (SELECT id FROM problem WHERE complexity > " + complexity + ")\n";
        query += limitQuery(0, 1);

        try {
            return jdbcTemplate.queryForObject(query, getRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    protected RowMapper<UserProblem> getRowMapper() {
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
    public void setTagDepot(TagDepot tagDepot) {
        this.tagDepot = tagDepot;
    }

}