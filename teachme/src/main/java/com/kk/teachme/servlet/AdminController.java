package com.kk.teachme.servlet;

import com.kk.teachme.db.ProblemDepot;
import com.kk.teachme.db.SolutionDepot;
import com.kk.teachme.db.TagDepot;
import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @RequestMapping(value = "/add_problem")
    public String addProblem(
            @RequestParam String name,
            @RequestParam String statement,
            @RequestParam String solution,
            @RequestParam int checker_id
    ) {
        List<Tag> tags = new ArrayList<Tag>();
        //for (String tagName : tagNames) {
        //    tags.add(tagDepot.getByName(tagName));
        //}

        Problem newProblem = new Problem(name, statement);
        newProblem.addTags(tags);
        int problem_id = problemDepot.addObject(newProblem);

        solutionDepot.addSolution(problem_id, solution, checker_id);

        return "ok";
    }

    @RequestMapping(value = "/admin")
    public String admin(Model model) {
        //return JSP with admin page

        //collect all checker
        //collect all tags
        //put it to Model
        return "admin";
    }

    @RequestMapping(value = "/problems")
    public String adminList(Model model, String tag) {
        //show all problems by tag (maybe null)

        model = model.addAttribute(problemDepot.getAllProblems());

        return "problems";
    }
}
