package com.deone.extrmtasks;

import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Constants.UDESCRIPTION;
import static com.deone.extrmtasks.tools.Constants.UNOMS;
import static com.deone.extrmtasks.tools.Constants.UTELEPHONE;
import static com.deone.extrmtasks.tools.Other.buildAlertDialog;
import static com.deone.extrmtasks.tools.Other.buildAlertDialogForSelectOption;
import static com.deone.extrmtasks.tools.Other.buildProgressDialog;
import static com.deone.extrmtasks.tools.Other.createTitle;
import static com.deone.extrmtasks.tools.Other.gotomain;
import static com.deone.extrmtasks.tools.Other.initLLanguage;
import static com.deone.extrmtasks.tools.Other.initThemeMode;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.deone.extrmtasks.tools.Fbtools;
import com.deone.extrmtasks.tools.Ivtools;
import com.deone.extrmtasks.tools.Sptools;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private Fbtools fbtools;
    private Sptools sptools;
    private Ivtools ivtools;
    private String idIntent;
    private boolean iscover = false;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initApp();
        super.onCreate(savedInstanceState);
        checkUser();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tvDeleteAccount)
            buildAlertDialog(this, getString(R.string.app_name), getString(R.string.delete_account_message),
                    null, getString(R.string.non), okDeleteListener, getString(R.string.oui)).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ivtools.requestCameraAndGalleryPermissions(requestCode, grantResults, imageUri);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

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
                        /*if (iscover)
                            ivCover.setImageURI(imageUri);
                        else
                            ivAvatar.setImageURI(imageUri);*/
                    }
                }
            });

    private void initApp() {
        sptools = Sptools.getInstance(this);
        fbtools = Fbtools.getInstance(this);
        ivtools = Ivtools.getInstance(this);
        initThemeMode(sptools.readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        initLLanguage(this, sptools.readStringData(APP_PREFS_LANGUE, EN));
    }

    private void checkUser() {
        if(isStringEmpty(fbtools.getId())){
            gotomain(this);
        }else {
            initViews();
        }
    }

    private void initViews() {
        setContentView(R.layout.activity_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ivAvatar = findViewById(R.id.ivAvatar);

        ivtools.setSomeActivityResultLauncher(someActivityResultLauncher);

        findViewById(R.id.tvDeleteAccount).setOnClickListener(this);
    }

    private final DialogInterface.OnClickListener okDeleteListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            /*sptools.removeAllData();
            gotomain(SettingsActivity.this);*/
        }
    };

    private final DialogInterface.OnClickListener optionListener = (dialogInterface, i) -> {
        /*switch (i) {
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
        }*/
    };


        /*if (item.getItemId() == R.id.itEditer){
            buildAlertDialogForSelectOption(this, getString(R.string.choisir_option),
                    optionListener, getResources().getStringArray(R.array.user_item)).create().show();
        }*/

    private final DialogInterface.OnClickListener itListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            /*switch(i){
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
            }*/
        }
    };

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
        //fbtools.updateStringWithFieldAndValue(pd, field, value);
    }
}