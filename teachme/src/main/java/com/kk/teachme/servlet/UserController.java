package com.kk.teachme.servlet;

import com.kk.teachme.db.ProblemDepot;
import com.kk.teachme.db.UserDepot;
import com.kk.teachme.db.UserProblemDepot;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {

    @Autowired
    UserDepot userDepot;

    @Autowired
    ProblemDepot problemDepot;

    @Autowired
    UserProblemDepot userProblemDepot;

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

            int id = jUser.getInt("user_id");

            request.getSession(true).setAttribute("user", userDepot.get(id));

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
        model.addAttribute("solved", userProblemDepot.getSolvedProblemsCount(user_id));
        model.addAttribute("all", problemDepot.getProblemsCount());
        return "user";
    }

    private String getCurrentAddress() {
        if (System.getProperty("user.name").equals("teachme")) {
            return "teachme.cloudapp.net";
        }
        return "teachme.cloudapp.net:8080";
    }

}
