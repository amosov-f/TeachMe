package com.kk.teachme.db;

import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Tag;
import org.springframework.beans.factory.annotation.Required;
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

public class ProblemDepot extends AbstractDepot<Problem> {

    private TagDepot tagDepot;

    public int add(final Problem problem) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final int update = jdbcTemplate.update(
                conn -> {
                    PreparedStatement preparedStatement = conn.prepareStatement(
                            "insert into problem (name, statement, figures, complexity, in_mind) values (?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    preparedStatement.setString(1, problem.getName());
                    preparedStatement.setString(2, problem.getStatement());
                    preparedStatement.setString(3, problem.getFiguresString());
                    preparedStatement.setInt(4, problem.getComplexity());
                    preparedStatement.setBoolean(5, problem.isInMind());

                    return preparedStatement;
                },
                keyHolder
        );
        if (update > 0) {
            int id = keyHolder.getKey().intValue();
            problem.setId(id);

            for (Tag tag : problem.getTags()) {
                addTagToProblem(problem, tag);
            }

            return id;
        }
        return -1;
    }

    @Override
    public Problem get(int id) {
        final Problem byId = super.get(id);
        if (byId != null) {
            final List<Integer> query = jdbcTemplate.query(
                    "select tag_id from problem_tag where problem_id = ?",
                    (resultSet, i) -> resultSet.getInt(1),
                    byId.getId()
            );

            final List<Tag> tags = new ArrayList<>();
            for (Integer integer : query) {
                final Tag tag = tagDepot.getCached(integer);
                if (tag != null) {
                    tags.add(tag);
                }
            }
            byId.addTags(tags);
        }
        return byId;
    }

    private String getIdsQuery(String table, String field, List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return "";
        }

        String query = "SELECT * FROM " + table + " WHERE " + field + " IN (";
        for (int i = 0; i < ids.size() - 1; ++i) {
            query += ids.get(i) + ", ";
        }
        query += ids.get(ids.size() - 1) + ")\n";

        return query;
    }

    public List<Problem> get(List<Integer> ids) {
        String query = getIdsQuery("problem", "id", ids);
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }

        List<Problem> problems = jdbcTemplate.query(query, getRowMapper());

        final Map<Integer, Problem> id2problem = new HashMap<>();
        for (Problem problem : problems) {
            id2problem.put(problem.getId(), problem);
        }

        jdbcTemplate.query(
                getIdsQuery("problem_tag", "problem_id", ids),
                (RowCallbackHandler) resultSet -> id2problem
                        .get(resultSet.getInt("problem_id"))
                        .addTag(tagDepot.getCached(resultSet.getInt("tag_id")))
        );

        List<Problem> result = new ArrayList<>();
        result.addAll(id2problem.values());

        return result;
    }

    public List<Problem> getAllProblems() {
        return jdbcTemplate.query("select * from problem", getProblemIdRowMapper("id"));
    }

    public int getProblemsCount() {
        return jdbcTemplate.queryForInt("SELECT COUNT(*) FROM problem");
    }

    public void setById(int id, Problem problem) {
        //if (get(id) == null) ... suppose, that problem with @id exists

        jdbcTemplate.update("delete from problem_tag where problem_id = ?", id);

        jdbcTemplate.update(
                "update problem set name = ?, statement = ?, figures = ?, complexity = ?, in_mind = ? where id = ?",
                problem.getName(),
                problem.getStatement(),
                problem.getFiguresString(),
                problem.getComplexity(),
                problem.isInMind(),
                id
        );

        problem.setId(id);
        for (Tag tag : problem.getTags()) {
            addTagToProblem(problem, tag);
        }
    }

    public void addTagToProblem(Problem problem, Tag tag) {
        jdbcTemplate.update("insert ignore into problem_tag values (?, ?)", problem.getId(), tag.getId());
    }

    public boolean deleteById(int id) {
        if (!contains(id)) {
            return false;
        }

        jdbcTemplate.update("delete from user_problem where problem_id = ?", id);
        jdbcTemplate.update("delete from problem_tag where problem_id = ?", id);
        jdbcTemplate.update("delete from solution where id = ?", id);
        jdbcTemplate.update("delete from problem where id = ?", id);

        return true;
    }


    public void deleteAllProblems() {
        jdbcTemplate.update("delete from user_problem where problem_id >= 1");
        jdbcTemplate.update("delete from problem_tag where problem_id >= 1");
        jdbcTemplate.update("delete from solution where id >= 1");
        jdbcTemplate.update("delete from problem where id >= 1");
    }

    @Override
    protected RowMapper<Problem> getRowMapper() {
        return (resultSet, i) -> new Problem(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("statement"),
                Problem.parseFiguresString(resultSet.getString("figures")),
                resultSet.getInt("complexity"),
                resultSet.getBoolean("in_mind")
        );
    }

    @Override
    protected String getTableName() {
        return "problem";
    }

    protected RowMapper<Problem> getProblemIdRowMapper(final String idTitle) {
        return (resultSet, i) -> get(resultSet.getInt(idTitle));
    }

    @Required
    public void setTagDepot(TagDepot tagDepot) {
        this.tagDepot = tagDepot;
    }

}
