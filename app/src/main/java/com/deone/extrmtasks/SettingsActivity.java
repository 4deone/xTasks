package com.deone.extrmtasks;

import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_ACCOUNT;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_AUTH;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_CONF;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_GROUP;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_KEY;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_NOT;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_STOCKAGE;
import static com.deone.extrmtasks.tools.Constants.IDFRAGMENT;
import static com.deone.extrmtasks.tools.Constants.UID;
import static com.deone.extrmtasks.tools.Ivtools.loadingImageWithPath;
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

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
    private ImageView ivSettingsCover;
    private ImageView ivSettingsAvatar;
    private TextView tvSettingsDate;
    private TextView tvSettingsCompteName;
    private TextView tvSettingsComptePhone;
    private TextView tvSettingsDisplayMode;
    private TextView tvSettingsLanguage;
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
            Intent intent = new Intent(this, TempActivity.class);
            intent.putExtra(UID, myuid);
            intent.putExtra(IDFRAGMENT, FRAGMENT_ACCOUNT);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotohome(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        if (id == R.id.swSettingsKey){

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name_lite));

        if (id == R.id.ivSettingsCover || id == R.id.ivSettingsAvatar)
            gotoUserDetails();
        else if (id == R.id.tvSettingsKeyList)
            showKeyList();
        else if (id == R.id.tvSettingsAutorisation)
            showAutorisationList();
        else if (id == R.id.tvSettingsNotification)
            showNotification();
        else if (id == R.id.tvSettingsDisplayMode)
            showDisplayMode(builder);
        else if (id == R.id.tvSettingsLanguage)
            showLanguageDialog(builder);
        else if (id == R.id.tvSettingsConfidentialite)
            sendConfidentialite();
        else if (id == R.id.tvSettingsDataStockage)
            showDataStockage();
        else if (id == R.id.tvSettingsGroupe)
            showDialogGroupe();
        else if (id == R.id.tvSettingsFaq)
            showFaq();
        else if (id == R.id.tvSettingsAbout)
            showDialogAbout();
        else if (id == R.id.tvSettingsSignOut)
            showSignoutDialog(builder);
    }

    /*
            Initialisation des l'activité
     */

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
        Toolbar toolbarSettings = findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbarSettings);

        ivSettingsCover = findViewById(R.id.ivSettingsCover);
        ivSettingsAvatar = findViewById(R.id.ivSettingsAvatar);
        tvSettingsDate = findViewById(R.id.tvSettingsDate);
        tvSettingsCompteName = findViewById(R.id.tvSettingsCompteName);
        tvSettingsComptePhone = findViewById(R.id.tvSettingsComptePhone);
        SwitchCompat swSettingsKey = findViewById(R.id.swSettingsKey);
        tvSettingsDisplayMode = findViewById(R.id.tvSettingsDisplayMode);
        tvSettingsLanguage = findViewById(R.id.tvSettingsLanguage);

        fbtools.lireUnUtilisateurSpecifique(vUser);

        ivSettingsAvatar.setOnClickListener(this);
        ivSettingsAvatar.setOnClickListener(this);
        tvSettingsDisplayMode.setOnClickListener(this);
        tvSettingsLanguage.setOnClickListener(this);
        findViewById(R.id.tvSettingsKeyList).setOnClickListener(this);
        findViewById(R.id.tvSettingsAutorisation).setOnClickListener(this);
        findViewById(R.id.tvSettingsNotification).setOnClickListener(this);
        findViewById(R.id.tvSettingsConfidentialite).setOnClickListener(this);
        findViewById(R.id.tvSettingsDataStockage).setOnClickListener(this);
        findViewById(R.id.tvSettingsGroupe).setOnClickListener(this);
        findViewById(R.id.tvSettingsFaq).setOnClickListener(this);
        findViewById(R.id.tvSettingsAbout).setOnClickListener(this);
        findViewById(R.id.tvSettingsSignOut).setOnClickListener(this);
        swSettingsKey.setOnCheckedChangeListener(this);
    }

    /*
        Goto user details
     */

    /**
     *
     */
    private void gotoUserDetails() {
        Intent intent = new Intent(this, TempActivity.class);
        intent.putExtra(UID, myuid);
        intent.putExtra(IDFRAGMENT, FRAGMENT_ACCOUNT);
        startActivity(intent);
    }

    /*
            Display user information
     */

    /**
     *
     * @param snapshot
     */
    private void DisplayUserInformation(DataSnapshot snapshot) {
        for (DataSnapshot ds : snapshot.getChildren()){
            User user = ds.getValue(User.class);
            assert user != null;
            tvSettingsCompteName.setText(user.getUnoms());
            tvSettingsDate.setText(formatLaDate(user.getUdate()));
            tvSettingsComptePhone.setText(safeShowValue(user.getUtelephone()));
            loadingImageWithPath(ivSettingsCover, R.drawable.russia, user.getUavatar());
            loadingImageWithPath(ivSettingsAvatar, R.drawable.wild, user.getUcover());
        }
    }

    private final ValueEventListener vUser = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            DisplayUserInformation(snapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(SettingsActivity.this, getString(R.string.download_error), Toast.LENGTH_SHORT).show();
        }
    };

    /*
            Display key word
     */

    private void showKeyList() {
        Intent intent = new Intent(this, TempActivity.class);
        intent.putExtra(UID, myuid);
        intent.putExtra(IDFRAGMENT, FRAGMENT_KEY);
        startActivity(intent);
    }

    /*
            Display app autorisations
     */

    private void showAutorisationList() {
        Intent intent = new Intent(this, TempActivity.class);
        intent.putExtra(UID, myuid);
        intent.putExtra(IDFRAGMENT, FRAGMENT_AUTH);
        startActivity(intent);
    }

    /*
            Display app notification
     */

    private void showNotification() {
        Intent intent = new Intent(this, TempActivity.class);
        intent.putExtra(UID, myuid);
        intent.putExtra(IDFRAGMENT, FRAGMENT_NOT);
        startActivity(intent);
    }

    /*
            Choose app theme mode
     */

    private void showDisplayMode(AlertDialog.Builder builder) {
        builder.setMessage(getString(R.string.choisir_mode));
        builder.setSingleChoiceItems(getResources().getStringArray(R.array.modes),
                sptools.readIntData(APP_PREFS_MODE, 0), modeListener);
        builder.create().show();
    }

    private final DialogInterface.OnClickListener modeListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            //initThemeMode(i);
            sptools.writeIntData(APP_PREFS_MODE, i);
            tvSettingsDisplayMode.setCompoundDrawablesRelativeWithIntrinsicBounds(chooseDrawable(i), 0, 0, 0);
        }
    };

    /*
            Display choose language
     */

    private void showLanguageDialog(AlertDialog.Builder builder) {
        builder.setMessage(getString(R.string.choisir_langue));
        builder.setSingleChoiceItems(getResources().getStringArray(R.array.langues),
                selectedLangue(sptools.readStringData(APP_PREFS_LANGUE, "")),
                langueListener);
        builder.create().show();
    }

    private final DialogInterface.OnClickListener langueListener = (dialogInterface, i) -> {
        tvSettingsLanguage.setText(getString(R.string.language));
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

    /*
            Display app confidentiality
     */

    private void sendConfidentialite() {
        Intent intent = new Intent(this, TempActivity.class);
        intent.putExtra(UID, myuid);
        intent.putExtra(IDFRAGMENT, FRAGMENT_CONF);
        startActivity(intent);
    }

    /*
            Display app data & stockage
     */

    private void showDataStockage() {
        Intent intent = new Intent(this, TempActivity.class);
        intent.putExtra(UID, myuid);
        intent.putExtra(IDFRAGMENT, FRAGMENT_STOCKAGE);
        startActivity(intent);
    }

    /*
            Display user groups
     */

    /**
     *
     */
    private void showDialogGroupe() {
        Intent intent = new Intent(this, TempActivity.class);
        intent.putExtra(UID, myuid);
        intent.putExtra(IDFRAGMENT, FRAGMENT_GROUP);
        startActivity(intent);
    }

    /*
            Display FAQ
     */

    private void showFaq() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.deone.com/corp/xtasks/faq"));
        startActivity(browserIntent);
    }

    /*
        Display About
     */

    /**
     *
     */
    private void showDialogAbout() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.deone.com/corp/xtasks/about"));
        startActivity(browserIntent);
    }

    /*
            Sign out
     */

    private void showSignoutDialog(AlertDialog.Builder builder) {
        builder.setMessage(getString(R.string.signout_message));
        builder.setNegativeButton(getString(R.string.non), null);
        builder.setPositiveButton(getString(R.string.oui), signoutListener);
        builder.create().show();
    }

    private final DialogInterface.OnClickListener signoutListener = (dialogInterface, i) -> {
        fbtools.signOut();
        dialogInterface.dismiss();
        gotomain(this);
    };

}