package com.score.payz.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.score.payz.R;
import com.score.payz.pojos.Matm;
import com.score.payz.pojos.TopUp;
import com.score.payz.utils.ActivityUtils;
import com.score.payz.utils.JSONUtils;
import com.score.payz.utils.SenzParser;
import com.score.senzc.pojos.Senz;

import org.json.JSONException;

public class TopUpActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    private static final String TAG = TopUpActivity.class.getName();

    // deals with NFC
    private NfcAdapter nfcAdapter;

    // custom type face
    private Typeface typeface;

    // UI components
    private TextView textViewAppIcon;
    private TextView clickToPay;
    private TextView payAmountText;

    // activity deal with TopUp object
    private TopUp topUp;

    // senz message receiver
    private BroadcastReceiver senzMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Got message from Senz service");
            handleSenzMessage(intent);
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.topup_activity_layout);

        initNfc();
        initUi();
        initActionBar();
        initTopUp();

        // register broadcast receiver
        registerReceiver(senzMessageReceiver, new IntentFilter("com.score.payz.DATA_SENZ"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(senzMessageReceiver);
    }

    /**
     * Initialize NFC components
     */
    private void initNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "[ERROR] No NFC supported", Toast.LENGTH_LONG).show();
        } else {
            nfcAdapter.setNdefPushMessageCallback(this, this);
            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }

    private void initUi() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");

        textViewAppIcon = (TextView) findViewById(R.id.app_icon);
        clickToPay = (TextView) findViewById(R.id.click_to_pay);
        payAmountText = (TextView) findViewById(R.id.pay_amount_text);

        textViewAppIcon.setTypeface(typeface, Typeface.BOLD);
        clickToPay.setTypeface(typeface, Typeface.BOLD);
        payAmountText.setTypeface(typeface, Typeface.BOLD);
    }

    private void initActionBar() {
        // Set up action bar.
        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("Top Up");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        // set custom font for
        //  1. action bar title
        //  2. other ui texts
        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitle = (TextView) (findViewById(titleId));
        actionBarTitle.setTextColor(getResources().getColor(R.color.white));
        actionBarTitle.setTypeface(typeface, Typeface.BOLD);
    }

    /**
     * Initialize top up from bundle extra
     */
    private void initTopUp() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            topUp = bundle.getParcelable("EXTRA");

            if (topUp != null) {
                Log.i(TAG, "TopUP account :" + topUp.getAccount());
                Log.i(TAG, "TopUP amount :" + topUp.getAmount());

                payAmountText.setText("$" + topUp.getAmount());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        // create JSON message from TopUp
        if (topUp != null) try {
            String message = JSONUtils.getTopUpJson(topUp);

            NdefRecord ndefRecord = NdefRecord.createMime("text/plain", message.getBytes());

            return new NdefMessage(ndefRecord);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNdefPushComplete(NfcEvent event) {
        Log.d(TAG, "NFC push completed");

        // start progress dialog
        // ActivityUtils.showProgressDialog(this, "Please wait...");

        // need to run on UI thread since beam touch involved here
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // toast to notify wait
                Toast.makeText(TopUpActivity.this, "Please wait", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.stay_in, R.anim.bottom_out);
    }

    /**
     * Handle broadcast message receives
     * Need to handle registration success failure here
     *
     * @param intent intent
     */
    private void handleSenzMessage(Intent intent) {
        String action = intent.getAction();

        if (action.equals("com.score.payz.DATA_SENZ")) {
            Senz senz = intent.getExtras().getParcelable("SENZ");

            if (senz.getAttributes().containsKey("tid") && senz.getAttributes().containsKey("key")) {
                // Matm response received
                ActivityUtils.cancelProgressDialog();

                // create Matm object from senz
                Matm matm = SenzParser.getMatm(senz);

                // launch Matm activity
                Intent mapIntent = new Intent(this, MatmActivity.class);
                mapIntent.putExtra("EXTRA", matm);
                startActivity(mapIntent);
                this.finish();
                overridePendingTransition(R.anim.stay_in, R.anim.right_in);
            }
        }
    }

    /**
     * Display message dialog
     *
     * @param messageHeader message header
     * @param message       message to be display
     */
    public void displayMessageDialog(String messageHeader, String message) {
        final Dialog dialog = new Dialog(this);

        //set layout for dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.information_message_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        // set dialog texts
        TextView messageHeaderTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_header_text);
        TextView messageTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_text);
        messageHeaderTextView.setText(messageHeader);
        messageTextView.setText(message);

        // set custom font
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");
        messageHeaderTextView.setTypeface(face);
        messageHeaderTextView.setTypeface(null, Typeface.BOLD);
        messageTextView.setTypeface(face);

        //set ok button
        Button okButton = (Button) dialog.findViewById(R.id.information_message_dialog_layout_ok_button);
        okButton.setTypeface(face);
        okButton.setTypeface(null, Typeface.BOLD);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

}
