package com.kk.teachme.servlet;

import com.kk.teachme.db.*;
import com.kk.teachme.support.JSONCreator;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProblemController {

    @Autowired
    StatusDepot statusDepot;

    @Autowired
    UserDepot userDepot;

    @Autowired
    ConfigDepot configDepot;

    @Autowired
    ProblemDepot problemDepot;

    @Autowired
    TagDepot tagDepot;

    @Autowired
    SolutionDepot solutionDepot;

    @Autowired
    UserProblemDepot userProblemDepot;

    @RequestMapping(value = "/problem_{problem_id:\\d+}")
    public String getProblem(@PathVariable int problem_id, Model model) {
        model.addAttribute("problem", problemDepot.get(problem_id));
        model.addAttribute("solution", solutionDepot.getSolution(problem_id));
        return "problem/problem_panel";
    }

    @RequestMapping(value = "/edit_{problem_id:\\d+}")
    public String editProblem(@PathVariable int problem_id, Model model) {
        model.addAttribute("problem", problemDepot.get(problem_id));
        model.addAttribute("solution", solutionDepot.getSolution(problem_id));
        return "edit";
    }

    @RequestMapping(value = "/delete_all_problems", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String deleteAllProblems() throws JSONException {
        problemDepot.deleteAllProblems();
        configDepot.setValue("tct", 0);
        return JSONCreator.okJson().toString();
    }

}