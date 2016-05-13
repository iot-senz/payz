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
 * Activity class for sharing
 * Implement sharing related functions
 *
 * @author erangaeb@gmail.com (eranga herath)
 */
public class TopUpListFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private static final String TAG = TopUpFragment.class.getName();

    // custom font
    private Typeface typeface;

    private TextView doller10;
    private TextView doller20;
    private TextView doller50;
    private TextView doller100;
    private TextView doller200;

    private RelativeLayout doller10Button;

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
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.topup_list_layout, container, false);

//        View v = inflater.inflate(R.layout.topup_list_layout, container, false);
//        doller10 = (TextView)v.findViewById(R.id.doller10);
//        doller10.setOnClickListener(TopUpListFragment.this);

        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initUi();
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

        // Set up action bar.
        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Top Up");
        actionBar.setBackgroundDrawable(new ColorDrawable(0xffb5c976));

        // set custom font for
        //  1. action bar title
        //  2. other ui texts
        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitle = (TextView) (getActivity().findViewById(titleId));
        actionBarTitle.setTextColor(getResources().getColor(R.color.white));
        actionBarTitle.setTypeface(typeface);

        doller10 = (TextView) getActivity().findViewById(R.id.doller10);
        doller20 = (TextView) getActivity().findViewById(R.id.doller20);
        doller50 = (TextView) getActivity().findViewById(R.id.doller50);
        doller100 = (TextView) getActivity().findViewById(R.id.doller100);
        doller200 = (TextView) getActivity().findViewById(R.id.doller200);

        doller10Button = (RelativeLayout) getActivity().findViewById(R.id.doller10Button);
        doller10Button.setOnClickListener(TopUpListFragment.this);

        doller10.setTypeface(typeface, Typeface.BOLD);
        doller20.setTypeface(typeface, Typeface.BOLD);
        doller50.setTypeface(typeface, Typeface.BOLD);
        doller100.setTypeface(typeface, Typeface.BOLD);
        doller200.setTypeface(typeface, Typeface.BOLD);
    }

    @Override
    public void onClick(View v) {

        if (v == doller10Button) {
            Intent mapIntent = new Intent(this.getActivity(), TopUpActivity.class);
            startActivity(mapIntent);
            this.getActivity().overridePendingTransition(R.anim.bottom_in, R.anim.stay_in);
        }
    }
}
