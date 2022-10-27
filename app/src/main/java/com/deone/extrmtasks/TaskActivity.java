package com.deone.extrmtasks;

import static android.view.View.GONE;
import static android.view.View.OVER_SCROLL_ALWAYS;
import static android.view.View.VISIBLE;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Constants.LOCATION_REQUEST_CODE_ACCESS_COARSE_LOCATION;
import static com.deone.extrmtasks.tools.Constants.LOCATION_REQUEST_CODE_ACCESS_FINE_LOCATION;
import static com.deone.extrmtasks.tools.Constants.TACHES;
import static com.deone.extrmtasks.tools.Constants.TADRESSE;
import static com.deone.extrmtasks.tools.Constants.TCODEPOSTAL;
import static com.deone.extrmtasks.tools.Constants.TDESCRIPTION;
import static com.deone.extrmtasks.tools.Constants.TID;
import static com.deone.extrmtasks.tools.Constants.TLATITUDE;
import static com.deone.extrmtasks.tools.Constants.TLONGITUDE;
import static com.deone.extrmtasks.tools.Constants.TPAYS;
import static com.deone.extrmtasks.tools.Constants.TSTATE;
import static com.deone.extrmtasks.tools.Constants.TTITRE;
import static com.deone.extrmtasks.tools.Constants.TVILLE;
import static com.deone.extrmtasks.tools.Constants.UID;
import static com.deone.extrmtasks.tools.Ivtools.loadingImageWithPath;
import static com.deone.extrmtasks.tools.Other.buildAlertDialog;
import static com.deone.extrmtasks.tools.Other.buildAlertDialogForSelectOption;
import static com.deone.extrmtasks.tools.Other.buildPathWithSlash;
import static com.deone.extrmtasks.tools.Other.buildProgressDialog;
import static com.deone.extrmtasks.tools.Other.checkBeforeFormatData;
import static com.deone.extrmtasks.tools.Other.formatLaDate;
import static com.deone.extrmtasks.tools.Other.gotoaccount;
import static com.deone.extrmtasks.tools.Other.gotohome;
import static com.deone.extrmtasks.tools.Other.initLLanguage;
import static com.deone.extrmtasks.tools.Other.initThemeMode;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;
import static com.deone.extrmtasks.tools.Other.rvLayoutManager;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.deone.extrmtasks.adapters.Cadapter;
import com.deone.extrmtasks.modeles.Comment;
import com.deone.extrmtasks.modeles.Taches;
import com.deone.extrmtasks.modeles.User;
import com.deone.extrmtasks.tools.Fbtools;
import com.deone.extrmtasks.tools.Ivtools;
import com.deone.extrmtasks.tools.Lctools;
import com.deone.extrmtasks.tools.Sptools;
import com.deone.extrmtasks.tools.Xlistener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener {

    private Fbtools fbtools;
    private Sptools sptools;
    private Lctools lctools;
    private Ivtools ivtools;
    private Uri imageUri;
    private ImageView ivAvatarUser;
    private ImageView ivTachesLogo;
    private TextView tvUsername;
    private TextView tvPublicationDate;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvAdresse;
    private TextView tvNcomment;
    private TextView tvNjaime;
    private RecyclerView rvComments;
    private EditText etvComment;
    private List<Comment> commentList;
    private String tid;
    private String uid;
    private String currentUid;
    private User currentUser;
    private Taches taches;

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
        editItem.setVisible(currentUid.equals(uid));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itDelete && currentUid.equals(taches.getUid())){
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

    private void initApp() {
        sptools = Sptools.getInstance(this);
        fbtools = Fbtools.getInstance(this);
        lctools = Lctools.getInstance(this);
        lctools.initLocation();
        tid = getIntent().getStringExtra(TID);
        uid = getIntent().getStringExtra(UID);
        currentUid = fbtools.getId();
        initThemeMode(sptools.readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        initLLanguage(this, sptools.readStringData(APP_PREFS_LANGUE, EN));
    }

    private void checkUser() {
        if(isStringEmpty(currentUid)||isStringEmpty(tid)){
            gotohome(this);
        }else {
            initViews();
        }
    }

    private void initViews() {
        setContentView(R.layout.activity_task);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ivAvatarUser = findViewById(R.id.ivAvatarUser);
        tvUsername = findViewById(R.id.tvUsername);
        tvPublicationDate = findViewById(R.id.tvPublicationDate);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvAdresse = findViewById(R.id.tvAdresse);
        ivTachesLogo = findViewById(R.id.ivTachesLogo);
        tvNcomment = findViewById(R.id.tvNcomment);
        tvNjaime = findViewById(R.id.tvNjaime);
        rvComments = findViewById(R.id.rvComments);
        rvComments.setLayoutManager(rvLayoutManager(this, 0));
        commentList = new ArrayList<>();
        etvComment = findViewById(R.id.etvComment);
        ivtools.setSomeActivityResultLauncher(someActivityResultLauncher);
        fbtools.specificTask(vCurrentUser, currentUid, vTask, vComment, tid);
        findViewById(R.id.ibJaime).setOnClickListener(this);
        findViewById(R.id.ibFavorite).setOnClickListener(this);
        findViewById(R.id.ibShare).setOnClickListener(this);
        findViewById(R.id.ibSendComment).setOnClickListener(this);
        ivAvatarUser.setOnClickListener(this);
        tvAdresse.setOnClickListener(this);
    }

    private void showEditDialog() {
        buildAlertDialogForSelectOption(
                this,
                getString(R.string.app_name_lite),
                optionListener,
                getResources().getStringArray(R.array.edit_task)
        ).create().show();
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
        alertDialog.setMessage(
                "city = "+taches.getTville() + ",\n state = "+taches.getTstate()+
                ",\n country = "+taches.getTpays()+ ",\n codepostal = "+taches.getTcodepostal()+
                        ",\n adresse = "+taches.getTadresse()
        );
        alertDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        alertDialog.setCancelable(false);
        AlertDialog alert=alertDialog.create();
        alert.show();
    }

    private void verifDataBeforeSendComment() {
        String comment = etvComment.getText().toString().trim();
        if (isStringEmpty(comment)){
            Toast.makeText(this, getString(R.string.comment_error), Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressDialog pd = buildProgressDialog(this, getString(R.string.app_name), getString(R.string.traitement_encours));
        pd.show();
        fbtools.addCommentInSpecificTask(
                pd,
                comment,
                tid,
                currentUser.getUid(),
                currentUser.getUnoms(),
                currentUser.getUavatar(),
                taches.getTncomment(),
                currentUser.getUncomments());
        etvComment.setText(null);
    }

    private void showCurrentUserInformation(DataSnapshot snapshot) {
        for (DataSnapshot ds : snapshot.getChildren()){
            currentUser = ds.getValue(User.class);
        }
    }

    private void showTaskInformation(DataSnapshot snapshot) {
        for (DataSnapshot ds : snapshot.getChildren()){
            taches = ds.getValue(Taches.class);
            assert taches != null;
            tvUsername.setText(taches.getUnoms());
            tvPublicationDate.setText(formatLaDate(taches.getTdate()));
            tvTitle.setText(taches.getTtitre());
            tvDescription.setText(taches.getTdescription());
            tvAdresse.setVisibility(isStringEmpty(taches.getTadresse())?GONE:VISIBLE);
            tvAdresse.setText(isStringEmpty(taches.getTadresse())?"":""+taches.getTville()+", "+taches.getTpays());
            tvNcomment.setText(checkBeforeFormatData(getString(R.string.comments), taches.getTncomment()));
            tvNjaime.setText(checkBeforeFormatData(getString(R.string.like), taches.getTnlike()));
            loadingImageWithPath(ivTachesLogo, R.drawable.wild, taches.getTcover());
            loadingImageWithPath(ivAvatarUser, R.drawable.russia, taches.getUavatar());
        }
    }

    private void showTaskComments(DataSnapshot snapshot) {
        commentList.clear();
        for (DataSnapshot ds : snapshot.getChildren()){
            Comment comment = ds.getValue(Comment.class);
            commentList.add(comment);
            Cadapter cadapter = new Cadapter(TaskActivity.this, commentList);
            rvComments.setAdapter(cadapter);
            cadapter.setListener(xListener);
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

    private final ValueEventListener vComment = new ValueEventListener() {
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

    private final DialogInterface.OnClickListener adListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            ProgressDialog pd = buildProgressDialog(TaskActivity.this, getString(R.string.app_name), getString(R.string.traitement_encours));
            pd.show();
            fbtools.deleteTask(pd,tid, currentUid, currentUser.getUntask(), currentUser.getUncomments());
        }
    };

    private final DialogInterface.OnClickListener coverListener = new DialogInterface.OnClickListener() {
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
            case 0: // cover
                buildAlertDialogForSelectOption(
                        this, getString(R.string.app_name_lite),
                        coverListener, getResources().getStringArray(R.array.select_place)).create().show();
                break;
            case 1: // titre
                updateString(getString(R.string.titre), i);
                break;
            case 2: // description
                updateString(getString(R.string.description), i);
                break;
            case 3: // localisation
                showLocalisationDialog();
                break;
        }
    };

    private void showLocalisationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name_lite));
        builder.setMessage(getString(R.string.loc_message));
        builder.setPositiveButton(getText(R.string.ok), (dialogInterface, i) -> {
            lctools.testGeocoder();
            fbtools.writeStringInDb(buildPathWithSlash(TACHES, tid, TPAYS), lctools.getCountry());
            fbtools.writeStringInDb(buildPathWithSlash(TACHES, tid, TVILLE), lctools.getCity());
            fbtools.writeStringInDb(buildPathWithSlash(TACHES, tid, TADRESSE), lctools.getAdresse());
            fbtools.writeStringInDb(buildPathWithSlash(TACHES, tid, TCODEPOSTAL), lctools.getCodepostal());
            fbtools.writeStringInDb(buildPathWithSlash(TACHES, tid, TSTATE), lctools.getState());
            fbtools.writeStringInDb(buildPathWithSlash(TACHES, tid, TLONGITUDE), ""+lctools.getLongitude());
            fbtools.writeStringInDb(buildPathWithSlash(TACHES, tid, TLATITUDE), ""+lctools.getLatitude());
            dialogInterface.dismiss();
        });
        builder.create().show();
    }

    private void updateString(String message, int i) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialod_edit);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(MessageFormat.format(getString(R.string.title_update), message));
        EditText edtValue = (EditText) dialog.findViewById(R.id.edtValue);
        switch (i) {
            case 0: // cover
                break;
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
            case 3: // localisation
                break;
        }

        Button dialogButton = (Button) dialog.findViewById(R.id.btUpdate);
        dialogButton.setOnClickListener(v -> {
            String value = edtValue.getText().toString().trim();
            if (isStringEmpty(value)){
                Toast.makeText(TaskActivity.this, getString(R.string.value_error), Toast.LENGTH_SHORT).show();
                return;
            }
            switch (i) {
                case 0: // cover
                    break;
                case 1: // titre
                    fbtools.writeStringInDb(buildPathWithSlash(TACHES, tid, TTITRE), value);
                    break;
                case 2: // description
                    fbtools.writeStringInDb(buildPathWithSlash(TACHES, tid, TDESCRIPTION), value);
                    break;
                case 3: // localisation
                    break;
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ibJaime)
            likeProcess();
        else if (id == R.id.ibFavorite)
            favorisProcess();
        else if (id == R.id.ibShare)
            shareProcess();
        else if (id == R.id.ibSendComment)
            verifDataBeforeSendComment();
        else if (id == R.id.ivAvatarUser || id == R.id.tvUsername)
            gotoaccount(this, currentUid);
        else if (id == R.id.tvAdresse)
            showAdresseDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE_ACCESS_COARSE_LOCATION || requestCode == LOCATION_REQUEST_CODE_ACCESS_FINE_LOCATION)
            lctools.requestLoccationPermissionsResults(requestCode, grantResults);
        else
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
                        updateTaskCoverImage();
                    }
                }
            });

    private void updateTaskCoverImage() {
        ProgressDialog pd = buildProgressDialog(this, getString(R.string.app_name), getString(R.string.task_image_path));
        pd.show();
        fbtools.deleteOldImage(pd, imageUri, ""+taches.getTcover(), "");
    }
}