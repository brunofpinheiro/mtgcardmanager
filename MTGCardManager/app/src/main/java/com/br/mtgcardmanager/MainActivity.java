package com.br.mtgcardmanager;

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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.br.mtgcardmanager.Adapter.PagerAdapter;
import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {
    FragmentSearch    fragmentSearch;
//    Button            btnHaveAdd;
//    Button            btnWantAdd;
    DatabaseHelper    dbHelper;
    ViewPager         viewPager;
    ArrayList<String> jsonCardsList;
    String            json;
    MenuItem          searchItem;
    private GoogleApiClient mGoogleApiClient;
    private JSONArray table_have_json;
    private AdView    mAdView;

    private static final String TAG = "MainActivity";
    private static boolean main_view = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_id);
        setSupportActionBar(toolbar);

//        exportDbToJSON();
//        exportJSONToDrive(table_have_json);

        // Create or update EDITIONS table
        dbHelper = DatabaseHelper.getInstance(this);
        dbHelper.populateEditionsList();
        dbHelper.getEditionsQty();

        if (dbHelper.editionsCount < dbHelper.currentEditions.size()) {
            dbHelper.insertAllEditions();
        }

        // Get the JSON containing all printed cards
        getCardsFromJSON();

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout_id);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.fragment_have_name)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.fragment_search_name)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.fragment_want_name)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager)findViewById(R.id.view_pager_id);
        final PagerAdapter pagerAdapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                viewPager.setCurrentItem(tab.getPosition());
            }//end onTabSelected

            @Override
            public void onTabUnselected(TabLayout.Tab tab){

            }//end onTabUnselected

            @Override
            public void onTabReselected(TabLayout.Tab tab){

            }//end onTabReselected
        });

        // Set Tab Search as the initial tab
        viewPager.setCurrentItem(1);

    }//end onCreate

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)
                searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setTextColor(Color.WHITE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.auto_complete_dropdown, jsonCardsList);
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
    public boolean onOptionsItemSelected(MenuItem item){
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
            int size       = is.available();
            byte[] buffer  = new byte[size];

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
            JSONObject obj     = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArray = obj.names();
            jsonCardsList      = new ArrayList();

            for (int i = 0; i < m_jArray.length(); i++) {
                jsonCardsList.add(m_jArray.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonCardsList;
    }


    public void exportDbToJSON (){
        dbHelper = DatabaseHelper.getInstance(this);

        JSONArray table_have_json = dbHelper.exportHaveToJSON(MainActivity.this);
//        JSONArray table_want_json = dbHelper.exportWantToJSON(MainActivity.this);

//        return table_have_json;
    }

    public void exportJSONToDrive(JSONArray table_have_json) {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .enableAutoManage(this, null)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .build();
    }

}//end main