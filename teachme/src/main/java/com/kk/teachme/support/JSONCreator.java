package com.kk.teachme.support;

import com.kk.teachme.db.UserDepot;
import com.kk.teachme.model.User;
import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Tag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONCreator {

    public static String errorJSON(String error_text) throws JSONException {
        /* create json with error from error_text */
        JSONObject result = new JSONObject();
        result.put("result", "error");
        result.put("error", error_text);
        return result.toString();
    }

    public static JSONObject resultJSON(JSONObject json) throws JSONException {
        /* create json with result from json */
        JSONObject result = okJson();
        result.put("problem", json);
        return result;
    }

    public static JSONObject okJson() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("result", "ok");
        return result;
    }

    public static JSONObject valueOf(Problem problem) throws JSONException {
        JSONObject json = new JSONObject();
        JSONArray tags = new JSONArray();
        for (Tag t : problem.getTags()) {
            tags.put(t.getName());
        }
        json.put("tags", tags);
        json.put("statement", problem.getStatement());
        json.put("name", problem.getName());
        json.put("id", problem.getId());
        return json;
    }

    public static JSONObject valueOf(User user) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", user.getId());
        json.put("login", user.getLogin());
        //JSONArray tags = new JSONArray();
        //for (Tag t : problem.getTags()) {
        //    tags.put(t.getName());
        //}
        //json.put("tags", tags);
        return json;
    }

    public static JSONObject valueOf(Tag tag) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", tag.getId());
        json.put("name", tag.getName());
        return json;
    }

}
