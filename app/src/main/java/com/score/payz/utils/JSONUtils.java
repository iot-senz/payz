package com.score.payz.utils;

import com.score.payz.pojos.Payz;
import com.score.payz.pojos.TopUp;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Utility class to handle JSON
 *
 * @author eranga bandara(erangaeb@gmail.com)
 */
public class JSONUtils {

    /**
     * Parse JSON string and populate Payz object
     *
     * @param jsonString json string
     * @return payz object
     * @throws JSONException
     */
    public static Payz getPay(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);

        String acc = jsonObject.getString("acc");
        String amnt = jsonObject.getString("amnt");

        return new Payz(acc, amnt, getCurrentTime());
    }

    /**
     * Create JSON string from TopUp
     *
     * @param topUp TopUp object
     * @return JSON string
     * @throws JSONException
     */
    public static String getTopUpJson(TopUp topUp) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("acc", topUp.getAccount());
        jsonObject.put("amnt", topUp.getAmount());

        return jsonObject.toString();
    }

    /**
     * Get current date and time as transaction time
     * format - yyyy:MM:dd HH:mm:ss
     *
     * @return
     */
    private static String getCurrentTime() {
        //date format
        String DATE_FORMAT_NOW = "yyyy.MM.dd HH:mm:ss";

        // generate time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_NOW);

        return simpleDateFormat.format(calendar.getTime());
    }
}
