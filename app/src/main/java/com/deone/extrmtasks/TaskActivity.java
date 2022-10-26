package com.deone.extrmtasks;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Constants.TID;
import static com.deone.extrmtasks.tools.Ivtools.loadingImageWithPath;
import static com.deone.extrmtasks.tools.Other.buildAlertDialog;
import static com.deone.extrmtasks.tools.Other.buildAlertDialogForSingleSelectOption;
import static com.deone.extrmtasks.tools.Other.buildProgressDialog;
import static com.deone.extrmtasks.tools.Other.checkBeforeFormatData;
import static com.deone.extrmtasks.tools.Other.formatLaDate;
import static com.deone.extrmtasks.tools.Other.gotoaccount;
import static com.deone.extrmtasks.tools.Other.gotohome;
import static com.deone.extrmtasks.tools.Other.initLLanguage;
import static com.deone.extrmtasks.tools.Other.initThemeMode;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;
import static com.deone.extrmtasks.tools.Other.rvLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.deone.extrmtasks.adapters.Cadapter;
import com.deone.extrmtasks.modeles.Comment;
import com.deone.extrmtasks.modeles.Taches;
import com.deone.extrmtasks.modeles.User;
import com.deone.extrmtasks.tools.Fbtools;
import com.deone.extrmtasks.tools.Sptools;
import com.deone.extrmtasks.tools.Xlistener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener {

    private Fbtools fbtools;
    private Sptools sptools;
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
        } else if (item.getItemId() == R.id.itEditer){
            showEditDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initApp() {
        sptools = Sptools.getInstance(this);
        fbtools = Fbtools.getInstance(this);
        tid = getIntent().getStringExtra(TID);
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
        fbtools.specificTask(vCurrentUser, currentUid, vTask, vComment, tid);
        findViewById(R.id.ibJaime).setOnClickListener(this);
        findViewById(R.id.ibFavorite).setOnClickListener(this);
        findViewById(R.id.ibShare).setOnClickListener(this);
        findViewById(R.id.ibSendComment).setOnClickListener(this);
        ivAvatarUser.setOnClickListener(this);
        tvAdresse.setOnClickListener(this);
    }

    private void showEditDialog() {
        buildAlertDialogForSingleSelectOption(
                this,
                getString(R.string.app_name_lite),
                optionListener,
                getResources().getStringArray(R.array.edit_task),
                0
        );
    }

    private void likeProcess() {
    }

    private void favorisProcess() {
    }

    private void shareProcess() {
    }

    private void showAdresseDialog() {
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

    private final DialogInterface.OnClickListener optionListener = (dialogInterface, i) -> {
        switch (i) {
            case 0: // cover
                break;
            case 1: // titre
                break;
            case 2: // description
                break;
            case 3: // localisation
                break;
        }
    };

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

}