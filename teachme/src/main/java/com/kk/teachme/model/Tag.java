package com.kk.teachme.model;

/**
 * @author akonst
 */
public class Tag {
    int id;
    String name;

    public Tag(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
