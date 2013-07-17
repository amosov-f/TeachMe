package com.kk.teachme.db;

import com.kk.teachme.model.*;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
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
        final int update = jdbcTemplate.getJdbcOperations().update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                        PreparedStatement preparedStatement =
                                conn.prepareStatement("insert into problem (name,statement) values(?,?)"
                                        , Statement.RETURN_GENERATED_KEYS);
                        preparedStatement.setString(1, problem.getName());
                        preparedStatement.setString(2, problem.getStatement());
                        return preparedStatement;
                    }
                }, keyHolder);
        if (update > 0) {
            int id = keyHolder.getKey().intValue();
            problem.setId(id);

            for (Tag tag: problem.getTags()) {
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
            final List<Integer> query = jdbcTemplate.query("select tag_id from problem_tag where problem_id = ?", new ParameterizedRowMapper<Integer>() {
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

        return jdbcTemplate.query(
                "select * from problem inner join (select * from problem_tag where tag_id = ?) t on problem.id = t.problem_id",
                getProblemIdRowMapper("id"),
                tag.getId()
        );
    }

    public List<Problem> getAllProblems() {
        return jdbcTemplate.query("select * from problem", getProblemIdRowMapper("id"));
    }

    public List<Problem> getSolvedProblems(User user) {
        return jdbcTemplate.query("select problem_id from user_problem where user_id = ? and status_id = ?",
                getProblemIdRowMapper("problem_id"),
                user.getId(),
                statusDepot.getStatusId(Status.SOLVED)
        );
    }

    public boolean addTagToProblem(Problem problem, Tag tag) {
        //todo add some code to second sql (on exists) and remove first call
        System.out.println(problem + " " + tag);

        if (!jdbcTemplate.queryForList(
                "select * from problem_tag where problem_id = ? and tag_id = ?",
                problem.getId(),
                tag.getId()
        ).isEmpty()) {
            return false;
        }

        jdbcTemplate.update("insert into problem_tag values (?, ?)", problem.getId(), tag.getId());
        return true;
    }

    public void changeProblemStatement(Problem problem, String newStatement) {
        jdbcTemplate.update("update problem set statement = ? where id = ?", newStatement, problem.getId());
    }

    public int getProblemsByTagCount(Tag tag) {
        return jdbcTemplate.query(
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

        jdbcTemplate.update("delete from user_problem where problem_id = ?", id);
        jdbcTemplate.update("delete from problem_tag where problem_id = ?", id);
        jdbcTemplate.update("delete from solution where id = ?", id);
        jdbcTemplate.update("delete from problem where id = ?", id);

        return true;
    }

    @Override
    protected ParameterizedRowMapper<Problem> getRowMapper() {
        return new ParameterizedRowMapper<Problem>() {
            public Problem mapRow(ResultSet resultSet, int i) throws SQLException {
                return new Problem(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("statement")
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
