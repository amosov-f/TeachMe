package com.kk.teachme.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author akonst
 */
public class Problem {
    private int id;
    private String situation;

    private List<Tag> tags = new ArrayList<Tag>();

    public Problem(int id, String situation) {
        this.id = id;
        this.situation = situation;
    }

    public int getId() {
        return id;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public String getSituation() {
        return situation;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addTags(List<Tag> list) {
        tags.addAll(list);
    }
}
