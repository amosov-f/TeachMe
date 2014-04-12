package com.kk.teachme.servlet;

import com.kk.teachme.checker.Checker;
import com.kk.teachme.db.ProblemDepot;
import com.kk.teachme.db.SolutionDepot;
import com.kk.teachme.db.TagDepot;
import com.kk.teachme.db.UserProblemDepot;
import com.kk.teachme.model.*;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static com.kk.teachme.checker.Checker.SolveStatus.*;

@Controller
public class UserProblemController {

    @Autowired
    ProblemDepot problemDepot;

    @Autowired
    SolutionDepot solutionDepot;

    @Autowired
    TagDepot tagDepot;

    @Autowired
    UserProblemDepot userProblemDepot;

    @RequestMapping(value = "/user_problem_panel")
    public String getProblemPanel(@RequestParam int problem_id, HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("problem", problemDepot.get(problem_id));

        return "user_problem/user_problem_panel";
    }

    @RequestMapping(value = "/user_problem_{problem_id:\\d+}")
    public String getUserProblem(
            @PathVariable int problem_id,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Boolean in_mind,
            HttpServletRequest request,
            Model model
    ) throws UnsupportedEncodingException {
        if (request.getSession().getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("problem", problemDepot.get(problem_id));
        model.addAttribute("tags", tags);
        model.addAttribute("inMind", in_mind);
        model.addAttribute("tagList", parseTagsString(tags));
        model.addAttribute("checker", solutionDepot.getSolution(problem_id).getChecker());

        return "problem";
    }

    @RequestMapping(value = "/problems")
    public String getProblems(
            @RequestParam(required = false) Integer problem_id,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Boolean in_mind,
            HttpServletRequest request,
            Model model
    ) throws UnsupportedEncodingException {
        if (request.getSession().getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("tagList", tagDepot.getAllTags());
        if (problem_id != null) {
            model.addAttribute("problemId", problem_id);
        }
        if (tags != null && !tags.isEmpty()) {
            model.addAttribute("tags", tags);
        }
        if (in_mind != null) {
            model.addAttribute("inMind", in_mind);
        }

        return "problems";
    }

    @RequestMapping(value = "/submit")
    public String submit(@RequestParam int problem_id, @RequestParam String solution_text, HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("user") == null) {
            return "redirect:/login";
        }

        User user = (User) request.getSession().getAttribute("user");
        Solution solution = solutionDepot.getSolution(problem_id);

        Checker.SolveStatus solveStatus = solution.check(solution_text);

        if (solveStatus == CORRECT) {
            userProblemDepot.attempt(user.getId(), problem_id, true);
        } else if (solveStatus == INCORRECT) {
            userProblemDepot.attempt(user.getId(), problem_id, false);
        }

        model.addAttribute("solveStatus", solveStatus);
        model.addAttribute(
                "itemClass",
                "user-problem-" + userProblemDepot.getStatus(user.getId(), problem_id).toString().toLowerCase()
        );

        return "user_problem/solve_status";
    }

    @RequestMapping(value = "/read")
    public String read(@RequestParam int problem_id, HttpServletRequest request, Model model) throws JSONException {
        Integer userId = getUserId(request);
        if (userId == null) {
            return "redirect:/login";
        }

        Status status = userProblemDepot.getStatus(userId, problem_id);

        if (status != Status.SOLVED) {
            userProblemDepot.addUserProblem(userId, problem_id);
        }

        return getItem(problem_id, request, model);
    }

    @RequestMapping(value = "/user_problem_item")
    public String getItem(@RequestParam int problem_id, HttpServletRequest request, Model model) throws JSONException {
        if (request.getSession().getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User)request.getSession().getAttribute("user");

        model.addAttribute("userProblem", userProblemDepot.getByIds(user.getId(), problem_id));

        return "user_problem/user_problem_item";
    }

    @RequestMapping(value = "/user_problem_list")
    public String getUserProblemList(
            @RequestParam String tags,
            @RequestParam String filter,
            @RequestParam boolean in_mind,
            @RequestParam int from,
            @RequestParam int to,
            HttpServletRequest request,
            Model model
    ) throws UnsupportedEncodingException {
        if (request.getSession().getAttribute("user") == null) {
            return null;
        }
        User user = (User) request.getSession().getAttribute("user");

        List<UserProblem> userProblems =
                userProblemDepot.getByFilters(user.getId(), parseTagsString(tags), filter, in_mind, from, to);

        model.addAttribute("userProblemList", userProblems);

        return "user_problem/user_problem_list";
    }

    @RequestMapping(value = "/{mode:\\w+}_user_problem")
    public String getEasierUserProblem(
            @PathVariable String mode,
            @RequestParam int problem_id,
            @RequestParam String tags,
            @RequestParam boolean in_mind,
            HttpServletRequest request,
            Model model
    ) throws UnsupportedEncodingException {
        int userId = getUserId(request);

        UserProblem problem = null;
        switch (mode) {
            case "easier":
                problem = userProblemDepot.getEasierProblem(userId, problem_id, parseTagsString(tags), in_mind);
                break;
            case "similar":
                problem = userProblemDepot.getSimilarProblem(userId, problem_id, parseTagsString(tags), in_mind);
                break;
            case "harder":
                problem = userProblemDepot.getHarderProblem(userId, problem_id, parseTagsString(tags), in_mind);
                break;
        }

        if (problem == null) {
            return "null";
        }

        model.addAttribute("problem", problem.getProblem());
        model.addAttribute("checker", solutionDepot.getSolution(problem.getProblem().getId()).getChecker());

        return "user_problem/user_problem_panel";
    }

    private List<Tag> parseTagsString(String tags) throws UnsupportedEncodingException {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }

        List<Tag> tagList = new ArrayList<>();
        for (String tag : URLDecoder.decode(tags, "UTF-8").split(",")) {
            if (tagDepot.getByName(tag) != null) {
                tagList.add(tagDepot.getByName(tag));
            }
        }
        return tagList;
    }

    @RequestMapping(value = "/")
    public String home(HttpServletRequest request) {
        if (request.getSession().getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "redirect:/problems";
    }

    private Integer getUserId(HttpServletRequest request) {
        if (request.getSession().getAttribute("user") == null) {
            return null;
        }
        return ((User) request.getSession().getAttribute("user")).getId();
    }

}
