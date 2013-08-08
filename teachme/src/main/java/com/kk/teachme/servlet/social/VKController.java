package com.kk.teachme.servlet.social;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class VKController {
    private static final String CLIENT_ID = "3810701";
    private static final String CLIENT_SECRET = "4FcroEDLVwMkKYpoRBBV";

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
        return new AuthorizationCodeFlow.Builder(BearerToken.queryParameterAccessMethod(),
            new NetHttpTransport(),
            new JacksonFactory(),
            new GenericUrl("https://oauth.vk.com/access_token"),
            new ClientParametersAuthentication(CLIENT_ID, CLIENT_SECRET),
            CLIENT_ID,
            "https://oauth.vk.com/authorize").build();
    }

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath("/oauth2callback");
        return url.build();
    }

    @Override
    protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
        return null;
    }
}
