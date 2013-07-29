package com.kk.teachme.servlet;

import com.kk.teachme.checker.SolveStatus;
import com.kk.teachme.db.ProblemDepot;
import com.kk.teachme.db.SolutionDepot;
import com.kk.teachme.db.TagDepot;
import com.kk.teachme.db.UserProblemDepot;
import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Tag;
import com.kk.teachme.model.UserProblem;
import com.kk.teachme.support.JSONCreator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    ProblemDepot problemDepot;

    @Autowired
    TagDepot tagDepot;

    @Autowired
    SolutionDepot solutionDepot;

    @Autowired
    UserProblemDepot userProblemDepot;


    @RequestMapping(value = "/problem_{problem_id:\\d+}", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getProblems(@PathVariable int problem_id) throws JSONException {
        Problem problem = problemDepot.getById(problem_id);
        if (problem == null) {
            return JSONCreator.errorJSON("Incorrect id").toString();
        }
        JSONObject json = JSONCreator.valueOf(problem);
        return JSONCreator.resultJSON(json).toString();
    }

    @RequestMapping(value = "/by_tag", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getProblemsByTagId(@RequestParam int tag_id) throws JSONException {
        Tag tag = tagDepot.getCached(tag_id);
        if (tag == null) {
            return JSONCreator.errorJSON("Incorrect id").toString();
        }

        List<Problem> problems = problemDepot.getByTag(tag);

        JSONObject result = JSONCreator.okJson();
        JSONArray array = new JSONArray();
        for (Problem problem : problems) {
            array.put(JSONCreator.valueOf(problem));
        }
        result.put("problems", array);
        return result.toString();
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

    //methods from UserProblemDepot

    @RequestMapping(value = "/all_uproblems", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getAllUserProblems(@RequestParam int userId) throws JSONException {
        List<UserProblem> answerList = userProblemDepot.getAllUserProblems(userId);
        return JSONCreator.valueOfList(answerList).toString();
    }

    @RequestMapping(value = "/solved_uproblems", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getSolvedUserProblems(@RequestParam int userId) throws JSONException {
        List<UserProblem> answerList = userProblemDepot.getSolvedProblems(userId);
        return JSONCreator.valueOfList(answerList).toString();
    }

    @RequestMapping(value = "/unsolved_uproblems", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getUnsolvedUserProblems(@RequestParam int userId) throws JSONException {
        List<UserProblem> answerList = userProblemDepot.getUnsolvedProblems(userId);
        return JSONCreator.valueOfList(answerList).toString();
    }

    @RequestMapping(value = "/uproblems_tag", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getUserProblemsByTag(@RequestParam int userId, @RequestParam int tagId) throws JSONException {
        List<UserProblem> answerList = userProblemDepot.getProblemsByTag(userId, tagId);
        return JSONCreator.valueOfList(answerList).toString();
    }

}