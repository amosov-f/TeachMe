package com.kk.teachme.db;

import com.kk.teachme.model.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author akonst
 */

public class ProblemDepot extends AbstractDepot<Problem> {

    private TagDepot tagDepot;
    private StatusDepot statusDepot;

    @Override
    public int addObject(final Problem problem) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final int update = simpleJdbcTemplate.getJdbcOperations().update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                        PreparedStatement preparedStatement = conn.prepareStatement(
                                "insert into problem (name, statement, figures, complexity, in_mind) values(?,?,?,?,?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        preparedStatement.setString(1, problem.getName());
                        preparedStatement.setString(2, problem.getStatement());
                        preparedStatement.setString(3, problem.getFiguresString());
                        preparedStatement.setInt(4, problem.getComplexity());
                        preparedStatement.setBoolean(5, problem.isInMind());

                        return preparedStatement;
                    }
                }, keyHolder);
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

    private Problem addTags(Problem problem, List<Integer> tagIds) {
        final List<Tag> tags = new ArrayList<Tag>();
        for (Integer tagId : tagIds) {
            final Tag tag = tagDepot.getCached(tagId);
            if (tag != null) {
                tags.add(tag);
            }
        }
        problem.addTags(tags);
        return problem;
    }

    @Override
    public Problem getById(int id) {
        final Problem byId = super.getById(id);
        if (byId != null) {
            final List<Integer> query = simpleJdbcTemplate.query(
                    "select tag_id from problem_tag where problem_id = ?",
                    new ParameterizedRowMapper<Integer>() {
                        @Override
                        public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                            return resultSet.getInt(1);
                        }
                    },
                    byId.getId()
            );

            final List<Tag> tags = new ArrayList<Tag>();
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

    public List<Problem> getByIds(List<Integer> ids) {
        String query = getIdsQuery("problem", "id", ids);
        if (query == null || query.isEmpty()) {
            return new ArrayList<Problem>();
        }

        List<Problem> problems = simpleJdbcTemplate.query(query, getRowMapper());

        final Map<Integer, Problem> id2problem = new HashMap<Integer, Problem>();
        for (Problem problem : problems) {
            id2problem.put(problem.getId(), problem);
        }

        simpleJdbcTemplate.getJdbcOperations().query(
                getIdsQuery("problem_tag", "problem_id", ids),
                new RowCallbackHandler() {
                        @Override
                        public void processRow(ResultSet resultSet) throws SQLException {
                            id2problem.get(resultSet.getInt("problem_id")).addTag(tagDepot.getCached(
                                    resultSet.getInt("tag_id"))
                            );
                        }
                }
        );

        List<Problem> result = new ArrayList<Problem>();
        result.addAll(id2problem.values());

        return result;
    }


    public List<Problem> getByTag(Tag tag) {
        if (tag == null) {
            return null;
        }

        return simpleJdbcTemplate.query(
                "select * from problem inner join (select * from problem_tag where tag_id = ?) t on problem.id = t.problem_id",
                getProblemIdRowMapper("id"),
                tag.getId()
        );
    }

    public List<Problem> getByTagList(List<Tag> tagList) {
        if (tagList == null || tagList.size() == 0) {
            return null;
        }

        List<Problem> problemList = new ArrayList<Problem>();

        for (Problem problem : getByTag(tagList.get(0))) {
            if (problem.getTags().containsAll(tagList)) {
                problemList.add(problem);
            }
        }

        return problemList;
    }

    public  List<Problem> getInMindProblems() {
        return simpleJdbcTemplate.query("select * from problem where in_mind = ?", getProblemIdRowMapper("id"), true);
    }

    public List<Problem> getAllProblems() {
        return simpleJdbcTemplate.query("select * from problem", getProblemIdRowMapper("id"));
    }

    public void setById(int id, Problem problem) {
        //if (getById(id) == null) ... suppose, that problem with @id exists

        simpleJdbcTemplate.update("delete from problem_tag where problem_id = ?", id);

        simpleJdbcTemplate.update(
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
        simpleJdbcTemplate.update("insert ignore into problem_tag values (?, ?)", problem.getId(), tag.getId());
    }

    public void addFigureToProblem(Problem problem, String figure) {
        simpleJdbcTemplate.update("insert ignore into problem_figure values (?, ?)", problem.getId(), figure);
    }

    public void changeProblemStatement(Problem problem, String newStatement) {
        simpleJdbcTemplate.update("update problem set statement = ? where id = ?", newStatement, problem.getId());
    }

    public int getProblemsByTagCount(Tag tag) {
        return simpleJdbcTemplate.query(
                "select * from problem_tag where tag_id = ?",
                getProblemIdRowMapper("problem_id"),
                tag.getId()
        ).size();
    }

    public boolean contains(int id) {
        return getById(id) != null;
    }

    public boolean deleteById(int id) {
        if (!contains(id)) {
            return false;
        }

        simpleJdbcTemplate.update("delete from user_problem where problem_id = ?", id);
        simpleJdbcTemplate.update("delete from problem_tag where problem_id = ?", id);
        simpleJdbcTemplate.update("delete from solution where id = ?", id);
        simpleJdbcTemplate.update("delete from problem where id = ?", id);

        return true;
    }


    public void deleteAllProblems() {
        simpleJdbcTemplate.update("delete from user_problem where problem_id >= 1");
        simpleJdbcTemplate.update("delete from problem_tag where problem_id >= 1");
        simpleJdbcTemplate.update("delete from solution where id >= 1");
        simpleJdbcTemplate.update("delete from problem where id >= 1");
    }

    @Override
    protected ParameterizedRowMapper<Problem> getRowMapper() {
        return new ParameterizedRowMapper<Problem>() {
            public Problem mapRow(ResultSet resultSet, int i) throws SQLException {
                return new Problem(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("statement"),
                        Problem.parseFiguresString(resultSet.getString("figures")),
                        resultSet.getInt("complexity"),
                        resultSet.getBoolean("in_mind")
                );
            }
        };
    }

    protected ParameterizedRowMapper<Problem> getProblemIdRowMapper(final String idTitle) {
        return new ParameterizedRowMapper<Problem>() {
            public Problem mapRow(ResultSet resultSet, int i) throws SQLException {
                return getById(resultSet.getInt(idTitle));
            }
        };
    }

    @Override
    protected String getQueryForOne() {
        return "select * from problem where id = ?";
    }

    @Required
    public void setTagDepot(TagDepot tagDepot) {
        this.tagDepot = tagDepot;
    }

    @Required
    public void setStatusDepot(StatusDepot statusDepot) {
        this.statusDepot = statusDepot;
    }

}
