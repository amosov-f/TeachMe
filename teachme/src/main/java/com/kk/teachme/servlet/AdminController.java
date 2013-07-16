package com.kk.teachme.servlet;

import com.kk.teachme.db.*;
import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Tag;
import com.kk.teachme.model.User;
import com.kk.teachme.support.JSONCreator;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class AdminController {

    @Autowired
    ProblemDepot problemDepot;

    @Autowired
    TagDepot tagDepot;

    @Autowired
    SolutionDepot solutionDepot;

    @Autowired
    CheckerDepot checkerDepot;

    @Autowired
    UserDepot userDepot;

    @RequestMapping(value = "/add_problem")
    public String addProblem(
            Model model,
            @RequestParam String name,
            @RequestParam String statement,
            @RequestParam String solution,
            @RequestParam int checker_id
    ) {
        name = name.trim();
        statement = statement.trim();
        solution = solution.trim();

        List<Tag> tags = new ArrayList<Tag>();
        //for (String tagName : tagNames) {
        //    tags.add(tagDepot.getByName(tagName));
        //}

        Problem newProblem = new Problem(name, statement);
        newProblem.addTags(tags);
        int problemId = problemDepot.addObject(newProblem);

        solutionDepot.addSolution(problemId, solution, checker_id);

        model.addAttribute("result", "ok");
        return "result";
    }

    @RequestMapping(value = "/admin")
    public String admin(Model model) {
        //return JSP with admin page
        //collect all checker
        //collect all tags
        //put it to Model

        model.addAttribute("checkerMap", checkerDepot.getAllCheckers());
        model.addAttribute("tagList", tagDepot.getAllTags());

        return "admin";
    }

    @RequestMapping(value = "/problems")
    public String adminList(Model model, @RequestParam(required = false) String tag) {
        //show all problems by tag  (may be null)
        if (tag == null) {
            model.addAttribute("problemList", problemDepot.getAllProblems());
            return "problems";
        }

        List<Problem> problems = problemDepot.getByTag(tagDepot.getByName(tag));

        if (problems == null) {
            problems = new ArrayList<Problem>();
        }

        model.addAttribute("problemList", problems);
        return "problems";
    }

}
