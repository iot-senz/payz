package com.score.payz.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.score.payz.R;
import com.score.payz.exceptions.NoUserException;
import com.score.payz.services.RemoteSenzService;
import com.score.payz.utils.PreferenceUtils;

/**
 * Empty launch activity, this will determine which activity to launch
 * 1. Login activity
 * 2. Home activity
 *
 * @author erangaeb@gmail.com (eranga herath)
 */
public class LaunchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        // determine where to go
        try {
            PreferenceUtils.getUser(this);

            // start service
            initSenzService();

            // have user, so move to home
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        } catch (NoUserException e) {
            e.printStackTrace();

            // no user, so move to registration
            Intent intent = new Intent(this, RegistrationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    /**
     * Initialize senz service
     */
    private void initSenzService() {
        // start service from here
        Intent serviceIntent = new Intent(this, RemoteSenzService.class);
        startService(serviceIntent);
    }
}