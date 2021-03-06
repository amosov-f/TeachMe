package com.kk.teachme.servlet;

import com.kk.teachme.db.*;
import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Solution;
import com.kk.teachme.model.Tag;
import com.kk.teachme.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;

@Controller
public class AdminController {

    private static final List<Integer> admins = Arrays.asList(
        /*Fedor Amosov*/ 98810985,
        /*Mark Yezhkov*/ 1857046,
        /*Ekaterina Sosa*/ 6053606,
        /*Alexander Konstantinov*/ 2745,
        /*Dmitry Kachmar*/ 15460,
        /*Ekaterina Danilova*/ 12484189,
        /*Michal Krajewski*/ 1969317
    );

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

    @Autowired
    AdminDepot adminDepot;


    @RequestMapping(value = "/is_admin")
    @ResponseBody
    public boolean isAdmin(HttpServletRequest request) {
        if (request.getSession().getAttribute("user") == null) {
            return false;
        }
        User user = (User) request.getSession().getAttribute("user");
        return adminDepot.contains(user.getId());
    }

    @RequestMapping(value = "/admin")
    public String admin(@RequestParam(required = false) Integer problem_id, HttpServletRequest request, Model model) {
        if (!isAdmin(request)) {
            return "redirect:/login";
        }

        List<Problem> problems;
        problems = problemDepot.getAllProblems();
        if (problems == null) {
            problems = new ArrayList<>();
        }

        Map<Integer, Solution> id2solution = new HashMap<>();
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

    @RequestMapping(value = "/add_problem")
    public String addProblem(
            @RequestParam int problem_id,
            @RequestParam String name,
            @RequestParam String statement,
            @RequestParam(required = false) String figures,
            @RequestParam int complexity,
            @RequestParam(required = false) Boolean in_mind,
            @RequestParam(required = false) String tags,
            @RequestParam String solution,
            @RequestParam int checker_id,
            @RequestParam(required = false) String new_tags
    ) throws IOException {

        name = name.trim();
        statement = statement.trim();
        solution = solution.trim();

        if (new_tags != null && !new_tags.isEmpty()) {
            for (String tagName : URLDecoder.decode(new_tags, "UTF-8").split(",")) {
                tagDepot.createIfNotExist(tagName);
            }
        }

        List<Tag> tagList = new ArrayList<>();
        if (tags != null && !tags.isEmpty()) {
            for (String tagName : URLDecoder.decode(tags, "UTF-8").split(",")) {
                tagList.add(tagDepot.getByName(tagName));
            }
        }

        boolean isInMind = (in_mind != null);

        System.out.println(isInMind);

        Problem problem = new Problem(name, statement, Problem.parseFiguresString(figures), complexity, isInMind, tagList);

        if (problem_id == -1) {
            problem_id = problemDepot.add(problem);
            solutionDepot.addSolution(problem_id, solution, checker_id);
        } else {
            problemDepot.setById(problem_id, problem);
            solutionDepot.setSolution(problem_id, solution, checker_id);
        }

        return "redirect:/admin?problem_id=" + problem_id;
    }

    @RequestMapping(value = "/delete_problem")
    public String deleteProblem(@RequestParam int problem_id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return "redirect:/login";
        }

        problemDepot.deleteById(problem_id);

        return "redirect:/admin";
    }

    @RequestMapping(value = "/new_problem")
    public String newProblem(HttpServletRequest request, Model model) {
        if (!isAdmin(request)) {
            return "redirect:/login";
        }

        model.addAttribute("checkerMap", checkerDepot.getAll());
        model.addAttribute("tagList", tagDepot.getAllTags());

        return "edit";
    }

    @RequestMapping(value = "/edit_problem")
    public String editProblem(@RequestParam int problem_id, HttpServletRequest request, Model model) {
        if (!isAdmin(request)) {
            return "redirect:/login";
        }

        model.addAttribute("problem", problemDepot.get(problem_id));
        model.addAttribute("solutionText", solutionDepot.getSolution(problem_id).getSolutionText());
        model.addAttribute("checkerId", solutionDepot.getCheckerId(problem_id));
        model.addAttribute("checkerMap", checkerDepot.getAll());
        model.addAttribute("tagList", tagDepot.getAllTags());

        return "edit";
    }

    @RequestMapping(value = "/add_admin")
    @ResponseBody   //не возвращает jsp-шку. Отображает возвращаемую строку сразу в браузере
    public String addAdmin(@RequestParam int admin_id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return "You are not admin";
        }
        if (adminDepot.contains(admin_id)) {
            return "Admin already added";
        }
        adminDepot.addAdmin(admin_id);
        return "Admin added";
    }

    @RequestMapping(value = "/admin_control")
    public String adminControl(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return "redirect:/login";
        }
        return "admin_control";
    }

}
