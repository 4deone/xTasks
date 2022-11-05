package com.deone.extrmtasks.tools;

import static com.deone.extrmtasks.preference.Sptools.readIntData;
import static com.deone.extrmtasks.tools.Constants.APP;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_CURRENT_POSITION_PRIORITY;
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
import static com.deone.extrmtasks.tools.Constants.d2km;
import static com.deone.extrmtasks.tools.Constants.d2r;

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
import com.deone.extrmtasks.adapters.Tadapter;
import com.deone.extrmtasks.modeles.Key;
import com.deone.extrmtasks.modeles.Localize;
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

    //

    public static List<Tache> orderListByPriority(Localize localize, List<Tache> tacheList) {
        int priority = readIntData(APP_PREFS_CURRENT_POSITION_PRIORITY, -1);
        if (priority != -1){
            List<Tache> temp = new ArrayList<>();
            for (Tache tache : tacheList) {
                if (priority == 0 && tache.getLocalize().getAddress().equals(localize.getAddress())) {
                    temp.add(tache);
                } else if (priority == 1 && tache.getLocalize().getCodepostal().equals(localize.getCodepostal())) {
                    temp.add(tache);
                } else if (priority == 2 && tache.getLocalize().getCountry().equals(localize.getCountry())) {
                    temp.add(tache);
                } else if (priority == 3 && tache.getLocalize().getCity().equals(localize.getCity())) {
                    temp.add(tache);
                } else if (priority == 4 && tache.getLocalize().getState().equals(localize.getState())) {
                    temp.add(tache);
                }
            }
            return temp;
        }
        return tacheList;
    }

    public static List<Tache> orderListByKeyWords(Localize localize, List<Tache> tacheList, List<Key> keyList) {
        //List<Tache> taches = orderListByPriority(localize, tacheList);
        List<Tache> taches = tacheList;
        if (keyList.size() != 0){
            List<Tache> temp = new ArrayList<>();
            for (Key key : keyList) {
                for (Tache tache : taches){
                    if (isContains(""+key.getKmessage(), ""+tache.getTtitre()) ||
                            isContains(""+key.getKmessage(), ""+tache.getTdescription())){
                        temp.add(tache);
                    }
                }
            }
            return temp;
        }
        return taches;
    }

    public static String tacheToString(Tache tache) {
        StringBuilder sb = new StringBuilder();
        sb.append(tache.getTid());
        sb.append(";\n");
        sb.append(tache.getTtitre());
        sb.append(";\n");
        sb.append(tache.getTdescription());
        sb.append(";\n");
        sb.append(tache.getLocalize().getCountry());
        sb.append(";\n");
        sb.append(tache.getLocalize().getCity());
        sb.append(";\n");
        sb.append(tache.getLocalize().getCodepostal());
        sb.append(";\n");
        sb.append(tache.getLocalize().getLatitude());
        sb.append(";\n");
        sb.append(tache.getLocalize().getLongitude());
        sb.append(";\n");
        sb.append(tache.getLocalize().getState());
        sb.append(";\n");
        sb.append(tache.getLocalize().getAddress());
        return sb.toString();
    }

    // TODO: Test calcul de distance entre deux points

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public static double meters(double lt1, double ln1, double lt2, double ln2) {
        double x = lt1 * d2r;
        double y = lt2 * d2r;
        return Math.acos( Math.sin(x) * Math.sin(y) + Math.cos(x) * Math.cos(y) * Math.cos(d2r * (ln1 - ln2))) * d2km;
    }
}
