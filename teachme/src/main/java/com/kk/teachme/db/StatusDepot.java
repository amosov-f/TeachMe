package com.kk.teachme.db;

import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Status;
import com.kk.teachme.model.User;
import org.springframework.beans.factory.annotation.Required;
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
                        List<Map.Entry<Integer, Status>> statuses = jdbcTemplate.query(
                                "select * from problem_status",
                                getRowMapper()
                        );
                        Map<Integer, Status> id2status = new HashMap<Integer, Status>();
                        for (Map.Entry<Integer, Status> entry : statuses) {
                            id2status.put(entry.getKey(), entry.getValue());
                        }
                        StatusDepot.this.id2status = id2status;

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

    public boolean setStatus(User user, Problem problem, Status status) {
        jdbcTemplate.update(
                "insert into user_problem values (?, ?, ?)",
                user.getId(),
                problem.getId(),
                getStatusId(status)
        );

        return false;
    }

    public Status getStatus(User user, Problem problem) {
        int status_id = jdbcTemplate.queryForInt(
                "select status_id from user_problem where user_id = ? and problem_id = ?",
                user.getId(),
                problem.getId()
        );

        return id2status.get(status_id);
    }

    public int getStatusId(Status status) {
        for (Map.Entry<Integer, Status> entry : id2status.entrySet()) {
            if (entry.getValue().equals(status)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    private ParameterizedRowMapper<Map.Entry<Integer, Status>> getRowMapper() {
        return new ParameterizedRowMapper<Map.Entry<Integer, Status>>() {
            public Map.Entry<Integer, Status> mapRow(ResultSet resultSet, int i) throws SQLException {
                return new AbstractMap.SimpleEntry(
                        resultSet.getInt("id"),
                        Status.valueOf(resultSet.getString("status").toUpperCase())
                );
            }
        };
    }

    @Required
    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
