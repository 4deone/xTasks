package com.deone.extrmtasks.persist;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Xapp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
