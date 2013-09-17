package com.kk.teachme.servlet;

import com.google.api.client.json.*;
import com.kk.teachme.checker.SolveStatus;
import com.kk.teachme.db.*;
import com.kk.teachme.model.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    UserDepot userDepot;

    @Autowired
    ProblemDepot problemDepot;

    @Autowired
    SolutionDepot solutionDepot;

    @Autowired
    TagDepot tagDepot;

    @Autowired
    StatusDepot statusDepot;

    @Autowired
    UserProblemDepot userProblemDepot;

    @RequestMapping(value = "/user_problem_panel")
    public String getProblem(@RequestParam int problem_id, HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("problem", problemDepot.getById(problem_id));
        return "user_problem/user_problem_panel";
    }

    @RequestMapping(value = "/problems")
    public String user(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User)request.getSession().getAttribute("user");

        List<UserProblem> userProblems;
        userProblems = userProblemDepot.getAllUserProblems(user.getId());

        model.addAttribute("userProblemList", userProblems);
        model.addAttribute("tagList", tagDepot.getAllTags());

        return "problems";
    }

    @RequestMapping(value = "/submit")
    public String submit(@RequestParam int problem_id, @RequestParam String solution_text, HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("user") == null) {
            return "redirect:/login";
        }

        User user = (User)request.getSession().getAttribute("user");
        Solution solution = solutionDepot.getSolution(problem_id);

        SolveStatus solveStatus = solution.getChecker().check(solution_text, solution.getSolutionText());

        if (solveStatus == SolveStatus.CORRECT) {
            userProblemDepot.attempt(user.getId(), problem_id, true);
        } else if (solveStatus == SolveStatus.INCORRECT) {
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
        if (request.getSession().getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User)request.getSession().getAttribute("user");

        Status status = userProblemDepot.getStatus(user.getId(), problem_id);

        if (!status.equals(Status.SOLVED)) {
            userProblemDepot.addUserProblem(user.getId(), problem_id);
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
    public String getByFilters(@RequestParam String tags, @RequestParam String filter, HttpServletRequest request, Model model) throws UnsupportedEncodingException {
        if (request.getSession().getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User)request.getSession().getAttribute("user");

        List<UserProblem> userProblems;

        if (tags == null || tags.isEmpty()) {
            userProblems = userProblemDepot.getAllUserProblems(user.getId());
        } else {
            List<Tag> tagList = new ArrayList<Tag>();
            for (String tag : URLDecoder.decode(tags, "UTF-8").split(",")) {
                if (tagDepot.getByName(tag) != null) {
                    tagList.add(tagDepot.getByName(tag));
                }
            }
            userProblems = userProblemDepot.getByTagList(user.getId(), tagList);
        }

        if (filter == null || filter.isEmpty()) {
            userProblems.retainAll(userProblemDepot.getAllUserProblems(user.getId()));
        } else if (filter.equals("unsolved")) {
            userProblems.retainAll(userProblemDepot.getUnsolvedProblems(user.getId()));
        } else if (filter.equals("read")) {
            userProblems.retainAll(userProblemDepot.getReadProblems(user.getId()));
        } else if (filter.equals("solved"))  {
            userProblems.retainAll(userProblemDepot.getSolvedProblems(user.getId()));
        } else if (filter.equals("attempted")) {
            userProblems.retainAll(userProblemDepot.getAttemptedProblems(user.getId()));
        }

        model.addAttribute("userProblemList", userProblems);

        return "user_problem/user_problem_list";
    }

    @RequestMapping(value = "/")
    public String home(HttpServletRequest request) {
        if (request.getSession().getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "redirect:/problems";
    }

    @RequestMapping(value = "/login")
    public String logIn(Model model, HttpServletRequest request) {
        if (request.getSession().getAttribute("user") == null) {
            model.addAttribute("adress", getCurrentAddress());
            return "login";
        }
        return "redirect:/problems";
    }

    @RequestMapping(value = "/vklogin")
    public String vkLogIn(@RequestParam("code") String code, HttpServletRequest request) {
        if (request.getSession().getAttribute("user") != null) {
            return "redirect:/problems";
        }

        DefaultHttpClient httpСlient = new DefaultHttpClient();
        HttpParams httpParams = new BasicHttpParams();
        httpParams.setIntParameter("http.socket.timeout", 30000);
        httpСlient.setParams(httpParams);

        String authorizeQuery =
                "https://oauth.vk.com/access_token?client_id=3810701&client_secret=4FcroEDLVwMkKYpoRBBV&code=" +
                code + "&redirect_uri=http://" + getCurrentAddress() + "/vklogin";

        try {
            HttpEntity en = httpСlient.execute(new HttpGet(authorizeQuery)).getEntity();
            String response = EntityUtils.toString(en);
            en.consumeContent();

            JSONObject jUser = new JSONObject(response);

            String username = jUser.get("user_id").toString();

            String userInfoGet = "https://api.vk.com/method/users.get?user_ids=" + username;

            en = httpСlient.execute(new HttpGet(userInfoGet)).getEntity();
            response = EntityUtils.toString(en);
            en.consumeContent();

            jUser = (JSONObject)((JSONArray)(new JSONObject(response).get("response"))).get(0);

            User user = new User(username, (String)jUser.get("first_name"), (String)jUser.get("last_name"));

            if (!userDepot.contains(user.getUsername())) {
                user.setId(userDepot.addObject(user));
            } else {
                user = userDepot.getByUsername(user.getUsername());
            }

            request.getSession(true).setAttribute("user", user);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/problems";
    }

    @RequestMapping(value = "/logout")
    public String logOut(HttpServletRequest request) {
        request.getSession().setAttribute("user", null);
        return "redirect:/login";
    }

    @RequestMapping(value = "/user_{user_id:\\d+}")
    public String user(@PathVariable int user_id, Model model) {
        model.addAttribute("user", userDepot.getById(user_id));
        model.addAttribute("solved", userProblemDepot.getSolvedProblems(user_id).size());
        model.addAttribute("all", userProblemDepot.getAllUserProblems(user_id).size());
        return "user";
    }

    private String getCurrentAddress() {
        String userName = System.getProperty("user.name");
        if ("teachme".equals(userName)) {
            return "friendrent.ru:8083";
        }
        return "localhost:8083";
    }

}
