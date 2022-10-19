package com.deone.extrmtasks;

import static com.deone.extrmtasks.tools.Constants.APP_PREFS_COUNTRY;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Other.buildProgressDialog;
import static com.deone.extrmtasks.tools.Other.checkUiCurrentMode;
import static com.deone.extrmtasks.tools.Other.gotohome;
import static com.deone.extrmtasks.tools.Other.gotonew;
import static com.deone.extrmtasks.tools.Other.initLLanguage;
import static com.deone.extrmtasks.tools.Other.initThemeMode;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.deone.extrmtasks.tools.Fbtools;
import com.deone.extrmtasks.tools.Sptools;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Fbtools fbtools;
    private Sptools sptools;
    private EditText etvLogin;
    private EditText etvMotdepasse;

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
        sptools.writeIntData(APP_PREFS_MODE, checkUiCurrentMode()==AppCompatDelegate.MODE_NIGHT_YES?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);
        initThemeMode(sptools.readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        sptools.writeStringData(APP_PREFS_LANGUE, Locale.getDefault().getLanguage());
        sptools.writeStringData(APP_PREFS_COUNTRY, Locale.getDefault().getCountry());
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
    private void connectWithGoogle() {
    }

    /**
     *
     */
    private void connectWithFacebook() {
    }

    /**
     *
     */
    private void initViews() {
        setContentView(R.layout.activity_main);
        etvLogin = findViewById(R.id.etvLogin);
        etvMotdepasse = findViewById(R.id.etvMotdepasse);
        findViewById(R.id.btProcess).setOnClickListener(this);
        findViewById(R.id.tvSignUp).setOnClickListener(this);
        findViewById(R.id.tvGoogle).setOnClickListener(this);
        findViewById(R.id.tvFacebook).setOnClickListener(this);
    }

    /**
     *
     */
    private void signInProcess() {
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
        ProgressDialog pd = buildProgressDialog(this, getString(R.string.app_name), getString(R.string.traitement_encours));
        pd.show();
        fbtools.checkEmailStatus(pd, email, motdepasse);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btProcess)
            signInProcess();
        else if (id == R.id.tvSignUp)
            gotonew(this);
        else if (id == R.id.tvGoogle)
            connectWithGoogle();
        else if (id == R.id.tvFacebook)
            connectWithFacebook();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sptools.removeAllData();
    }
}