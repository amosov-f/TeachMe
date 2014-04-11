package com.kk.teachme.db;

import com.kk.teachme.model.Tag;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagDepot extends AbstractDepot<Tag> {

    private Map<Integer, Tag> id2tag = new HashMap<>();
    private Map<String, Tag> name2tag = new HashMap<>();

    public Tag getCached(int id) {
        Tag tag = id2tag.get(id);
        if (tag == null) {
            tag = get(id);
            id2tag.put(id, tag);
        }
        return tag;
    }

    public void init() {
        new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    List<Tag> tags = getDataBaseTags();
                    Map<Integer, Tag> id2tag = new HashMap<>();
                    Map<String, Tag> name2tag = new HashMap<>();
                    for (Tag tag : tags) {
                        id2tag.put(tag.getId(), tag);
                        name2tag.put(tag.getName(), tag);
                    }
                    TagDepot.this.id2tag = id2tag;
                    TagDepot.this.name2tag = name2tag;
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

    public Tag createIfNotExist(final String name) {
        Tag tag = name2tag.get(name);
        if (tag == null) {
            List<Tag> tagList = getByNameFromDB(name);
            if (tagList.size() == 0) {
                System.out.println("Adding new tag: " + name);
                final KeyHolder keyHolder = new GeneratedKeyHolder();
                final int update = jdbcTemplate.update(
                        conn -> {
                            PreparedStatement preparedStatement = conn.prepareStatement(
                                    "insert into tag (name) values (?)",
                                    Statement.RETURN_GENERATED_KEYS
                            );
                            preparedStatement.setString(1, name);
                            return preparedStatement;
                        },
                        keyHolder
                );
                if (update > 0) {
                    tag = new Tag(keyHolder.getKey().intValue(), name);
                    id2tag.put(tag.getId(), tag);
                    name2tag.put(tag.getName(), tag);
                }
            } else {
                id2tag.put(tagList.get(0).getId(), tagList.get(0));
                name2tag.put(tagList.get(0).getName(), tagList.get(0));
            }
        }
        return tag;
    }

    @Override
    public int add(Tag tag) {
        throw new RuntimeException("Miss it");
    }

    public List<Tag> getAllTags() {
        List<Tag> result = new ArrayList<>();
        result.addAll(id2tag.values());
        return result;
    }

    public Tag getByName(String name) {
        return name2tag.get(name);
    }

    private List<Tag> getByNameFromDB(String name) {
        return jdbcTemplate.query(
                "select * from tag where name = ?",
                getRowMapper(),
                name
        );
    }

    private List<Tag> getDataBaseTags() {
        return new ArrayList<>(jdbcTemplate.query("select * from tag", getRowMapper()));
    }

    @Override
    protected RowMapper<Tag> getRowMapper() {
        return (resultSet, i) -> new Tag(resultSet.getInt("id"), resultSet.getString("name"));
    }

    @Override
    protected String getTableName() {
        return "tag";
    }

}
