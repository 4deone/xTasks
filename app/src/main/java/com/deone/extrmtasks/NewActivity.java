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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.deone.extrmtasks.tools.Fbtools;
import com.deone.extrmtasks.tools.Sptools;

public class NewActivity extends AppCompatActivity implements View.OnClickListener {

    private Fbtools fbtools;
    private Sptools sptools;
    private EditText etvLogin;
    private EditText etvMotdepasse;
    private EditText etvFullName;
    private EditText etvTelephone;
    private CheckBox cbAcceptRules;

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
        if(isStringEmpty(fbtools.userId())){
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
        cbAcceptRules = findViewById(R.id.cbAcceptRules);
        findViewById(R.id.btCreateAccount).setOnClickListener(this);
        findViewById(R.id.tvSignIn).setOnClickListener(this);
    }

    /**
     *
     */
    private void signUpProcess() {
        String email = etvLogin.getText().toString().trim();
        if (isStringEmpty(email)){
            Toast.makeText(this, getString(R.string.email_error), Toast.LENGTH_SHORT).show();
            return;
        }
        String motdepasse = etvMotdepasse.getText().toString().trim();
        if (isStringEmpty(motdepasse)){
            Toast.makeText(this, getString(R.string.motdepasse_error), Toast.LENGTH_SHORT).show();
            return;
        }
        String fullname = etvFullName.getText().toString().trim();
        if (isStringEmpty(fullname)){
            Toast.makeText(this, getString(R.string.fullname_error), Toast.LENGTH_SHORT).show();
            return;
        }
        String telephone = etvTelephone.getText().toString().trim();
        if (isStringEmpty(telephone)){
            Toast.makeText(this, getString(R.string.telephone_error), Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressDialog pd = buildProgressDialog(this, getString(R.string.app_name), getString(R.string.traitement_encours));
        pd.show();
        fbtools.checkEmailStatus(pd, null, email, motdepasse, fullname, telephone);
    }

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