package com.deone.extrmtasks.tools;

import static com.deone.extrmtasks.tools.Constants.AVATAR;
import static com.deone.extrmtasks.tools.Constants.COMMENTS;
import static com.deone.extrmtasks.tools.Constants.COVER;
import static com.deone.extrmtasks.tools.Constants.DATABASE;
import static com.deone.extrmtasks.tools.Constants.KEYS;
import static com.deone.extrmtasks.tools.Constants.SIGNALES;
import static com.deone.extrmtasks.tools.Constants.TACHES;
import static com.deone.extrmtasks.tools.Constants.TID;
import static com.deone.extrmtasks.tools.Constants.TNCOMMENTS;
import static com.deone.extrmtasks.tools.Constants.TPAYS;
import static com.deone.extrmtasks.tools.Constants.UAVATAR;
import static com.deone.extrmtasks.tools.Constants.UCOVER;
import static com.deone.extrmtasks.tools.Constants.UID;
import static com.deone.extrmtasks.tools.Constants.UNCOMMENTS;
import static com.deone.extrmtasks.tools.Constants.UNKEYS;
import static com.deone.extrmtasks.tools.Constants.UNTASK;
import static com.deone.extrmtasks.tools.Constants.USERS;
import static com.deone.extrmtasks.tools.Other.buildPathWithSlash;
import static com.deone.extrmtasks.tools.Other.decrementValue;
import static com.deone.extrmtasks.tools.Other.genHashMapComment;
import static com.deone.extrmtasks.tools.Other.getXtimestamp;
import static com.deone.extrmtasks.tools.Other.gotohome;
import static com.deone.extrmtasks.tools.Other.gotomain;
import static com.deone.extrmtasks.tools.Other.incrementValue;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.deone.extrmtasks.R;
import com.deone.extrmtasks.modeles.Commentaire;
import com.deone.extrmtasks.modeles.Key;
import com.deone.extrmtasks.modeles.Signale;
import com.deone.extrmtasks.modeles.Tache;
import com.deone.extrmtasks.modeles.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
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
     * @param applicationContext applicationContext Context de l'application qui appelle cette instance
     * @return Renvoie une instance unique de l'objet Signtools
     */
    public static synchronized Fbtools getInstance(Context applicationContext) {
        if (instance == null)
            instance = new Fbtools(applicationContext);
        return instance;
    }

    /**
     *
     * @param applicationContext applicationContext applicationContext Context de l'application qui appelle cette instance
     */
    private Fbtools (Context applicationContext) {
        appContext = applicationContext;
        auth = FirebaseAuth.getInstance();
        fUser = auth.getCurrentUser();
        if (fUser != null){
            id = fUser.getUid();
            email = fUser.getEmail();
        }
        ref = FirebaseDatabase.getInstance().getReference(DATABASE);
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    //Toutes les requetes permettant à l'utilisateur ou meme le système de lire les éléments contenu dans la base de données

    /**
     *
     * @param valueEventListener L'écouteur permettant de récupérer les élémnents dans la base de données
     */
    public void liretoutesmestaches(final ValueEventListener valueEventListener) {
        Query query = ref.child(TACHES).orderByChild(UID).equalTo(this.id);
        query.addValueEventListener(valueEventListener);
    }

    public static void liretoutesmestaches(final ValueEventListener valueEventListener, String uid) {
        Query query = ref.child(TACHES).orderByChild(UID).equalTo(uid);
        query.addValueEventListener(valueEventListener);
    }

    /**
     *
     * @param valueEventListener L'écouteur permettant de récupérer les élémnents dans la base de données
     */
    public static void liretouteslestaches(final ValueEventListener valueEventListener) {
        ref.child(TACHES).addValueEventListener(valueEventListener);
    }

    /**
     *
     * @param vCurrentUser
     * @param currentUid
     * @param vTask
     * @param vComment
     * @param tid
     */
    public static void lireunetachespecifique(
            final ValueEventListener vCurrentUser, String currentUid,
            final ValueEventListener vTask,
            final ValueEventListener vComment, String tid,
            final ValueEventListener vSignale) {
        /*

         */
        Query qCurrentUser = ref.child(USERS).orderByKey().equalTo(currentUid);
        qCurrentUser.addListenerForSingleValueEvent(vCurrentUser);
        /*

         */
        Query qTask = ref.child(TACHES).orderByKey().equalTo(tid);
        qTask.addValueEventListener(vTask);
        /*

         */
        Query qComments = ref.child(COMMENTS).orderByChild(TID).equalTo(tid);
        qComments.addValueEventListener(vComment);
        /*

         */
        Query qSignales = ref.child(SIGNALES).child(TACHES).child(tid).orderByKey().equalTo(currentUid);
        qSignales.addValueEventListener(vSignale);
    }

    /**
     *
     * @param valueEventListener L'écouteur permettant de récupérer les élémnents dans la base de données
     */
    public void lireUnUtilisateurSpecifique(final ValueEventListener valueEventListener) {
        Query query = ref.child(USERS).orderByKey().equalTo(this.id);
        query.addValueEventListener(valueEventListener);
    }

    public static void lireUnUtilisateurSpecifique(final ValueEventListener valueEventListener, String uid) {
        Query query = ref.child(USERS).orderByKey().equalTo(uid);
        query.addValueEventListener(valueEventListener);
    }

    public static void lireUnUtilisateurkeys(final ValueEventListener valueEventListener, String uid) {
        ref.child(USERS).child(uid).child(KEYS).addValueEventListener(valueEventListener);
    }

    //Toutes les requetes permettant à l'utilisateur ou meme le système de d'ecrire, de modifier ou
    // de supprimer les éléments contenu dans la base de données

    /*
        Les taches
     */
    public void ecrireunenouvelletache(Tache tache) {
        ref.child(buildPathWithSlash(TACHES, tache.getTid())).setValue(tache)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Toast.makeText(appContext, appContext.getString(R.string.success_operation), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(appContext, appContext.getString(R.string.not_auth_to_do), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(appContext, appContext.getString(R.string.error_operation), Toast.LENGTH_SHORT).show());
    }

    public void ecrireUneNouvelleTacheEtMajLutilisateur(ProgressDialog pd, Tache tache, String ntask) {
        pd.setMessage(appContext.getString(R.string.save_task_data));
        ref.child(buildPathWithSlash(TACHES, tache.getTdate())).setValue(tache)
                .addOnSuccessListener(unused -> {
                    ref.child(buildPathWithSlash(USERS, tache.getUid(), UNTASK)).setValue(incrementValue(ntask))
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

    public void ecrireUneNouvelleTacheApresMajPhotoUtilisateur(ProgressDialog pd, Uri imageUri, Tache tache, String ntask) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(buildPathWithSlash(
                TACHES,
                tache.getUid(),
                tache.getTdate()
        ));
        storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());

            String downloadUri = uriTask.getResult().toString();
            if (uriTask.isSuccessful()){
                pd.dismiss();
                tache.setTcover(downloadUri);
                ecrireUneNouvelleTacheEtMajLutilisateur(pd, tache, ntask);
            }else
                pd.dismiss();
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(appContext, appContext.getString(R.string.save_task_image_error), Toast.LENGTH_SHORT).show();
        });
    }

    /*
        Les utilisateurs
     */

    public void ecrireunnouvelutilisateur(User user) {
        ref.child(buildPathWithSlash(USERS, user.getUid())).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Toast.makeText(appContext, appContext.getString(R.string.success_operation), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(appContext, appContext.getString(R.string.not_auth_to_do), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(appContext, appContext.getString(R.string.error_operation), Toast.LENGTH_SHORT).show());
    }

    /*
        Les commentaires
     */

    public static void ecrireUnNouveauCommentaire(ProgressDialog pd, Commentaire commentaire, String ntComment, String nuComment) {
        String timestamp = getXtimestamp();
        commentaire.setCid(timestamp);
        commentaire.setCdate(timestamp);
        ref.child(buildPathWithSlash(COMMENTS, timestamp)).setValue(commentaire)
                .addOnSuccessListener(unused -> {
                    ref.child(buildPathWithSlash(TACHES, commentaire.getTid(), TNCOMMENTS))
                            .setValue(incrementValue(ntComment))
                            .addOnCompleteListener(task -> {
                                ref.child(buildPathWithSlash(USERS, commentaire.getUid(), UNCOMMENTS))
                                        .setValue(incrementValue(nuComment))
                                        .addOnCompleteListener(task1 -> pd.dismiss())
                                        .addOnFailureListener(e -> pd.dismiss());
                            })
                            .addOnFailureListener(e -> pd.dismiss());
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                });
    }

    /*
        Les Keys
     */

    public static void ecrireUneNouvelleKey(ProgressDialog pd, Key key, String myuid, String nkeys) {
        ref.child(buildPathWithSlash(USERS, myuid, KEYS, key.getKid())).setValue(key)
                .addOnSuccessListener(unused -> {
                    ref.child(buildPathWithSlash(USERS, myuid, UNKEYS))
                            .setValue(incrementValue(nkeys))
                            .addOnCompleteListener(task1 -> pd.dismiss())
                            .addOnFailureListener(e -> pd.dismiss());
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                });
    }

    public static void deleteKey(String path, String myuid, String nkeys) {
        ref.child(path).removeValue()
                .addOnSuccessListener(unused -> {
                    ref.child(buildPathWithSlash(USERS, myuid, UNKEYS))
                            .setValue(decrementValue(nkeys))
                            .addOnCompleteListener(task1 -> {
                                //pd.dismiss();
                            })
                            .addOnFailureListener(e -> {
                                //pd.dismiss();
                            });
                }).addOnFailureListener(e -> {
                    //pd.dismiss();
                });
    }

    /*
        Les autres méthodes
     */

    public void ecrireDansUnChamp(String path, String value) {
        ref.child(path).setValue(value)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Toast.makeText(appContext, appContext.getString(R.string.updated_ok), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(appContext, appContext.getString(R.string.not_auth_to_do), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(appContext, appContext.getString(R.string.updated_not_ok), Toast.LENGTH_SHORT).show());
    }

    public static void ecrireUnSignalementDeTache(Signale signale, ImageButton ibTaskSignale) {
        signale.setSdate(getXtimestamp());
        ref.child(buildPathWithSlash(SIGNALES, TACHES, signale.getTid(), signale.getSid())).setValue(signale)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        ibTaskSignale.setImageResource(R.drawable.ic_action_signaler);
                        Toast.makeText(appContext, appContext.getString(R.string.success_operation), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        ibTaskSignale.setImageResource(R.drawable.ic_action_signaler_old);
                        Toast.makeText(appContext, appContext.getString(R.string.not_auth_to_do), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(appContext, appContext.getString(R.string.updated_not_ok), Toast.LENGTH_SHORT).show());
    }

    public static void ecrireUnSignalementDuser(Signale signale) {
        signale.setSdate(getXtimestamp());
        ref.child(buildPathWithSlash(SIGNALES, USERS, signale.getUid(), signale.getSid())).setValue(signale)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Toast.makeText(appContext, appContext.getString(R.string.success_operation), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(appContext, appContext.getString(R.string.not_auth_to_do), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(appContext, appContext.getString(R.string.updated_not_ok), Toast.LENGTH_SHORT).show());
    }

    /*
        Sign out
     */

    public static void signOut() {
        auth.signOut();
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
            saveImage(pd, imageUri, iscover, id);
        }else {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(oldPath);
            storageReference.delete().addOnSuccessListener(unused -> saveImage(pd, imageUri, iscover, id)).addOnFailureListener(e -> {
                pd.dismiss();
                Toast.makeText(appContext, appContext.getString(R.string.delete_image_error), Toast.LENGTH_SHORT).show();
            });
        }
    }

    public void deleteOldImage(ProgressDialog pd, Uri imageUri, String pathStorage, String pathRealtime) {
        String timestamp = getXtimestamp();
        if (isStringEmpty(pathStorage)){
            saveImage(pd, imageUri, buildPathWithSlash(TACHES, id, timestamp), pathRealtime);
        }else {
            pd.setMessage(appContext.getString(R.string.task_image_delete));
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(pathStorage);
            storageReference.delete().addOnSuccessListener(unused ->
                    saveImage(pd, imageUri, buildPathWithSlash(TACHES, id, timestamp), pathRealtime))
                    .addOnFailureListener(e -> {
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
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(buildPathWithSlash(USERS, iscover?COVER:AVATAR, id));
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

    public void saveImage(ProgressDialog pd, Uri imageUri, String pathStorage, String pathRealtime) {
        pd.setMessage(appContext.getString(R.string.task_image_save_storage));
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(pathStorage);
        storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());

            String downloadUri = uriTask.getResult().toString();
            if (uriTask.isSuccessful()){
                pd.setMessage(appContext.getString(R.string.task_image_realtime_path));
                writeStringInDb(pathRealtime, downloadUri);
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
