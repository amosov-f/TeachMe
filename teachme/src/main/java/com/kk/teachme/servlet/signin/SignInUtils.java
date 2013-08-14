package com.kk.teachme.servlet.signin;

import com.kk.teachme.db.UserDepot;
import com.kk.teachme.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
* User: katya
* Date: 8/14/13
* Time: 2:13 AM
*/
public class SignInUtils {

    public static void signIn(final String username) {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(username, null, null));

    }

}
