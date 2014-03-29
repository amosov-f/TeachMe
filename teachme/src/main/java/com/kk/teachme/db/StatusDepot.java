package com.kk.teachme.db;

import com.kk.teachme.model.Status;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class StatusDepot {

    JdbcTemplate jdbcTemplate;

    private Map<Integer, Status> id2status = new HashMap<>();

    public void init() {
        new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    final Map<Integer, Status> map = new HashMap<>();
                    jdbcTemplate.query(
                            "select * from problem_status",
                            (ResultSet resultSet) -> {
                                int id = resultSet.getInt("id");
                                Status status = Status.valueOf(resultSet.getString("status").toUpperCase());
                                map.put(id, status);
                            }
                    );
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
        }).start();
    }

    public int getStatusId(Status status) {
        for (Map.Entry<Integer, Status> entry : id2status.entrySet()) {
            if (entry.getValue().equals(status)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    @Required
    public void setJdbcTemplate(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
