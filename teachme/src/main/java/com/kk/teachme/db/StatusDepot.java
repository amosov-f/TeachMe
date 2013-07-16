package com.kk.teachme.db;

import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Status;
import com.kk.teachme.model.User;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusDepot {

    SimpleJdbcTemplate jdbcTemplate;

    private Map<Integer, Status> id2status = new HashMap<Integer, Status>();

    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        final Map<Integer, Status> map = new HashMap<Integer, Status>();
                        jdbcTemplate.getJdbcOperations().query("select * from problem_status", new RowCallbackHandler() {
                            @Override
                            public void processRow(ResultSet resultSet) throws SQLException {
                                int id = resultSet.getInt("id");
                                Status status = Status.valueOf(resultSet.getString("status").toUpperCase());
                                map.put(id, status);
                            }
                        });
                        id2status = map;

                        System.out.println("Loaded");
                    } catch (Throwable tr) {
                        tr.printStackTrace();
                    } finally {
                        try {
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public Status getStatus(User user, Problem problem) {
        List<Status> statuses = jdbcTemplate.query(
                "select status_id from user_problem where user_id = ? and problem_id = ?",
                getRowMapper(),
                user.getId(),
                problem.getId()
        );

        if (statuses.isEmpty()) {
            return Status.NEW;
        }

        return statuses.get(0);
    }

    public int getStatusId(Status status) {
        for (Map.Entry<Integer, Status> entry : id2status.entrySet()) {
            if (entry.getValue().equals(status)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    public boolean setStatus(User user, Problem problem, Status status) {
        jdbcTemplate.update(
                "update user_problem set user_id = ? and problem_id = ? and status_id = ?",
                user.getId(),
                problem.getId(),
                getStatusId(status)
        );
        return false;
    }

    protected ParameterizedRowMapper<Status> getRowMapper() {
        return new ParameterizedRowMapper<Status>() {
            @Override
            public Status mapRow(ResultSet resultSet, int i) throws SQLException {
                return id2status.get(resultSet.getInt("status_id"));
            }
        };
    }

    @Required
    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
