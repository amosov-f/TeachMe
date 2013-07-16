package com.kk.teachme.servlet;

import com.kk.teachme.db.ProblemDepot;
import com.kk.teachme.db.StatusDepot;
import com.kk.teachme.db.UserDepot;
import com.kk.teachme.model.Problem;
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

@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    UserDepot userDepot;

    @Autowired
    ProblemDepot problemDepot;

    @Autowired
    StatusDepot statusDepot;

    @RequestMapping(value = "/login")
    public String loginForm(Model model) {
        return "login";
    }

    @RequestMapping(value = "/login_user")
    public String loginUser(@RequestParam String userName, HttpServletRequest request, Model model) {
        boolean userExists = userDepot.checkIfExists(userName);
        String resultMessage = null;
        if (!userExists) {
            resultMessage = "Error! User not exists";
        } else {
            HttpSession session = request.getSession(true);
            session.setAttribute("username", userName);
            resultMessage = "ok";
        }
        model.addAttribute("result", resultMessage);
        return "result";
    }

    @RequestMapping(value = "/reg_user")
    public String regUser(@RequestParam String userName, Model model) {
        userName = userName.trim();
        boolean userExists = userDepot.checkIfExists(userName);
        String resultMessage = null;
        if (userExists) {
            resultMessage = "Error! user already exists";
        } else {
            userDepot.addObject(new User(userName));
            resultMessage = "ok";
        }
        model.addAttribute("result", resultMessage);
        return "result";
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
