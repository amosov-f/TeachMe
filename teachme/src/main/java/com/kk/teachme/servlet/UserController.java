package com.kk.teachme.servlet;

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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

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
    public String getProblemPanel(@RequestParam int problem_id, HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("problem", problemDepot.get(problem_id));
        return "user_problem/user_problem_panel";
    }

    @RequestMapping(value = "/user_problem_{problem_id:\\d+}")
    public String getUserProblem(
            @PathVariable int problem_id,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Boolean in_mind,
            HttpServletRequest request,
            Model model
    ) throws UnsupportedEncodingException {
        if (request.getSession().getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("problem", problemDepot.get(problem_id));
        model.addAttribute("tags", tags);
        model.addAttribute("inMind", in_mind);
        model.addAttribute("tagList", parseTagsString(tags));

        return "problem";
    }

    @RequestMapping(value = "/problems")
    public String getProblems(
            @RequestParam(required = false) Integer problem_id,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Boolean in_mind,
            HttpServletRequest request,
            Model model
    ) throws UnsupportedEncodingException {
        if (request.getSession().getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User)request.getSession().getAttribute("user");

        List<UserProblem> userProblems;
        userProblems = userProblemDepot.getAllUserProblems(user.getId());

        model.addAttribute("userProblemList", userProblems);
        model.addAttribute("tagList", tagDepot.getAllTags());
        if (problem_id != null) {
            model.addAttribute("problemId", problem_id);
        }
        if (tags != null && !tags.isEmpty()) {
            model.addAttribute("tags", tags);
        }
        if (in_mind != null) {
            model.addAttribute("inMind", in_mind);
        }

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
    public String getUserProblemList(
            @RequestParam String tags,
            @RequestParam String filter,
            @RequestParam boolean in_mind,
            @RequestParam int from,
            @RequestParam int to,
            HttpServletRequest request,
            Model model
    ) throws UnsupportedEncodingException {
        if (request.getSession().getAttribute("user") == null) {
            return null;
        }
        User user = (User)request.getSession().getAttribute("user");

        List<UserProblem> userProblems =
                userProblemDepot.getByFilters(user.getId(), parseTagsString(tags), filter, in_mind, from, to);

        model.addAttribute("userProblemList", userProblems);

        return "user_problem/user_problem_list";
    }

    @RequestMapping(value = "/easier_user_problem")
    public String getEasierUserProblem(
            @RequestParam int problem_id,
            @RequestParam String tags,
            @RequestParam boolean in_mind,
            HttpServletRequest request,
            Model model
    ) throws UnsupportedEncodingException {
        List<UserProblem> userProblems = getByFilters(tags, "unsolved", in_mind, request);
        userProblems = filterByComplexity(userProblems, 1, getById(problem_id, request).getProblem().getComplexity() - 1);
        if (userProblems == null || userProblems.isEmpty()) {
            return "null";
        }

        model.addAttribute("problem", userProblems.get(userProblems.size() - 1).getProblem());

        return "user_problem/user_problem_panel";
    }

    @RequestMapping(value = "/similar_user_problem")
    public String getSimilarUserProblem(
            @RequestParam int problem_id,
            @RequestParam String tags,
            @RequestParam boolean in_mind,
            HttpServletRequest request,
            Model model
    ) throws UnsupportedEncodingException {
        List<UserProblem> userProblems = getByFilters(tags, "unsolved", in_mind, request);
        int complexity = getById(problem_id, request).getProblem().getComplexity();
        userProblems = filterByComplexity(userProblems, complexity, complexity);
        if (userProblems == null || userProblems.isEmpty()) {
            return "null";
        }

        for (int i = 0; i < userProblems.size(); ++i) {
            if (userProblems.get(i).getProblem().getId() == problem_id) {
                userProblems.remove(i);
                break;
            }
        }
        if (userProblems.isEmpty()) {
            return "null";
        }

        model.addAttribute("problem", userProblems.get(new Random().nextInt(userProblems.size())).getProblem());

        return "user_problem/user_problem_panel";
    }

    @RequestMapping(value = "/harder_user_problem")
    public String getHarderUserProblem(
            @RequestParam int problem_id,
            @RequestParam String tags,
            @RequestParam boolean in_mind,
            HttpServletRequest request,
            Model model
    ) throws UnsupportedEncodingException {
        List<UserProblem> userProblems = getByFilters(tags, "unsolved", in_mind, request);
        userProblems = filterByComplexity(userProblems, getById(problem_id, request).getProblem().getComplexity() + 1, 10);
        if (userProblems == null || userProblems.isEmpty()) {
            return "null";
        }

        model.addAttribute("problem", userProblems.get(0).getProblem());
        return "user_problem/user_problem_panel";
    }

    private UserProblem getById(int problemId, HttpServletRequest request) {
        if (request.getSession().getAttribute("user") == null) {
            return null;
        }

        return userProblemDepot.getByIds(((User) request.getSession().getAttribute("user")).getId(), problemId);
    }

    private List<UserProblem> getByFilters(
            String tags,
            String filter,
            boolean inMind,
            HttpServletRequest request
    ) throws UnsupportedEncodingException {
        if (request.getSession().getAttribute("user") == null) {
            return null;
        }
        User user = (User)request.getSession().getAttribute("user");

        List<UserProblem> userProblems = new ArrayList<>();
        for (UserProblem userProblem : getByTags(tags, request)) {
            if (userProblem.getProblem().isInMind() || !inMind) {
                userProblems.add(userProblem);
            }
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

        return userProblems;
    }

    private List<UserProblem> getByTags(String tags, HttpServletRequest request) throws UnsupportedEncodingException {
        User user = (User)request.getSession().getAttribute("user");

        if (tags == null || tags.isEmpty()) {
            return userProblemDepot.getAllUserProblems(user.getId());
        }

        return userProblemDepot.getByTagList(user.getId(), parseTagsString(tags));
    }

    private List<UserProblem> filterByComplexity(List<UserProblem> userProblems, int min, int max) {
        if (userProblems == null) {
            return null;
        }
        List<UserProblem> result = new ArrayList<>();

        for (UserProblem userProblem : userProblems) {
            if (min <= userProblem.getProblem().getComplexity() && userProblem.getProblem().getComplexity() <= max) {
                result.add(userProblem);
            }
        }

        Collections.sort(
                result,
                (o1, o2) -> new Integer(o1.getProblem().getComplexity()).compareTo(o2.getProblem().getComplexity())
        );

        return result;
    }

    private List<Tag> parseTagsString(String tags) throws UnsupportedEncodingException {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }

        List<Tag> tagList = new ArrayList<>();
        for (String tag : URLDecoder.decode(tags, "UTF-8").split(",")) {
            if (tagDepot.getByName(tag) != null) {
                tagList.add(tagDepot.getByName(tag));
            }
        }
        return tagList;
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
            model.addAttribute("address", getCurrentAddress());
            return "login";
        }
        return "redirect:/problems";
    }

    @RequestMapping(value = "/vklogin")
    public String vkLogIn(@RequestParam String code, HttpServletRequest request) {
        if (request.getSession().getAttribute("user") != null) {
            return "redirect:/problems";
        }

        DefaultHttpClient httpСlient = new DefaultHttpClient();
        HttpParams httpParams = new BasicHttpParams();
        httpParams.setIntParameter("http.socket.timeout", 30000);
        httpСlient.setParams(httpParams);

        String authorizeQuery =
                "https://oauth.vk.com/access_token?client_id=4269266&client_secret=BfKJ25hQcSJodd2NSkvQ&code=" +
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

            jUser = (JSONObject) ((JSONArray) (new JSONObject(response).get("response"))).get(0);

            User user = new User(username, (String) jUser.get("first_name"), (String) jUser.get("last_name"));

            if (!userDepot.contains(user.getUsername())) {
                user.setId(userDepot.add(user));
            } else {
                user = userDepot.getByUsername(user.getUsername());
            }

            request.getSession(true).setAttribute("user", user);

        } catch (Exception e) {
            throw new RuntimeException(e);
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
        model.addAttribute("user", userDepot.get(user_id));
        model.addAttribute("solved", userProblemDepot.getSolvedProblems(user_id).size());
        model.addAttribute("all", userProblemDepot.getAllUserProblems(user_id).size());
        return "user";
    }

    private String getCurrentAddress() {
       // System.out.println(System.getProperty("user.name"));
        if (System.getProperty("user.name").equals("teachme")) {
            return "teachme.cloudapp.net";
        }
        return "teachme.cloudapp.net:8080";
    }

}
