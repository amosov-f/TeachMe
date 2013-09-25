package com.kk.teachme.service;

import com.kk.teachme.db.*;
import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Tag;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProblemsRuLoader {

    ConfigDepot configDepot;

    TagDepot tagDepot;

    FileDepot fileDepot;

    ProblemDepot problemDepot;

    SolutionDepot solutionDepot;

    public void fill() {           //i = 101870; i < 109953
        for (int i = 97810; i < 97811; i++) {     //c 109850 по 109952 добавлены на сервер
            load(i);
        }
    }

    public boolean load(int id) {

        String URLString = "http://problems.ru/view_problem_details_new.php?id=" + id;
        Document doc;
        try {
            doc = Jsoup.connect(URLString).get();
        } catch (IOException e) {
            return false;
        }
        if (doc == null) {
            return false;
        }
        String block = extractBlock(doc);
        if (block == null || block.length() == 0){
            return false;
        }
        Integer dif = getDif(block);
        String statement = deleteAuthors(tagTrim(extractSection(block, "Условие")));
        if (statement == null || statement.length() == 0) {
            return false;
        }
        String answer = tagTrim(extractSection(block, "Ответ"));
        if (answer == null || answer.length() == 0) {
            return false;
        }
        Integer intAnswer = getInt(answer);
        if (intAnswer == null) {
            System.out.println("Not an int");
            return false;
        }
        statement += "\n<p align=\"right\"><small>Источник: <a href=\"http://problems.ru/\" target=\"_blank\">problems.ru</a> #" + id + ".</small></p>";
        List<String> figures = new ArrayList<String>();
        figures.add("");
        List<Tag> tags = new ArrayList<Tag>();
        solutionDepot.addSolution(
                problemDepot.addObject(new Problem("Задача с problems.ru #" + id, statement, figures, dif, false, tags)),
                intAnswer.toString(),
                1
        );
        System.out.println("Success at " + id);

        return true;

    }

    private String extractBlock(Document doc) {
        try {
            String block = doc
                    .body()
                    .getElementsByAttributeValue("class", "viewingtable").get(0)
                    .getElementsByAttributeValue("class", "viewingtablecell").get(0)
                    .getElementsByAttributeValue("class", "viewingtablecell").get(0)
                    .getElementsByAttributeValue("class", "componentboxsecond").get(0)
                    .getElementsByAttributeValue("class", "componentboxcontents").get(0)
                    .toString();
            return block;
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getDif(String statement) {
        try {
            Integer dif = getInt(statement
                    .split("< *td *class *= *\"problemdetailsdifficulty\" *> *")[1]
                    .split("< *nobr *>")[1]
                    .split("< */ *nobr *>")[0]
                    .split("[Сс]ложность *:")[1]
            );
            if (dif == null) {
                dif = 1;
            }
            return dif;
        } catch (Exception e) {
            return 1;
        }
    }

    private String extractSection(String block, String section) {
        try {
        String string = block
                .split(section + ".*</h3>")[1]
                .split("<h3>")[0]
                .split("<small>")[0]
                .trim()
                .replaceAll("show_document.php", "http://problems.ru/show_document.php")
                .replaceAll("\n *", "")
                .replaceAll(" {2,}", " ")
            ;
            System.out.println("extractSection:");
            System.out.println(string);
            return string;
        } catch (Exception e) {
            return null;
        }
    }

    private String tagTrim(String string) {
        try {
            String regex = "< */* *[pP] */* *>|" +
                    "< */* *[bB][rR] */* *>";
            String[] parts = string.split(regex);
            string = "";
            for (String part : parts) {
                part = part.trim();
                if (part.length() != 0) {
                    string += " " + part;
                }
            }
            string = string.trim();
            System.out.println("tagTrim:");
            System.out.println(string);
            return string;
        } catch (Exception e) {
            return null;
        }
    }

    private String deleteAuthors(String statement) {
        String[] split = statement.split("< *div +class *= *\"catalogueproblemauthor[^\"]*\" *>.*< */ *div *>");
        if (split.length == 1) {
            split = split[0].split("< *div +class *= *\"catalogueproblemauthorold[^\"]*\" *>.*< */ *div *>");
            if (split.length == 1) {
                return split[0];
            }
            return split[1];
        }
        return split[1];
    }

    Integer getInt(String answer) {
        try {
            if (answer.matches(".*img.*")){
                return null;
            }
            String regex = "\\D*\\d+\\.?0*\\D*";
            if (answer.matches(regex)) {
                String[] parts = answer.split("\\D+");
                for (String part : parts) {
                    if (part.matches("\\d+")) {
                        return Integer.parseInt(part);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Required
    public void setConfigDepot(ConfigDepot configDepot) {
        this.configDepot = configDepot;
    }

    @Required
    public void setTagDepot(TagDepot tagDepot) {
        this.tagDepot = tagDepot;
    }

    @Required
    public void setProblemDepot(ProblemDepot problemDepot) {
        this.problemDepot = problemDepot;
    }

    @Required
    public void setSolutionDepot(SolutionDepot solutionDepot) {
        this.solutionDepot = solutionDepot;
    }

    @Required
    public void setFileDepot(FileDepot fileDepot) {
        this.fileDepot = fileDepot;
    }

}
