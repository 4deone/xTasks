package com.deone.extrmtasks.tools;

import static com.deone.extrmtasks.tools.Constants.LOCATION_REQUEST_CODE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.deone.extrmtasks.modeles.Localize;
import com.deone.extrmtasks.modeles.Tache;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Lctools {
    private static Context appContext;
    private static Lctools instance;

    public static synchronized Lctools getInstance(Context applicationContext) {
        if (instance == null)
            instance = new Lctools(applicationContext);
        return instance;
    }

    private Lctools(Context applicationContext) {
        appContext = applicationContext;
    }

    // TODO: Localisation du 02 Novembre 2022

    public static boolean checkAccessFineLocationPermissions(Context appContext) {
        return ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == (PackageManager.PERMISSION_GRANTED);
    }

    public static void requestAccessFineLocationPermissions(Context appContext) {
        ActivityCompat.requestPermissions((Activity) appContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    public static Localize displayTaskLocation(Context appContext, Location location){
        try {
            Geocoder geocoder = new Geocoder(appContext, Locale.getDefault());
            List<Address> addresses  = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            return new Localize(
                    addresses.get(0).getPostalCode(),
                    addresses.get(0).getLocality(),
                    addresses.get(0).getAdminArea(),
                    addresses.get(0).getCountryName(),
                    addresses.get(0).getAddressLine(0),
                    ""+location.getLatitude(),
                    ""+location.getLongitude());

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
