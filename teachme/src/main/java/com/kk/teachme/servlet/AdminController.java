package com.kk.teachme.servlet;

import com.kk.teachme.db.ProblemDepot;
import com.kk.teachme.db.SolutionDepot;
import com.kk.teachme.db.TagDepot;
import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Mary
 * Date: 12.07.13
 * Time: 20:55
 * To change this template use File | Settings | File Templates.
 */
public class AdminController {

    @Autowired
    ProblemDepot problemDepot;

    @Autowired
    TagDepot tagDepot;

    @Autowired
    SolutionDepot solutionDepot;

    public String addProblem(String name, String statement,  List<String> tagNames, String solution, int checker_id) {
        List<Tag> tags = new ArrayList<Tag>();
        for (String tagName : tagNames) {
            tags.add(tagDepot.getByName(tagName));
        }

        Problem newProblem = new Problem(name, statement);
        newProblem.addTags(tags);
        int problem_id = problemDepot.addObject(newProblem);

        solutionDepot.addSolution(problem_id, solution, checker_id);

        return "problem";
    }

    public String admin(Model model) {
        //return JSP with admin page

        //collect all checker
        //collect all tags
        //put it to Model
        return "admin";
    }

    public String adminList(Model model, String tag) {
        //show all problems by tag (maybe null)
        return "problems";
    }
}
