package com.deone.extrmtasks.tools;

import static com.deone.extrmtasks.tools.Constants.APP;
import static com.deone.extrmtasks.tools.Constants.CAMERA_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Constants.CONDITIONS;
import static com.deone.extrmtasks.tools.Constants.FORMAT_DATE;
import static com.deone.extrmtasks.tools.Constants.LOCATION_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Constants.STORAGE_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Constants.CONTACT_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Constants.UDESCRIPTION;
import static com.deone.extrmtasks.tools.Constants.UID;
import static com.deone.extrmtasks.tools.Constants.UNOMS;
import static com.deone.extrmtasks.tools.Constants.UTELEPHONE;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.text.format.DateFormat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deone.extrmtasks.TempActivity;
import com.deone.extrmtasks.AddActivity;
import com.deone.extrmtasks.HomeActivity;
import com.deone.extrmtasks.MainActivity;
import com.deone.extrmtasks.NewActivity;
import com.deone.extrmtasks.R;
import com.deone.extrmtasks.SettingsActivity;
import com.deone.extrmtasks.TaskActivity;
import com.deone.extrmtasks.modeles.Key;
import com.deone.extrmtasks.modeles.Tache;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Other {

    /**
     *
     * @param value
     * @return
     */
    public static boolean isStringEmpty(String value) {
        return TextUtils.isEmpty(value);
    }

    /**
     *
     * @param task
     * @return
     */
    public static boolean isNewAccountMain(Task<SignInMethodQueryResult> task) {
        return Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty();
    }

    /**
     *
     * @param appContext
     * @param titre
     * @param message
     * @return
     */
    public static AlertDialog.Builder showDialog(Context appContext, String titre, String message) {
        return new AlertDialog.Builder(appContext)
                .setTitle(titre)
                .setMessage(message);
    }

    /**
     *
     * @param i
     */
    public static void initThemeMode(int i) {
        AppCompatDelegate.setDefaultNightMode(i == 2 ? AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);
    }

    /**
     *
     * @param langue
     */
    public static void initLLanguage(Context appContext, String langue) {
        Locale locale = new Locale(langue.toLowerCase());
        Locale.setDefault(locale);
        Configuration conf = new Configuration();
        conf.locale = locale;
        ((Activity) appContext).getBaseContext().getResources().updateConfiguration(
                conf, ((Activity) appContext).getBaseContext().getResources().getDisplayMetrics());
    }

    /**
     *
     * @param appContext
     */
    public static void gotohome(Context appContext) {
        appContext.startActivity(new Intent(appContext, HomeActivity.class));
        ((Activity) appContext).finish();
    }

    /**
     *
     * @param appContext
     */
    public static void gotonew(Context appContext) {
        appContext.startActivity(new Intent(appContext, NewActivity.class));
        ((Activity) appContext).finish();
    }

    /**
     *
     * @param appContext
     */
    public static void gotomain(Context appContext) {
        appContext.startActivity(new Intent(appContext, MainActivity.class));
        ((Activity) appContext).finish();
    }

    /**
     *
     * @param appContext
     */
    public static void gotoaddtask(Context appContext) {
        appContext.startActivity(new Intent(appContext, AddActivity.class));
    }

    /**
     *
     * @param appContext
     */
    public static void gotosettings(Context appContext) {
        appContext.startActivity(new Intent(appContext, SettingsActivity.class));
        ((Activity) appContext).finish();
    }

    public static void gotoaccount(Context appContext, String myuid) {
        Intent intent = new Intent(appContext, TempActivity.class);
        intent.putExtra(UID, myuid);
        appContext.startActivity(intent);
    }

    /**
     *
     * @param appContext
     * @param nameTui
     * @param valueTui
     * @param nameUid
     * @param valueUid
     */
    public static void gotoTask(Context appContext, String nameTui, String valueTui, String nameUid, String valueUid) {
        Intent intent = new Intent(appContext, TaskActivity.class);
        intent.putExtra(nameTui, valueTui);
        intent.putExtra(nameUid, valueUid);
        appContext.startActivity(intent);
    }

    /**
     *
     * @return
     */
    public static String getXtimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     *
     * @param list
     * @return
     */
    public static String buildPathWithSlash(String... list) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String item : list){
            sb.append(item);
            if (count < list.length)
                sb.append("/");
            count++;
        }
        return sb.toString();
    }

    /**
     *
     * @param list
     * @return
     */
    public static String buildPathWithSpace(String... list) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String item : list){
            sb.append(item);
            if (count < list.length)
                sb.append(" ");
            count++;
        }
        return sb.toString();
    }

    /**
     *
     * @param uid
     * @param fullname
     * @param avatar
     * @param telephone
     * @param email
     * @param timestamp
     * @return
     */
    public static HashMap<String, String> genHashMapUser(
            String uid,
            String fullname,
            String avatar,
            String telephone,
            String email,
            String timestamp,
            String ville,
            String pays) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("unoms", fullname);
        if(isStringEmpty(avatar))
            hashMap.put("uavatar", avatar);
        hashMap.put("utelephone", telephone);
        hashMap.put("uemail", email);
        hashMap.put("udate", timestamp);
        hashMap.put("ucity", ville);
        hashMap.put("ucountry", pays);
        return hashMap;
    }

    /**
     *
     * @param uid
     * @param titre
     * @param description
     * @param timestamp
     * @return
     */
    public static HashMap<String, String> genHashMapTask(
            String uid,
            String tcover,
            String titre,
            String description,
            String unoms,
            String uavatar,
            String timestamp,
            String tpays) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("tid", timestamp);
        hashMap.put("tcover", tcover);
        hashMap.put("ttitre", titre);
        hashMap.put("tdescription", description);
        hashMap.put("tdate", timestamp);
        hashMap.put("uid", uid);
        hashMap.put("unoms", unoms);
        hashMap.put("uavatar", uavatar);
        hashMap.put("tpays", tpays);
        return hashMap;
    }

    public static HashMap<String, String> genHashMapComment(
            String comment,
            String timestamp,
            String tid,
            String uid,
            String unoms,
            String uavatar) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("cid", timestamp);
        hashMap.put("cmessage", comment);
        hashMap.put("cdate", timestamp);
        hashMap.put("tid", tid);
        hashMap.put("uid", uid);
        hashMap.put("unoms", unoms);
        hashMap.put("uavatar", uavatar);
        return hashMap;
    }

    /**
     *
     * @param date
     * @return
     */
    public static String formatLaDate(String date) {
        return (String) DateFormat.format(FORMAT_DATE, Long.parseLong(date));
    }

    /**
     *
     * @param format
     * @param date
     * @return
     */
    public static String formateDateWithSpecificFormat(String format, String date) {
        return (String) DateFormat.format(format, Long.parseLong(date));
    }

    /**
     *
     * @return
     */
    public static RecyclerView.LayoutManager rvLayoutManager(Context appContext, int lastPosition, int orientation) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(appContext);
        layoutManager.setOrientation(orientation);
        layoutManager.scrollToPosition(lastPosition);
        return layoutManager;
    }

    /**
     *
     * @param message
     * @param value
     * @return
     */
    public static String checkBeforeFormatData(String message, String value) {
        return MessageFormat.format(buildPathWithSpace("{0}", message), isStringEmpty(value) ? "0" : value);
    }

    /**
     *
     * @param i
     * @return
     */
    public static int chooseDrawable(int i) {
        return i == 0 ? R.drawable.ic_action_nuit:R.drawable.ic_action_jour;
    }

    /**
     *
     * @return
     */
    public static int checkUiCurrentMode() {
        return AppCompatDelegate.getDefaultNightMode();
    }

    /**
     *
     * @param langue
     * @return
     */
    public static int selectedLangue(String langue) {
        switch (langue) {
            case "en":
                return 0;
            case "ar":
                return 1;
            case "fr":
                return 2;
        }
        return -1;
    }

    /**
     *
     * @param value
     * @return
     */
    public static String incrementValue(String value) {
        return isStringEmpty(value) ? "1" : ""+(Integer.parseInt(value)+1);
    }

    /**
     *
     * @param value
     * @return
     */
    public static String decrementValue(String value) {
        return isStringEmpty(value) ? "0" : ""+(Integer.parseInt(value)-1);
    }

    /**
     *
     * @param value
     * @return
     */
    public static String safeShowValue(String value) {
        return isStringEmpty(value) ? "0" : value;
    }

    /**
     *
     * @param appContext
     * @param title
     * @param message
     * @return
     */
    public static ProgressDialog buildProgressDialog(Context appContext, String title, String message) {
        ProgressDialog pd = new ProgressDialog(appContext);
        pd.setTitle(title);
        pd.setMessage(message);
        pd.setCancelable(false);
        return pd;
    }

    /**
     *
     * @param appContext
     * @param title
     * @param message
     * @param noListener
     * @param noTitle
     * @param okListener
     * @param okTitle
     * @return
     */
    public static AlertDialog.Builder buildAlertDialog(Context appContext, String title, String message,
                                                       DialogInterface.OnClickListener noListener, String noTitle,
                                                       DialogInterface.OnClickListener okListener, String okTitle) {
        AlertDialog.Builder ad = new AlertDialog.Builder(appContext);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setNegativeButton(noTitle, noListener);
        ad.setPositiveButton(okTitle, okListener);
        return ad;
    }

    /**
     *
     * @param appContext
     * @param title
     * @param listener
     * @param items
     * @return
     */
    public static AlertDialog.Builder buildAlertDialogForSelectOption(Context appContext, String title,
                                                                      DialogInterface.OnClickListener listener,
                                                                      String[] items) {
        AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
        builder.setTitle(title);
        builder.setItems(items, listener);
        return builder;
    }

    public static AlertDialog.Builder buildAlertDialogForSingleSelectOption(Context appContext, String title,
                                                                            DialogInterface.OnClickListener listener,
                                                                            String[] items, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
        builder.setTitle(title);
        builder.setSingleChoiceItems(items, position, listener);
        return builder;
    }

    public static String createTitle(Context appContext, String field) {
        switch (field) {
            case UNOMS:
                return MessageFormat.format(appContext.getString(R.string.dialog_title), appContext.getString(R.string.full_name));
            case UTELEPHONE:
                return MessageFormat.format(appContext.getString(R.string.dialog_title), appContext.getString(R.string.telephone));
            case UDESCRIPTION:
                return MessageFormat.format(appContext.getString(R.string.dialog_title), appContext.getString(R.string.description));
        }
        return null;
    }

    public static void toutesLesConditions(ValueEventListener vConditions) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(APP);
        ref.child(CONDITIONS).addValueEventListener(vConditions);
    }

    public static boolean checkCameraPermissions(Context appContext) {
        boolean result = ContextCompat.checkSelfPermission(appContext, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(appContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    public static void requestCameraPermissions(Context appContext, String[] cameraPermissions) {
        ActivityCompat.requestPermissions((Activity) appContext, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    public static boolean checkStoragePermissions(Context appContext) {
        return ContextCompat.checkSelfPermission(appContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
    }

    public static void requestStoragePermissions(Context appContext, String[] storagePermissions) {
        ActivityCompat.requestPermissions((Activity) appContext, storagePermissions, STORAGE_REQUEST_CODE);
    }

    public static boolean checkLocationPermissions(Context appContext) {
        boolean result = ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    public static boolean checkContactPermissions(Context appContext) {
        boolean result = ContextCompat.checkSelfPermission(appContext, Manifest.permission.READ_CONTACTS)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(appContext, Manifest.permission.WRITE_CONTACTS)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    public static void requestLocationPermissions(Context appContext, String[] locationPermissions) {
        ActivityCompat.requestPermissions((Activity) appContext, locationPermissions, LOCATION_REQUEST_CODE);
    }

    public static void requestContactPermissions(Context appContext, String[] contactPermissions) {
        ActivityCompat.requestPermissions((Activity) appContext, contactPermissions, CONTACT_REQUEST_CODE);
    }

    public static boolean isContains(String contenu, String contenant) {
        return contenant.toLowerCase().contains(contenu.toLowerCase());
    }

    public static String formatAdresse(String... items) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String item : items){
            sb.append(item);
            if(i!=items.length)
                sb.append(";\n");
            else
                sb.append(".");
            i++;
        }
        return sb.toString();
    }

    public static List<Tache> orderListByKeyWords(List<Tache> tacheList, List<Key> keyList) {
        if (keyList.size() == 0)
            return tacheList;
        else {
            List<Tache> temp = new ArrayList<>();
            for (Key key : keyList) {
                for (Tache tache : tacheList){
                    if (isContains(""+key.getKmessage(), ""+tache.getTtitre())
                            ||
                            isContains(""+key.getKmessage(), ""+tache.getTdescription())){
                        temp.add(tache);
                    }
                }
            }
            return temp;
        }
    }

    public static String tacheToString(Tache tache) {
        StringBuilder sb = new StringBuilder();
        sb.append(tache.getTid());
        sb.append(";\n");
        sb.append(tache.getTtitre());
        sb.append(";\n");
        sb.append(tache.getTdescription());
        sb.append(";\n");
        sb.append(tache.getTpays());
        sb.append(";\n");
        sb.append(tache.getTville());
        sb.append(";\n");
        sb.append(tache.getTcodepostal());
        sb.append(";\n");
        sb.append(tache.getTlatitude());
        sb.append(";\n");
        sb.append(tache.getTlongitude());
        sb.append(";\n");
        sb.append(tache.getTstate());
        sb.append(";\n");
        sb.append(tache.getTadresse());
        return sb.toString();
    }
}
