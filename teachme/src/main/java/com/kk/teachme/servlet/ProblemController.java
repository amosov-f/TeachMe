package com.kk.teachme.servlet;

import com.kk.teachme.checker.Checker;
import com.kk.teachme.db.ProblemDepot;
import com.kk.teachme.db.SolutionDepot;
import com.kk.teachme.db.TagDepot;
import com.kk.teachme.db.UserDepot;
import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Tag;
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
            return JSONCreator.errorJSON("Incorrect id");
        }
        JSONObject json = JSONCreator.valueOf(problem);
        return JSONCreator.resultJSON(json).toString();
    }

    @RequestMapping(value = "/by_tag", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getProblemsByTagId(@RequestParam int tag_id) throws JSONException {
        Tag tag = tagDepot.getCached(tag_id);
        if (tag == null) {
            return JSONCreator.errorJSON("Incorrect id");
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

    @RequestMapping(value = "/add_tag_to_problem", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String addTagToProblem(@RequestParam int problem_id, @RequestParam int tag_id) throws JSONException {
        Problem problem = problemDepot.getById(problem_id);
        if (problem == null) {
            return JSONCreator.errorJSON("Incorrect problem id");
        }

        Tag tag = tagDepot.getCached(tag_id);
        if (tag == null) {
            return JSONCreator.errorJSON("Incorrect tag id");
        }

        if (!problemDepot.addTagToProblem(problem, tag)) {
            return JSONCreator.errorJSON("This tag already exists");
        }

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
            return JSONCreator.errorJSON("Incorrect tag id");
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
            return JSONCreator.errorJSON("Incorrect problem id");
        }
        problemDepot.changeProblemStatement(problem, new_text);
        return JSONCreator.okJson().toString();
    }

    @RequestMapping(value = "/tasksWithTag", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getTaskNumberByTag(@RequestParam int tag_id) throws JSONException {
        Tag tag = tagDepot.getCached(tag_id);
        if (tag == null) {
            return JSONCreator.errorJSON("No tag");
        }
        JSONObject result = JSONCreator.okJson();
        result.put("count", problemDepot.getTaskNumberByTag(tag));
        return result.toString();
    }

    @RequestMapping(value = "/check_answer", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String checkAnswer(@RequestParam int problem_id, @RequestParam String user_answer, HttpServletRequest request) throws JSONException {
        Checker.SolveStatus answerStatus = solutionDepot.check(problem_id, user_answer);
        String methodAnswer = null;
        switch (answerStatus)  {
            case CORRECT:   methodAnswer =   JSONCreator.okJson().toString();
                            break;
            case INCORRECT: methodAnswer = JSONCreator.errorJSON("incorrect answer").toString();
                            break;
            case INVALID:   methodAnswer =  JSONCreator.errorJSON("bad answer type").toString();
                            break;
        }
        return methodAnswer;
    }


}