package com.deone.extrmtasks;

import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.CAMERA_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Constants.STORAGE_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Lctools.checkAccessFineLocationPermissions;
import static com.deone.extrmtasks.tools.Lctools.displayTaskLocation;
import static com.deone.extrmtasks.tools.Lctools.requestAccessFineLocationPermissions;
import static com.deone.extrmtasks.tools.Other.buildProgressDialog;
import static com.deone.extrmtasks.tools.Other.getXtimestamp;
import static com.deone.extrmtasks.tools.Other.initLLanguage;
import static com.deone.extrmtasks.tools.Other.initThemeMode;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;
import static com.deone.extrmtasks.preference.Sptools.readIntData;
import static com.deone.extrmtasks.preference.Sptools.readStringData;

import android.Manifest;
import android.app.ProgressDialog;
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
import com.deone.extrmtasks.database.Fbtools;
import com.deone.extrmtasks.picture.Ivtools;
import com.deone.extrmtasks.preference.Sptools;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AddActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    // TODO: Initialisation des variables
    private Fbtools fbtools;
    private Ivtools ivtools;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ImageView ivAddTachesCover;
    private EditText edtvAddTitre;
    private EditText edtvAddDescription;
    private Uri imageUri;
    private Tache tache;
    private User userCreator;

    // TODO: Les écouteurs vUser pour firebase
    private final ValueEventListener vUserCreator = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            showUserInformation(snapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(AddActivity.this, getString(R.string.database_error), Toast.LENGTH_SHORT).show();
        }
    };
    // TODO: Les écouteurs selectCoverListener pour ivAddImage
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

    // TODO: Override methods for AddActivity
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

            ivAddTachesCover.setImageURI(imageUri);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btAddTask)
            this.verifDataBeforeAddProcess();
        else if (id == R.id.ivAddImage) {
            this.askWhereUserSelectPhoto();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        if (id == R.id.swAddLocate) {
            if (b) {
                if (checkAccessFineLocationPermissions(AddActivity.this)){
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                    showLocation();
                }
                else
                    requestAccessFineLocationPermissions(AddActivity.this);
            }
        }
    }

    // TODO: onCreate  methods
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
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            initViews();
        }
    }

    /**
     *
     */
    private void initViews() {
        setContentView(R.layout.activity_add);
        this.initAddTachesCover();
        this.initAddTitre();
        this.initAddDescription();
        this.initSwAddLocate();
        this.initIvAddImage();
        this.initBtAddTask();
        fbtools.lireUnUtilisateurSpecifique(vUserCreator);
        tache = new Tache();
    }

    // TODO: initViews methods
    private void initAddTachesCover() {
        ivAddTachesCover = findViewById(R.id.ivAddTachesCover);
    }

    private void initAddTitre() {
        edtvAddTitre = findViewById(R.id.edtvAddTitre);
    }

    private void initAddDescription() {
        edtvAddDescription = findViewById(R.id.edtvAddDescription);
    }

    private void initSwAddLocate() {
        SwitchCompat swAddLocate = findViewById(R.id.swAddLocate);
        swAddLocate.setOnCheckedChangeListener(this);
    }

    private void initIvAddImage() {
        findViewById(R.id.ivAddImage).setOnClickListener(this);
    }

    private void initBtAddTask() {
        findViewById(R.id.btAddTask).setOnClickListener(this);
    }

    // TODO: onClick methods
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
        tache.setUid("" + userCreator.getUid());
        tache.setUnoms("" + userCreator.getUnoms());
        tache.setUavatar("" + userCreator.getUavatar());
        if (imageUri != null) {
            pd.setMessage(getString(R.string.save_task_image));
            fbtools.ecrireUneNouvelleTacheApresMajPhotoUtilisateur(pd, imageUri, tache, userCreator.getUntask());
        } else
            fbtools.ecrireUneNouvelleTacheEtMajLutilisateur(pd, tache, userCreator.getUntask());
    }

    /**
     *
     */
    private void askWhereUserSelectPhoto() {
        AlertDialog.Builder builderCoverImage = new AlertDialog.Builder(this);
        builderCoverImage.setTitle(getString(R.string.app_name_lite));
        builderCoverImage.setItems(getResources().getStringArray(R.array.select_place), selectCoverListener);
        builderCoverImage.create().show();
    }

    // TODO: onCheckedChanged methods
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
                tache.setLocalize(displayTaskLocation(AddActivity.this, location));
                Toast.makeText(AddActivity.this, ""+tache.toString(), Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(AddActivity.this, ""+getString(R.string.loc_error), Toast.LENGTH_SHORT).show();
        });
    }

    // TODO: vUserCreator methods
    /**
     *
     * @param snapshot
     */
    private void showUserInformation(DataSnapshot snapshot) {
        for (DataSnapshot ds : snapshot.getChildren()) {
            userCreator = ds.getValue(User.class);
        }
    }




}