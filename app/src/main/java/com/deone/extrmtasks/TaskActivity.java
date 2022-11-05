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
import static com.deone.extrmtasks.tools.Constants.STORAGE_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Constants.TACHES;
import static com.deone.extrmtasks.tools.Constants.TCOVER;
import static com.deone.extrmtasks.tools.Constants.TDESCRIPTION;
import static com.deone.extrmtasks.tools.Constants.TID;
import static com.deone.extrmtasks.tools.Constants.TTITRE;
import static com.deone.extrmtasks.tools.Constants.UID;
import static com.deone.extrmtasks.database.Fbtools.ecrireUnNouveauCommentaire;
import static com.deone.extrmtasks.database.Fbtools.ecrireUnSignalementDeTache;
import static com.deone.extrmtasks.database.Fbtools.lireunetachespecifique;
import static com.deone.extrmtasks.picture.Ivtools.loadingImageWithPath;
import static com.deone.extrmtasks.tools.Lctools.checkAccessFineLocationPermissions;
import static com.deone.extrmtasks.tools.Lctools.displayTaskLocation;
import static com.deone.extrmtasks.tools.Lctools.requestAccessFineLocationPermissions;
import static com.deone.extrmtasks.tools.Other.buildPathWithSlash;
import static com.deone.extrmtasks.tools.Other.buildProgressDialog;
import static com.deone.extrmtasks.tools.Other.checkBeforeFormatData;
import static com.deone.extrmtasks.tools.Other.formatAdresse;
import static com.deone.extrmtasks.tools.Other.formatLaDate;
import static com.deone.extrmtasks.tools.Other.initLLanguage;
import static com.deone.extrmtasks.tools.Other.initThemeMode;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;
import static com.deone.extrmtasks.tools.Other.rvLayoutManager;
import static com.deone.extrmtasks.preference.Sptools.readIntData;
import static com.deone.extrmtasks.preference.Sptools.readStringData;

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
import com.deone.extrmtasks.modeles.Localize;
import com.deone.extrmtasks.modeles.Signale;
import com.deone.extrmtasks.modeles.Tache;
import com.deone.extrmtasks.modeles.User;
import com.deone.extrmtasks.database.Fbtools;
import com.deone.extrmtasks.picture.Ivtools;
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
    // TODO: Initialisation des variables
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
    private String idCurrentTask;
    private String uidCurrentTaskAdmin;
    private String uidVisitor;
    private User userVisitor;
    private Tache currentTask;

    // TODO: Les écouteurs de l'adaptateur Cadapter
    private final Xlistener xListener = new Xlistener() {
        @Override
        public void onItemClick(View view, int position) {

        }

        @Override
        public void onLongItemClick(View view, int position) {

        }
    };

    // TODO: Les écouteurs firebase
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
    private final ValueEventListener vCurrentTask = new ValueEventListener() {
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
    private final ValueEventListener vCurrentTaskComments = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            showTaskComments(snapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(TaskActivity.this, getString(R.string.database_error), Toast.LENGTH_SHORT).show();
        }
    };
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

    // TODO: DialogInterface OnClickListener
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
    private final DialogInterface.OnClickListener chooseOptionEditListener = (dialogInterface, i) -> {
        switch (i) {
            case 0: // cover
                this.updateCoverDialog();
                break;
            case 1: // titre
                this.updateTitreOrDescription(getString(R.string.titre), i);
                break;
            case 2: // description
                this.updateTitreOrDescription(getString(R.string.description), i);
                break;
            case 3: // localisation
                if (checkAccessFineLocationPermissions(TaskActivity.this)){
                    this.initFusedLocationProviderClient();
                    this.checkUserLocation();
                } else
                    requestAccessFineLocationPermissions(TaskActivity.this);
                Toast.makeText(this, ""+ currentTask.toString(), Toast.LENGTH_SHORT).show();
                break;
        }
    };
    private final DialogInterface.OnClickListener yesDeleteTaskListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            ProgressDialog pd = buildProgressDialog(TaskActivity.this, getString(R.string.app_name), getString(R.string.traitement_encours));
            pd.show();
            fbtools.deleteTask(pd, idCurrentTask, uidVisitor, userVisitor.getUntask(), userVisitor.getUncomments());
        }
    };

    // TODO: Override methods for TaskActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.initApp();
        super.onCreate(savedInstanceState);
        this.checkUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_menu, menu);
        this.initMenuItem(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itDelete && uidVisitor.equals(currentTask.getUid())){
            this.showDeleteDialog();
        }
        if (item.getItemId() == R.id.itEditer){
            this.showEditDialog();
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
        else if (!uidVisitor.equals(currentTask.getUid()) && (id == R.id.ivtaskAvatarUser || id == R.id.tvTaskUsername))
            openTempActivityAndShowUserDetails();
        else if (id == R.id.tvTaskAdresse)
            showAdresseDialog();
        else if (id == R.id.ibTaskSignale)
            showSignalerDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            this.updateTaskCover(requestCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // TODO: onCreate  methods
    private void initApp() {

        fbtools = Fbtools.getInstance(this);
        uidVisitor = fbtools.getId();

        ivtools = Ivtools.getInstance(this);

        idCurrentTask = getIntent().getStringExtra(TID);
        uidCurrentTaskAdmin = getIntent().getStringExtra(UID);

        initThemeMode(readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        initLLanguage(this, readStringData(APP_PREFS_LANGUE, EN));
    }

    private void checkUser() {
        if(isStringEmpty(uidVisitor)||isStringEmpty(idCurrentTask)){
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }else {
            initViews();
        }
    }

    private void initViews() {
        setContentView(R.layout.activity_task);
        this.initToolbarTask();
        this.initIvtaskAvatarUser();
        this.initTaskUsername();
        this.initTaskPublicationDate();
        this.initTaskTitle();
        this.initTaskDescription();
        this.initTaskAdresse();
        this.initTaskCover();
        this.initTaskNcomment();
        this.initTaskNjaime();
        this.initRvTaskComments();
        this.initTaskComment();
        this.initTaskSignale();
        this.initIbTaskJaime();
        this.initIbTaskFavorite();
        this.initIbTaskShare();
        this.initIbTaskSendComment();
        lireunetachespecifique(vCurrentUser, uidVisitor, vCurrentTask, vCurrentTaskComments, idCurrentTask, vSignale);
    }

    // TODO: initViews methods
    private void initToolbarTask() {
        Toolbar toolbarTask = findViewById(R.id.toolbarTask);
        toolbarTask.setTitle(getString(R.string.one_task));
        toolbarTask.setSubtitle(getString(R.string.open_one_task));
        setSupportActionBar(toolbarTask);
    }

    private void initIvtaskAvatarUser() {
        ivtaskAvatarUser = findViewById(R.id.ivtaskAvatarUser);
        ivtaskAvatarUser.setOnClickListener(this);
    }

    private void initTaskUsername() {
        tvTaskUsername = findViewById(R.id.tvTaskUsername);
        tvTaskUsername.setOnClickListener(this);
    }

    private void initTaskPublicationDate() {
        tvTaskPublicationDate = findViewById(R.id.tvTaskPublicationDate);
    }

    private void initTaskTitle() {
        tvTaskTitle = findViewById(R.id.tvTaskTitle);
    }

    private void initTaskDescription() {
        tvTaskDescription = findViewById(R.id.tvTaskDescription);
    }

    private void initTaskAdresse() {
        tvTaskAdresse = findViewById(R.id.tvTaskAdresse);
        tvTaskAdresse.setOnClickListener(this);
    }

    private void initTaskCover() {
        ivTaskCover = findViewById(R.id.ivTaskCover);
    }

    private void initTaskNcomment() {
        tvTaskNcomment = findViewById(R.id.tvTaskNcomment);
    }

    private void initTaskNjaime() {
        tvTaskNjaime = findViewById(R.id.tvTaskNjaime);
    }

    private void initRvTaskComments() {
        rvTaskComments = findViewById(R.id.rvTaskComments);
        rvTaskComments.setLayoutManager(rvLayoutManager(this, 0, LinearLayoutManager.VERTICAL));
    }

    private void initTaskComment() {
        commentaireList = new ArrayList<>();
        etvTaskComment = findViewById(R.id.etvTaskComment);
    }

    private void initFusedLocationProviderClient() {
        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(this);
    }

    private void initIbTaskJaime() {
        findViewById(R.id.ibTaskJaime).setOnClickListener(this);
    }

    private void initIbTaskFavorite() {
        findViewById(R.id.ibTaskFavorite).setOnClickListener(this);
    }

    private void initIbTaskShare() {
        findViewById(R.id.ibTaskShare).setOnClickListener(this);
    }

    private void initIbTaskSendComment() {
        findViewById(R.id.ibTaskSendComment).setOnClickListener(this);
    }

    private void initTaskSignale() {
        ibTaskSignale = findViewById(R.id.ibTaskSignale);
        ibTaskSignale.setVisibility(uidVisitor.equals(uidCurrentTaskAdmin)?GONE:VISIBLE);
        ibTaskSignale.setOnClickListener(this);
    }

    // TODO: onCreateOptionsMenu methods
    private void initMenuItem(Menu menu) {
        MenuItem editItem = menu.findItem(R.id.itEditer);
        editItem.setVisible(uidVisitor.equals(uidCurrentTaskAdmin));
        MenuItem deleteItem = menu.findItem(R.id.itDelete);
        deleteItem.setVisible(uidVisitor.equals(uidCurrentTaskAdmin));
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builderDelete = new AlertDialog.Builder(this);
        builderDelete.setTitle(getString(R.string.app_name_lite));
        builderDelete.setMessage(getString(R.string.delete_task_message));
        builderDelete.setNegativeButton(getString(R.string.non), null);
        builderDelete.setPositiveButton(getString(R.string.oui), yesDeleteTaskListener);
        builderDelete.create().show();
    }

    private void showEditDialog() {
        AlertDialog.Builder builderEdit = new AlertDialog.Builder(this);
        builderEdit.setTitle(getString(R.string.app_name_lite));
        builderEdit.setItems(getResources().getStringArray(R.array.edit_task), chooseOptionEditListener);
        builderEdit.create().show();
    }

    // TODO: onClick methods
    private void openTempActivityAndShowUserDetails() {
        Intent intent = new Intent(this, TempActivity.class);
        intent.putExtra(UID, uidCurrentTaskAdmin);
        intent.putExtra(IDFRAGMENT, FRAGMENT_ACCOUNT);
        startActivity(intent);
    }

    private void likeProcess() {
    }

    private void favorisProcess() {
    }

    private void shareProcess() {
    }

    private void showAdresseDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.position_tache));
        alertDialog.setMessage(formatAdresse(
                getString(R.string.ville) + " : " + currentTask.getLocalize().getCity(),
                getString(R.string.state) + " : " + currentTask.getLocalize().getState(),
                getString(R.string.pays) + " : " + currentTask.getLocalize().getCountry(),
                getString(R.string.codepostal) + " : " + currentTask.getLocalize().getCodepostal(),
                getString(R.string.adresse) + " : " + currentTask.getLocalize().getAddress()));
        alertDialog.setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.cancel());
        alertDialog.setCancelable(false);
        AlertDialog alert=alertDialog.create();
        alert.show();
    }

    private void showSignalerDialog() {
        final Dialog dialogSignaler = new Dialog(this);
        dialogSignaler.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSignaler.setContentView(R.layout.dialod_signal);

        TextView tvSignalTitle = dialogSignaler.findViewById(R.id.tvSignalTitle);
        tvSignalTitle.setText(getString(R.string.signaler_la_tache, currentTask.getTtitre()));
        EditText edtSignalRaison =  dialogSignaler.findViewById(R.id.edtSignalRaison);

        Button btSendSignal = dialogSignaler.findViewById(R.id.btSendSignal);
        btSendSignal.setOnClickListener(v -> {
            String value = edtSignalRaison.getText().toString().trim();
            if (isStringEmpty(value)){
                Toast.makeText(TaskActivity.this, getString(R.string.reason_error), Toast.LENGTH_SHORT).show();
                return;
            }
            Signale signale = new Signale(""+ userVisitor.getUid(), ""+value, "", ""+ currentTask.getTid(),
                    ""+ currentTask.getTtitre(), ""+ currentTask.getTcover(), ""+ currentTask.getTdescription(),
                    ""+ userVisitor.getUid(), ""+ userVisitor.getUnoms(), ""+ userVisitor.getUavatar());
            ecrireUnSignalementDeTache(signale, ibTaskSignale);
            dialogSignaler.dismiss();
        });
        dialogSignaler.show();
    }

    private void verifDataBeforeSendComment() {
        String comment = etvTaskComment.getText().toString().trim();
        if (isStringEmpty(comment)){
            Toast.makeText(this, getString(R.string.comment_error), Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressDialog pd = buildProgressDialog(this, getString(R.string.app_name), getString(R.string.traitement_encours));
        pd.show();
        Commentaire commentaire = new Commentaire(comment, idCurrentTask, userVisitor.getUid(), userVisitor.getUnoms(), userVisitor.getUavatar());
        ecrireUnNouveauCommentaire(pd, commentaire, currentTask.getTncomment(), userVisitor.getUncomments());
        etvTaskComment.setText(null);
    }

    // TODO: onActivityResult methods
    private void updateTaskCover(int requestCode, Intent data) {
        if (requestCode == STORAGE_REQUEST_CODE && data != null){
            imageUri = data.getData();
        } else if (requestCode == CAMERA_REQUEST_CODE && data != null){
            imageUri = ivtools.getImageUri();
        }
        ProgressDialog pd = buildProgressDialog(this, getString(R.string.app_name), getString(R.string.task_image_path));
        pd.show();
        fbtools.deleteOldImage(pd, imageUri, ""+ currentTask.getTcover(), ""+buildPathWithSlash(TACHES, idCurrentTask, TCOVER));
    }

    // TODO: Afficher les informations de l'utilisateur
    private void showCurrentUserInformation(DataSnapshot snapshot) {
        for (DataSnapshot ds : snapshot.getChildren()){
            userVisitor = ds.getValue(User.class);
        }
    }

    // TODO: Afficher les informations de la tache
    private void showTaskInformation(DataSnapshot snapshot) {
        for (DataSnapshot ds : snapshot.getChildren()){
            currentTask = ds.getValue(Tache.class);
            if (currentTask != null){
                this.showUserName(currentTask.getUid(), currentTask.getUnoms());
                this.showPublicationDate(currentTask.getTdate());
                this.showTitle(currentTask.getTtitre());
                this.showDescription(currentTask.getTdescription());
                this.showAddress(currentTask.getLocalize());
                this.showATaskNcomment(currentTask.getTncomment());
                this.showATaskNjaime(currentTask.getTnlike());
                loadingImageWithPath(ivTaskCover, R.drawable.wild, currentTask.getTcover());
                loadingImageWithPath(ivtaskAvatarUser, R.drawable.russia, currentTask.getUavatar());

            }
        }
    }

    // TODO: showTaskInformation methods
    private void showUserName(String uid, String unoms) {
        tvTaskUsername.setText(uidVisitor.equals(uid) ? getString(R.string.you) : unoms);
    }

    private void showPublicationDate(String tdate) {
        tvTaskPublicationDate.setText(formatLaDate(tdate));
    }

    private void showTitle(String titre) {
        tvTaskTitle.setText(titre);
    }

    private void showDescription(String description) {
        tvTaskDescription.setText(description);
    }

    private void showAddress(Localize localize) {
        tvTaskAdresse.setVisibility(this.isLocalize(localize) ? VISIBLE : GONE);
        tvTaskAdresse.setText(this.isLocalize(localize) ? ""+localize.getCity()+", "+localize.getCountry() : "");
    }

    private boolean isLocalize(Localize localize) {
        return localize != null && !isStringEmpty(localize.getAddress());
    }

    private void showATaskNcomment(String tncomment) {
        tvTaskNcomment.setText(checkBeforeFormatData(getString(R.string.comments), tncomment));
    }

    private void showATaskNjaime(String tnlike) {
        tvTaskNjaime.setText(checkBeforeFormatData(getString(R.string.like), currentTask.getTnlike()));
    }

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

    // TODO: chooseOptionEditListener methods
    private void checkUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            requestAccessFineLocationPermissions(this);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null){
                currentTask.setLocalize(displayTaskLocation(TaskActivity.this, location));
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
                    fbtools.ecrireDansUnChamp(buildPathWithSlash(TACHES, idCurrentTask, TTITRE), value);
                    break;
                case 2: // description
                    fbtools.ecrireDansUnChamp(buildPathWithSlash(TACHES, idCurrentTask, TDESCRIPTION), value);
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
            fbtools.ecrireunenouvelletache(currentTask);
            dialogInterface.dismiss();
        });
        builder.create().show();
    }

}