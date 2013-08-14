package com.kk.teachme.servlet.signup;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.kk.teachme.db.UserDepot;
import com.kk.teachme.model.User;
import com.kk.teachme.servlet.signin.SignInUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    UserDepot userDepot;

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signUp(WebRequest request) {
        final Connection<?> connection = ProviderSignInUtils.getConnection(request);
        Preconditions.checkNotNull(connection);
        Preconditions.checkArgument(
                connection.getApi() instanceof VKontakte, "expected VKontakte request");

        final VKontakte vkontakte = (VKontakte) connection.getApi();
        final VKontakteProfile profile = vkontakte.usersOperations().getProfile();

        User user = new User(profile.getScreenName(), profile.getFirstName(), profile.getLastName());

        SignInUtils.signIn(user.getUsername());
        ProviderSignInUtils.handlePostSignUp(user.getUsername(), request);

        return "user";
    }
}