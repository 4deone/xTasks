package com.deone.extrmtasks;

import static com.deone.extrmtasks.tools.Constants.APP_PREFS_KEY;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Constants.TID;
import static com.deone.extrmtasks.tools.Constants.UID;
import static com.deone.extrmtasks.tools.Fbtools.lireUnUtilisateurkeys;
import static com.deone.extrmtasks.tools.Fbtools.liretouteslestaches;
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
import static com.deone.extrmtasks.tools.Sptools.readBooleanData;
import static com.deone.extrmtasks.tools.Sptools.readIntData;
import static com.deone.extrmtasks.tools.Sptools.readStringData;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deone.extrmtasks.adapters.Tadapter;
import com.deone.extrmtasks.modeles.Key;
import com.deone.extrmtasks.modeles.Tache;
import com.deone.extrmtasks.tools.Fbtools;
import com.deone.extrmtasks.tools.Sptools;
import com.deone.extrmtasks.tools.Xlistener;
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

    /**
     *
     */
    private void initApp() {
        fbtools = Fbtools.getInstance(this);
        sptools = Sptools.getInstance(this);
        initThemeMode(readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        initLLanguage(this, readStringData(APP_PREFS_LANGUE, EN));
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.fabAddTachesHome)
            gotoaddtask(this);
    }

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

}