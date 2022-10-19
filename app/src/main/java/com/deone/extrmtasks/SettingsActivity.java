package com.deone.extrmtasks;

import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Constants.UDESCRIPTION;
import static com.deone.extrmtasks.tools.Constants.UID;
import static com.deone.extrmtasks.tools.Constants.UNOMS;
import static com.deone.extrmtasks.tools.Constants.UTELEPHONE;
import static com.deone.extrmtasks.tools.Other.buildAlertDialog;
import static com.deone.extrmtasks.tools.Other.buildAlertDialogForSelectOption;
import static com.deone.extrmtasks.tools.Other.buildAlertDialogForSingleSelectOption;
import static com.deone.extrmtasks.tools.Other.buildProgressDialog;
import static com.deone.extrmtasks.tools.Other.chooseDrawable;
import static com.deone.extrmtasks.tools.Other.createTitle;
import static com.deone.extrmtasks.tools.Other.formatLaDate;
import static com.deone.extrmtasks.tools.Other.gotohome;
import static com.deone.extrmtasks.tools.Other.gotomain;
import static com.deone.extrmtasks.tools.Other.initLLanguage;
import static com.deone.extrmtasks.tools.Other.initThemeMode;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;
import static com.deone.extrmtasks.tools.Other.safeShowValue;
import static com.deone.extrmtasks.tools.Other.selectedLangue;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.deone.extrmtasks.modeles.User;
import com.deone.extrmtasks.tools.Fbtools;
import com.deone.extrmtasks.tools.Ivtools;
import com.deone.extrmtasks.tools.Sptools;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private Fbtools fbtools;
    private Sptools sptools;
    private Ivtools ivtools;
    private ImageView ivCoverUser;
    private ImageView ivAvatarUser;
    private TextView tvfullname;
    private TextView tvDateCreateAccount;
    private TextView tvDisplayMode;
    private TextView tvTasks;
    private TextView tvGroups;
    private TextView tvComments;
    private TextView tvLikes;
    private TextView tvFavoris;
    private TextView tvTelephone;
    private TextView tvDescription;
    private TextView tvBloquer;
    private User user;
    private Uri imageUri;
    private String idIntent;
    private boolean iscover = false;

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
        if (item.getItemId() == R.id.itEditer){
            buildAlertDialogForSelectOption(this, getString(R.string.choisir_option),
                    optionListener, getResources().getStringArray(R.array.user_item)).create().show();
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
        idIntent = getIntent().getStringExtra(UID);
        initThemeMode(sptools.readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        initLLanguage(this, sptools.readStringData(APP_PREFS_LANGUE, EN));
    }

    /**
     *
     */
    private void checkUser() {
        if(isStringEmpty(fbtools.userId())){
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
        ivCoverUser = findViewById(R.id.ivCoverUser);
        ivAvatarUser = findViewById(R.id.ivAvatarUser);
        tvfullname = findViewById(R.id.tvfullname);
        tvDateCreateAccount = findViewById(R.id.tvDateCreateAccount);
        tvDisplayMode = findViewById(R.id.tvDisplayMode);
        tvTasks = findViewById(R.id.tvTasks);
        tvGroups = findViewById(R.id.tvGroups);
        tvComments = findViewById(R.id.tvComments);
        tvLikes = findViewById(R.id.tvLikes);
        tvFavoris = findViewById(R.id.tvFavoris);
        tvTelephone = findViewById(R.id.tvTelephone);
        tvDescription = findViewById(R.id.tvDescription);
        tvBloquer = findViewById(R.id.tvBloquer);
        tvBloquer.setVisibility(idIntent.equals(fbtools.userId())?View.GONE:View.VISIBLE);
        ivtools.setSomeActivityResultLauncher(someActivityResultLauncher);
        fbtools.specificUser(vUser);
        tvDisplayMode.setOnClickListener(this);
        findViewById(R.id.tvLanguage).setOnClickListener(this);
        findViewById(R.id.tvGroupe).setOnClickListener(this);
        findViewById(R.id.tvUserInfo).setOnClickListener(this);
        findViewById(R.id.tvAbout).setOnClickListener(this);
        findViewById(R.id.tvSignOut).setOnClickListener(this);
        findViewById(R.id.tvDeleteAccount).setOnClickListener(this);
        findViewById(R.id.tvSignaler).setOnClickListener(this);
        findViewById(R.id.tvSave).setOnClickListener(this);
        findViewById(R.id.tvCall).setOnClickListener(this);
        findViewById(R.id.tvSendMessage).setOnClickListener(this);
        tvBloquer.setOnClickListener(this);
        tvTasks.setOnClickListener(this);
        tvFavoris.setOnClickListener(this);
        tvLikes.setOnClickListener(this);
        tvComments.setOnClickListener(this);
        tvGroups.setOnClickListener(this);
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

    private void showGroups() {
    }

    private void showComments() {
    }

    private void showLikes() {
    }

    private void showFavoris() {
    }

    private void showTasks() {
    }

    private void sendMessageUser() {
    }

    private void callUser() {
    }

    private void saveUser() {
    }

    private void signalerUser() {
    }

    private void bloquerUser() {
    }

    /**
     *
     * @param snapshot
     */
    private void showUser(DataSnapshot snapshot) {
        for (DataSnapshot ds : snapshot.getChildren()){
            user = ds.getValue(User.class);
            assert user != null;
            tvfullname.setText(user.getUnoms());
            tvDateCreateAccount.setText(formatLaDate(user.getUdate()));
            tvTasks.setText(safeShowValue(user.getUntask()));
            tvFavoris.setText(safeShowValue(user.getUnfavoris()));
            tvLikes.setText(safeShowValue(user.getUnlikes()));
            tvComments.setText(safeShowValue(user.getUncomments()));
            tvGroups.setText(safeShowValue(user.getUngroups()));
            tvTelephone.setText(safeShowValue(user.getUtelephone()));
            tvDescription.setText(safeShowValue(user.getUdescription()));
        }
    }

    /**
     *
     * @param field
     */
    private void showDialog(String field) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialod_edit);
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        String title = createTitle(this, field);
        tvTitle.setText(isStringEmpty(title)?getString(R.string.new_value):title);
        EditText edtValue = dialog.findViewById(R.id.edtValue);
        Button btUpdate = dialog.findViewById(R.id.btUpdate);
        btUpdate.setOnClickListener(view -> {
            verifValueData(edtValue, field);
        });
        dialog.show();
    }

    /**
     *
     * @param edtValue
     * @param field
     */
    public void verifValueData(EditText edtValue, String field) {
        String value = edtValue.getText().toString().trim();
        if (isStringEmpty(value)){
            Toast.makeText(this, getString(R.string.value_error), Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressDialog pd = buildProgressDialog(this, getString(R.string.app_name), getString(R.string.traitement_encours));
        pd.show();
        fbtools.updateStringWithFieldAndValue(pd, field, value);
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

    private final DialogInterface.OnClickListener itListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch(i){
                case 0 :
                    if (!ivtools.checkCameraPermissions()){
                        ivtools.requestCameraPermissions();
                    }else{
                        ivtools.pickFromCamera(imageUri);
                    }
                    break;
                case 1 :
                    if (!ivtools.checkStoragePermissions()){
                        ivtools.requestStoragePermissions();
                    }else{
                        ivtools.pickFromGallery();
                    }
                    break;
                default:
            }
        }
    };

    private final DialogInterface.OnClickListener optionListener = (dialogInterface, i) -> {
        switch (i) {
            case 0: // Avatar
                iscover = false;
                buildAlertDialogForSelectOption(this, getString(R.string.app_name_lite),
                        itListener, getResources().getStringArray(R.array.select_place)).create().show();
                ProgressDialog pdAvatar = buildProgressDialog(this, getString(R.string.app_name), getString(R.string.traitement_encours));
                pdAvatar.show();
                fbtools.deleteOldImage(pdAvatar, imageUri, iscover, user.getUavatar());
                break;
            case 1: // Couverture
                iscover = true;
                buildAlertDialogForSelectOption(this, getString(R.string.app_name_lite),
                        itListener, getResources().getStringArray(R.array.select_place)).create().show();
                ProgressDialog pdCover = buildProgressDialog(this, getString(R.string.app_name), getString(R.string.traitement_encours));
                pdCover.show();
                fbtools.deleteOldImage(pdCover, imageUri, iscover, user.getUcover());
                break;
            case 2: // Full name
                showDialog(UNOMS);
                break;
            case 3: // Telephone
                showDialog(UTELEPHONE);
                break;
            case 4: // Description
                showDialog(UDESCRIPTION);
                break;
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

    private final DialogInterface.OnClickListener okDeleteListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            sptools.removeAllData();
            gotomain(SettingsActivity.this);
        }
    };

    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null){
                            imageUri = data.getData();
                        }
                        if (iscover)
                            ivCoverUser.setImageURI(imageUri);
                        else
                            ivAvatarUser.setImageURI(imageUri);
                    }
                }
            });


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ivtools.requestCameraAndGalleryPermissions(requestCode, grantResults, imageUri);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tvDisplayMode)
            buildAlertDialogForSingleSelectOption(this, getString(R.string.choisir_mode),
                    modeListener, getResources().getStringArray(R.array.modes),
                    sptools.readIntData(APP_PREFS_MODE, 0)).create().show();
        else if (id == R.id.tvLanguage)
            buildAlertDialogForSingleSelectOption(this, getString(R.string.choisir_langue),
                    langueListener, getResources().getStringArray(R.array.langues),
                    selectedLangue(sptools.readStringData(APP_PREFS_LANGUE, ""))).create().show();
        else if (id == R.id.tvGroupe)
            showDialogGroupe();
        else if (id == R.id.tvUserInfo)
            showDialogUserInfo();
        else if (id == R.id.tvAbout)
            showDialogAbout();
        else if (id == R.id.tvSignOut)
            buildAlertDialog(this, getString(R.string.app_name), getString(R.string.signout_message),
                    null, getString(R.string.non), okModeListener, getString(R.string.oui)).create().show();
        else if (id == R.id.tvDeleteAccount)
            buildAlertDialog(this, getString(R.string.app_name), getString(R.string.delete_account_message),
                    null, getString(R.string.non), okDeleteListener, getString(R.string.oui)).create().show();

        else if (id == R.id.tvBloquer)
            bloquerUser();
        else if (id == R.id.tvSignaler)
            signalerUser();
        else if (id == R.id.tvSave)
            saveUser();
        else if (id == R.id.tvCall)
            callUser();
        else if (id == R.id.tvSendMessage)
            sendMessageUser();
        else if (id == R.id.tvTasks)
            showTasks();
        else if (id == R.id.tvFavoris)
            showFavoris();
        else if (id == R.id.tvLikes)
            showLikes();
        else if (id == R.id.tvComments)
            showComments();
        else if (id == R.id.tvGroups)
            showGroups();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotohome(this);
    }

}