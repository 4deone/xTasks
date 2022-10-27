package com.deone.extrmtasks.tools;

import static com.deone.extrmtasks.tools.Constants.AVATAR;
import static com.deone.extrmtasks.tools.Constants.COMMENTS;
import static com.deone.extrmtasks.tools.Constants.COVER;
import static com.deone.extrmtasks.tools.Constants.DATABASE;
import static com.deone.extrmtasks.tools.Constants.TACHES;
import static com.deone.extrmtasks.tools.Constants.TID;
import static com.deone.extrmtasks.tools.Constants.TNCOMMENTS;
import static com.deone.extrmtasks.tools.Constants.UAVATAR;
import static com.deone.extrmtasks.tools.Constants.UCOVER;
import static com.deone.extrmtasks.tools.Constants.UID;
import static com.deone.extrmtasks.tools.Constants.UNCOMMENTS;
import static com.deone.extrmtasks.tools.Constants.UNTASK;
import static com.deone.extrmtasks.tools.Constants.USERS;
import static com.deone.extrmtasks.tools.Other.buildPathWithSlash;
import static com.deone.extrmtasks.tools.Other.buildProgressDialog;
import static com.deone.extrmtasks.tools.Other.decrementValue;
import static com.deone.extrmtasks.tools.Other.genHashMapComment;
import static com.deone.extrmtasks.tools.Other.genHashMapTask;
import static com.deone.extrmtasks.tools.Other.genHashMapUser;
import static com.deone.extrmtasks.tools.Other.getXtimestamp;
import static com.deone.extrmtasks.tools.Other.gotohome;
import static com.deone.extrmtasks.tools.Other.gotomain;
import static com.deone.extrmtasks.tools.Other.gotonew;
import static com.deone.extrmtasks.tools.Other.incrementValue;
import static com.deone.extrmtasks.tools.Other.isNewAccountMain;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;
import static com.deone.extrmtasks.tools.Other.showDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.deone.extrmtasks.HomeActivity;
import com.deone.extrmtasks.R;
import com.deone.extrmtasks.TaskActivity;
import com.deone.extrmtasks.modeles.Taches;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class Fbtools {
    private static DatabaseReference ref;
    private static FirebaseAuth auth;
    private static FirebaseUser fUser;
    private static Context appContext;
    private static Fbtools instance;
    private String id;
    private String email;

    /**
     *
     * @param applicationContext
     * @return
     */
    public static synchronized Fbtools getInstance(Context applicationContext) {
        if (instance == null)
            instance = new Fbtools(applicationContext);
        return instance;
    }

    /**
     *
     * @param applicationContext
     */
    private Fbtools (Context applicationContext) {
        appContext = applicationContext;
        auth = FirebaseAuth.getInstance();
        fUser = auth.getCurrentUser();
        if (fUser != null){
            this.id = fUser.getUid();
            this.email = fUser.getEmail();
        }
        ref = FirebaseDatabase.getInstance().getReference(DATABASE);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @param fUser
     */
    public static void setfUser(FirebaseUser fUser) {
        Fbtools.fUser = fUser;
    }

    /**
     *
     * @param pd
     * @param email
     * @param motdepasse
     */
    public void checkEmailStatus(ProgressDialog pd, String email, String motdepasse){
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (isNewAccountMain(task)){
                pd.dismiss();
                showDialog(
                        appContext,
                        appContext.getString(R.string.app_name),
                        appContext.getString(R.string.account_not_exist))
                        .setPositiveButton(appContext.getString(R.string.ok), (dialogInterface, i) -> gotonew(appContext))
                        .create()
                        .show();
            } else {
                pd.setMessage(appContext.getString(R.string.user_auth));
                signin(pd, email, motdepasse);
            }
        });
    }

    /**
     *
     * @param imageUri
     * @param email
     * @param motdepasse
     * @param fullname
     * @param telephone
     */
    public void checkEmailStatus(ProgressDialog pd, Uri imageUri, String email, String motdepasse, String fullname, String telephone, String ville, String pays){
        pd.setMessage(appContext.getString(R.string.verif_if_account_exist));
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (isNewAccountMain(task)){
                pd.setMessage(appContext.getString(R.string.mep_creating_account));
                createUserAccount(pd, imageUri, email, motdepasse, fullname, telephone, ville, pays);
            }else {
                pd.dismiss();
                showDialog(
                        appContext,
                        appContext.getString(R.string.app_name),
                        appContext.getString(R.string.account_exist))
                        .setPositiveButton(appContext.getString(R.string.ok), (dialogInterface, i) -> gotohome(appContext))
                        .create()
                        .show();
            }
        });
    }

    /**
     *
     * @param pd
     * @param imageUri
     * @param email
     * @param motdepasse
     * @param fullname
     * @param telephone
     */
    public void createUserAccount(ProgressDialog pd, Uri imageUri, String email, String motdepasse,
                                  String fullname, String telephone, String ville, String pays) {
        auth.createUserWithEmailAndPassword(email, motdepasse)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        checkUser(pd, imageUri, email, fullname, telephone, ville, pays);
                    } else {
                        pd.dismiss();
                        Toast.makeText(appContext, appContext.getString(R.string.create_account_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     *
     * @param pd
     * @param imageUri
     * @param email
     * @param fullname
     * @param telephone
     */
    public void checkUser(ProgressDialog pd, Uri imageUri, String email, String fullname, String telephone, String ville, String pays) {
        setfUser(auth.getCurrentUser());
        if (fUser != null) {
            createUserProfile(pd, imageUri, fUser.getUid(), email, fullname, telephone, ville, pays);
        } else {
            Toast.makeText(appContext, appContext.getString(R.string.no_user), Toast.LENGTH_SHORT).show();
            gotohome(appContext);
        }
    }

    /**
     *
     * @param pd
     * @param imageUri
     * @param uid
     * @param email
     * @param fullname
     * @param telephone
     */
    public void createUserProfile(ProgressDialog pd, Uri imageUri, String uid, String email, String fullname, String telephone, String ville, String pays) {
        if (imageUri != null)
            ajouterUserProfileImage(pd, imageUri, uid, fullname, telephone, email, ville, pays);
        else
            ajouterUnUtilisateur(pd, uid, fullname, "", telephone, email, ville, pays);
    }

    /**
     *
     * @param pd
     * @param imageUri
     * @param uid
     * @param fullname
     * @param telephone
     * @param email
     */
    private void ajouterUserProfileImage(ProgressDialog pd, Uri imageUri, String uid, String fullname, String telephone, String email, String ville, String pays) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(buildPathWithSlash(USERS, AVATAR, uid));
        storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());

            String downloadUri = uriTask.getResult().toString();
            if (uriTask.isSuccessful()){
                pd.dismiss();
                ajouterUnUtilisateur(pd, uid, fullname, downloadUri, telephone, email, ville, pays);
            }else
                pd.dismiss();
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(appContext, appContext.getString(R.string.save_image_error), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     *
     * @param pd
     * @param uid
     * @param fullname
     * @param avatar
     * @param telephone
     * @param email
     */
    private void ajouterUnUtilisateur(ProgressDialog pd, String uid, String fullname, String avatar, String telephone, String email, String ville, String pays) {
        ref.child(buildPathWithSlash(USERS, uid)).setValue(genHashMapUser(uid, fullname, avatar, telephone, email, getXtimestamp(), ville, pays))
                .addOnSuccessListener(unused -> {
                    pd.dismiss();
                    Toast.makeText(appContext, appContext.getString(R.string.save_user_info_ok), Toast.LENGTH_SHORT).show();
                    gotohome(appContext);
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(appContext, appContext.getString(R.string.save_user_info_error), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     *
     * @param pd
     * @param email
     * @param motdepasse
     */
    public void signin(ProgressDialog pd, String email, String motdepasse) {
        auth.signInWithEmailAndPassword(email, motdepasse).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                pd.dismiss();
                this.id = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                this.email = Objects.requireNonNull(auth.getCurrentUser()).getEmail();
                gotohome(appContext);
            }else{
                pd.dismiss();
                Toast.makeText(appContext, appContext.getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(appContext, appContext.getString(R.string.connexion_error), Toast.LENGTH_SHORT).show());
    }

    /**
     *
     * @param valueEventListener
     */
    public void toutesMesTaches(final ValueEventListener valueEventListener) {
        Query query = ref.child(TACHES).orderByChild(UID).equalTo(this.id);
        query.addValueEventListener(valueEventListener);
    }

    public void toutesLesTaches(final ValueEventListener valueEventListener) {
        ref.child(TACHES).addValueEventListener(valueEventListener);
    }

    /**
     *
     * @param valueEventListener
     */
    public void specificUser(final ValueEventListener valueEventListener) {
        Query query = ref.child(USERS).orderByKey().equalTo(this.id);
        query.addValueEventListener(valueEventListener);
    }

    /**
     *
     * @param vCurrentUser
     * @param currentUid
     * @param vTask
     * @param vComment
     * @param tid
     */
    public void specificTask(
            final ValueEventListener vCurrentUser, String currentUid,
            final ValueEventListener vTask,
            final ValueEventListener vComment, String tid) {
        Query qCurrentUser = ref.child(USERS).orderByKey().equalTo(currentUid);
        qCurrentUser.addValueEventListener(vCurrentUser);
        Query qTask = ref.child(TACHES).orderByKey().equalTo(tid);
        qTask.addValueEventListener(vTask);
        Query qComments = ref.child(COMMENTS).orderByChild(TID).equalTo(tid);
        qComments.addValueEventListener(vComment);
    }

    /**
     *
     * @param pd
     * @param taches
     * @param ntask
     */
    public void addTaskInSpecificUserAccount(ProgressDialog pd, Taches taches, String ntask) {
        pd.setMessage(appContext.getString(R.string.save_task_data));
        ref.child(buildPathWithSlash(TACHES, taches.getTdate())).setValue(taches)
                .addOnSuccessListener(unused -> {
                    ref.child(buildPathWithSlash(USERS, taches.getUid(), UNTASK)).setValue(incrementValue(ntask))
                            .addOnCompleteListener(task -> {
                                pd.dismiss();
                                Toast.makeText(appContext, appContext.getString(R.string.add_task_ok), Toast.LENGTH_SHORT).show();
                                gotohome(appContext);
                            })
                            .addOnFailureListener(e -> pd.dismiss());
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(appContext, appContext.getString(R.string.add_task_error), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     *
     * @param pd
     * @param imageUri
     * @param taches
     * @param ntask
     */
    public void addPictureInSpecificTask(ProgressDialog pd, Uri imageUri, Taches taches, String ntask) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(buildPathWithSlash(
                TACHES,
                taches.getUid(),
                taches.getTdate()
        ));
        storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());

            String downloadUri = uriTask.getResult().toString();
            if (uriTask.isSuccessful()){
                pd.dismiss();
                taches.setTcover(downloadUri);
                addTaskInSpecificUserAccount(pd, taches, ntask);
            }else
                pd.dismiss();
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(appContext, appContext.getString(R.string.save_task_image_error), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     *
     * @param pd
     * @param comment
     * @param tid
     */
    public void addCommentInSpecificTask(ProgressDialog pd, String comment, String tid, String uid, String unoms, String uavatar, String ntComment, String nuComment) {
        String timestamp = getXtimestamp();
        ref.child(buildPathWithSlash(COMMENTS, timestamp)).setValue(genHashMapComment(comment, timestamp, tid, uid, unoms, uavatar))
                .addOnSuccessListener(unused -> {
                    ref.child(buildPathWithSlash(TACHES, tid, TNCOMMENTS)).setValue(incrementValue(ntComment))
                            .addOnCompleteListener(task -> {
                                ref.child(buildPathWithSlash(USERS, uid, UNCOMMENTS)).setValue(incrementValue(nuComment))
                                        .addOnCompleteListener(task1 -> pd.dismiss())
                                        .addOnFailureListener(e -> pd.dismiss());
                            })
                            .addOnFailureListener(e -> pd.dismiss());
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                });
    }

    /**
     *
     * @param pd
     * @param tid
     * @param uid
     * @param ntask
     * @param nucomment
     */
    public void deleteTask(ProgressDialog pd,String tid, String uid, String ntask, String nucomment) {
        ref.child(buildPathWithSlash(TACHES, tid)).removeValue()
                .addOnSuccessListener(unused -> {
                    ref.child(buildPathWithSlash(USERS, uid, UNTASK)).setValue(decrementValue(ntask))
                            .addOnCompleteListener(task -> {
                                //Supprimer tous les commentaires liés à la tache  ??????
                                //deleteTaskComments(pd, tid, uid, nucomment);
                                pd.dismiss();
                                Toast.makeText(appContext, appContext.getString(R.string.delete_task_ok), Toast.LENGTH_SHORT).show();
                                ((Activity) appContext).finish();
                            })
                            .addOnFailureListener(e -> pd.dismiss());
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(appContext, appContext.getString(R.string.delete_task_error), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     *
     * @param pd
     * @param tid
     * @param uid
     * @param nucomment
     */
    public void deleteTaskComments(ProgressDialog pd,String tid, String uid, String nucomment) {
        Query qComment = ref.child(COMMENTS).orderByChild(TID).equalTo(tid);
        qComment.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    ds.getRef().removeValue().addOnCompleteListener(task ->
                            ref.child(buildPathWithSlash(USERS, uid, UNCOMMENTS)).setValue(decrementValue(nucomment)));
                }
                /*{
                    pd.dismiss();
                    Toast.makeText(appContext, appContext.getString(R.string.delete_task_ok), Toast.LENGTH_SHORT).show();
                    ((Activity) appContext).finish();
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(appContext, appContext.getString(R.string.delete_task_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     * @param pd
     * @param imageUri
     * @param iscover
     * @param oldPath
     */
    public void deleteOldImage(ProgressDialog pd, Uri imageUri, boolean iscover, String oldPath) {
        if (isStringEmpty(oldPath)){
            saveImage(pd, imageUri, iscover, this.id);
        }else {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(oldPath);
            storageReference.delete().addOnSuccessListener(unused -> saveImage(pd, imageUri, iscover, this.id)).addOnFailureListener(e -> {
                pd.dismiss();
                Toast.makeText(appContext, appContext.getString(R.string.delete_image_error), Toast.LENGTH_SHORT).show();
            });
        }
    }

    /**
     *
     * @param pd
     * @param imageUri
     * @param iscover
     * @param uid
     */
    public void saveImage(ProgressDialog pd, Uri imageUri, boolean iscover, String uid) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(buildPathWithSlash(USERS, iscover?COVER:AVATAR, this.id));
        storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());

            String downloadUri = uriTask.getResult().toString();
            if (uriTask.isSuccessful()){
                updateUserAvatarOrCover(pd, uid, downloadUri, iscover?UCOVER:UAVATAR);
            }else
                pd.dismiss();
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(appContext, appContext.getString(R.string.save_image_error), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     *
     * @param pd
     * @param uid
     * @param downloadUri
     * @param field
     */
    public void updateUserAvatarOrCover(ProgressDialog pd, String uid, String downloadUri, String field) {
        ref.child(buildPathWithSlash(USERS, uid, field)).setValue(downloadUri)
                .addOnCompleteListener(task -> pd.dismiss())
                .addOnFailureListener(e -> pd.dismiss());
    }

    /**
     *
     * @param pd
     * @param field
     * @param value
     */
    public void updateStringWithFieldAndValue(ProgressDialog pd, String field, String value) {
        ref.child(buildPathWithSlash(USERS, this.id, field)).setValue(value)
                .addOnCompleteListener(task -> pd.dismiss())
                .addOnFailureListener(e -> pd.dismiss());
    }

    public void writeStringInDb(String path, String value) {
        ref.child(path).setValue(value)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Toast.makeText(appContext, appContext.getString(R.string.updated_ok), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(appContext, appContext.getString(R.string.not_auth_to_do), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(appContext, appContext.getString(R.string.updated_not_ok), Toast.LENGTH_SHORT).show());
    }

    public void deleteUserAccount(ProgressDialog pd, String email, String motdepasse) {
        final FirebaseUser user = auth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(""+email, ""+motdepasse);
        assert user != null;
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> user.delete().addOnCompleteListener(task1 -> {
                    pd.dismiss();
                    if (task1.isSuccessful()) {
                        Toast.makeText(appContext, ""+appContext.getString(R.string.account_deleted_ok), Toast.LENGTH_SHORT).show();
                        gotomain(appContext);
                    }
                }));
    }

}
