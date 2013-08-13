package com.kk.teachme.servlet;

import org.jetbrains.annotations.Nullable;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;

@Controller
public class HomeController {
    private final @Nullable VKontakte vkontakte;

    @Inject
    public HomeController(final @Nullable VKontakte vkontakte) {
        this.vkontakte = vkontakte;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(final Model model) {
        if (vkontakte == null){
            return "redirect:/login";
        }

        final String userName = vkontakte.usersOperations().getProfile().getScreenName();
        model.addAttribute("username", userName);
        return "home";
    }

}
