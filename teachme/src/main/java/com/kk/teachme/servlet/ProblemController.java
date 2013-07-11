package com.kk.teachme.servlet;

import com.kk.teachme.db.ProblemDepot;
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

    String makeErrorJSON(String error_text) throws JSONException {
        /* create json with error from error_text */
        JSONObject result = new JSONObject();
        result.put("result", "error");
        result.put("error", error_text);
        return result.toString();
    }

    JSONObject  makeJSONResult(JSONObject json) throws JSONException {
        /* create json with result from json */
        JSONObject result = new JSONObject();
        result.put("result", "ok");
        result.put("problem", json);
        return result;
    }

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

    private JSONObject toJson(Problem problem) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", problem.getId());
        json.put("statement", problem.getStatement());
        JSONArray tags = new JSONArray();
        for (Tag t: problem.getTags()) {
            tags.put(t.getName());
        }
        json.put("tags", tags);
        return json;
    }

    private JSONObject toJson(Tag tag) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", tag.getId());
        json.put("name", tag.getName());
        return json;
    }

    @RequestMapping(value = "/by_tag", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getProblemsByTagId(@RequestParam int tag_id) throws JSONException {
        Tag tag = tagDepot.getCached(tag_id);
        if (tag == null) {
            return makeErrorJSON("Incorrect id");
        }

        List<Problem> problems = problemDepot.getByTag(tag);

        JSONObject result = new JSONObject();
        result.put("result", "ok");
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
        if (problemDepot.getById(problem_id) == null) {
            return makeErrorJSON("Incorrect problem id");
        }
        if (tagDepot.getCached(tag_id) == null) {
            return makeErrorJSON("Incorrect tag id");
        }
        if (!problemDepot.addTagToProblem(problem_id, tag_id)) {
           return makeErrorJSON("this tag  already exists");
        }
        JSONObject json = toJson(problemDepot.getById(problem_id));
        return makeJSONResult(json).toString();
    }

    @RequestMapping(value = "/all_tags", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getAllTags() throws JSONException {
        JSONArray result = new JSONArray();
        for (Tag tag : tagDepot.getAllTags()) {
            result.put(toJson(tag));
        }
        return result.toString();
    }

    @RequestMapping(value = "/change_statement", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String changeProblemStatement(@RequestParam int problem_id, @RequestParam String new_text) throws JSONException {
        if (problemDepot.getById(problem_id) == null) {
          return makeErrorJSON("Incorrect problem id");
        }
        if (!problemDepot.changeProblemStatement(problem_id,new_text)){
            return makeErrorJSON("Couldn't change statement");
        }
        JSONObject json = toJson(problemDepot.getById(problem_id));
        return makeJSONResult(json).toString();
    }
}