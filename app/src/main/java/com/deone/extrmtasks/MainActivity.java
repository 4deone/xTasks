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

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.deone.extrmtasks.modeles.User;
import com.deone.extrmtasks.tools.Signtools;
import com.deone.extrmtasks.tools.Sptools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Signtools signtools;
    private Sptools sptools;
    private EditText etvLoginMain;
    private EditText etvMotdepasseMain;

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
        sptools.writeIntData(APP_PREFS_MODE, appActualThemeMode());
        initThemeMode(sptools.readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        sptools.writeStringData(APP_PREFS_LANGUE, Locale.getDefault().getLanguage());
        sptools.writeStringData(APP_PREFS_COUNTRY, Locale.getDefault().getCountry());
        initLLanguage(this, sptools.readStringData(APP_PREFS_LANGUE, EN));

        signtools = Signtools.getInstance(this);
    }

    private int appActualThemeMode() {
        return checkUiCurrentMode()==AppCompatDelegate.MODE_NIGHT_YES
                ?
                AppCompatDelegate.MODE_NIGHT_YES
                :
                AppCompatDelegate.MODE_NIGHT_NO;
    }

    /**
     *
     */
    private void checkUser() {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(fuser != null){
            gotohome(this);
        }else {
            initViews();
        }
    }

    /**
     *
     */
    private void initViews() {
        setContentView(R.layout.activity_main);
        etvLoginMain = findViewById(R.id.etvLoginMain);
        etvMotdepasseMain = findViewById(R.id.etvMotdepasseMain);
        findViewById(R.id.btProcessMain).setOnClickListener(this);
        findViewById(R.id.tvSignUpMain).setOnClickListener(this);
    }

    /**
     *
     */
    private void signInProcess() {
        ProgressDialog pd = buildProgressDialog(this, getString(R.string.app_name), getString(R.string.verif_entries));
        pd.show();
        String email = etvLoginMain.getText().toString().trim();
        if (isStringEmpty(email)){
            pd.dismiss();
            Toast.makeText(this, getString(R.string.email_error), Toast.LENGTH_SHORT).show();
            return;
        }
        String motdepasse = etvMotdepasseMain.getText().toString().trim();
        if (isStringEmpty(motdepasse)){
            pd.dismiss();
            Toast.makeText(this, getString(R.string.motdepasse_error), Toast.LENGTH_SHORT).show();
            return;
        }
        User user = new User(""+email, ""+motdepasse);
        signtools.checkEmailStatus(pd, user);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btProcessMain)
            signInProcess();
        else if (id == R.id.tvSignUpMain)
            gotonew(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sptools.removeAllData();
    }
}