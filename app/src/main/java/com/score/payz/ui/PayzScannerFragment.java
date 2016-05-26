package com.score.payz.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.score.payz.R;
import com.score.payz.pojos.Payz;
import com.score.payz.utils.JSONUtils;

import org.json.JSONException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by eranga on 5/26/16.
 */
public class PayzScannerFragment extends Fragment implements ZXingScannerView.ResultHandler {

    private static final String TAG = PayzScannerFragment.class.getName();

    private ZXingScannerView scannerView;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        scannerView = new ZXingScannerView(getActivity());
        return scannerView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initActionBar();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
        super.onStop();
        scannerView.stopCamera();
    }

    /**
     * Initialize UI components
     */
    private void initActionBar() {
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/vegur_2.otf");

        // Set up action bar.
        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("PayZ");
        actionBar.setBackgroundDrawable(new ColorDrawable(0xffd26c6c));

        // set custom font for
        //  1. action bar title
        //  2. other ui texts
        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitle = (TextView) (getActivity().findViewById(titleId));
        actionBarTitle.setTextColor(getResources().getColor(R.color.white));
        actionBarTitle.setTypeface(typeface);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleResult(Result result) {
        Log.v(TAG, "Scan result " + result.getText());
        Log.v(TAG, "Scan barcode format " + result.getBarcodeFormat().toString());

        // create Payz object
        try {
            // parse JSON and get Pay
            Payz payz = JSONUtils.getPay(result.getText());

            // launch pay activity
            Intent mapIntent = new Intent(getActivity(), PayzActivity.class);
            mapIntent.putExtra("EXTRA", payz);
            startActivity(mapIntent);
            getActivity().overridePendingTransition(R.anim.bottom_in, R.anim.stay_in);
        } catch (JSONException e) {
            e.printStackTrace();

            Toast.makeText(getActivity(), "[ERROR] Invalid data", Toast.LENGTH_LONG).show();
        }
    }
}
