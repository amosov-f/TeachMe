package com.kk.teachme.servlet;

import com.kk.teachme.db.*;
import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Tag;
import com.kk.teachme.support.JSONCreator;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
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
            @RequestParam(required = false) Integer problem_id,
            @RequestParam String name,
            @RequestParam String statement,
            @RequestParam(required = false) String figures,
            @RequestParam(required = false) String tags,
            @RequestParam String solution,
            @RequestParam int checker_id,
            @RequestParam(required = false) String newTags,
            Model model
    ) throws IOException {
        name = name.trim();
        statement = statement.trim();
        solution = solution.trim();

        if (newTags != null && !newTags.isEmpty()) {
            for (String tagName : newTags.replace('_', ' ').split(",")) {
                tagDepot.createIfNotExist(tagName);
            }
        }

        List<Tag> tagList = new ArrayList<Tag>();
        if (tags != null && !tags.isEmpty()) {
            for (String tagName : tags.replace('_', ' ').split(",")) {
                tagList.add(tagDepot.getByName(tagName));
            }
        }

        Problem problem = new Problem(name, statement, Problem.parseFiguresString(figures), tagList);

        if (problem_id == null) {
            problem_id = problemDepot.addObject(problem);
            solutionDepot.addSolution(problem_id, solution, checker_id);
        } else {
            problemDepot.setById(problem_id, problem);
            solutionDepot.setSolution(problem_id, solution, checker_id);
        }

        return adminList(model, null);
    }

    @RequestMapping(value = "/admin")
    public String admin(@RequestParam(required = false) Integer problem_id, Model model) {
        //return JSP with admin page
        //collect all checker
        //collect all tags
        //put it to Model

        if (problem_id != null) {
            model.addAttribute("problem", problemDepot.getById(problem_id));
            model.addAttribute("solution", solutionDepot.getSolution(problem_id).getSolutionText());
            model.addAttribute("checkerId", solutionDepot.getCheckerId(problem_id));
        }

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

    @RequestMapping(value = "/test")
    public String test() {
        return "test";
    }

}
