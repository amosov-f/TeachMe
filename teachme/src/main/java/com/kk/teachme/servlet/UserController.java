package com.kk.teachme.servlet;

import com.kk.teachme.db.*;
import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Solution;
import com.kk.teachme.model.Status;
import com.kk.teachme.model.User;
import com.kk.teachme.support.JSONCreator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    UserDepot userDepot;

    @Autowired
    ProblemDepot problemDepot;

    @Autowired
    SolutionDepot solutionDepot;

    @Autowired
    TagDepot tagDepot;

    @Autowired
    StatusDepot statusDepot;

    @RequestMapping(value = "/user_problem")
    public String getProblem(@RequestParam int problem_id, Model model) {
        model.addAttribute("problem", problemDepot.getById(problem_id));
        return "user_problem/user_problem";
    }

    @RequestMapping(value = "/user")
    public String user(Model model) {
        List<Problem> problems;
        problems = problemDepot.getAllProblems();
        if (problems == null) {
            problems = new ArrayList<Problem>();
        }

        model.addAttribute("problemList", problems);
        model.addAttribute("tagList", tagDepot.getAllTags());

        return "user";
    }

    @RequestMapping(value = "/submit")
    public String submit(@RequestParam int problem_id, @RequestParam String solution_text, Model model) {
        Solution solution = solutionDepot.getSolution(problem_id);

        model.addAttribute("solveStatus", solution.getChecker().check(solution_text, solution.getSolutionText()));

        return "user_problem/solve_status";
    }

    @RequestMapping(value = "/login")
    public String loginForm(Model model) {
        return "login";
    }

    @RequestMapping(value = "/login_user")
    public String loginUser(@RequestParam String login, HttpServletRequest request, Model model) {
        if (!userDepot.checkIfExists(login)) {
            model.addAttribute("result", "Error! This user does not exist!");
            return "result";
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("user", userDepot.getByLogin(login));

        return user(model);
    }

    @RequestMapping(value = "/reg_user")
    public String regUser(@RequestParam String login, Model model) {
        login = login.trim();

        if (userDepot.checkIfExists(login)) {
            model.addAttribute("result", "Error! User already exists!");
            return "result";
        }

        userDepot.addObject(new User(login));

        return "login";
    }

    @RequestMapping(value = "/logout_user")
    public String logoutUser(HttpServletRequest request) {
        request.getSession().setAttribute("user", null);
        return "login";
    }

    @RequestMapping(value = "/user_{user_id:\\d+}", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getUser(@PathVariable int user_id) throws JSONException {
        User user = userDepot.getById(user_id);

        if (user == null) {
            return JSONCreator.errorJSON("Incorrect id").toString();
        }

        JSONObject result = new JSONObject();
        result.put("user", JSONCreator.valueOf(user));

        JSONArray array = new JSONArray();
        for (Problem problem : problemDepot.getSolvedProblems(user)) {
            array.put(JSONCreator.valueOf(problem));
        }

        result.put("solved", array);

        return JSONCreator.resultJSON(result).toString();
    }

    @RequestMapping(value = "/status", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getStatus(@RequestParam int user_id, @RequestParam int problem_id) throws JSONException {
        User user = userDepot.getById(user_id);
        if (user == null) {
            return JSONCreator.errorJSON("Incorrect user id").toString();
        }

        Problem problem = problemDepot.getById(problem_id);
        if (problem == null) {
            return JSONCreator.errorJSON("Incorrect problem id").toString();
        }

        Status status = statusDepot.getStatus(user, problem);
        if (status == null) {
            status = Status.NEW;
        }

        JSONObject result = new JSONObject();
        result.put("status", status);

        return JSONCreator.resultJSON(result).toString();
    }

}
