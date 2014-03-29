package com.kk.teachme.db;

import com.kk.teachme.checker.Checker;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class CheckerDepot implements ApplicationContextAware {

    ApplicationContext applicationContext;
    JdbcTemplate jdbcTemplate;

    private Map<Integer, Checker> id2checker = new HashMap<>();

    public Checker getChecker(int id) {
        return id2checker.get(id);
    }

    public Map<Integer, Checker> getAllCheckers() {
        return id2checker;
    }

    public void init() {

        new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    final Map<Integer, Checker> map = new HashMap<>();
                    jdbcTemplate.query("select * from checker", (ResultSet resultSet) -> {
                        int id = resultSet.getInt("id");
                        String beanName = resultSet.getString("bean_name");
                        Checker checker = (Checker) applicationContext.getBean(beanName);
                        map.put(id, checker);
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
        }).start();
    }

    @Required
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        System.out.println("!!!");
        this.applicationContext = applicationContext;
    }

}
