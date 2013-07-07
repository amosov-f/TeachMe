package com.kk.teachme.servlet;

import com.kk.teachme.db.ProblemDepot;
import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Tag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: akonst
 * Time: 07.06.13 23:08
 */
@Controller
@RequestMapping("/")
public class ProblemController {
    @Autowired
    ProblemDepot problemDepot;

    @RequestMapping(value = "/problem_{problem_id:\\d+}\"", produces = "application/json; charset=utf-8")
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
        JSONObject json = new JSONObject();
        json.put("id", problem.getId());
        json.put("situation", problem.getSituation());
        JSONArray tags = new JSONArray();
        for (Tag t: problem.getTags()) {
            tags.put(t.getName());
        }
        json.put("tags", tags);
        result.put("problem", json);
        return result.toString();
    }
}