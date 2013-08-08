package com.kk.teachme.servlet.social;

import org.jetbrains.annotations.Nullable;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;

@Controller
public class VKController {
    @Inject
    private ConnectionRepository connectionRepository;

    @RequestMapping(value="/vkontakte", method=RequestMethod.GET)
    public String home(final Model model) {
        final @Nullable Connection<VKontakte> connection = connectionRepository.findPrimaryConnection(VKontakte.class);
        if (connection == null) {
            return "redirect:/connect/vkontakte";
        }

        model.addAttribute("profile", connection.getApi().usersOperations().getProfile());
        return "vkontakte/profile";
    }
}
