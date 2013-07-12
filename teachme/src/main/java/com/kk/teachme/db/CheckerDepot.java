package com.kk.teachme.db;


import com.kk.teachme.checker.Checker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CheckerDepot {
    @Autowired
    ApplicationContext applicationContext;
    SimpleJdbcTemplate jdbcTemplate;

    private Map<Integer, Checker> id2checker = new HashMap<Integer, Checker>();

    public Checker getChecker(int id) {
        return id2checker.get(id);
    }

    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        final Map<Integer, Checker> map = new HashMap<Integer, Checker>();
                        jdbcTemplate.getJdbcOperations().query("select * from checker", new RowCallbackHandler() {
                            @Override
                            public void processRow(ResultSet resultSet) throws SQLException {
                                int id = resultSet.getInt("id");
                                String beanName = resultSet.getString("bean_name");
                                Checker checker = (Checker) applicationContext.getBean(beanName);
                                map.put(id, checker);
                            }
                        });
                        id2checker = map;
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

    @Required
    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
