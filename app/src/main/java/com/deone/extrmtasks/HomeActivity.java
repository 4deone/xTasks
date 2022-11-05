package com.deone.extrmtasks;

import static com.deone.extrmtasks.database.Fbtools.lireUnUtilisateurkeys;
import static com.deone.extrmtasks.database.Fbtools.liretouteslestaches;
import static com.deone.extrmtasks.preference.Sptools.readBooleanData;
import static com.deone.extrmtasks.preference.Sptools.readIntData;
import static com.deone.extrmtasks.preference.Sptools.readStringData;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_CURRENT_POSITION;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_KEY;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Constants.TID;
import static com.deone.extrmtasks.tools.Constants.UID;
import static com.deone.extrmtasks.tools.Lctools.displayTaskLocation;
import static com.deone.extrmtasks.tools.Lctools.requestAccessFineLocationPermissions;
import static com.deone.extrmtasks.tools.Other.initLLanguage;
import static com.deone.extrmtasks.tools.Other.initThemeMode;
import static com.deone.extrmtasks.tools.Other.isContains;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;
import static com.deone.extrmtasks.tools.Other.rvLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.deone.extrmtasks.modeles.Localize;
import com.deone.extrmtasks.modeles.Tache;
import com.deone.extrmtasks.tools.Xlistener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    // TODO: Les variables de notre activité
    private Fbtools fbtools;
    private String ma_recherche;
    private List<Tache> tacheList;
    private List<Key> keyList;
    private RecyclerView rvTachesHome;
    private Localize localize;

    // TODO: Les écouteurs du menu de HomeActivity
    private final SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            searchAndFindTasks(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            searchAndFindTasks(newText);
            return false;
        }
    };

    // TODO: Les écouteurs de firebase pour HomeActivity
    private final ValueEventListener vTaches = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            showToutesMesTaches(snapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("XtremTasks", error.getMessage());
        }
    };
    private final ValueEventListener vKeys = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            loadMyKeys(snapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("XtremTasks", error.getMessage());
        }
    };

    // TODO: Les écouteurs de l'adaptateur Tadapter
    private final Xlistener xListener = new Xlistener() {
        @Override
        public void onItemClick(View view, int position) {
            Intent intent = new Intent(HomeActivity.this, TaskActivity.class);
            intent.putExtra(TID, tacheList.get(position).getTid());
            intent.putExtra(UID, tacheList.get(position).getUid());
            startActivity(intent);
        }

        @Override
        public void onLongItemClick(View view, int position) {
            Log.e("XtremTasks", "onLongItemClick on rv position " + position);
        }
    };

    // TODO: Les méthodes Override
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.initApp();
        super.onCreate(savedInstanceState);
        this.checkUser();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        this.initSearchItem(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.chooseSettings(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.fabAddTachesHome) {
            startActivity(new Intent(this, AddActivity.class));
        }
    }

    // TODO: Les méthodes d'initialisation de l'activité
    private void initApp() {
        fbtools = Fbtools.getInstance(this);
        initThemeMode(readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        initLLanguage(this, readStringData(APP_PREFS_LANGUE, EN));
    }

    private void checkUser() {
        if(isStringEmpty(fbtools.getId())){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            initViews();
        }
    }

    private void initViews() {
        setContentView(R.layout.activity_home);
        this.initToolbar();
        tacheList = new ArrayList<>();
        tacheList.add(new Tache("String tid", "String ttitre", "String tdescription"));
        keyList = new ArrayList<>();
        this.initRecycleview();
        this.initFab();
        if (readBooleanData(APP_PREFS_KEY, false))
            lireUnUtilisateurkeys(vKeys, fbtools.getId());
        if (readBooleanData(APP_PREFS_CURRENT_POSITION, false)){
            showLocation();
        } else
            liretouteslestaches(vTaches);
    }

    // TODO: Les méthodes de initViews
    private void initToolbar() {
        Toolbar toolbarHome = findViewById(R.id.toolbarHome);
        toolbarHome.setTitle(getString(R.string.home));
        toolbarHome.setSubtitle(getString(R.string.welcome_xtask));
        setSupportActionBar(toolbarHome);
    }

    private void initRecycleview() {
        rvTachesHome = findViewById(R.id.rvTachesHome);
        rvTachesHome.setLayoutManager(rvLayoutManager(this, 0, LinearLayoutManager.VERTICAL));
    }

    private void initFab() {
        findViewById(R.id.fabAddTachesHome).setOnClickListener(this);
    }

    private void launchMyLocationBackground() {
        Log.e("HomeActivity", "launchMyLocationBackground");
        new Thread(){
            @Override
            public void run() {
                Log.e("HomeActivity", "Thread run");
                showLocation();
            }
        }.start();
    }

    /*private void initFusedLocationProviderClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        checkPositionPermission();
    }*/

    // TODO: showToutesMesTaches methods
    private void showLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            requestAccessFineLocationPermissions(this);
            return;
        }
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(HomeActivity.this);
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null){
                localize = displayTaskLocation(HomeActivity.this, location);
                if (localize != null) {
                    liretouteslestaches(vTaches);
                }
            }
            else
                Toast.makeText(HomeActivity.this, ""+getString(R.string.loc_error), Toast.LENGTH_SHORT).show();
        });
    }

    // TODO: Les méthodes d'initialisation du menu de HomeActivity
    private void initSearchItem(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.itSearch);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(onQueryTextListener);
        searchView.setQueryHint(getString(R.string.enter_your_search));
    }

    private void chooseSettings(MenuItem item) {
        if (item.getItemId() == R.id.itParametres){
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        }
    }

    private void searchAndFindTasks(String query) {
        if (!TextUtils.isEmpty(query))
            ma_recherche = query;
        liretouteslestaches(vTaches);
    }

    // TODO: Les méthodes de firebase pour HomeActivity
    private boolean isSearchFound(Tache tache) {
        return isContains(ma_recherche, tache.getTtitre()) ||
                isContains(ma_recherche, tache.getTdescription());
    }

    private boolean isNotNull(String data){
        return data != null && !data.isEmpty();
    }

    private boolean isPosititonFound(Tache tache) {
        if (tache.getLocalize().getLatitude() != null || tache.getLocalize().getLongitude() != null)
            return tache.getLocalize().getAddress().equals(localize.getAddress()) ||
                    tache.getLocalize().getCodepostal().equals(localize.getCodepostal()) ||
                    tache.getLocalize().getCity().equals(localize.getCity()) ||
                    tache.getLocalize().getState().equals(localize.getState()) ||
                    tache.getLocalize().getCountry().equals(localize.getCountry()) ||
                    tache.getLocalize().getCountry().equals(localize.getLatitude()) ||
                    tache.getLocalize().getCountry().equals(localize.getLongitude());
        return false;
    }

    private void showToutesMesTaches(DataSnapshot snapshot) {
        // TODO: Nous collectons toutes les taches de notre base de données firebase
        tacheList.clear();
        for (DataSnapshot ds : snapshot.getChildren()){
            Tache tache = ds.getValue(Tache.class);
            tacheList.add(tache);
        }
        // TODO: trions tacheList en fonction des mots clé de l'utilisateur
        if (readBooleanData(APP_PREFS_KEY, false)){
            List < Predicate < Tache >> prs = new ArrayList<>();
            for (Key key : keyList){
                Predicate<Tache> byItem = tache -> !isContains(key.getKmessage().toLowerCase(), tache.getTtitre().toLowerCase())
                        && !isContains(key.getKmessage().toLowerCase(), tache.getTdescription().toLowerCase());
                prs.add(byItem);
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                tacheList.removeIf(prs.stream().reduce(x -> true, Predicate::and));
            }
        }
        // TODO: Filtrage de tacheList par rapport à ma recherche
        if (!isStringEmpty(ma_recherche)) {
            Predicate<Tache> byTitreOrDescription = tache -> !isContains(ma_recherche.toLowerCase(), tache.getTtitre().toLowerCase())
                            && !isContains(ma_recherche.toLowerCase(), tache.getTdescription().toLowerCase());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                tacheList.removeIf(byTitreOrDescription);
            }
        }
        // TODO: Nous allons trier la liste en fonction de la dernière position de l'utilisateur
        if (readBooleanData(APP_PREFS_CURRENT_POSITION, false)) {
            /*if (isPosititonFound(tache)) {
                tacheList.add(tache);
            }*/
        }
        // TODO: Nous allons afficher la liste sur l'écran de l'utilisateur
        Tadapter tadapter = new Tadapter(HomeActivity.this, tacheList);
        rvTachesHome.setAdapter(tadapter);
        tadapter.setListener(xListener);
    }

    private void loadMyKeys(DataSnapshot snapshot) {
        keyList.clear();
        for (DataSnapshot ds : snapshot.getChildren()){
            keyList.add(ds.getValue(Key.class));
        }
    }
}