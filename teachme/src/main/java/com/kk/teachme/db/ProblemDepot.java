package com.kk.teachme.db;

import com.kk.teachme.model.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                        PreparedStatement preparedStatement =
                                conn.prepareStatement("insert into problem (name,statement,figures) values(?,?,?)"
                                        , Statement.RETURN_GENERATED_KEYS);
                        preparedStatement.setString(1, problem.getName());
                        preparedStatement.setString(2, problem.getStatement());
                        preparedStatement.setString(3, problem.getFiguresString());

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

    @Override
    public Problem getById(int id) {
        final Problem byId = super.getById(id);
        if (byId != null) {
            final List<Integer> query = simpleJdbcTemplate.query("select tag_id from problem_tag where problem_id = ?", new ParameterizedRowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getInt(1);
                }
            }, byId.getId());

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

    public List<Problem> getSolvedProblems(User user) {
        return simpleJdbcTemplate.query("select problem_id from user_problem where user_id = ? and status_id = ?",
                getProblemIdRowMapper("problem_id"),
                user.getId(),
                statusDepot.getStatusId(Status.SOLVED)
        );
    }

    public List<Problem> getAllProblems() {
        return simpleJdbcTemplate.query("select * from problem", getProblemIdRowMapper("id"));
    }

    public void setById(int id, Problem problem) {
        //if (getById(id) == null) ... suppose, that problem with @id exists

        simpleJdbcTemplate.update("delete from problem_tag where problem_id = ?", id);

        simpleJdbcTemplate.update(
                "update problem set name = ?, statement = ?, figures = ? where id = ?",
                problem.getName(),
                problem.getStatement(),
                problem.getFiguresString(),
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
        return getById(id) == null ? false : true;
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
                        Problem.parseFiguresString(resultSet.getString("figures"))
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
