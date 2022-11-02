package com.deone.extrmtasks.tools;

import static com.deone.extrmtasks.tools.Constants.CAMERA_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Constants.LOCATION_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Constants.LOCATION_REQUEST_CODE_ACCESS_COARSE_LOCATION;
import static com.deone.extrmtasks.tools.Constants.LOCATION_REQUEST_CODE_ACCESS_FINE_LOCATION;
import static com.deone.extrmtasks.tools.Other.formatAdresse;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.deone.extrmtasks.R;
import com.deone.extrmtasks.modeles.Tache;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Lctools {

    private static Context appContext;
    private static Lctools instance;
    private static LocationManager locationManager;
    private static String[] locationPermissions;
    private String city;
    private String country;
    private String codepostal;
    private String state;
    private String adresse;
    private double longitude;
    private double latitude;

    /**
     *
     * @param applicationContext
     * @return
     */
    public static synchronized Lctools getInstance(Context applicationContext) {
        if (instance == null)
            instance = new Lctools(applicationContext);
        return instance;
    }

    /**
     *
     * @param applicationContext
     */
    private Lctools(Context applicationContext) {
        appContext = applicationContext;
        locationPermissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        locationManager = (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
        initLocation();
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getCodepostal() {
        return codepostal;
    }

    public String getState() {
        return state;
    }

    public String getAdresse() {
        return adresse;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    /**
     *
     */

    private void initLocation() {
        //https://stackoverflow.com/questions/33327984/call-requires-permissions-that-may-be-rejected-by-user
        Criteria criteria = new Criteria();
        String providerName = locationManager.getBestProvider(criteria, true);
        if (ContextCompat.checkSelfPermission(appContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(appContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (providerName == null || providerName.equals("")) {
                appContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
            Location location = locationManager.getLastKnownLocation(providerName);
            if (location != null){
                this.latitude = location.getLatitude();
                this.longitude = location.getLongitude();
            }else {
                Toast.makeText(appContext, "Localisation null", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!checkLocationPermissions()) {
                requestLocationPermissions();
            }
        }
        isLocationEnabled();
    }

    public boolean checkLocationPermissions() {
        boolean result = ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    public void requestLocationPermissions() {
        ActivityCompat.requestPermissions((Activity) appContext, locationPermissions, LOCATION_REQUEST_CODE);
    }

    public void isLocationEnabled() {
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(appContext);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    appContext.startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            AlertDialog alert=alertDialog.create();
            alert.show();
        }
    }

    public void displayTaskLocation(){
        try {
            Geocoder geocoder = new Geocoder(appContext, Locale.getDefault());

            List<Address> addresses  = geocoder.getFromLocation(latitude, longitude, 1);

            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            codepostal = addresses.get(0).getPostalCode();
            adresse = addresses.get(0).getAddressLine(0);

            AlertDialog.Builder alertDialog=new AlertDialog.Builder(appContext);
            alertDialog.setTitle("Votre position actuelle");
            alertDialog.setMessage(
                    formatAdresse(
                            appContext.getString(R.string.ville) + " : " + city,
                            appContext.getString(R.string.state) + " : " + state,
                            appContext.getString(R.string.pays) + " : " + country,
                            appContext.getString(R.string.codepostal) + " : " + codepostal,
                            appContext.getString(R.string.adresse) + " : " + adresse)
            );
            alertDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
            alertDialog.setCancelable(false);
            AlertDialog alert=alertDialog.create();
            alert.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // TODO: Localisation du 02 Novembre 2022

    public static boolean checkAccessFineLocationPermissions(Context appContext) {
        return ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == (PackageManager.PERMISSION_GRANTED);
    }

    public static void requestAccessFineLocationPermissions(Context appContext) {
        ActivityCompat.requestPermissions((Activity) appContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    public static void displayTaskLocation(Context appContext, Location location, Tache tache){
        try {
            Geocoder geocoder = new Geocoder(appContext, Locale.getDefault());
            List<Address> addresses  = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tache.setTville(addresses.get(0).getLocality());
            tache.setTville(addresses.get(0).getAdminArea());
            tache.setTville(addresses.get(0).getCountryName());
            tache.setTville(addresses.get(0).getPostalCode());
            tache.setTville(addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
