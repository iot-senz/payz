package com.score.payz.ui;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.score.payz.R;
import com.score.payz.exceptions.NoUserException;
import com.score.payz.pojos.DrawerItem;
import com.score.payz.pojos.Payz;
import com.score.payz.utils.JSONUtils;
import com.score.payz.utils.PreferenceUtils;
import com.score.senzc.pojos.User;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Main activity class of MY.sensors
 * Implement navigation drawer here
 *
 * @author erangaeb@gmail.com (eranga herath)
 */
public class HomeActivity extends FragmentActivity {

    private static final String TAG = HomeActivity.class.getName();

    // Ui components
    private ListView drawerListView;
    private DrawerLayout drawerLayout;
    private RelativeLayout drawerContainer;
    private HomeActionBarDrawerToggle homeActionBarDrawerToggle;

    // drawer components
    private ArrayList<DrawerItem> drawerItemList;
    private DrawerAdapter drawerAdapter;

    // custom type face
    private Typeface typeface;

    // user components
    private CircularImageView userImage;
    private TextView username;

    // deals with NFC
    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    private IntentFilter[] nfcIntentFilters;
    private String[][] nfcTechLists;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        initNfc();
        initDrawer();
        initDrawerUser();
        initDrawerList();
        loadFragment(new PayzFragment());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();

        // enable foreground dispatch
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, nfcIntentFilters, nfcTechLists);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        super.onPause();

        // disable foreground dispatch
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNewIntent(Intent intent) {
        String action = intent.getAction();
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d(TAG, "New intent action " + action);
        Log.d(TAG, "New intent tag " + tag.toString());

        // parse through all NDEF messages and their records and pick text type only
        // we only send one NDEF message(as a JSON string)
        Parcelable[] data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (data != null) {
            NdefMessage message = (NdefMessage) data[0];
            String jsonString = new String(message.getRecords()[0].getPayload());
            Log.d(TAG, "NFC Data received, " + jsonString);

            try {
                // parse JSON and get Pay
                Payz payz = JSONUtils.getPay(jsonString);

                // launch pay activity
                Intent mapIntent = new Intent(this, PayzActivity.class);
                mapIntent.putExtra("EXTRA", payz);
                startActivity(mapIntent);
                overridePendingTransition(R.anim.bottom_in, R.anim.stay_in);
            } catch (JSONException e) {
                e.printStackTrace();

                Toast.makeText(this, "[ERROR] Invalid data", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1888 && resultCode == -1) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            userImage.setImageBitmap(photo);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteStream = byteArrayOutputStream.toByteArray();
            String encodedData = Base64.encodeToString(byteStream, Base64.DEFAULT);

            // save image in shared preference
            PreferenceUtils.saveUserImage(this, encodedData);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        return homeActionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /**
     * Initialize NFC components
     */
    private void initNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "[ERROR] No NFC supported", Toast.LENGTH_LONG).show();
        } else {
            // create an intent with tag data and deliver to this activity
            nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            // set an intent filter for all MIME data
            IntentFilter nfcDiscoveredIntentFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            try {
                nfcDiscoveredIntentFilter.addDataType("text/plain");
                nfcIntentFilters = new IntentFilter[]{nfcDiscoveredIntentFilter};
            } catch (Exception e) {
                Log.e("TagDispatch", e.toString());
            }

            // tech list
            nfcTechLists = new String[][]{new String[]{NfcF.class.getName()}};
        }
    }

    /**
     * Initialize Drawer UI components
     */
    private void initDrawer() {
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerContainer = (RelativeLayout) findViewById(R.id.drawer_container);

        // set a custom shadow that overlays the senz_map_layout content when the drawer opens
        // set up drawer listener
        //drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        homeActionBarDrawerToggle = new HomeActionBarDrawerToggle(this, drawerLayout);
        drawerLayout.setDrawerListener(homeActionBarDrawerToggle);
    }

    /**
     * Initialize drawer user image
     */
    private void initDrawerUser() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");

        userImage = (CircularImageView) findViewById(R.id.contact_image);
        username = (TextView) findViewById(R.id.home_user_text);

        // find image from shared preference and display it
        String encodedImage = PreferenceUtils.getUserImage(this);
        if (!encodedImage.isEmpty()) {
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            userImage.setImageBitmap(decodedByte);
        } else {
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.default_user_icon);
            userImage.setImageBitmap(largeIcon);
        }

        // launch camera to selfie
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                camera.putExtra("android.intent.extras.CAMERA_FACING", 1);
                startActivityForResult(camera, 1888);
            }
        });

        // display username
        try {
            User user = PreferenceUtils.getUser(this);
            username.setText("@" + user.getUsername());
            username.setTypeface(typeface, Typeface.BOLD);
        } catch (NoUserException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize Drawer list
     */
    private void initDrawerList() {
        // initialize drawer content
        // need to determine selected item according to the currently selected sensor type
        drawerItemList = new ArrayList();
        drawerItemList.add(new DrawerItem("PayZ [10.20$]", "#eada00", R.drawable.drawer_list_row_selector, R.drawable.drawer_list_row_selector, true, false));
        drawerItemList.add(new DrawerItem("Top Up", "#eada00", R.drawable.drawer_list_row_selector, R.drawable.drawer_list_row_selector, false, false));
        drawerItemList.add(new DrawerItem("History", "#eada00", R.drawable.drawer_list_row_selector, R.drawable.drawer_list_row_selector, false, false));
        drawerItemList.add(new DrawerItem("Settings", "#eada00", R.drawable.drawer_list_row_selector, R.drawable.drawer_list_row_selector, false, false));

        drawerAdapter = new DrawerAdapter(HomeActivity.this, drawerItemList);
        drawerListView = (ListView) findViewById(R.id.drawer);

        if (drawerListView != null)
            drawerListView.setAdapter(drawerAdapter);

        drawerListView.setOnItemClickListener(new DrawerItemClickListener());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        homeActionBarDrawerToggle.syncState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        homeActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Handle open/close behaviours of Navigation Drawer
     */
    private class HomeActionBarDrawerToggle extends ActionBarDrawerToggle {

        public HomeActionBarDrawerToggle(Activity mActivity, DrawerLayout mDrawerLayout) {
            super(mActivity, mDrawerLayout, R.drawable.ic_navigation_drawer, R.string.ns_menu_open, R.string.ns_menu_close);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onDrawerClosed(View view) {
            invalidateOptionsMenu();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onDrawerOpened(View drawerView) {
            invalidateOptionsMenu();
        }
    }

    /**
     * Drawer click event handler
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Highlight the selected item, update the title, and close the drawer
            // update selected item and title, then close the drawer
            drawerLayout.closeDrawer(drawerContainer);

            //  reset content in drawer list
            for (DrawerItem drawerItem : drawerItemList) {
                drawerItem.setSelected(false);
            }

            if (position == 0) {
                drawerItemList.get(0).setSelected(true);

                // set image
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.wireless_app1);
                //userImage.setImageBitmap(largeIcon);

                loadFragment(new PayzFragment());
            } else if (position == 1) {
                drawerItemList.get(1).setSelected(true);

                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.payment_icon);
                //userImage.setImageBitmap(largeIcon);

                loadFragment(new TopUpFragment());
            } else if (position == 2) {
                drawerItemList.get(2).setSelected(true);

                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.history_red);
                //userImage.setImageBitmap(largeIcon);

                loadFragment(new HistoryFragment());
            } else if (position == 3) {
                drawerItemList.get(3).setSelected(true);

                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.settings_icon);
                //userImage.setImageBitmap(largeIcon);

                loadFragment(new HistoryFragment());
            }

            drawerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Load fragment to main view
     *
     * @param fragment loading fragment
     */
    private void loadFragment(Fragment fragment) {
        // fragment transitions
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main, fragment);
        transaction.commit();
    }

}

