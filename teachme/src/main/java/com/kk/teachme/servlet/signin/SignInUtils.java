package com.kk.teachme.servlet.signin;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
* User: katya
* Date: 8/14/13
* Time: 2:13 AM
*/
public class SignInUtils {
    public static void signin(final String userId) {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(userId, null, null));
    }
}
