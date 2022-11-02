package com.deone.extrmtasks;

import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.CAMERA_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Constants.LOCATION_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Constants.STORAGE_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Lctools.checkAccessFineLocationPermissions;
import static com.deone.extrmtasks.tools.Lctools.displayTaskLocation;
import static com.deone.extrmtasks.tools.Lctools.requestAccessFineLocationPermissions;
import static com.deone.extrmtasks.tools.Other.buildProgressDialog;
import static com.deone.extrmtasks.tools.Other.getXtimestamp;
import static com.deone.extrmtasks.tools.Other.gotomain;
import static com.deone.extrmtasks.tools.Other.initLLanguage;
import static com.deone.extrmtasks.tools.Other.initThemeMode;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;
import static com.deone.extrmtasks.tools.Sptools.readIntData;
import static com.deone.extrmtasks.tools.Sptools.readStringData;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.deone.extrmtasks.modeles.Tache;
import com.deone.extrmtasks.modeles.User;
import com.deone.extrmtasks.tools.Fbtools;
import com.deone.extrmtasks.tools.Ivtools;
import com.deone.extrmtasks.tools.Lctools;
import com.deone.extrmtasks.tools.Sptools;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AddActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Fbtools fbtools;
    private Ivtools ivtools;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ImageView ivAddTachesLogo;
    private EditText edtvAddTitre;
    private EditText edtvAddDescription;
    private Uri imageUri;
    private Tache tache;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initApp();
        super.onCreate(savedInstanceState);
        checkUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == STORAGE_REQUEST_CODE) {
                assert data != null;
                imageUri = data.getData();
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                imageUri = ivtools.getImageUri();
            }

            ivAddTachesLogo.setImageURI(imageUri);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.enabled_camera_permissions), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.enable_camera_permissions), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.enabled_storage_permissions), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.enable_storage_permissions), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.enabled_location_permissions), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.enable_location_permissions), Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btAddTask)
            verifDataBeforeAddProcess();
        else if (id == R.id.ivAddImage) {
            AlertDialog.Builder builderCoverImage = new AlertDialog.Builder(this);
            builderCoverImage.setTitle(getString(R.string.app_name_lite));
            builderCoverImage.setItems(getResources().getStringArray(R.array.select_place), selectCoverListener);
            builderCoverImage.create().show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        if (id == R.id.swAddLocate) {
            if (b) {
                if (checkAccessFineLocationPermissions(AddActivity.this))
                    showLocation();
                else
                    requestAccessFineLocationPermissions(AddActivity.this);
            }
        }
    }

    /**
     *
     */
    private void initApp() {
        // TODO: Init theme mode & language
        Sptools sptools = Sptools.getInstance(this);
        initThemeMode(readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        initLLanguage(this, readStringData(APP_PREFS_LANGUE, EN));
        // TODO: Init firbase tools
        fbtools = Fbtools.getInstance(this);
        // TODO: Init pictures tools
        ivtools = Ivtools.getInstance(this);
    }

    /**
     *
     */
    private void checkUser() {
        if (isStringEmpty(fbtools.getId())) {
            gotomain(this);
        } else {
            initViews();
        }
    }

    /**
     *
     */
    private void initViews() {
        setContentView(R.layout.activity_add);
        ivAddTachesLogo = findViewById(R.id.ivAddTachesLogo);
        edtvAddTitre = findViewById(R.id.edtvAddTitre);
        edtvAddDescription = findViewById(R.id.edtvAddDescription);
        SwitchCompat swAddLocate = findViewById(R.id.swAddLocate);
        fbtools.lireUnUtilisateurSpecifique(vUser);
        tache = new Tache();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        findViewById(R.id.ivAddImage).setOnClickListener(this);
        findViewById(R.id.btAddTask).setOnClickListener(this);
        swAddLocate.setOnCheckedChangeListener(this);
    }

    /**
     *
     */
    private void verifDataBeforeAddProcess() {
        ProgressDialog pd = buildProgressDialog(this, getString(R.string.app_name), getString(R.string.verif_entries));
        pd.show();
        String title = edtvAddTitre.getText().toString().trim();
        if (isStringEmpty(title)) {
            pd.dismiss();
            Toast.makeText(this, getString(R.string.titre_error), Toast.LENGTH_SHORT).show();
            return;
        }
        String description = edtvAddDescription.getText().toString().trim();
        if (isStringEmpty(description)) {
            pd.dismiss();
            Toast.makeText(this, getString(R.string.description_error), Toast.LENGTH_SHORT).show();
            return;
        }
        pd.setMessage(getString(R.string.init_task_id));
        String timestamp = getXtimestamp();
        tache.setTid("" + timestamp);
        tache.setTtitre("" + title);
        tache.setTdescription("" + description);
        tache.setTdate("" + timestamp);
        tache.setUid("" + user.getUid());
        tache.setUnoms("" + user.getUnoms());
        tache.setUavatar("" + user.getUavatar());
        if (imageUri != null) {
            pd.setMessage(getString(R.string.save_task_image));
            fbtools.ecrireUneNouvelleTacheApresMajPhotoUtilisateur(pd, imageUri, tache, user.getUntask());
        } else
            fbtools.ecrireUneNouvelleTacheEtMajLutilisateur(pd, tache, user.getUntask());
    }

    /**
     *
     */
    private void showLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            requestAccessFineLocationPermissions(this);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null){
                displayTaskLocation(AddActivity.this, location, tache);
                Toast.makeText(AddActivity.this, ""+tache.toString(), Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(AddActivity.this, ""+getString(R.string.loc_error), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     *
     * @param snapshot
     */
    private void showUserInformation(DataSnapshot snapshot) {
        for (DataSnapshot ds : snapshot.getChildren()) {
            user = ds.getValue(User.class);
        }
    }

    // TODO: Les écouteurs vUser & selectCoverListener

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

    private final DialogInterface.OnClickListener selectCoverListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (i) {
                case 0:
                    if (!ivtools.checkCameraPermissions()) {
                        ivtools.requestCameraPermissions();
                    } else {
                        ivtools.pickFromCamera();
                    }
                    break;
                case 1:
                    if (!ivtools.checkStoragePermissions()) {
                        ivtools.requestStoragePermissions();
                    } else {
                        ivtools.pickFromGallery();
                    }
                    break;
                default:
            }
        }
    };


}