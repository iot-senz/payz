package com.score.payz.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.score.senzc.pojos.User;
import com.score.payz.R;
import com.score.payz.exceptions.NoUserException;

/**
 * Utility class to deal with Share Preferences
 *
 * @author erangaeb@gmail.com (eranga herath)
 */
public class PreferenceUtils {

    /**
     * Save user credentials in shared preference
     *
     * @param context application context
     * @param user    logged-in user
     */
    public static void saveUser(Context context, User user) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        //keys should be constants as well, or derived from a constant prefix in a loop.
        editor.putString("id", user.getId());
        editor.putString("username", user.getUsername());
        editor.commit();
    }

    /**
     * Get user details from shared preference
     *
     * @param context application context
     * @return user object
     */
    public static User getUser(Context context) throws NoUserException {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_MULTI_PROCESS);
        String id = preferences.getString("id", "0");
        String username = preferences.getString("username", "");

        if (username.isEmpty())
            throw new NoUserException();

        User user = new User(id, username);
        user.setUsername(username);
        return user;
    }

    /**
     * Save public/private keys in shared preference,
     *
     * @param context application context
     * @param key     public/private keys(encoded key string)
     * @param keyType public_key, private_key, server_key
     */
    public static void saveRsaKey(Context context, String key, String keyType) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(keyType, key);
        editor.commit();
    }

    /**
     * Get saved RSA key string from shared preference
     *
     * @param context application context
     * @param keyType public_key, private_key, server_key
     * @return key string
     */
    public static String getRsaKey(Context context, String keyType) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return preferences.getString(keyType, "");
    }

    /**
     * Save printer bluetooth address in shared preference
     *
     * @param context        application context
     * @param printerAddress printer bluetooth address
     */
    public static void savePrinterAddress(Context context, String printerAddress) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("PRINTER_ADDRESS", printerAddress);
        editor.commit();
    }

    /**
     * Get saved printer bluetooth address from shared preference
     *
     * @param context
     * @return
     */
    public static String getPrinterAddress(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return preferences.getString("PRINTER_ADDRESS", "");
    }

}
