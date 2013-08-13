package com.kk.teachme.servlet.signup;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.kk.teachme.model.User;
import com.kk.teachme.servlet.signin.SignInUtils;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.social.vkontakte.api.VKontakteProfile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

@Controller
public class SignupController {
    @RequestMapping(value="/signup", method=RequestMethod.GET)
    public String signup(WebRequest request) {
        final Connection<?> connection = ProviderSignInUtils.getConnection(request);
        Preconditions.checkNotNull(connection);
        Preconditions.checkArgument(
                connection.getApi() instanceof VKontakte, "expected VKontakte request");

        final VKontakte vkontakte = (VKontakte) connection.getApi();
        final VKontakteProfile profile = vkontakte.usersOperations().getProfile();
        final User user =
            new User(profile.getScreenName(), profile.getFirstName(), profile.getLastName());

        SignInUtils.signin(user.getUsername());
        ProviderSignInUtils.handlePostSignUp(user.getUsername(), request);
        return "redirect:/";
    }
}