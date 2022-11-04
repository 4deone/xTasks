package com.deone.extrmtasks;

import static com.deone.extrmtasks.database.Fbtools.lireUnUtilisateurkeys;
import static com.deone.extrmtasks.database.Fbtools.liretouteslestaches;
import static com.deone.extrmtasks.preference.Sptools.readBooleanData;
import static com.deone.extrmtasks.preference.Sptools.readIntData;
import static com.deone.extrmtasks.preference.Sptools.readStringData;
import static com.deone.extrmtasks.tools.Constants.ACTION_START_LOCATION_SERVICE_ID;
import static com.deone.extrmtasks.tools.Constants.ACTION_STOP_LOCATION_SERVICE_ID;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_KEY;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Constants.LOCATION_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Constants.TID;
import static com.deone.extrmtasks.tools.Constants.UID;
import static com.deone.extrmtasks.tools.Lctools.checkAccessFineLocationPermissions;
import static com.deone.extrmtasks.tools.Lctools.requestAccessFineLocationPermissions;
import static com.deone.extrmtasks.tools.Other.gotoTask;
import static com.deone.extrmtasks.tools.Other.gotoaddtask;
import static com.deone.extrmtasks.tools.Other.gotomain;
import static com.deone.extrmtasks.tools.Other.gotosettings;
import static com.deone.extrmtasks.tools.Other.initLLanguage;
import static com.deone.extrmtasks.tools.Other.initThemeMode;
import static com.deone.extrmtasks.tools.Other.isContains;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;
import static com.deone.extrmtasks.tools.Other.orderListByKeyWords;
import static com.deone.extrmtasks.tools.Other.rvLayoutManager;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deone.extrmtasks.adapters.Tadapter;
import com.deone.extrmtasks.database.Fbtools;
import com.deone.extrmtasks.modeles.Key;
import com.deone.extrmtasks.modeles.Tache;
import com.deone.extrmtasks.preference.Sptools;
import com.deone.extrmtasks.tools.LocService;
import com.deone.extrmtasks.tools.Xlistener;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Fbtools fbtools;
    private Sptools sptools ;
    private RecyclerView rvTachesHome;
    private List<Tache> tacheList;
    private List<Key> keyList;
    private String ma_recherche;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initApp();
        super.onCreate(savedInstanceState);
        checkUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.itSearch);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(searchManage);
        searchView.setOnQueryTextFocusChangeListener(searchQueryTextFocusChange);
        searchView.setOnCloseListener(searchClose);
        searchView.setOnSuggestionListener(searchSuggestion);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itParametres){
            gotosettings(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LOCATION_REQUEST_CODE && grantResults.length > 0)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startLocationService();
            else
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.fabAddTachesHome)
            gotoaddtask(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*if (checkAccessFineLocationPermissions(HomeActivity.this))
            startLocationService();
        else
            requestAccessFineLocationPermissions(HomeActivity.this);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (checkAccessFineLocationPermissions(HomeActivity.this))
            startLocationService();
        else
            requestAccessFineLocationPermissions(HomeActivity.this);*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stopLocationService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopLocationService();
    }

    /**
     *
     */
    private void initApp() {
        fbtools = Fbtools.getInstance(this);
        sptools = Sptools.getInstance(this);
        initThemeMode(readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        initLLanguage(this, readStringData(APP_PREFS_LANGUE, EN));
        /*if (checkAccessFineLocationPermissions(HomeActivity.this))
            startLocationService();
        else
            requestAccessFineLocationPermissions(HomeActivity.this);*/
    }

    /**
     *
     */
    private void checkUser() {
        if(isStringEmpty(fbtools.getId())){
            gotomain(this);
        }else {
            initViews();
        }
    }

    /**
     *
     */
    private void initViews() {
        setContentView(R.layout.activity_home);
        Toolbar toolbarHome = findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbarHome);
        rvTachesHome = findViewById(R.id.rvTachesHome);
        rvTachesHome.setLayoutManager(rvLayoutManager(this, 0, LinearLayoutManager.VERTICAL));
        tacheList = new ArrayList<>();
        keyList = new ArrayList<>();
        if (readBooleanData(APP_PREFS_KEY, false))
            lireUnUtilisateurkeys(vKeys, fbtools.getId());
        liretouteslestaches(vTaches);
        findViewById(R.id.fabAddTachesHome).setOnClickListener(this);
    }

    /**
     *
     * @param snapshot
     */
    private void showToutesMesTaches(DataSnapshot snapshot) {
        tacheList.clear();
        for (DataSnapshot ds : snapshot.getChildren()){
            Tache tache = ds.getValue(Tache.class);
            tacheList.add(tache);
            Tadapter tadapter = new Tadapter(HomeActivity.this, orderListByKeyWords(tacheList, keyList));
            rvTachesHome.setAdapter(tadapter);
            tadapter.setListener(xListener);
        }
    }

    /**
     *
     * @param snapshot
     */
    private void showToutesMesRechercheDeTaches(DataSnapshot snapshot) {
        tacheList.clear();
        for (DataSnapshot ds : snapshot.getChildren()){
            Tache tache = ds.getValue(Tache.class);
            assert tache != null;
            if (isContains(ma_recherche, tache.getTtitre())||isContains(ma_recherche, tache.getTdescription())){
                tacheList.add(tache);
            }
            Tadapter tadapter = new Tadapter(HomeActivity.this, orderListByKeyWords(tacheList, keyList));
            rvTachesHome.setAdapter(tadapter);
            tadapter.setListener(xListener);
        }
    }

    /**
     *
     * @param query
     */
    private void makeAndFindYourSearch(String query) {
        if (!TextUtils.isEmpty(query)){
            ma_recherche = query;
            liretouteslestaches(vSearchTaches);
        }else {
            liretouteslestaches(vTaches);
        }
    }

    // TODO: Les écouteurs vTaches, vSearchTaches, vKeys & xListener

    private final ValueEventListener vTaches  = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            showToutesMesTaches(snapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(HomeActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private final ValueEventListener vSearchTaches  = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            showToutesMesRechercheDeTaches(snapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            //Toast.makeText(HomeActivity.this, getString(R.string.database_error), Toast.LENGTH_SHORT).show();
            Toast.makeText(HomeActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private final ValueEventListener vKeys = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            keyList.clear();
            for (DataSnapshot ds : snapshot.getChildren()){
                keyList.add(ds.getValue(Key.class));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private final Xlistener xListener = new Xlistener() {
        @Override
        public void onItemClick(View view, int position) {
            gotoTask(HomeActivity.this, TID, tacheList.get(position).getTid(), UID, tacheList.get(position).getUid());
        }

        @Override
        public void onLongItemClick(View view, int position) {

        }
    };

    // TODO: Les écouteurs SearchView : searchManage, searchClose, searchQueryTextFocusChange & searchSuggestion

    private final SearchView.OnQueryTextListener searchManage = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            makeAndFindYourSearch(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            makeAndFindYourSearch(newText);
            return false;
        }
    };

    private final SearchView.OnCloseListener searchClose = new SearchView.OnCloseListener() {
        @Override
        public boolean onClose() {
            return false;
        }
    };

    private final View.OnFocusChangeListener searchQueryTextFocusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {

        }
    };

    private final SearchView.OnSuggestionListener searchSuggestion = new SearchView.OnSuggestionListener() {
        @Override
        public boolean onSuggestionSelect(int position) {
            return false;
        }

        @Override
        public boolean onSuggestionClick(int position) {
            return false;
        }
    };

    // TODO: Location service
    private boolean isLocationServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null){
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
                if (LocationServices.class.getName().equals(service.service.getClassName())){
                    if (service.foreground)
                        return true;
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService(){
        if (!isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocService.class);
            intent.setAction(ACTION_START_LOCATION_SERVICE_ID);
            startService(intent);
            Toast.makeText(this, "Location service started!", Toast.LENGTH_SHORT).show();
        }

    }

    private void stopLocationService(){
        if (!isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocService.class);
            intent.setAction(ACTION_STOP_LOCATION_SERVICE_ID);
            startService(intent);
            Toast.makeText(this, "Location service stopped!", Toast.LENGTH_SHORT).show();
        }

    }

    /*Toast.makeText(context, "Latitude = " + intent.getExtras().getString(LOCATION_SERVICE_SIGNAL_LATITUDE)
                    + ", Longitude = " + intent.getExtras().getString(LOCATION_SERVICE_SIGNAL_LONGITUDE), Toast.LENGTH_SHORT).show();*/
}