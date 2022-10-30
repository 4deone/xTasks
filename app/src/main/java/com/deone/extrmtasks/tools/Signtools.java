package com.deone.extrmtasks.tools;

import static com.deone.extrmtasks.tools.Constants.AVATAR;
import static com.deone.extrmtasks.tools.Constants.DATABASE;
import static com.deone.extrmtasks.tools.Constants.USERS;
import static com.deone.extrmtasks.tools.Other.buildPathWithSlash;
import static com.deone.extrmtasks.tools.Other.checkCameraPermissions;
import static com.deone.extrmtasks.tools.Other.checkLocationPermissions;
import static com.deone.extrmtasks.tools.Other.checkStoragePermissions;
import static com.deone.extrmtasks.tools.Other.getXtimestamp;
import static com.deone.extrmtasks.tools.Other.gotohome;
import static com.deone.extrmtasks.tools.Other.gotonew;
import static com.deone.extrmtasks.tools.Other.isNewAccountMain;
import static com.deone.extrmtasks.tools.Other.requestCameraPermissions;
import static com.deone.extrmtasks.tools.Other.requestLocationPermissions;
import static com.deone.extrmtasks.tools.Other.requestStoragePermissions;
import static com.deone.extrmtasks.tools.Other.showDialog;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.deone.extrmtasks.R;
import com.deone.extrmtasks.modeles.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Signtools {
    private static DatabaseReference ref;
    private static FirebaseAuth auth;
    private static Context appContext;
    private static Signtools instance;
    /**
     *
     * @param applicationContext Context de l'application qui appelle cette instance
     * @return Renvoie une instance unique de l'objet Signtools
     */
    public static synchronized Signtools getInstance(Context applicationContext) {
        if (instance == null)
            instance = new Signtools(applicationContext);
        return instance;
    }

    /**
     *
     * @param applicationContext Context de l'application qui appelle cette instance
     */
    private Signtools(Context applicationContext) {
        appContext = applicationContext;
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
    }

    /*Ces methodes qui suivent concerne uniquemment la connexion de l'utilisateur à l'appplication.
      Elles sont utilisées dans le Main*/

    /**
     * Fonction utiliser dans le Main pour verifier le status du compte qui tente de se connecter à l'application
     * @param pd Progressdialog indiquant à l'utilisateur l'avancement des traitements encours
     * @param user Encapsulation des données de connexion de l'utilisateur
     */
    public void checkEmailStatus(ProgressDialog pd, User user){
        auth.fetchSignInMethodsForEmail(user.getUemail()).addOnCompleteListener(task -> {
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
                signin(pd, ""+user.getUemail(), ""+user.getUmotdepasse());
            }
        });
    }

    /**
     *
     * @param pd Progressdialog indiquant à l'utilisateur l'avancement des traitements encours
     * @param email Adresse mail unique qui autorise un utilisteur à avoir un compte pour pouvoir avoir accès à l'application
     * @param motdepasse Il doit être unique et connu du seul utilisateur du compte
     */
    private void signin(ProgressDialog pd, String email, String motdepasse) {
        auth.signInWithEmailAndPassword(email, motdepasse).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                pd.dismiss();
                gotohome(appContext);
            }else{
                pd.dismiss();
                Toast.makeText(appContext, appContext.getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(appContext, appContext.getString(R.string.connexion_error), Toast.LENGTH_SHORT).show());
    }

    /*Ces methodes qui suivent concerne uniquemment la création du compte de l'utilisateur.
      Elles sont utilisées dans le New*/

    /**
     * Fonction utiliser dans le Main pour verifier le status du compte qui tente de se créer un compte
     * @param pd Progressdialog indiquant à l'utilisateur l'avancement des traitements encours
     * @param imageUri Element comportant une image prise par l'utilisateur
     * @param user Encapsulation des données de connexion de l'utilisateur
     */
    public void checkEmailStatus(ProgressDialog pd, Uri imageUri, User user){
        pd.setMessage(appContext.getString(R.string.verif_if_account_exist));
        auth.fetchSignInMethodsForEmail(user.getUemail()).addOnCompleteListener(task -> {
            if (isNewAccountMain(task)){
                pd.setMessage(appContext.getString(R.string.check_requires_permission));
                if (!checkCameraPermissions(appContext)) {
                    requestCameraPermissions(appContext, new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE});
                }
                if (!checkStoragePermissions(appContext)) {
                    requestStoragePermissions(appContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                }
                if (!checkLocationPermissions(appContext)) {
                    requestLocationPermissions(appContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION});
                }
                createUserAccount(pd, imageUri, user);
            }else {
                pd.dismiss();
                showDialog(
                        appContext,
                        appContext.getString(R.string.app_name),
                        appContext.getString(R.string.account_exist)
                ).setPositiveButton(appContext.getString(R.string.ok), (dialogInterface, i) -> gotohome(appContext)).create().show();
            }
        });
    }

    /**
     *
     * @param pd Progressdialog indiquant à l'utilisateur l'avancement des traitements encours
     * @param imageUri Element comportant une image prise par l'utilisateur
     * @param user Encapsulation des données de connexion de l'utilisateur
     */
    private void createUserAccount(ProgressDialog pd, Uri imageUri, User user) {
        pd.setMessage(appContext.getString(R.string.create_user_account));
        auth.createUserWithEmailAndPassword(user.getUemail(), user.getUmotdepasse())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        checkUser(pd, imageUri, user);
                    } else {
                        pd.dismiss();
                        Toast.makeText(appContext, appContext.getString(R.string.create_account_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     *
     * @param pd Progressdialog indiquant à l'utilisateur l'avancement des traitements encours
     * @param imageUri Element comportant une image prise par l'utilisateur
     * @param user Encapsulation des données de connexion de l'utilisateur
     */
    private void checkUser(ProgressDialog pd, Uri imageUri, User user) {
        FirebaseUser fUser = auth.getCurrentUser();
        if (fUser != null) {
            user.setUid(fUser.getUid());
            pd.setMessage(appContext.getString(R.string.create_user_profile));
            createUserProfile(pd, imageUri, user);
        } else {
            Toast.makeText(appContext, appContext.getString(R.string.no_user), Toast.LENGTH_SHORT).show();
            gotohome(appContext);
        }
    }

    /**
     *
     * @param pd Progressdialog indiquant à l'utilisateur l'avancement des traitements encours
     * @param imageUri Element comportant une image prise par l'utilisateur
     * @param user Encapsulation des données de connexion de l'utilisateur
     */
    public void createUserProfile(ProgressDialog pd, Uri imageUri, User user) {
        if (imageUri != null)
            ajouterUserProfileImage(pd, imageUri, user);
        else
            ajouterUnUtilisateur(pd, user);
    }

    /**
     *
     * @param pd Progressdialog indiquant à l'utilisateur l'avancement des traitements encours
     * @param imageUri Element comportant une image prise par l'utilisateur
     * @param user Encapsulation des données de connexion de l'utilisateur
     */
    private void ajouterUserProfileImage(ProgressDialog pd, Uri imageUri, User user) {
        pd.setMessage(appContext.getString(R.string.create_user_avatar));
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference(buildPathWithSlash(USERS, AVATAR, user.getUid()));
        storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());

            String downloadUri = uriTask.getResult().toString();
            if (uriTask.isSuccessful()){
                user.setUavatar(downloadUri);
                ajouterUnUtilisateur(pd, user);
            }else
                pd.dismiss();
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(appContext, appContext.getString(R.string.save_image_error), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     *
     * @param pd Progressdialog indiquant à l'utilisateur l'avancement des traitements encours
     * @param user Encapsulation des données de connexion de l'utilisateur
     */
    private void ajouterUnUtilisateur(ProgressDialog pd, User user) {
        pd.setMessage(appContext.getString(R.string.save_user_infos));
        user.setUdate(getXtimestamp());
        ref.child(buildPathWithSlash(DATABASE, USERS, user.getUid()))
                .setValue(user)
                .addOnSuccessListener(unused -> {
                    pd.dismiss();
                    Toast.makeText(appContext, appContext.getString(R.string.save_user_info_ok), Toast.LENGTH_SHORT).show();
                    gotohome(appContext);
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(appContext, appContext.getString(R.string.save_user_info_error), Toast.LENGTH_SHORT).show();
                });
    }

}
