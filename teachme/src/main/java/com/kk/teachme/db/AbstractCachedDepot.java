package com.kk.teachme.db;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCachedDepot<T> extends AbstractDepot<T> {

    protected Map<Integer, T> id2object = new HashMap<>();

    public AbstractCachedDepot() {
        new Thread(() -> {
            while (!Thread.interrupted()) {
                Map<Integer, T> map = new HashMap<>();
                jdbcTemplate.query("SELECT * FROM " + getTableName(), (ResultSet resultSet) -> {
                    int id = resultSet.getInt("id");
                    T object = getRowMapper().mapRow(resultSet, -1);
                    map.put(id, object);
                });
                id2object = map;

                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    public T get(int id) {
        return id2object.get(id);
    }
}
