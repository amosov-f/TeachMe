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
import java.util.ArrayList;

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

    @RequestMapping(value = "/problem_{problem_id:\\d+}", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getProblems(@PathVariable int problem_id) throws JSONException {
        Problem problem = problemDepot.getById(problem_id);
        if (problem == null) {
            JSONObject result = new JSONObject();
            result.put("result", "error");
            result.put("error", "Incorrect id");
            return result.toString();
        }
        JSONObject result = new JSONObject();
        result.put("result", "ok");
        JSONObject json = toJson(problem);
        result.put("problem", json);
        return result.toString();
    }

    private JSONObject toJson(Problem problem) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", problem.getId());
        json.put("situation", problem.getSituation());
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
            JSONObject result = new JSONObject();
            result.put("result", "error");
            result.put("error", "Incorrect id");
            return result.toString();
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
            JSONObject result = new JSONObject();
            result.put("result", "error");
            result.put("error", "Incorrect problem id");
            return result.toString();
        }
        if (tagDepot.getCached(tag_id) == null) {
            JSONObject result = new JSONObject();
            result.put("result", "error");
            result.put("error", "Incorrect tag id");
            return result.toString();
        }


        if (!problemDepot.addTagToProblem(problem_id, tag_id)) {
            JSONObject result = new JSONObject();
            result.put("result", "error");
            result.put("error", "this tag is already exist");
            return result.toString();
        }


        JSONObject result = new JSONObject();
        result.put("result", "ok");
        JSONObject json = toJson(problemDepot.getById(problem_id));
        result.put("problem", json);
        return result.toString();
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
}