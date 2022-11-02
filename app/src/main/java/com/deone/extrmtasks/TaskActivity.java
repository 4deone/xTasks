package com.deone.extrmtasks;

import static android.view.View.GONE;
import static android.view.View.OVER_SCROLL_ALWAYS;
import static android.view.View.VISIBLE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.CAMERA_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_ACCOUNT;
import static com.deone.extrmtasks.tools.Constants.IDFRAGMENT;
import static com.deone.extrmtasks.tools.Constants.LOCATION_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Constants.STORAGE_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Constants.TACHES;
import static com.deone.extrmtasks.tools.Constants.TCOVER;
import static com.deone.extrmtasks.tools.Constants.TDESCRIPTION;
import static com.deone.extrmtasks.tools.Constants.TID;
import static com.deone.extrmtasks.tools.Constants.TTITRE;
import static com.deone.extrmtasks.tools.Constants.UID;
import static com.deone.extrmtasks.tools.Fbtools.ecrireUnNouveauCommentaire;
import static com.deone.extrmtasks.tools.Fbtools.ecrireUnSignalementDeTache;
import static com.deone.extrmtasks.tools.Fbtools.lireunetachespecifique;
import static com.deone.extrmtasks.tools.Ivtools.loadingImageWithPath;
import static com.deone.extrmtasks.tools.Lctools.checkAccessFineLocationPermissions;
import static com.deone.extrmtasks.tools.Lctools.displayTaskLocation;
import static com.deone.extrmtasks.tools.Lctools.requestAccessFineLocationPermissions;
import static com.deone.extrmtasks.tools.Other.buildAlertDialog;
import static com.deone.extrmtasks.tools.Other.buildPathWithSlash;
import static com.deone.extrmtasks.tools.Other.buildProgressDialog;
import static com.deone.extrmtasks.tools.Other.checkBeforeFormatData;
import static com.deone.extrmtasks.tools.Other.formatAdresse;
import static com.deone.extrmtasks.tools.Other.formatLaDate;
import static com.deone.extrmtasks.tools.Other.gotohome;
import static com.deone.extrmtasks.tools.Other.initLLanguage;
import static com.deone.extrmtasks.tools.Other.initThemeMode;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;
import static com.deone.extrmtasks.tools.Other.rvLayoutManager;
import static com.deone.extrmtasks.tools.Sptools.readIntData;
import static com.deone.extrmtasks.tools.Sptools.readStringData;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deone.extrmtasks.adapters.Cadapter;
import com.deone.extrmtasks.modeles.Commentaire;
import com.deone.extrmtasks.modeles.Signale;
import com.deone.extrmtasks.modeles.Tache;
import com.deone.extrmtasks.modeles.User;
import com.deone.extrmtasks.tools.Fbtools;
import com.deone.extrmtasks.tools.Ivtools;
import com.deone.extrmtasks.tools.Xlistener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener {

    private Fbtools fbtools;
    private Ivtools ivtools;
    private Uri imageUri;
    private ImageView ivtaskAvatarUser;
    private ImageView ivTaskCover;
    private ImageButton ibTaskSignale;
    private TextView tvTaskUsername;
    private TextView tvTaskPublicationDate;
    private TextView tvTaskTitle;
    private TextView tvTaskDescription;
    private TextView tvTaskAdresse;
    private TextView tvTaskNcomment;
    private TextView tvTaskNjaime;
    private RecyclerView rvTaskComments;
    private EditText etvTaskComment;
    private List<Commentaire> commentaireList;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String tid;
    private String uid;
    private String myuid;
    private User CurrentUser;
    private Tache tache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initApp();
        super.onCreate(savedInstanceState);
        checkUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_menu, menu);
        MenuItem editItem = menu.findItem(R.id.itEditer);
        editItem.setVisible(myuid.equals(uid));
        MenuItem deleteItem = menu.findItem(R.id.itDelete);
        deleteItem.setVisible(myuid.equals(uid));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itDelete && myuid.equals(tache.getUid())){
            buildAlertDialog(
                    this, getString(R.string.app_name),
                    getString(R.string.delete_task_message),
                    null, getString(R.string.non),
                    adListener, getString(R.string.oui)).create().show();
        }
        if (item.getItemId() == R.id.itEditer){
            showEditDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ibTaskJaime)
            likeProcess();
        else if (id == R.id.ibTaskFavorite)
            favorisProcess();
        else if (id == R.id.ibTaskShare)
            shareProcess();
        else if (id == R.id.ibTaskSendComment)
            verifDataBeforeSendComment();
        else if (!myuid.equals(tache.getUid()) && (id == R.id.ivtaskAvatarUser || id == R.id.tvTaskUsername))
            showUserDetails();
        else if (id == R.id.tvTaskAdresse)
            showAdresseDialog();
        else if (id == R.id.ibTaskSignale)
            showSignalerDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == STORAGE_REQUEST_CODE){
                assert data != null;
                imageUri = data.getData();
            } else if (requestCode == CAMERA_REQUEST_CODE){
                imageUri = ivtools.getImageUri();
            }
            ProgressDialog pd = buildProgressDialog(this, getString(R.string.app_name), getString(R.string.task_image_path));
            pd.show();
            fbtools.deleteOldImage(pd, imageUri, ""+ tache.getTcover(), ""+buildPathWithSlash(TACHES, tid, TCOVER));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.enabled_location_permissions), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.enable_location_permissions), Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // TODO: Les methodes usuellles pour l'initialisation de l'activité

    private void initApp() {

        fbtools = Fbtools.getInstance(this);
        myuid = fbtools.getId();

        ivtools = Ivtools.getInstance(this);

        tid = getIntent().getStringExtra(TID);
        uid = getIntent().getStringExtra(UID);

        initThemeMode(readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        initLLanguage(this, readStringData(APP_PREFS_LANGUE, EN));
    }

    private void checkUser() {
        if(isStringEmpty(myuid)||isStringEmpty(tid)){
            gotohome(this);
        }else {
            initViews();
        }
    }

    private void initViews() {
        setContentView(R.layout.activity_task);

        Toolbar toolbarTask = findViewById(R.id.toolbarTask);
        setSupportActionBar(toolbarTask);

        ivtaskAvatarUser = findViewById(R.id.ivtaskAvatarUser);
        tvTaskUsername = findViewById(R.id.tvTaskUsername);
        tvTaskPublicationDate = findViewById(R.id.tvTaskPublicationDate);
        tvTaskTitle = findViewById(R.id.tvTaskTitle);
        tvTaskDescription = findViewById(R.id.tvTaskDescription);
        tvTaskAdresse = findViewById(R.id.tvTaskAdresse);
        ivTaskCover = findViewById(R.id.ivTaskCover);
        tvTaskNcomment = findViewById(R.id.tvTaskNcomment);
        tvTaskNjaime = findViewById(R.id.tvTaskNjaime);
        rvTaskComments = findViewById(R.id.rvTaskComments);
        rvTaskComments.setLayoutManager(rvLayoutManager(this, 0, LinearLayoutManager.VERTICAL));
        commentaireList = new ArrayList<>();
        etvTaskComment = findViewById(R.id.etvTaskComment);
        ibTaskSignale = findViewById(R.id.ibTaskSignale);
        ibTaskSignale.setVisibility(myuid.equals(uid)?GONE:VISIBLE);
        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(this);
        lireunetachespecifique(vCurrentUser, myuid, vTask, vComments, tid, vSignale);
        findViewById(R.id.ibTaskJaime).setOnClickListener(this);
        findViewById(R.id.ibTaskFavorite).setOnClickListener(this);
        findViewById(R.id.ibTaskShare).setOnClickListener(this);
        findViewById(R.id.ibTaskSendComment).setOnClickListener(this);
        ibTaskSignale.setOnClickListener(this);
        ivtaskAvatarUser.setOnClickListener(this);
        tvTaskUsername.setOnClickListener(this);
        tvTaskAdresse.setOnClickListener(this);
    }

    // TODO: Afficher les informations de l'utilisateur

    private void showCurrentUserInformation(DataSnapshot snapshot) {
        for (DataSnapshot ds : snapshot.getChildren()){
            CurrentUser = ds.getValue(User.class);
        }
    }

    private final ValueEventListener vCurrentUser = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            showCurrentUserInformation(snapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(TaskActivity.this, getString(R.string.database_error), Toast.LENGTH_SHORT).show();
        }
    };

    // TODO: Afficher les informations de la tache

    private void showTaskInformation(DataSnapshot snapshot) {
        for (DataSnapshot ds : snapshot.getChildren()){
            tache = ds.getValue(Tache.class);
            assert tache != null;
            tvTaskUsername.setText(myuid.equals(tache.getUid())?getString(R.string.you):tache.getUnoms());
            tvTaskPublicationDate.setText(formatLaDate(tache.getTdate()));
            tvTaskTitle.setText(tache.getTtitre());
            tvTaskDescription.setText(tache.getTdescription());
            tvTaskAdresse.setVisibility(isStringEmpty(tache.getTadresse())?GONE:VISIBLE);
            tvTaskAdresse.setText(isStringEmpty(tache.getTadresse())?"":""+ tache.getTville()+", "+ tache.getTpays());
            tvTaskNcomment.setText(checkBeforeFormatData(getString(R.string.comments), tache.getTncomment()));
            tvTaskNjaime.setText(checkBeforeFormatData(getString(R.string.like), tache.getTnlike()));
            loadingImageWithPath(ivTaskCover, R.drawable.wild, tache.getTcover());
            loadingImageWithPath(ivtaskAvatarUser, R.drawable.russia, tache.getUavatar());
        }
    }

    private final ValueEventListener vTask = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            showTaskInformation(snapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            //Toast.makeText(TaskActivity.this, getString(R.string.database_error), Toast.LENGTH_SHORT).show();
            Toast.makeText(TaskActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    // TODO: Afficher les commentaires de la tache

    private void showTaskComments(DataSnapshot snapshot) {
        commentaireList.clear();
        for (DataSnapshot ds : snapshot.getChildren()){
            Commentaire commentaire = ds.getValue(Commentaire.class);
            commentaireList.add(commentaire);
            Cadapter cadapter = new Cadapter(TaskActivity.this, commentaireList);
            rvTaskComments.setAdapter(cadapter);
            cadapter.setListener(xListener);
        }
    }

    private final ValueEventListener vComments = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            showTaskComments(snapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(TaskActivity.this, getString(R.string.database_error), Toast.LENGTH_SHORT).show();
        }
    };

    private final Xlistener xListener = new Xlistener() {
        @Override
        public void onItemClick(View view, int position) {

        }

        @Override
        public void onLongItemClick(View view, int position) {

        }
    };

    // TODO:

    private void showUserDetails() {
        Intent intent = new Intent(this, TempActivity.class);
        intent.putExtra(UID, uid);
        intent.putExtra(IDFRAGMENT, FRAGMENT_ACCOUNT);
        startActivity(intent);
    }

    // TODO: Liker la tache

    private void likeProcess() {
    }

    // TODO: Rendre favorite la tache

    private void favorisProcess() {
    }

    // TODO: Partager la tache

    private void shareProcess() {
    }

    // TODO: Formater l'adresse de la tache

    private void showAdresseDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.position_tache));
        alertDialog.setMessage(formatAdresse(
                getString(R.string.ville) + " : " + tache.getTville(),
                getString(R.string.state) + " : " + tache.getTstate(),
                getString(R.string.pays) + " : " + tache.getTpays(),
                getString(R.string.codepostal) + " : " + tache.getTcodepostal(),
                getString(R.string.adresse) + " : " + tache.getTadresse()));
        alertDialog.setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.cancel());
        alertDialog.setCancelable(false);
        AlertDialog alert=alertDialog.create();
        alert.show();
    }

    // TODO: Ajouter un commentaire

    private void verifDataBeforeSendComment() {
        String comment = etvTaskComment.getText().toString().trim();
        if (isStringEmpty(comment)){
            Toast.makeText(this, getString(R.string.comment_error), Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressDialog pd = buildProgressDialog(this, getString(R.string.app_name), getString(R.string.traitement_encours));
        pd.show();
        Commentaire commentaire = new Commentaire(comment, tid, CurrentUser.getUid(), CurrentUser.getUnoms(), CurrentUser.getUavatar());
        ecrireUnNouveauCommentaire(pd, commentaire, tache.getTncomment(), CurrentUser.getUncomments());
        etvTaskComment.setText(null);
    }

    // TODO:

    private void showSignalerDialog() {
        final Dialog dialogSignaler = new Dialog(this);
        dialogSignaler.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSignaler.setContentView(R.layout.dialod_signal);

        TextView tvSignalTitle = dialogSignaler.findViewById(R.id.tvSignalTitle);
        tvSignalTitle.setText(getString(R.string.signaler_la_tache, tache.getTtitre()));
        EditText edtSignalRaison =  dialogSignaler.findViewById(R.id.edtSignalRaison);

        Button btSendSignal = dialogSignaler.findViewById(R.id.btSendSignal);
        btSendSignal.setOnClickListener(v -> {
            String value = edtSignalRaison.getText().toString().trim();
            if (isStringEmpty(value)){
                Toast.makeText(TaskActivity.this, getString(R.string.reason_error), Toast.LENGTH_SHORT).show();
                return;
            }
            Signale signale = new Signale(""+CurrentUser.getUid(), ""+value, "", ""+tache.getTid(),
                    ""+tache.getTtitre(), ""+tache.getTcover(), ""+tache.getTdescription(),
                    ""+CurrentUser.getUid(), ""+CurrentUser.getUnoms(), ""+CurrentUser.getUavatar());
            ecrireUnSignalementDeTache(signale, ibTaskSignale);
            dialogSignaler.dismiss();
        });
        dialogSignaler.show();
    }

    private final ValueEventListener vSignale = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            ibTaskSignale.setImageResource(snapshot.hasChildren()?R.drawable.ic_action_signaler:R.drawable.ic_action_signaler_old);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(TaskActivity.this, getString(R.string.database_error), Toast.LENGTH_SHORT).show();
        }
    };

    // TODO: Modifier la tache

    private void showEditDialog() {
        AlertDialog.Builder builderEdit = new AlertDialog.Builder(this);
        builderEdit.setTitle(getString(R.string.app_name_lite));
        builderEdit.setItems(getResources().getStringArray(R.array.edit_task), optionEditListener);
        builderEdit.create().show();
    }

    private final DialogInterface.OnClickListener coverListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch(i){
                case 0 :
                    if (!ivtools.checkCameraPermissions()){
                        ivtools.requestCameraPermissions();
                    }else{
                        ivtools.pickFromCamera();
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

    private final DialogInterface.OnClickListener optionEditListener = (dialogInterface, i) -> {
        switch (i) {
            case 0: // cover
                updateCoverDialog();
                break;
            case 1: // titre
                updateTitreOrDescription(getString(R.string.titre), i);
                break;
            case 2: // description
                updateTitreOrDescription(getString(R.string.description), i);
                break;
            case 3: // localisation
                if (checkAccessFineLocationPermissions(TaskActivity.this))
                    showLocation();
                else
                    requestAccessFineLocationPermissions(TaskActivity.this);
                Toast.makeText(this, ""+tache.toString(), Toast.LENGTH_SHORT).show();
                break;
        }
    };

    private void showLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            requestAccessFineLocationPermissions(this);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null){
                displayTaskLocation(TaskActivity.this, location, tache);
                updateLocalisationDialog();
            }
            else
                Toast.makeText(TaskActivity.this, ""+getString(R.string.loc_error), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateCoverDialog() {
        AlertDialog.Builder builderCover = new AlertDialog.Builder(this);
        builderCover.setTitle(getString(R.string.app_name_lite));
        builderCover.setItems(getResources().getStringArray(R.array.select_place), coverListener);
        builderCover.create().show();
    }

    private void updateTitreOrDescription(String message, int i) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialod_edit);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(MessageFormat.format(getString(R.string.title_update), message));
        EditText edtValue =  dialog.findViewById(R.id.edtValue);
        switch (i) {
            case 1: // titre
                edtValue.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                edtValue.setHint(getString(R.string.enter_titre_de_la_tache));
                break;
            case 2: // description
                edtValue.setHint(getString(R.string.enter_description_de_la_tache));
                edtValue.setMaxHeight(300);
                edtValue.setGravity(Gravity.TOP);
                edtValue.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                edtValue.setLines(5);
                edtValue.setMaxLines(10);
                edtValue.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                edtValue.setOverScrollMode(OVER_SCROLL_ALWAYS);
                edtValue.setMovementMethod(ScrollingMovementMethod.getInstance());
                edtValue.setVerticalScrollBarEnabled(true);
                edtValue.setSingleLine(false);
                edtValue.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                break;
            default:
        }

        Button dialogButton = dialog.findViewById(R.id.btUpdate);
        dialogButton.setOnClickListener(v -> {
            String value = edtValue.getText().toString().trim();
            if (isStringEmpty(value)){
                Toast.makeText(TaskActivity.this, getString(R.string.value_error), Toast.LENGTH_SHORT).show();
                return;
            }
            switch (i) {
                case 1: // titre
                    fbtools.ecrireDansUnChamp(buildPathWithSlash(TACHES, tid, TTITRE), value);
                    break;
                case 2: // description
                    fbtools.ecrireDansUnChamp(buildPathWithSlash(TACHES, tid, TDESCRIPTION), value);
                    break;
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateLocalisationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name_lite));
        builder.setMessage(getString(R.string.loc_message));
        builder.setPositiveButton(getText(R.string.ok), (dialogInterface, i) -> {
            fbtools.ecrireunenouvelletache(tache);
            dialogInterface.dismiss();
        });
        builder.create().show();
    }

    // TODO: Suppression de la tache

    private final DialogInterface.OnClickListener adListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            ProgressDialog pd = buildProgressDialog(TaskActivity.this, getString(R.string.app_name), getString(R.string.traitement_encours));
            pd.show();
            fbtools.deleteTask(pd,tid, myuid, CurrentUser.getUntask(), CurrentUser.getUncomments());
        }
    };

}