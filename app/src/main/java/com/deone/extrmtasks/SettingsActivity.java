package com.deone.extrmtasks;

import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Constants.UID;
import static com.deone.extrmtasks.tools.Ivtools.loadingImageWithPath;
import static com.deone.extrmtasks.tools.Other.buildAlertDialog;
import static com.deone.extrmtasks.tools.Other.buildAlertDialogForSingleSelectOption;
import static com.deone.extrmtasks.tools.Other.chooseDrawable;
import static com.deone.extrmtasks.tools.Other.formatLaDate;
import static com.deone.extrmtasks.tools.Other.gotohome;
import static com.deone.extrmtasks.tools.Other.gotomain;
import static com.deone.extrmtasks.tools.Other.initLLanguage;
import static com.deone.extrmtasks.tools.Other.initThemeMode;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;
import static com.deone.extrmtasks.tools.Other.safeShowValue;
import static com.deone.extrmtasks.tools.Other.selectedLangue;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.deone.extrmtasks.modeles.User;
import com.deone.extrmtasks.tools.Fbtools;
import com.deone.extrmtasks.tools.Ivtools;
import com.deone.extrmtasks.tools.Sptools;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Fbtools fbtools;
    private Sptools sptools;
    private Ivtools ivtools;
    private ImageView ivAvatar;
    private ImageView ivCover;
    private TextView tvDate;
    private TextView tvCompteName;
    private TextView tvComptePhone;
    private SwitchCompat swKey;
    private TextView tvKeyList;
    private TextView tvAutorisation;
    private TextView tvNotification;
    private TextView tvDisplayMode;
    private TextView tvLanguage;
    private TextView tvConfidentialite;
    private TextView tvDataStockage;
    private TextView tvGroupe;
    private TextView tvFaq;
    private TextView tvAbout;
    private TextView tvSignOut;
    private User user;
    private String myuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initApp();
        super.onCreate(savedInstanceState);
        checkUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itAccount){
            Intent intent = new Intent(this, AccountActivity.class);
            intent.putExtra(UID, myuid);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    private void initApp() {
        sptools = Sptools.getInstance(this);
        fbtools = Fbtools.getInstance(this);
        ivtools = Ivtools.getInstance(this);
        initThemeMode(sptools.readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        initLLanguage(this, sptools.readStringData(APP_PREFS_LANGUE, EN));
    }

    /**
     *
     */
    private void checkUser() {
        myuid = fbtools.getId();
        if(isStringEmpty(myuid)){
            gotomain(this);
        }else {
            initViews();
        }
    }

    /**
     *
     */
    private void initViews() {
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ivAvatar = findViewById(R.id.ivAvatar);
        tvDate = findViewById(R.id.tvDate);
        ivCover = findViewById(R.id.ivCover);
        tvCompteName = findViewById(R.id.tvCompteName);
        tvComptePhone = findViewById(R.id.tvComptePhone);
        swKey = findViewById(R.id.swKey);
        tvKeyList = findViewById(R.id.tvKeyList);
        tvAutorisation = findViewById(R.id.tvAutorisation);
        tvNotification = findViewById(R.id.tvNotification);
        tvDisplayMode = findViewById(R.id.tvDisplayMode);
        tvLanguage = findViewById(R.id.tvLanguage);
        tvConfidentialite = findViewById(R.id.tvConfidentialite);
        tvDataStockage = findViewById(R.id.tvDataStockage);
        tvGroupe = findViewById(R.id.tvGroupe);
        tvFaq = findViewById(R.id.tvFaq);
        tvAbout = findViewById(R.id.tvAbout);
        tvSignOut = findViewById(R.id.tvSignOut);

        fbtools.specificUser(vUser);

        ivAvatar.setOnClickListener(this);
        ivCover.setOnClickListener(this);
        tvKeyList.setOnClickListener(this);
        tvAutorisation.setOnClickListener(this);
        tvNotification.setOnClickListener(this);
        tvDisplayMode.setOnClickListener(this);
        tvLanguage.setOnClickListener(this);
        tvConfidentialite.setOnClickListener(this);
        tvDataStockage.setOnClickListener(this);
        tvGroupe.setOnClickListener(this);
        tvFaq.setOnClickListener(this);
        tvAbout.setOnClickListener(this);
        tvSignOut.setOnClickListener(this);
    }

    /**
     *
     */
    private void showDialogGroupe() {
    }

    /**
     *
     */
    private void showDialogUserInfo() {
    }

    /**
     *
     */
    private void showDialogAbout() {
    }

    /**
     *
     * @param snapshot
     */
    private void showUser(DataSnapshot snapshot) {
        for (DataSnapshot ds : snapshot.getChildren()){
            user = ds.getValue(User.class);
            assert user != null;
            tvCompteName.setText(user.getUnoms());
            tvDate.setText(formatLaDate(user.getUdate()));
            tvComptePhone.setText(safeShowValue(user.getUtelephone()));
            loadingImageWithPath(ivAvatar, R.drawable.russia, user.getUavatar());
            loadingImageWithPath(ivCover, R.drawable.wild, user.getUcover());
        }
    }

    private final ValueEventListener vUser = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            showUser(snapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(SettingsActivity.this, getString(R.string.download_error), Toast.LENGTH_SHORT).show();
        }
    };

    private final DialogInterface.OnClickListener modeListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            //initThemeMode(i);
            sptools.writeIntData(APP_PREFS_MODE, i);
            tvDisplayMode.setCompoundDrawablesRelativeWithIntrinsicBounds(chooseDrawable(i), 0, 0, 0);
        }
    };

    private final DialogInterface.OnClickListener langueListener = (dialogInterface, i) -> {
        /*if (i == 0){
            setLocale("ar");
            recreate();
        } else if (i == 1){
            setLocale("en");
            recreate();
        } else if (i == 2){
            setLocale("fr");
            recreate();
        }*/
    };

    private final DialogInterface.OnClickListener okModeListener = (dialogInterface, i) -> gotomain(SettingsActivity.this);

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ivAvatar || id == R.id.ivCover)
            showDialogUserInfo();
        else if (id == R.id.tvKeyList)
            showKeyList();
        else if (id == R.id.tvAutorisation)
            showAutorisationList();
        else if (id == R.id.tvNotification)
            showNotification();
        else if (id == R.id.tvDisplayMode)
            buildAlertDialogForSingleSelectOption(this, getString(R.string.choisir_mode),
                    modeListener, getResources().getStringArray(R.array.modes),
                    sptools.readIntData(APP_PREFS_MODE, 0)).create().show();
        else if (id == R.id.tvLanguage)
            buildAlertDialogForSingleSelectOption(this, getString(R.string.choisir_langue),
                    langueListener, getResources().getStringArray(R.array.langues),
                    selectedLangue(sptools.readStringData(APP_PREFS_LANGUE, ""))).create().show();
        else if (id == R.id.tvConfidentialite)
            sendConfidentialite();
        else if (id == R.id.tvDataStockage)
            showDataStockage();
        else if (id == R.id.tvGroupe)
            showDialogGroupe();
        else if (id == R.id.tvFaq)
            showFaq();
        else if (id == R.id.tvAbout)
            showDialogAbout();
        else if (id == R.id.tvSignOut)
            buildAlertDialog(this, getString(R.string.app_name), getString(R.string.signout_message),
                    null, getString(R.string.non), okModeListener, getString(R.string.oui)).create().show();
    }

    private void showKeyList() {
    }

    private void showAutorisationList() {
    }

    private void showNotification() {
    }

    private void sendConfidentialite() {
    }

    private void showDataStockage() {
    }

    private void showFaq() {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotohome(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        if (id == R.id.swKey){

        }
    }
}