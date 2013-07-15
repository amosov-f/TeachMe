package com.kk.teachme.servlet;

import com.kk.teachme.db.ProblemDepot;
import com.kk.teachme.db.StatusDepot;
import com.kk.teachme.db.UserDepot;
import com.kk.teachme.model.Problem;
import com.kk.teachme.model.User;
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

@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    UserDepot userDepot;

    @Autowired
    ProblemDepot problemDepot;

    @Autowired
    StatusDepot statusDepot;

    @RequestMapping(value = "/user_{user_id:\\d+}", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getUser(@PathVariable int user_id) throws JSONException {
        User user = userDepot.getById(user_id);
        if (user == null) {
            return JSONCreator.errorJSON("Incorrect id");
        }

        JSONObject result = JSONCreator.okJson();
        result.put("user", JSONCreator.valueOf(user));

        JSONArray array = new JSONArray();
        for (Problem problem : userDepot.getSolvedProblems(user)) {
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
            return JSONCreator.errorJSON("Incorrect user id");
        }

        Problem problem = problemDepot.getById(problem_id);
        if (problem == null) {
            return JSONCreator.errorJSON("Incorrect problem id");
        }

        JSONObject result = JSONCreator.okJson();
        result.put("status", statusDepot.getStatus(user, problem));

        return JSONCreator.resultJSON(result).toString();
    }

}