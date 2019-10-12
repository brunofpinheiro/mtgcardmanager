package com.br.mtgcardmanager.View;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.br.mtgcardmanager.Adapter.PagerAdapter;
import com.br.mtgcardmanager.DriveBackupService;
import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.br.mtgcardmanager.Model.APICards;
import com.br.mtgcardmanager.Model.Card;
import com.br.mtgcardmanager.Network.GetDataService;
import com.br.mtgcardmanager.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;



public class MainActivity extends AppCompatActivity {

    private              FragmentSearch                fragmentSearch;
    private              DatabaseHelper                dbHelper;
    private              ViewPager                     viewPager;
    private              List<String>                  jsonCardsList = new ArrayList<>();
    private              MenuItem                      searchMenu;
    private              AdView                        mAdView;
    public static        boolean                       running = false;
    private              SearchView.SearchAutoComplete searchAutoComplete;
    private              Call<APICards>                call = null;
    private              ArrayAdapter<String>          adapter = null;
    private              SearchView                    searchView = null;
    private              DriveBackupService            driveBackupService;
    private              GoogleSignInClient            client;
    private              GoogleAccountCredential       credential;
    private static final String                        TAG = "MainActivity";
    private static final int                           REQUEST_CODE_SIGN_IN = 1;
    private static final int                           REQUEST_CODE_UPLOAD_BACKUP = 2;
    private static final int                           REQUEST_CODE_DOWNLOAD_BACKUP = 3;
    private              ProgressDialog                progressDialog;
    private              int                           pageCount = 1;
    private              GetDataService                service;
    private              List<Card>                    allCardsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        running = true;
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
        dbHelper = new DatabaseHelper(this);
        dbHelper.populateEditionsList();
        dbHelper.getEditionsQty();

        if (dbHelper.editionsCount < dbHelper.currentEditions.size()) {
            dbHelper.insertAllEditions();
        }

        getAllCardsFromAsset();

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
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Set Tab Search as the initial tab
        viewPager.setCurrentItem(1);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        running = true;
        restoreBackup();
    }

    @Override
    public void onResume() {
        super.onResume();
        running = true;

        if (mAdView != null) {
            mAdView.resume();
        }
//        scheduleAlarm();
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (driveBackupService != null) {
            driveBackupService.googleApiClient.disconnect();
        }
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

        searchMenu         = menu.findItem(R.id.search);
        searchView         = (SearchView) MenuItemCompat.getActionView(searchMenu);
        searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setTextColor(Color.WHITE);
        searchAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String query = (String) parent.getItemAtPosition(position);
            searchCard(query);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (viewPager.getCurrentItem() == 0) {
                    filterCardList("have", query);
                } else if (viewPager.getCurrentItem() == 1) {
                    searchCard(query);
                } else if (viewPager.getCurrentItem() == 2) {
                    filterCardList("want", query);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (viewPager.getCurrentItem() == 1) {
                    if (newText.length() > 3) {
//                        if (call != null && call.isExecuted()) {
//                            call.cancel();
//                        }
//                        apiSearchByName(newText);
                        //aqui
                        if (dbHelper == null)
                            dbHelper = new DatabaseHelper(MainActivity.this);

                        allCardsList = dbHelper.getSuggestionByName(newText);
                        for (Card card: allCardsList) {
                            if (!jsonCardsList.contains(card.getName_en()))
                                jsonCardsList.add(card.getName_en());

                            if (card.getName_pt() != null && !jsonCardsList.contains(card.getName_pt()))
                                jsonCardsList.add(card.getName_pt());
                        }

                        setAutoCompleteAdapter();
                    }
                }

                return true;
            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(this.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_backup) {
            startBackup();
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Filters the cards based on what the user typed
     * @param fragmentName
     * @param query
     */
    private void filterCardList(String fragmentName, String query) {
        if (fragmentName.equalsIgnoreCase("have")) {
            FragmentHave fragmentHave = new FragmentHave();
            fragmentHave.getHaveCards();

            Card current = new Card();

            for (Iterator<Card> it = fragmentHave.haveCardsList.iterator(); it.hasNext();) {
                if (it.hasNext())
                    current = it.next();

                if (!current.getName_en().toLowerCase().contains(query.toLowerCase())
                        && !current.getName_pt().toLowerCase().contains(query.toLowerCase()))
                    it.remove();
            }
            fragmentHave.refreshRecyclerView(false);
        } else if (fragmentName.equalsIgnoreCase("want")) {
            FragmentWant fragmentWant = new FragmentWant();
            fragmentWant.getWantCards();

            Card current = new Card();

            for (Iterator<Card> it = fragmentWant.wantCardsList.iterator(); it.hasNext();) {
                if (it.hasNext())
                    current = it.next();

                if (!current.getName_en().toLowerCase().contains(query)
                        && !current.getName_pt().toLowerCase().contains(query))
                    it.remove();
            }
            fragmentWant.refreshRecyclerView(false);
        }

        MenuItemCompat.collapseActionView(searchMenu); //hides the menu search field
        searchAutoComplete.dismissDropDown();

    }


    public void searchCard(String query) {
        fragmentSearch = new FragmentSearch();
        MenuItemCompat.collapseActionView(searchMenu); //hides the menu search field
        searchAutoComplete.dismissDropDown();

        // If not in Tab Search, then go to Tab Search
        // 0 = Tab Have / 1 = Tab Search / 2 = Tab Want
        if (viewPager.getCurrentItem() != 1) {
            viewPager.setCurrentItem(1);
        }

        fragmentSearch.searchLigaMagic(MainActivity.this, query);
    }

    /**
     * Retrieves the content of the AllCards.json file and inserts it in table all_cards
     */
    private void getAllCardsFromAsset() {
        String      json;
        InputStream is;
        int         size;
        byte[]      buffer;
        JSONArray   jsonArray;
        List<Card>  cardsList;

        try {
            is     = this.getAssets().open("AllCards.json");
            size   = is.available();
            buffer = new byte[size];

            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

            jsonArray = new JSONArray(json);
            cardsList = new ArrayList<>();

            if (dbHelper == null)
                dbHelper = new DatabaseHelper(this);

            if (dbHelper.getAllCardsCount() != jsonArray.length()) {
                dbHelper.deleteAllCards();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = new JSONObject(jsonArray.get(i).toString());

                    Card card = new Card();
                    if (obj.has("nameEN"))
                        card.setName_en(obj.getString("nameEN"));

                    if (obj.has("namePT"))
                        card.setName_pt(obj.getString("namePT"));

                    cardsList.add(card);
                }

                dbHelper.insertAllCards(cardsList);
            }
        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        }
    }

//    private void getAllCardsFromAPI() {
//        if (service == null)
//            service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
//
//        call = service.getAllCards(pageCount);
//
//        call.enqueue(new Callback<APICards>() {
//            @Override
//            public void onResponse(Call<APICards> call, Response<APICards> response) {
//                List<APICards> returnedCards = new ArrayList<>();
//                List<Card>     allCards = new ArrayList<>();
//                int            cardsQty;
//                APICards       apiCards;
//
//                returnedCards.add(response.body());
//                apiCards = returnedCards.get(0);
//                cardsQty = apiCards.getCards().length;
//
//                for (int i = 0; i < cardsQty; i++) {
//                    Card card = new Card();
//                    card.setName_en(apiCards.getCards()[i].getName());
//
//                    for (int y = 0; y < apiCards.getCards()[i].getForeignNames().length; y++) {
//                        if (apiCards.getCards()[i].getForeignNames()[y].getLanguage().equalsIgnoreCase("Portuguese (Brazil)")) {
//                            card.setName_pt(apiCards.getCards()[i].getForeignNames()[y].getName());
//                        }
//                    }
//                    allCards.add(card);
//                }
//
//                dbHelper.insertAllCards(allCards);
//
//                if (cardsQty == 100) {
//                    service = null;
//                    call    = null;
//                    pageCount++;
//                    getAllCardsFromAPI();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<APICards> call, Throwable t) {
//                Log.e("getAllCardsFromAPI", t.toString());
//            }
//        });
//    }


    /**
     * Search a list of cards from the API that have a certain name.
     * @param name
     */
//    private void apiSearchByName(String name) {
//        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
//        call = service.getCardsByName(name, getString(R.string.pt_br), 10);
//
//        call.enqueue(new Callback<APICards>() {
//            @Override
//            public void onResponse(Call<APICards> call, Response<APICards> response) {
//                List<APICards> returnedCards = new ArrayList<>();
//                jsonCardsList = new ArrayList<>();
//                int cardsQty;
//
//                returnedCards.add(response.body());
//                APICards apiCards = returnedCards.get(0);
//                cardsQty = apiCards.getCards().length;
//
//                for (int i = 0; i < cardsQty; i++) {
//                    for (int y = 0; y < apiCards.getCards()[i].getForeignNames().length; y++) {
//                        if (apiCards.getCards()[i].getForeignNames()[y].getLanguage().equalsIgnoreCase("Portuguese (Brazil)")) {
//                            jsonCardsList.add(apiCards.getCards()[i].getForeignNames()[y].getName());
//                        }
//                    }
//                }
//
//                setAutoCompleteAdapter();
//            }
//
//            @Override
//            public void onFailure(Call<APICards> call, Throwable t) {
//                if (call.isCanceled()) {
//                    Log.e("apiSearchByName","request cancelled");
//                } else {
//                    Log.e("apiSearchByName", t.toString());
//                }
//            }
//        });
//    }

    /**
     * Sets the adapter for the autocomplete and shows the dropdown list.
     */
    private void setAutoCompleteAdapter() {
        adapter = new ArrayAdapter<>(this, R.layout.auto_complete_dropdown, jsonCardsList);
        searchAutoComplete.setAdapter(adapter);
        searchAutoComplete.showDropDown();
    }


    /**
     * Starts a sign-in activity using {@link #REQUEST_CODE_SIGN_IN}.
     */
    private void requestSignIn() {
        Log.d(TAG, "Requesting sign-in");

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        client = GoogleSignIn.getClient(this, signInOptions);
        if (client.getSignInIntent() == null) {
            // The result of the sign-in Intent is handled in onActivityResult.
            startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }
    }

    /**
     * Starts the backup process.
     */
    private void startBackup() {
        progressDialog = new ProgressDialog(this, R.style.customProgressDialog);
        progressDialog.setMessage(this.getString(R.string.backup_in_progress));
        progressDialog.show();

        requestSignIn();
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_UPLOAD_BACKUP);
    }

    /**
     * Restores the data found on Google Drive
     */
    private void restoreBackup() {
        DatabaseHelper  dbHelper  = new DatabaseHelper(this);
        ArrayList<Card> haveCards = dbHelper.getAllHaveCards();
        ArrayList<Card> wantCards = dbHelper.getAllWantCards();

        if (haveCards.size() == 0 && wantCards.size() == 0) {
            requestSignIn();
            startActivityForResult(client.getSignInIntent(), REQUEST_CODE_DOWNLOAD_BACKUP);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                }
                break;
            case REQUEST_CODE_UPLOAD_BACKUP:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                    if (driveBackupService != null) {
                        driveBackupService.backupFiles(this, progressDialog);
                    }
                }
            case REQUEST_CODE_DOWNLOAD_BACKUP:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                    if (driveBackupService != null) {
                        driveBackupService.restoreBackup(this);
                    }
                }
        }
    }

    /**
     * Handles the {@code result} of a completed sign-in activity initiated from {@link
     * #requestSignIn()}.
     */
    private void handleSignInResult(Intent result) {
        GoogleSignInAccount signedInAccount;

        credential = GoogleAccountCredential.usingOAuth2(
                this, Collections.singleton(DriveScopes.DRIVE_FILE));

        signedInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signedInAccount != null) {
            credential.setSelectedAccount(signedInAccount.getAccount());
        } else {
            GoogleSignIn.getSignedInAccountFromIntent(result)
                    .addOnSuccessListener(googleAccount -> {
                        credential.setSelectedAccount(googleAccount.getAccount());
                    })
                    .addOnFailureListener(exception -> Log.e(TAG, "Unable to sign in.", exception));
        }

        Drive googleDriveService = new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential)
                .setApplicationName("MTG Card Manager")
                .build();
        driveBackupService = new DriveBackupService(googleDriveService);
    }
}
