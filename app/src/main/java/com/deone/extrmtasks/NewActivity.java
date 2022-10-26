package com.deone.extrmtasks;

import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Other.buildProgressDialog;
import static com.deone.extrmtasks.tools.Other.gotohome;
import static com.deone.extrmtasks.tools.Other.gotomain;
import static com.deone.extrmtasks.tools.Other.initLLanguage;
import static com.deone.extrmtasks.tools.Other.initThemeMode;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;
import static com.deone.extrmtasks.tools.Other.rvLayoutManager;
import static com.deone.extrmtasks.tools.Other.toutesLesConditions;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import com.deone.extrmtasks.adapters.Condadapter;
import com.deone.extrmtasks.modeles.Condition;
import com.deone.extrmtasks.tools.Fbtools;
import com.deone.extrmtasks.tools.Sptools;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewActivity extends AppCompatActivity implements View.OnClickListener {

    private Fbtools fbtools;
    private Sptools sptools;
    private EditText etvLogin;
    private EditText etvMotdepasse;
    private EditText etvFullName;
    private EditText etvTelephone;
    private EditText etvVille;
    private EditText etvPays;
    private RecyclerView rvConditions;
    private CheckBox cbAcceptRules;
    private List<Condition> conditionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initApp();
        super.onCreate(savedInstanceState);
        checkUser();
    }

    /**
     *
     */
    private void initApp() {
        sptools = Sptools.getInstance(this);
        fbtools = Fbtools.getInstance(this);
        initThemeMode(sptools.readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        initLLanguage(this, sptools.readStringData(APP_PREFS_LANGUE, EN));
    }

    /**
     *
     */
    private void checkUser() {
        if(isStringEmpty(fbtools.getId())){
            initViews();
        }else {
            gotohome(this);
        }
    }

    /**
     *
     */
    private void initViews() {
        setContentView(R.layout.activity_new);
        etvLogin = findViewById(R.id.etvLogin);
        etvMotdepasse = findViewById(R.id.etvMotdepasse);
        etvFullName = findViewById(R.id.etvFullName);
        etvTelephone = findViewById(R.id.etvTelephone);
        etvVille = findViewById(R.id.etvVille);
        etvPays = findViewById(R.id.etvPays);
        rvConditions = findViewById(R.id.rvConditions);
        rvConditions.setLayoutManager(rvLayoutManager(this, 0));
        cbAcceptRules = findViewById(R.id.cbAcceptRules);
        conditionsList = new ArrayList<>();
        toutesLesConditions(vConditions);
        findViewById(R.id.btCreateAccount).setOnClickListener(this);
        findViewById(R.id.tvSignIn).setOnClickListener(this);
    }

    /**
     *
     */
    private void signUpProcess() {
        ProgressDialog pd = buildProgressDialog(this, getString(R.string.app_name), getString(R.string.verif_entries));
        pd.show();
        String email = etvLogin.getText().toString().trim();
        if (isStringEmpty(email)){
            pd.dismiss();
            Toast.makeText(this, getString(R.string.email_error), Toast.LENGTH_SHORT).show();
            return;
        }
        String motdepasse = etvMotdepasse.getText().toString().trim();
        if (isStringEmpty(motdepasse)){
            pd.dismiss();
            Toast.makeText(this, getString(R.string.motdepasse_error), Toast.LENGTH_SHORT).show();
            return;
        }
        String fullname = etvFullName.getText().toString().trim();
        if (isStringEmpty(fullname)){
            pd.dismiss();
            Toast.makeText(this, getString(R.string.fullname_error), Toast.LENGTH_SHORT).show();
            return;
        }
        String telephone = etvTelephone.getText().toString().trim();
        if (isStringEmpty(telephone)){
            pd.dismiss();
            Toast.makeText(this, getString(R.string.telephone_error), Toast.LENGTH_SHORT).show();
            return;
        }
        String ville = etvVille.getText().toString().trim();
        if (isStringEmpty(ville)){
            pd.dismiss();
            Toast.makeText(this, getString(R.string.ville_error), Toast.LENGTH_SHORT).show();
            return;
        }
        String pays = etvPays.getText().toString().trim();
        if (isStringEmpty(pays)){
            pd.dismiss();
            Toast.makeText(this, getString(R.string.pays_error), Toast.LENGTH_SHORT).show();
            return;
        }
        fbtools.checkEmailStatus(pd, null, email, motdepasse, fullname, telephone, ville, pays);
    }

    /**
     *
     * @param snapshot
     */
    private void showToutesMesConditions(DataSnapshot snapshot) {
        Log.e("Conditions", ""+snapshot.toString());
        conditionsList.clear();
        for (DataSnapshot ds : snapshot.getChildren()){
            Condition condition = ds.getValue(Condition.class);
            conditionsList.add(condition);
            Condadapter condadapter = new Condadapter(conditionsList);
            rvConditions.setAdapter(condadapter);
        }
        Log.e("Conditions - size", ""+conditionsList.size());
        Log.e("Conditions - item", ""+conditionsList.get(0).getTitre());
    }

    private final ValueEventListener vConditions  = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            showToutesMesConditions(snapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(NewActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btCreateAccount && cbAcceptRules.isChecked())
            signUpProcess();
        else if (id == R.id.tvSignIn)
            gotomain(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sptools.removeAllData();
        gotomain(this);
    }

}