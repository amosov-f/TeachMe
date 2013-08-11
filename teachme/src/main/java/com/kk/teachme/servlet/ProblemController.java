package com.kk.teachme.servlet;

import com.kk.teachme.checker.SolveStatus;
import com.kk.teachme.db.*;
import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Tag;
import com.kk.teachme.model.UserProblem;
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
import java.util.List;

/**
 * User: akonst
 * Time: 07.06.13 23:08
 */
@Controller
@RequestMapping("/")
public class ProblemController {
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
        model.addAttribute("problem", problemDepot.getById(problem_id));
        model.addAttribute("solution", solutionDepot.getSolution(problem_id));
        return "problem/problem_panel";
    }

    @RequestMapping(value = "/by_tag")
    public String getProblemsByTag(@RequestParam(required = false) String tag, Model model) {
        List<Problem> problems;
        if (tag == null || tag.isEmpty()) {
            problems = problemDepot.getAllProblems();
        } else {
            problems = problemDepot.getByTag(tagDepot.getByName(tag));
        }

        model.addAttribute("problemList", problems);

        return "problem/problem_list";
    }

    @RequestMapping(value = "/by_tag_list")
    public String getProblemsByTagList(@RequestParam(required = false) String tags, Model model) {
        List<Problem> problems;

        if (tags == null || tags.isEmpty()) {
            problems = problemDepot.getAllProblems();
        } else {
            problems = null;
        }

        return  "problem/problem_list";
    }

    @RequestMapping(value = "/count_by_tag", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getProblemsByTagIdCount(@RequestParam int tag_id) throws JSONException {
        Tag tag = tagDepot.getCached(tag_id);
        if (tag == null) {
            return JSONCreator.errorJSON("Incorrect id").toString();
        }

        JSONObject result = new JSONObject();
        result.put("count", problemDepot.getProblemsByTagCount(tag));
        return JSONCreator.resultJSON(result).toString();
    }

    @RequestMapping(value = "/add_tag_to_problem", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String addTagToProblem(@RequestParam int problem_id, @RequestParam int tag_id) throws JSONException {
        Problem problem = problemDepot.getById(problem_id);
        if (problem == null) {
            return JSONCreator.errorJSON("Incorrect problem id").toString();
        }

        Tag tag = tagDepot.getCached(tag_id);
        if (tag == null) {
            return JSONCreator.errorJSON("Incorrect tag id").toString();
        }

        problemDepot.addTagToProblem(problem, tag);

        return JSONCreator.okJson().toString();
    }

    @RequestMapping(value = "/all_tags", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getAllTags() throws JSONException {
        JSONObject result = JSONCreator.okJson();
        JSONArray tags = new JSONArray();
        for (Tag tag : tagDepot.getAllTags()) {
            tags.put(JSONCreator.valueOf(tag));
        }
        result.put("tags", tags);
        return result.toString();
    }

    @RequestMapping(value = "/change_tag_name", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String changeTagName(@RequestParam int tag_id, String new_name) throws JSONException {
        Tag tag = tagDepot.getById(tag_id);
        if (tag == null) {
            return JSONCreator.errorJSON("Incorrect tag id").toString();
        }
        tagDepot.changeTagName(tag, new_name);
        JSONObject result = new JSONObject();
        result.put("result", "ok");
        return result.toString();
    }

    @RequestMapping(value = "/change_statement", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String changeProblemStatement(@RequestParam int problem_id, @RequestParam String new_text) throws JSONException {
        Problem problem = problemDepot.getById(problem_id);
        if (problem == null) {
            return JSONCreator.errorJSON("Incorrect problem id").toString();
        }
        problemDepot.changeProblemStatement(problem, new_text);
        return JSONCreator.okJson().toString();
    }

    @RequestMapping(value = "/check_solution", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String checkSolution(@RequestParam int problem_id, @RequestParam String user_answer, HttpServletRequest request) throws JSONException {
        SolveStatus answerStatus = solutionDepot.check(problem_id, user_answer);
        String methodAnswer = null;
        switch (answerStatus) {
            case CORRECT:
                methodAnswer = JSONCreator.okJson().toString();
                break;
            case INCORRECT:
                methodAnswer = JSONCreator.errorJSON("incorrect answer").toString();
                break;
            case INVALID:
                methodAnswer = JSONCreator.errorJSON("bad answer type").toString();
                break;
        }
        return methodAnswer;
    }

    @RequestMapping(value = "/delete", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String deleteById(@RequestParam int problem_id) throws JSONException {

        if (problemDepot.deleteById(problem_id)) {
            return JSONCreator.okJson().toString();
        }

        return JSONCreator.errorJSON("Incorrect problem id").toString();
    }

    @RequestMapping(value = "/delete_all_problems", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String deleteAllProblems() throws JSONException {
        problemDepot.deleteAllProblems();
        configDepot.setValue("tct", 0);
        return JSONCreator.okJson().toString();
    }

    //methods from UserProblemDepot

    @RequestMapping(value = "/add_uproblem", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String addUserProblem(@RequestParam int user_id, @RequestParam int problem_id)
            throws JSONException {
        userProblemDepot.addUserProblem(user_id, problem_id);
        return JSONCreator.okJson().toString();
    }

    @RequestMapping(value = "/all_uproblems", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getAllUserProblems(@RequestParam int user_id) throws JSONException {
        List<UserProblem> answerList = userProblemDepot.getAllUserProblems(user_id);
        System.out.println(answerList.get(0).getProblem().getId());
        return JSONCreator.valueOfList(answerList).toString();
    }

    @RequestMapping(value = "/solved_uproblems", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getSolvedUserProblems(@RequestParam int user_id) throws JSONException {
        List<UserProblem> answerList = userProblemDepot.getSolvedProblems(user_id);
        return JSONCreator.valueOfList(answerList).toString();
    }

    @RequestMapping(value = "/unsolved_uproblems", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getUnsolvedUserProblems(@RequestParam int user_id) throws JSONException {
        List<UserProblem> answerList = userProblemDepot.getUnsolvedProblems(user_id);
        return JSONCreator.valueOfList(answerList).toString();
    }

    @RequestMapping(value = "/uproblems_tag", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getUserProblemsByTag(@RequestParam int user_id, @RequestParam int tag_id) throws JSONException {
        List<UserProblem> answerList = userProblemDepot.getProblemsByTag(user_id, tag_id);
        return JSONCreator.valueOfList(answerList).toString();
    }

}