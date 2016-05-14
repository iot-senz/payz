package com.score.payz.utils;

import com.score.payz.pojos.Payz;
import com.score.payz.pojos.TopUp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class to handle JSON
 *
 * @author eranga bandara(erangaeb@gmail.com)
 */
public class JSONUtils {
    public static Payz getPay(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);

        String acc = jsonObject.getString("acc");
        String amnt = jsonObject.getString("amnt");

        return new Payz(acc, amnt, "TIME");
    }

    public static String getTopUpJson(TopUp topUp) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("acc", topUp.getAccount());
        jsonObject.put("amnt", topUp.getAmount());

        return jsonObject.toString();
    }
}
