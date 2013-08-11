package com.kk.teachme.servlet;

import com.kk.teachme.db.*;
import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Solution;
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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if (statement.isEmpty()) {
            if (problem_id != null) {
                problemDepot.deleteById(problem_id);
            }
            return admin(-1, model);
        }

        if (newTags != null && !newTags.isEmpty()) {
            for (String tagName : URLDecoder.decode(newTags, "UTF-8").split(",")) {
                tagDepot.createIfNotExist(tagName);
            }
        }

        List<Tag> tagList = new ArrayList<Tag>();
        if (tags != null && !tags.isEmpty()) {
            for (String tagName : URLDecoder.decode(tags, "UTF-8").split(",")) {
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

        return admin(problem_id, model);
    }

    @RequestMapping(value = "/delete_problem")
    public String deleteProblem(@RequestParam int problem_id, Model model) {
        problemDepot.deleteById(problem_id);
        return admin(-1, model);
    }

    @RequestMapping(value = "/new_problem")
    public String newProblem(Model model) {
        model.addAttribute("checkerMap", checkerDepot.getAllCheckers());
        model.addAttribute("tagList", tagDepot.getAllTags());
        return "edit";
    }

    @RequestMapping(value = "/edit_problem")
    public String editProblem(@RequestParam int problem_id, Model model) {
        model.addAttribute("problem", problemDepot.getById(problem_id));
        model.addAttribute("solution", solutionDepot.getSolution(problem_id).getSolutionText());
        model.addAttribute("checkerId", solutionDepot.getCheckerId(problem_id));
        model.addAttribute("checkerMap", checkerDepot.getAllCheckers());
        model.addAttribute("tagList", tagDepot.getAllTags());
        return "edit";
    }

    @RequestMapping(value = "/admin")
    public String admin(@RequestParam(required = false) Integer problem_id, Model model) {
        List<Problem> problems;
        problems = problemDepot.getAllProblems();
        if (problems == null) {
            problems = new ArrayList<Problem>();
        }

        Map<Integer, Solution> id2solution = new HashMap<Integer, Solution>();
        for (Problem problem : problemDepot.getAllProblems()) {
            id2solution.put(problem.getId(), solutionDepot.getSolution(problem.getId()));
        }

        if (problem_id != null && problem_id != -1) {
            model.addAttribute("problemId", problem_id);
        }

        model.addAttribute("problemList", problems);
        model.addAttribute("solutionMap", id2solution);
        model.addAttribute("tagList", tagDepot.getAllTags());

        return "admin";
    }

    @RequestMapping(value = "/test")
    public String test() {
        return "test";
    }

}
