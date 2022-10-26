package com.deone.extrmtasks;

import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Constants.LOCATION_REQUEST_CODE_ACCESS_COARSE_LOCATION;
import static com.deone.extrmtasks.tools.Constants.LOCATION_REQUEST_CODE_ACCESS_FINE_LOCATION;
import static com.deone.extrmtasks.tools.Constants.MINIMUM_DISTANCE;
import static com.deone.extrmtasks.tools.Constants.MINIMUM_TIME;
import static com.deone.extrmtasks.tools.Other.buildAlertDialogForSelectOption;
import static com.deone.extrmtasks.tools.Other.buildProgressDialog;
import static com.deone.extrmtasks.tools.Other.getXtimestamp;
import static com.deone.extrmtasks.tools.Other.gotomain;
import static com.deone.extrmtasks.tools.Other.initLLanguage;
import static com.deone.extrmtasks.tools.Other.initThemeMode;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.deone.extrmtasks.modeles.Taches;
import com.deone.extrmtasks.modeles.User;
import com.deone.extrmtasks.tools.Fbtools;
import com.deone.extrmtasks.tools.Ivtools;
import com.deone.extrmtasks.tools.Lctools;
import com.deone.extrmtasks.tools.Sptools;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Fbtools fbtools;
    private Sptools sptools;
    private Ivtools ivtools;
    private Lctools lctools;
    private ImageView ivTachesLogo;
    private TextView tvWindowTitle;
    private TextView tvWindowSubtitle;
    private TextView tvTitle;
    private TextView tvDescription;
    private EditText etvTitre;
    private EditText etvDescription;
    private SwitchCompat swLocate;
    private Uri imageUri;
    private Taches taches;
    private User user;

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
        ivtools = Ivtools.getInstance(this);
        lctools = Lctools.getInstance(this);
        initThemeMode(sptools.readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        initLLanguage(this, sptools.readStringData(APP_PREFS_LANGUE, EN));
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lctools.setLocationManager(locationManager);
        lctools.initLocation();
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
        setContentView(R.layout.activity_add);
        ivTachesLogo = findViewById(R.id.ivTachesLogo);
        tvWindowTitle = findViewById(R.id.tvWindowTitle);
        tvWindowSubtitle = findViewById(R.id.tvWindowSubtitle);
        tvTitle = findViewById(R.id.tvTitle);
        etvTitre = findViewById(R.id.etvTitre);
        tvDescription = findViewById(R.id.tvDescription);
        etvDescription = findViewById(R.id.etvDescription);
        swLocate = findViewById(R.id.swLocate);
        ivtools.setSomeActivityResultLauncher(someActivityResultLauncher);
        fbtools.specificUser(vUser);
        findViewById(R.id.ivAddImage).setOnClickListener(this);
        findViewById(R.id.btAddTask).setOnClickListener(this);
        swLocate.setOnCheckedChangeListener(this);
    }

    /**
     *
     */
    private void verifDataBeforeAddProcess() {
        ProgressDialog pd = buildProgressDialog(this, getString(R.string.app_name), getString(R.string.verif_entries));
        pd.show();
        String title = etvTitre.getText().toString().trim();
        if (isStringEmpty(title)){
            pd.dismiss();
            Toast.makeText(this, getString(R.string.titre_error), Toast.LENGTH_SHORT).show();
            return;
        }
        String description = etvDescription.getText().toString().trim();
        if (isStringEmpty(description)){
            pd.dismiss();
            Toast.makeText(this, getString(R.string.description_error), Toast.LENGTH_SHORT).show();
            return;
        }
        pd.setMessage(getString(R.string.init_task_id));
        String timestamp = getXtimestamp();
        if (swLocate.isChecked()){
            pd.setMessage(getString(R.string.locate_task));
            taches = new Taches(""+timestamp, "", ""+title, ""+description,
                    ""+lctools.getCountry(), ""+lctools.getCity(), ""+lctools.getAdresse(),
                    ""+lctools.getCodepostal(), ""+lctools.getLongitude(),
                    ""+lctools.getLatitude(), ""+lctools.getState(), ""+timestamp,
                    "0", "0", ""+user.getUid(), ""+user.getUnoms(), ""+user.getUavatar());
        } else {
            taches = new Taches(""+timestamp, "", ""+title, ""+description,
                    "", "", "", "", "", "", "",
                    ""+timestamp, "0", "0", ""+user.getUid(),
                    ""+user.getUnoms(), ""+user.getUavatar());
        }

        if(imageUri != null){
            pd.setMessage(getString(R.string.save_task_image));
            fbtools.addPictureInSpecificTask(pd, imageUri, taches, user.getUntask());
        }
        else
            fbtools.addTaskInSpecificUserAccount(pd, taches, user.getUntask());
    }

    /**
     *
     * @param snapshot
     */
    private void showUserInformation(DataSnapshot snapshot) {
        for (DataSnapshot ds : snapshot.getChildren()){
            user = ds.getValue(User.class);
        }
    }

    private final ValueEventListener vUser = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            showUserInformation(snapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(AddActivity.this, getString(R.string.database_error), Toast.LENGTH_SHORT).show();
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
                        ivTachesLogo.setImageURI(imageUri);
                    }
                }
            });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE_ACCESS_COARSE_LOCATION || requestCode == LOCATION_REQUEST_CODE_ACCESS_FINE_LOCATION)
            lctools.requestLoccationPermissionsResults(requestCode, grantResults);
        else
            ivtools.requestCameraAndGalleryPermissions(requestCode, grantResults, imageUri);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btAddTask)
            verifDataBeforeAddProcess();
        else if (id == R.id.ivAddImage)
            buildAlertDialogForSelectOption(
                    this, getString(R.string.app_name_lite),
                    itListener, getResources().getStringArray(R.array.select_place)).create().show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        if (id == R.id.swLocate && b){
            lctools.testGeocoder();
        }
    }

}