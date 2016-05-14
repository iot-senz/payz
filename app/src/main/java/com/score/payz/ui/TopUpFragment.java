package com.score.payz.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.score.payz.R;

/**
 * TopUp fragment
 *
 * @author erangaeb@gmail.com (eranga bandra)
 */
public class TopUpFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    // custom font
    private Typeface typeface;

    // UI components
    private TextView textViewDollar10;
    private TextView textViewDollar20;
    private TextView textViewDollar50;
    private TextView textViewDollar100;
    private TextView textViewDollar200;

    private RelativeLayout relativeLayoutDollar10;
    private RelativeLayout relativeLayoutDollar20;
    private RelativeLayout relativeLayoutDollar50;
    private RelativeLayout relativeLayoutDollar100;
    private RelativeLayout relativeLayoutDollar200;

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
        return inflater.inflate(R.layout.topup_fragment_layout, container, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initUi();
        initActionBar();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * Initialize UI components
     */
    private void initUi() {
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/vegur_2.otf");

        relativeLayoutDollar10 = (RelativeLayout) getActivity().findViewById(R.id.dollar10_button);
        relativeLayoutDollar20 = (RelativeLayout) getActivity().findViewById(R.id.dollar20_button);
        relativeLayoutDollar50 = (RelativeLayout) getActivity().findViewById(R.id.dollar50_button);
        relativeLayoutDollar100 = (RelativeLayout) getActivity().findViewById(R.id.dollar100_button);
        relativeLayoutDollar200 = (RelativeLayout) getActivity().findViewById(R.id.dollar200_button);

        relativeLayoutDollar10.setOnClickListener(TopUpFragment.this);
        relativeLayoutDollar20.setOnClickListener(TopUpFragment.this);
        relativeLayoutDollar50.setOnClickListener(TopUpFragment.this);
        relativeLayoutDollar100.setOnClickListener(TopUpFragment.this);
        relativeLayoutDollar200.setOnClickListener(TopUpFragment.this);

        textViewDollar10 = (TextView) getActivity().findViewById(R.id.dollar10_text);
        textViewDollar20 = (TextView) getActivity().findViewById(R.id.dollar20_text);
        textViewDollar50 = (TextView) getActivity().findViewById(R.id.dollar50_text);
        textViewDollar100 = (TextView) getActivity().findViewById(R.id.dollar100_text);
        textViewDollar200 = (TextView) getActivity().findViewById(R.id.dollar200_text);

        textViewDollar10.setTypeface(typeface, Typeface.BOLD);
        textViewDollar20.setTypeface(typeface, Typeface.BOLD);
        textViewDollar50.setTypeface(typeface, Typeface.BOLD);
        textViewDollar100.setTypeface(typeface, Typeface.BOLD);
        textViewDollar200.setTypeface(typeface, Typeface.BOLD);
    }

    /**
     * Initialize UI components
     */
    private void initActionBar() {
        // Set up action bar.
        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Top up");
        actionBar.setBackgroundDrawable(new ColorDrawable(0xffb5c976));

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
    public void onClick(View v) {
        if (v == relativeLayoutDollar10) {
            navigateToTopUpActivity("10");
        } else if (v == relativeLayoutDollar20) {
            navigateToTopUpActivity("20");
        } else if (v == relativeLayoutDollar50) {
            navigateToTopUpActivity("50");
        } else if (v == relativeLayoutDollar100) {
            navigateToTopUpActivity("100");
        } else if (v == relativeLayoutDollar200) {
            navigateToTopUpActivity("200");
        }
    }

    /**
     * Launch TopUpActivity from here
     *
     * @param amount amount to topup
     */
    private void navigateToTopUpActivity(String amount) {
        Intent intent = new Intent(this.getActivity(), TopUpActivity.class);
        startActivity(intent);
        this.getActivity().overridePendingTransition(R.anim.bottom_in, R.anim.stay_in);
    }
}
