package com.kk.teachme.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author akonst
 */
public class Problem {
    private int id;
    private String statement;
    private String name;

    private List<Tag> tags = new ArrayList<Tag>();

    public Problem(String name, String statement) {
        this(-1, name, statement);
    }

    public Problem(int id, String name, String statement) {
        this.id = id;
        this.name = name;
        this.statement = statement;
    }

    public int getId() {
        return id;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public String getStatement() {
        return statement;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addTags(List<Tag> list) {
        tags.addAll(list);
    }
}
