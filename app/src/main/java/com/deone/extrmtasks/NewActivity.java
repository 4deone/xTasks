package com.deone.extrmtasks;

import static com.deone.extrmtasks.tools.Constants.APP_PREFS_CAMERA;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_GALLERY;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LOCATION;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.CAMERA_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Constants.LOCATION_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Constants.STORAGE_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Ivtools.isCameraAccepted;
import static com.deone.extrmtasks.tools.Ivtools.isWriteStorageAccepted;
import static com.deone.extrmtasks.tools.Other.buildProgressDialog;
import static com.deone.extrmtasks.tools.Other.gotohome;
import static com.deone.extrmtasks.tools.Other.gotomain;
import static com.deone.extrmtasks.tools.Other.initLLanguage;
import static com.deone.extrmtasks.tools.Other.initThemeMode;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;
import static com.deone.extrmtasks.tools.Other.rvLayoutManager;
import static com.deone.extrmtasks.tools.Other.toutesLesConditions;
import static com.deone.extrmtasks.tools.Signtools.checkEmailStatus;
import static com.deone.extrmtasks.tools.Sptools.readIntData;
import static com.deone.extrmtasks.tools.Sptools.readStringData;
import static com.deone.extrmtasks.tools.Sptools.removeAllData;
import static com.deone.extrmtasks.tools.Sptools.writeBooleanData;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deone.extrmtasks.adapters.Condadapter;
import com.deone.extrmtasks.modeles.Condition;
import com.deone.extrmtasks.modeles.User;
import com.deone.extrmtasks.tools.Signtools;
import com.deone.extrmtasks.tools.Sptools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewActivity extends AppCompatActivity implements View.OnClickListener {

    private Signtools signtools;
    private Sptools sptools;
    private EditText etvLoginNew;
    private EditText etvMotdepasseNew;
    private EditText etvFullNameNew;
    private EditText etvTelephoneNew;
    private EditText etvVilleNew;
    private EditText etvPaysNew;
    private RecyclerView rvConditionsNew;
    private CheckBox cbAcceptRulesNew;
    private List<Condition> conditionsList;

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
        initThemeMode(readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        initLLanguage(this, readStringData(APP_PREFS_LANGUE, EN));
        signtools = Signtools.getInstance(this);
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
        setContentView(R.layout.activity_new);
        etvLoginNew = findViewById(R.id.etvLoginNew);
        etvMotdepasseNew = findViewById(R.id.etvMotdepasseNew);
        etvFullNameNew = findViewById(R.id.etvFullNameNew);
        etvTelephoneNew = findViewById(R.id.etvTelephoneNew);
        etvVilleNew = findViewById(R.id.etvVilleNew);
        etvPaysNew = findViewById(R.id.etvPaysNew);
        rvConditionsNew = findViewById(R.id.rvConditionsNew);
        rvConditionsNew.setLayoutManager(rvLayoutManager(this, 0, LinearLayoutManager.VERTICAL));
        cbAcceptRulesNew = findViewById(R.id.cbAcceptRulesNew);
        conditionsList = new ArrayList<>();
        toutesLesConditions(vConditions);
        findViewById(R.id.btCreateAccountNew).setOnClickListener(this);
        findViewById(R.id.tvSignInNew).setOnClickListener(this);
    }

    /**
     *
     */
    private void signUpProcess() {
        ProgressDialog pd = buildProgressDialog(this, getString(R.string.app_name), getString(R.string.verif_entries));
        pd.show();
        String email = etvLoginNew.getText().toString().trim();
        if (isStringEmpty(email)){
            pd.dismiss();
            Toast.makeText(this, getString(R.string.email_error), Toast.LENGTH_SHORT).show();
            return;
        }
        String motdepasse = etvMotdepasseNew.getText().toString().trim();
        if (isStringEmpty(motdepasse)){
            pd.dismiss();
            Toast.makeText(this, getString(R.string.motdepasse_error), Toast.LENGTH_SHORT).show();
            return;
        }
        String fullname = etvFullNameNew.getText().toString().trim();
        if (isStringEmpty(fullname)){
            pd.dismiss();
            Toast.makeText(this, getString(R.string.fullname_error), Toast.LENGTH_SHORT).show();
            return;
        }
        String telephone = etvTelephoneNew.getText().toString().trim();
        if (isStringEmpty(telephone)){
            pd.dismiss();
            Toast.makeText(this, getString(R.string.telephone_error), Toast.LENGTH_SHORT).show();
            return;
        }
        String ville = etvVilleNew.getText().toString().trim();
        if (isStringEmpty(ville)){
            pd.dismiss();
            Toast.makeText(this, getString(R.string.ville_error), Toast.LENGTH_SHORT).show();
            return;
        }
        String pays = etvPaysNew.getText().toString().trim();
        if (isStringEmpty(pays)){
            pd.dismiss();
            Toast.makeText(this, getString(R.string.pays_error), Toast.LENGTH_SHORT).show();
            return;
        }
        User user = new User(""+email, ""+motdepasse, ""+fullname, ""+telephone,
                ""+ville, ""+pays);
        checkEmailStatus(pd, null, user);
    }

    /**
     *
     * @param snapshot Liste des informations relatives aux conditions inscrites dans la base de données
     */
    private void showToutesMesConditions(DataSnapshot snapshot) {
        conditionsList.clear();
        for (DataSnapshot ds : snapshot.getChildren()){
            Condition condition = ds.getValue(Condition.class);
            conditionsList.add(condition);
            Condadapter condadapter = new Condadapter(conditionsList);
            rvConditionsNew.setAdapter(condadapter);
        }
    }

    private final ValueEventListener vConditions  = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            showToutesMesConditions(snapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(NewActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    if (isCameraAccepted(grantResults[0]) && isWriteStorageAccepted(grantResults[1]))
                        writeBooleanData(APP_PREFS_CAMERA, true);
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    if (isWriteStorageAccepted(grantResults[0]))
                        writeBooleanData(APP_PREFS_GALLERY, true);
                }
            }
            break;
            case LOCATION_REQUEST_CODE:{
                if (grantResults.length>0){
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                        writeBooleanData(APP_PREFS_LOCATION, true);
                    else {
                        Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btCreateAccountNew && cbAcceptRulesNew.isChecked())
            signUpProcess();
        else if (id == R.id.tvSignInNew)
            gotomain(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeAllData();
        gotomain(this);
    }

}