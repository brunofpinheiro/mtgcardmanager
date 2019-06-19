package com.br.mtgcardmanager.View;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.br.mtgcardmanager.Adapter.PagerAdapter;
import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.br.mtgcardmanager.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


//public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener { //com drive api
public class MainActivity extends AppCompatActivity { //sem drive api

    FragmentSearch fragmentSearch;
    DatabaseHelper                dbHelper;
    ViewPager                     viewPager;
    ArrayList<String>             jsonCardsList;
    String                        json;
    MenuItem                      searchItem;
    private AdView                mAdView;
//    private static final String   TAG = "MainActivity";
//    private static final String   DRIVE_TAG = "Google Drive Activity";
//    private static final int      REQUEST_CODE_RESOLUTION = 1;
    public static GoogleApiClient mGoogleApiClient;
    public static boolean         running = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        running = true;

//        scheduleAlarm();

        mAdView = findViewById(R.id.ad_view);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, getString(R.string.admob_app_id));

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);

        Toolbar toolbar = findViewById(R.id.toolbar_id);
        setSupportActionBar(toolbar);

        // Create or update EDITIONS table
        dbHelper = DatabaseHelper.getInstance(this);
        dbHelper.populateEditionsList();
        dbHelper.getEditionsQty();

        if (dbHelper.editionsCount < dbHelper.currentEditions.size()) {
            dbHelper.insertAllEditions();
        }

        // Get the JSON containing all printed cards
//        getCardsFromJSON();

        TabLayout tabLayout = findViewById(R.id.tab_layout_id);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.fragment_have_name)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.fragment_search_name)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.fragment_want_name)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = findViewById(R.id.view_pager_id);
        final PagerAdapter pagerAdapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }//end onTabSelected

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }//end onTabUnselected

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }//end onTabReselected
        });

        // Set Tab Search as the initial tab
        viewPager.setCurrentItem(1);
    }//end onCreate

    @Override
    protected void onStart() {
        super.onStart();
//        running = true;
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        running = true;

        if (mAdView != null) {
            mAdView.resume();
        }

//        if (checkIfAlarmIsUp() == false) {
//            scheduleAlarm();
//        }

//        if (mGoogleApiClient == null) {
//            /**
//             * Create the API client and bind it to an instance variable.
//             * We use this instance as the callback for connection and connection failures.
//             * Since no account name is passed, the user is prompted to choose.
//             */
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addApi(Drive.API)
//                    .addScope(Drive.SCOPE_FILE)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .build();
//        }
//        mGoogleApiClient.connect();

//        scheduleAlarm();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (mGoogleApiClient != null) {
//            // disconnect Google Android Drive API connection.
//            mGoogleApiClient.disconnect();
//        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        dbHelper.close();
        if (mAdView != null) {
            mAdView.destroy();
        }
        running = false;
//        cancelAlarm();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        final SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setTextColor(Color.WHITE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.auto_complete_dropdown, jsonCardsList);
        searchAutoComplete.setAdapter(adapter);
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String query = (String) parent.getItemAtPosition(position);
                searchCard(query);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchCard(query);
                return false;
            }//end onQueryTextSubmit

            @Override
            public boolean onQueryTextChange(String newText) {
                // User changed the text
                return false;
            }// end onQueryTextChange
        });

        SearchManager searchManager = (SearchManager) getSystemService(this.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }//end onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }//end if

        return super.onOptionsItemSelected(item);
    }//end onOptionsItemSelected


    public void searchCard(String query) {
        fragmentSearch = new FragmentSearch();
        MenuItemCompat.collapseActionView(searchItem); //recolhe o campo de busca do menu

        // If not in Tab Search, then go to Tab Search
        // 0 = Tab Have / 1 = Tab Search / 2 = Tab Want
        if (viewPager.getCurrentItem() != 1) {
            viewPager.setCurrentItem(1);
        }

        fragmentSearch.searchLigaMagic(MainActivity.this, query);
    }

    private String loadJSONFromAsset() {
        json = null;
        try {
            InputStream is = this.getAssets().open("AllCards.json");
            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private ArrayList<String> getCardsFromJSON() {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArray = obj.names();
//            Iterator iter = obj.keys();
            jsonCardsList = new ArrayList();

//            while (iter.hasNext()) {
//                String key = iter.next().toString();
//                Object value = obj.get(key);
//                System.out.print(value);
//            }

            for (int i = 0; i < m_jArray.length(); i++) {
                jsonCardsList.add(m_jArray.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonCardsList;
    }


    //daqui pra baixo é sem drive api
    // ******************************
    // ** Google Drive Api Methods **
    // ******************************
//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
//        // Called whenever the API client fails to connect.
//        Log.i(DRIVE_TAG, "GoogleApiClient connection failed: " + result.toString());
//
//        if (!result.hasResolution()) {
//            // show the localized error dialog.
//            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
//            return;
//        }
//
//        /**
//         *  The failure has a resolution. Resolve it.
//         *  Called typically when the app is not yet authorized, and an  authorization
//         *  dialog is displayed to the user.
//         */
//        try {
//            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
//        } catch (IntentSender.SendIntentException e) {
//            Log.e(TAG, "Exception while starting resolution activity", e);
//        }
//    }
//
//    /**
//     * It invoked when Google API client connected
//     *
//     * @param connectionHint
//     */
//    @Override
//    public void onConnected(Bundle connectionHint) {
//        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
////        scheduleAlarm();
////        createHaveDriveFile();
////        createWantDriveFile();
//    }
//
//    /**
//     * It invoked when connection suspended
//     *
//     * @param cause
//     */
//    @Override
//    public void onConnectionSuspended(int cause) {
//        Log.i(DRIVE_TAG, "GoogleApiClient connection suspended");
////        cancelAlarm();
//    }
//
////    public void createHaveDriveFile() {
////        fileOperation = true;
////        table_to_backup = "have";
////        // create new contents resource
////        Drive.DriveApi.newDriveContents(mGoogleApiClient)
////                .setResultCallback(driveContentsCallback);
////    }
////
////    public void createWantDriveFile() {
////        fileOperation = true;
////        table_to_backup = "want";
////        // create new contents resource
////        Drive.DriveApi.newDriveContents(mGoogleApiClient)
////                .setResultCallback(driveContentsCallback);
////    }
////
////    /**
////     * This is Result result handler of Drive contents.
////     * this callback method call CreateFileOnGoogleDrive() method.
////     */
////    final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
////            new ResultCallback<DriveApi.DriveContentsResult>() {
////                @Override
////                public void onResult(DriveApi.DriveContentsResult result) {
////                    if (result.getStatus().isSuccess()) {
////                        if (fileOperation == true) {
////                            CreateFileOnGoogleDrive(result, table_to_backup);
////                        }
////                    }
////                }
////            };
////
////    /**
////     * Create a file in root folder using MetadataChangeSet object.
////     *
////     * @param result
////     */
////    public void CreateFileOnGoogleDrive(DriveApi.DriveContentsResult result, final String table_to_backup) {
////        // Exports the database to a JSONArray
////        exportDbToJSON();
////
////        final DriveContents driveContents = result.getDriveContents();
////
////        // Perform I/O off the UI thread.
////        new Thread() {
////            @Override
////            public void run() {
////                // write content to DriveContents
////                writeContentToDriveContents(driveContents, table_to_backup);
////            }
////        }.start();
////    }
////
////    private void writeContentToDriveContents(DriveContents driveContents, String table_to_backup) {
////        OutputStream outputStream = driveContents.getOutputStream();
////        Writer writer = new OutputStreamWriter(outputStream);
////        try {
////            if (table_to_backup.equals("have")) {
////                writer.write(json_have_cards.toString());
////            } else if (table_to_backup.equals("want")) {
////                writer.write(json_want_cards.toString());
////            }
////            writer.close();
////        } catch (IOException e) {
////            Log.e(DRIVE_TAG, e.getMessage());
////        }
////
////        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
////                .setTitle("mtgcardmanager_" + table_to_backup + ".json")
////                .setMimeType("application/json")
////                .setStarred(true).build();
////
////        // create a file in root folder
////        Drive.DriveApi.getRootFolder(mGoogleApiClient)
////                .createFile(mGoogleApiClient, changeSet, driveContents).
////                setResultCallback(fileCallback);
////    }
////
////
////    /**
////     * Handle result of Created file
////     */
////    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
////            ResultCallback<DriveFolder.DriveFileResult>() {
////                @Override
////                public void onResult(DriveFolder.DriveFileResult result) {
////                    if (result.getStatus().isSuccess()) {
////                        Toast.makeText(getApplicationContext(), "file created: " + "" +
////                                result.getDriveFile().getDriveId(), Toast.LENGTH_LONG).show();
////                        if (is_table_want_ready_for_bkp) {
////                            createWantDriveFile();
////                            is_table_want_ready_for_bkp = false;
////                        }
////                    }
////                    return;
////                }
////            };
////
////
////    public void exportDbToJSON (){
////        json_have_cards = convertDbToJSON("have");
////        json_want_cards = convertDbToJSON("want");
////}
////
////    private JSONArray convertDbToJSON(String table_name)
////    {
////        String myTable      = table_name;//Set name of your table
////        JSONArray resultSet = new JSONArray();
////
////        dbHelper = DatabaseHelper.getInstance(this);
////        SQLiteDatabase myDataBase = dbHelper.getReadableDatabase();
////        String searchQuery = "SELECT  * FROM " + myTable;
////        Cursor cursor = myDataBase.rawQuery(searchQuery, null );
////
////        cursor.moveToFirst();
////        while (cursor.isAfterLast() == false) {
////            int totalColumn = cursor.getColumnCount();
////            JSONObject rowObject = new JSONObject();
////            for( int i=0 ;  i< totalColumn ; i++ ) {
////                if( cursor.getColumnName(i) != null ) {
////                    try {
////                        if( cursor.getString(i) != null ) {
////                            Log.d("TAG_NAME", cursor.getString(i) );
////                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
////                        } else {
////                            rowObject.put( cursor.getColumnName(i) ,  "" );
////                        }
////                    } catch( Exception e ) {
////                        Log.d("TAG_NAME", e.getMessage()  );
////                    }
////                }
////            }
////            resultSet.put(rowObject);
////            cursor.moveToNext();
////        }
////        cursor.close();
////        Log.d("TAG_NAME", resultSet.toString() );
////
////        return resultSet;
////    }
//
//    public void scheduleAlarm() {
//        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
//        final PendingIntent pending_intent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
////        long first_millis = System.currentTimeMillis(); // alarm is set right away
//        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 20);
//        calendar.set(Calendar.MINUTE, 40);
//
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 1, pending_intent);
//
////        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, first_millis,
////                AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendint_intent);
//    }
//
////    public void cancelAlarm() {
////        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
////        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
////                intent, PendingIntent.FLAG_UPDATE_CURRENT);
////        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
////        alarm.cancel(pIntent);
////        pIntent.cancel();
////    }
////
////    public boolean checkIfAlarmIsUp() {
////        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);//the same as up
////        intent.setAction(AlarmReceiver.ACTION);//the same as up
////        boolean alarmUp = (PendingIntent.getBroadcast(MainActivity.this, 1001, intent,
////                PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
////
////        return alarmUp;
////    }
//
}//end main