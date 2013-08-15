package com.kk.teachme.servlet.signin;

import com.kk.teachme.db.UserDepot;
import com.kk.teachme.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.social.vkontakte.api.VKontakteProfile;
import org.springframework.web.context.request.NativeWebRequest;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SimpleSignInAdapter implements SignInAdapter {

    private final RequestCache requestCache;

    @Autowired
    UserDepot userDepot;

    @Inject
    public SimpleSignInAdapter(final RequestCache requestCache) {
        this.requestCache = requestCache;
    }

    @Override
    public String signIn(String localUserId, Connection<?> connection, NativeWebRequest request) {
        SignInUtils.signIn(localUserId);

        final VKontakte vkontakte = (VKontakte) connection.getApi();
        final VKontakteProfile profile = vkontakte.usersOperations().getProfile();

        User user = new User(profile.getUid(), profile.getFirstName(), profile.getLastName());

        if (!userDepot.contains(user.getUsername())) {

            user.setId(userDepot.addObject(user));
        } else {

            user = userDepot.getByUsername(user.getUsername());
        }

        ((HttpServletRequest)request.getNativeRequest()).getSession(true).setAttribute("user", user);

        return extractOriginalUrl(request);
    }

    private String extractOriginalUrl(NativeWebRequest request) {
        HttpServletRequest nativeReq = request.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse nativeRes = request.getNativeResponse(HttpServletResponse.class);
        SavedRequest saved = requestCache.getRequest(nativeReq, nativeRes);
        if (saved == null) {
            return null;
        }
        requestCache.removeRequest(nativeReq, nativeRes);
        removeAutheticationAttributes(nativeReq.getSession(false));
        return saved.getRedirectUrl();
    }

    private void removeAutheticationAttributes(final HttpSession session) {
        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

}