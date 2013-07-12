package com.kk.teachme.servlet;

import com.kk.teachme.model.Tag;
import org.springframework.ui.Model;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Mary
 * Date: 12.07.13
 * Time: 20:55
 * To change this template use File | Settings | File Templates.
 */
public class AdminController {
    public String addProblem(String name, String text, int checkerId, String answer, List<String> tags) {
        return null;
    }

    public String admin(Model model) {
        //return JSP with admin page

        //collect all checker
        //collect all tags
        //put it to Model
        return "admin";
    }

    public String adminList(Model model, String tag) {
        //show all problems by tag (maybe null)
        return "problems";
    }
}
