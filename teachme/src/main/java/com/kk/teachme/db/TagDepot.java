package com.kk.teachme.db;

import com.kk.teachme.model.Tag;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.*;

import static java.lang.Math.min;

/**
 * User: akonst
 */

public class TagDepot extends AbstractDepot<Tag> {

    Map<Integer, Tag> id2tag = new HashMap<Integer, Tag>();
    Map<String, Tag> name2tag = new HashMap<String, Tag>();

    public Tag getCached(int id) {
        Tag byId = id2tag.get(id);
        if (byId == null) {
            byId = getById(id);
            id2tag.put(id, byId);
        }
        return byId;
    }

    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        List<Tag> tags = getDataBaseTags();
                        Map<Integer, Tag> id2tag = new HashMap<Integer, Tag>();
                        Map<String, Tag> name2tag = new HashMap<String, Tag>();
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
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
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
                final int update = simpleJdbcTemplate.getJdbcOperations().update(
                        new PreparedStatementCreator() {
                            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                                PreparedStatement preparedStatement =
                                        conn.prepareStatement("insert into tag (name)values(?)"
                                                , Statement.RETURN_GENERATED_KEYS);
                                preparedStatement.setString(1, name);
                                return preparedStatement;
                            }
                        }, keyHolder);
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
    public int addObject(Tag tag) {
        throw new RuntimeException("Miss it");
    }

    public List<Tag> getTagList(int numTags) {
        final ArrayList<Tag> tags = new ArrayList<Tag>(id2tag.values());
        return tags.subList(0, min(tags.size(), numTags));
    }

    public List<Tag> getAllTags() {
        List<Tag> result = new ArrayList<Tag>();
        result.addAll(id2tag.values());
        return result;
    }

    public Tag getByName(String name) {
        return name2tag.get(name);
    }

    public void changeTagName(Tag tag, String newName) {
        id2tag.remove(tag.getId());
        name2tag.remove(tag.getName());

        tag.setName(newName);

        id2tag.put(tag.getId(), tag);
        name2tag.put(tag.getName(), tag);

        simpleJdbcTemplate.update("update tag set name = ? where id = ?", tag.getName(), tag.getId());
    }

    private List<Tag> getByNameFromDB(String name) {
        return simpleJdbcTemplate.query("select * from tag where name = ?",
                getRowMapper(),
                name);
    }

    private List<Tag> getDataBaseTags() {
        return new ArrayList<Tag>(simpleJdbcTemplate.query("select * from tag", getRowMapper()));
    }

    @Override
    protected ParameterizedRowMapper<Tag> getRowMapper() {
        return new ParameterizedRowMapper<Tag>() {
            @Override
            public Tag mapRow(ResultSet resultSet, int i) throws SQLException {
                return new Tag(resultSet.getInt("id"), resultSet.getString("name"));
            }
        };
    }

    @Override
    protected String getQueryForOne() {
        return "select * from tag where id = ?";
    }

}
