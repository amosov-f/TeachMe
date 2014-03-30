package com.kk.teachme.db;

import com.kk.teachme.model.User;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class UserDepot {

    public User get(int id) {
        try {
            String userInfoGet = "https://api.vk.com/method/users.get?user_ids=" + id;

            HttpEntity en = new DefaultHttpClient().execute(new HttpGet(userInfoGet)).getEntity();
            String response = EntityUtils.toString(en);
            en.consumeContent();

            JSONObject jUser = (JSONObject) ((JSONArray) (new JSONObject(response).get("response"))).get(0);

            return new User(id, (String) jUser.get("first_name"), (String) jUser.get("last_name"));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
