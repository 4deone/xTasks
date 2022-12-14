package com.deone.extrmtasks;

import static com.deone.extrmtasks.database.Fbtools.signOut;
import static com.deone.extrmtasks.picture.Ivtools.loadingImageWithPath;
import static com.deone.extrmtasks.preference.Sptools.readBooleanData;
import static com.deone.extrmtasks.preference.Sptools.readIntData;
import static com.deone.extrmtasks.preference.Sptools.readStringData;
import static com.deone.extrmtasks.preference.Sptools.writeBooleanData;
import static com.deone.extrmtasks.preference.Sptools.writeIntData;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_CURRENT_POSITION;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_CURRENT_POSITION_PRIORITY;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_KEY;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_ACCOUNT;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_CONF;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_GROUP;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_KEY;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_NOT;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_STOCKAGE;
import static com.deone.extrmtasks.tools.Constants.IDFRAGMENT;
import static com.deone.extrmtasks.tools.Constants.UID;
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
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import com.deone.extrmtasks.database.Fbtools;
import com.deone.extrmtasks.modeles.User;
import com.deone.extrmtasks.preference.Sptools;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Fbtools fbtools;
    private ImageView ivSettingsCover;
    private ImageView ivSettingsAvatar;
    private TextView tvSettingsDate;
    private TextView tvSettingsCompteName;
    private TextView tvSettingsComptePhone;
    private TextView tvSettingsDisplayMode;
    private TextView tvSettingsLanguage;
    private TextView tvSettingsKeyList;
    private TextView tvSettingsLocPriority;
    private SwitchCompat swSettingsKey;
    private SwitchCompat swSettingsCurrentPostion;
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
            openTempActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openTempActivity() {
        Intent intent = new Intent(this, TempActivity.class);
        intent.putExtra(UID, myuid);
        intent.putExtra(IDFRAGMENT, FRAGMENT_ACCOUNT);
        startActivity(intent);
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
            saveViewPreferences(tvSettingsKeyList, APP_PREFS_KEY, b);
        } else if (id == R.id.swSettingsCurrentPostion){
            saveViewPreferences(tvSettingsLocPriority, APP_PREFS_CURRENT_POSITION, b);
        }
    }

    private void saveViewPreferences(View view, String appPrefsKey, boolean bol) {
        view.setEnabled(bol);
        writeBooleanData(appPrefsKey, bol);
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
        else if (id == R.id.tvSettingsLocPriority)
            showKeyLocPriority();
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

    // TODO: Initialisation des l'activit??

    /**
     *
     */
    private void initApp() {
        // TODO: Init sharedpreferences tools
        Sptools sptools = Sptools.getInstance(this);
        initThemeMode(readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        initLLanguage(this, readStringData(APP_PREFS_LANGUE, EN));
        // TODO: Init firebase database tools
        fbtools = Fbtools.getInstance(this);
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
        this.initToolbar();
        this.initCover();
        this.initAvatar();
        this.initDate();
        this.initName();
        this.initPhone();
        this.initSettingsKey();
        this.initSettingsKeyList();
        this.initSettingsCurrentPosition();
        this.initSettingsCurrentPositionPriority();
        this.initDisplayMode();
        this.initSettingsLanguage();
        this.initSettingsNotification();
        this.initSettingsConfidentialite();
        this.initSettingsDataStockage();
        this.initSettingsGroupe();
        this.initSettingsFaq();
        this.initSettingsAbout();
        this.initSettingsSignOut();
        fbtools.lireUnUtilisateurSpecifique(vUser);
    }

    private void initToolbar() {
        Toolbar toolbarSettings = findViewById(R.id.toolbarSettings);
        toolbarSettings.setTitle(getString(R.string.parametres));
        toolbarSettings.setSubtitle(getString(R.string.param_your_app));
        setSupportActionBar(toolbarSettings);
    }

    private void initCover() {
        ivSettingsCover = findViewById(R.id.ivSettingsCover);
    }

    private void initSettingsSignOut() {
        findViewById(R.id.tvSettingsSignOut).setOnClickListener(this);
    }

    private void initSettingsAbout() {
        findViewById(R.id.tvSettingsAbout).setOnClickListener(this);
    }

    private void initSettingsFaq() {
        findViewById(R.id.tvSettingsFaq).setOnClickListener(this);
    }

    private void initSettingsGroupe() {
        findViewById(R.id.tvSettingsGroupe).setOnClickListener(this);
    }

    private void initSettingsDataStockage() {
        findViewById(R.id.tvSettingsDataStockage).setOnClickListener(this);
    }

    private void initSettingsConfidentialite() {
        findViewById(R.id.tvSettingsConfidentialite).setOnClickListener(this);
    }

    private void initSettingsNotification() {
        findViewById(R.id.tvSettingsNotification).setOnClickListener(this);
    }

    private void initSettingsKeyList() {
        tvSettingsKeyList = findViewById(R.id.tvSettingsKeyList);
        tvSettingsKeyList.setEnabled(swSettingsKey.isChecked());
        tvSettingsKeyList.setOnClickListener(this);
    }

    private void initSettingsCurrentPositionPriority() {
        tvSettingsLocPriority = findViewById(R.id.tvSettingsLocPriority);
        tvSettingsLocPriority.setText(formatPriority(readIntData(APP_PREFS_CURRENT_POSITION_PRIORITY, -1)));
        tvSettingsLocPriority.setEnabled(swSettingsCurrentPostion.isChecked());
        tvSettingsLocPriority.setOnClickListener(this);
    }

    private String formatPriority(int priority) {
        if (priority == 0)
            return getString(R.string.priority_custom, getString(R.string.adresse));
        else if (priority == 1)
            return getString(R.string.priority_custom, getString(R.string.codepostal));
        else if (priority == 2)
            return getString(R.string.priority_custom, getString(R.string.pays));
        else if (priority == 3)
            return getString(R.string.priority_custom, getString(R.string.ville));
        else if (priority == 4)
            return getString(R.string.priority_custom, getString(R.string.state));
        return getString(R.string.priority_custom, getString(R.string.by_default));
    }

    private void initSettingsLanguage() {
        tvSettingsLanguage = findViewById(R.id.tvSettingsLanguage);
        tvSettingsLanguage.setOnClickListener(this);
    }

    private void initDisplayMode() {
        tvSettingsDisplayMode = findViewById(R.id.tvSettingsDisplayMode);
        tvSettingsDisplayMode.setOnClickListener(this);
    }

    private void initSettingsKey() {
        swSettingsKey = findViewById(R.id.swSettingsKey);
        swSettingsKey.setChecked(readBooleanData(APP_PREFS_KEY, false));
        swSettingsKey.setOnCheckedChangeListener(this);
    }

    private void initSettingsCurrentPosition() {
        swSettingsCurrentPostion = findViewById(R.id.swSettingsCurrentPostion);
        swSettingsCurrentPostion.setChecked(readBooleanData(APP_PREFS_CURRENT_POSITION, false));
        swSettingsCurrentPostion.setOnCheckedChangeListener(this);
    }

    private void initPhone() {
        tvSettingsComptePhone = findViewById(R.id.tvSettingsComptePhone);
    }

    private void initName() {
        tvSettingsCompteName = findViewById(R.id.tvSettingsCompteName);
    }

    private void initDate() {
        tvSettingsDate = findViewById(R.id.tvSettingsDate);
    }

    private void initAvatar() {
        ivSettingsAvatar = findViewById(R.id.ivSettingsAvatar);
        ivSettingsAvatar.setOnClickListener(this);
    }

    // TODO: Goto user details

    /**
     *
     */
    private void gotoUserDetails() {
        Intent intent = new Intent(this, TempActivity.class);
        intent.putExtra(UID, myuid);
        intent.putExtra(IDFRAGMENT, FRAGMENT_ACCOUNT);
        startActivity(intent);
    }

    // TODO: Display user information

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

    // TODO: Display Location priority

    private void showKeyLocPriority() {
        String[] priorities = {
                getString(R.string.adresse),
                getString(R.string.codepostal),
                getString(R.string.pays),
                getString(R.string.ville),
                getString(R.string.state)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name_lite));
        builder.setMessage(getString(R.string.choisir_mode));
        builder.setSingleChoiceItems(priorities, readIntData(APP_PREFS_CURRENT_POSITION_PRIORITY, 0), priorityListener);
        builder.create().show();
    }

    // TODO: Display key word

    private void showKeyList() {
        Intent intent = new Intent(this, TempActivity.class);
        intent.putExtra(UID, myuid);
        intent.putExtra(IDFRAGMENT, FRAGMENT_KEY);
        startActivity(intent);
    }

    // TODO: Display app notification

    private void showNotification() {
        Intent intent = new Intent(this, TempActivity.class);
        intent.putExtra(UID, myuid);
        intent.putExtra(IDFRAGMENT, FRAGMENT_NOT);
        startActivity(intent);
    }

    // TODO: Choose app theme mode

    private void showDisplayMode(AlertDialog.Builder builder) {
        builder.setMessage(getString(R.string.choisir_mode));
        builder.setSingleChoiceItems(getResources().getStringArray(R.array.modes),
                readIntData(APP_PREFS_MODE, 0), modeListener);
        builder.create().show();
    }

    private final DialogInterface.OnClickListener modeListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            //initThemeMode(i);
            writeIntData(APP_PREFS_MODE, i);
            tvSettingsDisplayMode.setCompoundDrawablesRelativeWithIntrinsicBounds(chooseDrawable(i), 0, 0, 0);
        }
    };

    // TODO: Choose priority

    private final DialogInterface.OnClickListener priorityListener = (dialogInterface, i) ->
            writeIntData(APP_PREFS_CURRENT_POSITION_PRIORITY, i);

    // TODO: Display choose language

    private void showLanguageDialog(AlertDialog.Builder builder) {
        builder.setMessage(getString(R.string.choisir_langue));
        builder.setSingleChoiceItems(getResources().getStringArray(R.array.langues),
                selectedLangue(readStringData(APP_PREFS_LANGUE, "")),
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

    // TODO: Display app confidentiality

    private void sendConfidentialite() {
        Intent intent = new Intent(this, TempActivity.class);
        intent.putExtra(UID, myuid);
        intent.putExtra(IDFRAGMENT, FRAGMENT_CONF);
        startActivity(intent);
    }

    // TODO: Display app data & stockage

    private void showDataStockage() {
        Intent intent = new Intent(this, TempActivity.class);
        intent.putExtra(UID, myuid);
        intent.putExtra(IDFRAGMENT, FRAGMENT_STOCKAGE);
        startActivity(intent);
    }

    // TODO: Display user groups

    /**
     *
     */
    private void showDialogGroupe() {
        Intent intent = new Intent(this, TempActivity.class);
        intent.putExtra(UID, myuid);
        intent.putExtra(IDFRAGMENT, FRAGMENT_GROUP);
        startActivity(intent);
    }

    // TODO: Display FAQ

    private void showFaq() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.deone.com/corp/xtasks/faq"));
        startActivity(browserIntent);
    }

    // TODO: Display About

    /**
     *
     */
    private void showDialogAbout() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.deone.com/corp/xtasks/about"));
        startActivity(browserIntent);
    }

    // TODO: Sign out

    private void showSignoutDialog(AlertDialog.Builder builder) {
        builder.setMessage(getString(R.string.signout_message));
        builder.setNegativeButton(getString(R.string.non), null);
        builder.setPositiveButton(getString(R.string.oui), signoutListener);
        builder.create().show();
    }

    private final DialogInterface.OnClickListener signoutListener = (dialogInterface, i) -> {
        signOut();
        dialogInterface.dismiss();
        gotomain(this);
    };

}