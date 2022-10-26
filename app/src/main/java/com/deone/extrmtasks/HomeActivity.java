package com.deone.extrmtasks;

import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Constants.TID;
import static com.deone.extrmtasks.tools.Other.gotoTask;
import static com.deone.extrmtasks.tools.Other.gotoaddtask;
import static com.deone.extrmtasks.tools.Other.gotomain;
import static com.deone.extrmtasks.tools.Other.gotosettings;
import static com.deone.extrmtasks.tools.Other.initLLanguage;
import static com.deone.extrmtasks.tools.Other.initThemeMode;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;
import static com.deone.extrmtasks.tools.Other.rvLayoutManager;

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
import androidx.recyclerview.widget.RecyclerView;

import com.deone.extrmtasks.adapters.Tadapter;
import com.deone.extrmtasks.modeles.Taches;
import com.deone.extrmtasks.tools.Fbtools;
import com.deone.extrmtasks.tools.Sptools;
import com.deone.extrmtasks.tools.Xlistener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Fbtools fbtools;
    private Sptools sptools;
    private RecyclerView rvTaches;
    private List<Taches> tachesList;
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
        initThemeMode(sptools.readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        initLLanguage(this, sptools.readStringData(APP_PREFS_LANGUE, EN));
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rvTaches = findViewById(R.id.rvTaches);
        rvTaches.setLayoutManager(rvLayoutManager(this, 0));
        tachesList = new ArrayList<>();
        fbtools.toutesLesTaches(vTaches);
        findViewById(R.id.fabAddTaches).setOnClickListener(this);
    }

    /**
     *
     * @param snapshot
     */
    private void showToutesMesTaches(DataSnapshot snapshot) {
        tachesList.clear();
        for (DataSnapshot ds : snapshot.getChildren()){
            Taches taches = ds.getValue(Taches.class);
            tachesList.add(taches);
            Tadapter tadapter = new Tadapter(HomeActivity.this, tachesList);
            rvTaches.setAdapter(tadapter);
            tadapter.setListener(xListener);
        }
    }

    /**
     *
     * @param snapshot
     */
    private void showToutesMesRechercheDeTaches(DataSnapshot snapshot) {
        tachesList.clear();
        for (DataSnapshot ds : snapshot.getChildren()){
            Taches taches = ds.getValue(Taches.class);
            assert taches != null;
            if (ma_recherche.equals(taches.getTtitre())||ma_recherche.equals(taches.getTdescription())){
                tachesList.add(taches);
                Tadapter tadapter = new Tadapter(HomeActivity.this, tachesList);
                rvTaches.setAdapter(tadapter);
                tadapter.setListener(xListener);
            }
        }
    }

    private void makeAndFindYourSearch(String query) {
        if (!TextUtils.isEmpty(query)){
            ma_recherche = query;
            fbtools.toutesLesTaches(vSearchTaches);
        }else {
            fbtools.toutesLesTaches(vTaches);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.fabAddTaches)
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

    private final Xlistener xListener = new Xlistener() {
        @Override
        public void onItemClick(View view, int position) {
            gotoTask(HomeActivity.this, TID, tachesList.get(position).getTid());
        }

        @Override
        public void onLongItemClick(View view, int position) {

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

}