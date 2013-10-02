package com.kk.teachme.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author akonst
 */
public class Problem {
    private int id = -1;
    private String name = "";
    private String statement = "";
    private List<String> figures = new ArrayList<String>();
    private List<Tag> tags = new ArrayList<Tag>();
    private int complexity = 1;
    private boolean inMind = false;

    public Problem() {
        this(-1, "", "", new ArrayList<String>(), 1, false, new ArrayList<Tag>());
    }

    public Problem(int id, String name, String statement, List<String> figures, int complexity, boolean inMind) {
        this(id, name, statement, figures, complexity, inMind, new ArrayList<Tag>());
    }

    public Problem(String name, String statement, List<String> figures, int complexity, boolean inMind) {
        this(-1, name, statement, figures, complexity, inMind);
    }

    public Problem(String name, String statement, List<String> figures, int complexity, boolean inMind, List<Tag> tags) {
        this(-1, name, statement, figures, complexity, inMind, tags);
    }

    public Problem(
            int id,
            String name,
            String statement,
            List<String> figures,
            int complexity,
            boolean isInMind,
            List<Tag> tags
    ) {
        this.id = id;
        this.name = name;
        this.statement = statement;
        this.figures = figures;
        this.tags = tags;
        this.complexity = complexity;
        this.inMind = isInMind;
    }

    public int getId() {
        return id;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public String getTagsString(boolean production) {
        String result = "";

        for (int i = 0; i < tags.size(); ++i) {
            result += tags.get(i).getName();
            if (i != tags.size() - 1) {
                result += ",";
                if (production) {
                    result += " ";
                }
            }
        }

        return result;
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

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void addTags(List<Tag> tags) {
        this.tags.addAll(tags);
    }

    public List<String> getFigures() {
        return figures;
    }

    public String getFiguresString() {
        String result = "";

        for (int i = 0; i < figures.size(); ++i) {
            result += figures.get(i);
            if (i != figures.size() - 1) {
                result += ",";
            }
        }

        return result;
    }

    public int getComplexity() {
        return complexity;
    }

    public boolean isInMind() {
        return inMind;
    }

    public static List<String> parseFiguresString(String figures) {
        if (figures == null || figures.isEmpty()) {
            return new ArrayList<String>();
        }
        return Arrays.asList(figures.split(","));
    }

    public void addFigures(List<String> figures) {
        this.figures.addAll(figures);
    }

    @Override
    public String toString() {
        return "Problem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", statement='" + statement + '\'' +
                ", figures=" + figures +
                ", tags=" + tags +
                ", complexity=" + complexity +
                ", inMind=" + inMind +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Problem problem = (Problem) o;

        if (id != problem.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
