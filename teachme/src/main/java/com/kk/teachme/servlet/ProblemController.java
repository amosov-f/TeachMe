package com.kk.teachme.servlet;

import com.kk.teachme.db.CheckerDepot;
import com.kk.teachme.db.ProblemDepot;
import com.kk.teachme.db.SolutionDepot;
import com.kk.teachme.db.TagDepot;
import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Tag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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


    @RequestMapping(value = "/problem_{problem_id:\\d+}", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getProblems(@PathVariable int problem_id) throws JSONException {
        Problem problem = problemDepot.getById(problem_id);
        if (problem == null) {
            return makeErrorJSON("Incorrect id");
        }
        JSONObject json = toJson(problem);
        return makeJSONResult(json).toString();
    }

    @RequestMapping(value = "/by_tag", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getProblemsByTagId(@RequestParam int tag_id) throws JSONException {
        Tag tag = tagDepot.getCached(tag_id);
        if (tag == null) {
            return makeErrorJSON("Incorrect id");
        }

        List<Problem> problems = problemDepot.getByTag(tag);

        JSONObject result = okJson();
        JSONArray array = new JSONArray();
        for (Problem problem : problems) {
            array.put(toJson(problem));
        }
        result.put("problems", array);
        return result.toString();
    }


    @RequestMapping(value = "/add_tag_to_problem", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String addTagToProblem(@RequestParam int problem_id, @RequestParam int tag_id) throws JSONException {
        Problem problem = problemDepot.getById(problem_id);
        if (problem == null) {
            return makeErrorJSON("Incorrect problem id");
        }

        Tag tag = tagDepot.getCached(tag_id);
        if (tag == null) {
            return makeErrorJSON("Incorrect tag id");
        }

        if (!problemDepot.addTagToProblem(problem, tag)) {
            return makeErrorJSON("This tag already exists");
        }

        return okJson().toString();
    }

    @RequestMapping(value = "/all_tags", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getAllTags() throws JSONException {
        JSONObject result = okJson();
        JSONArray tags = new JSONArray();
        for (Tag tag : tagDepot.getAllTags()) {
            tags.put(toJson(tag));
        }
        result.put("tags", tags);
        return result.toString();
    }

    @RequestMapping(value = "/change_tag_name", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String changeTagName(@RequestParam int tag_id, String new_name) throws JSONException {
        Tag tag = tagDepot.getById(tag_id);
        if (tag == null) {
            return makeErrorJSON("Incorrect tag id");
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
            return makeErrorJSON("Incorrect problem id");
        }
        problemDepot.changeProblemStatement(problem, new_text);
        return okJson().toString();
    }

    @RequestMapping(value = "/tasksWithTag", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getTaskNumberByTag(@RequestParam int tag_id) throws JSONException {
        Tag tag = tagDepot.getCached(tag_id);
        if (tag == null) {
            return makeErrorJSON("No tag");
        }
        JSONObject result = okJson();
        result.put("count", problemDepot.getTaskNumberByTag(tag));
        return result.toString();
    }

    private String makeErrorJSON(String error_text) throws JSONException {
        /* create json with error from error_text */
        JSONObject result = new JSONObject();
        result.put("result", "error");
        result.put("error", error_text);
        return result.toString();
    }

    private JSONObject makeJSONResult(JSONObject json) throws JSONException {
        /* create json with result from json */
        JSONObject result = okJson();
        result.put("problem", json);
        return result;
    }

    private JSONObject okJson() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("result", "ok");
        return result;
    }

    private JSONObject toJson(Problem problem) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", problem.getId());
        json.put("statement", problem.getStatement());
        JSONArray tags = new JSONArray();
        for (Tag t : problem.getTags()) {
            tags.put(t.getName());
        }
        json.put("tags", tags);
        return json;
    }

    @RequestMapping(value = "/check_answer", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String checkAnswer(@RequestParam int problemId, @RequestParam String userAnswer) throws JSONException {
        if (solutionDepot.check(problemId, userAnswer)){
            return okJson().toString();
        }
        else {
            return makeErrorJSON("wrong answer");
        }
    }

    private JSONObject toJson(Tag tag) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", tag.getId());
        json.put("name", tag.getName());
        return json;
    }
}