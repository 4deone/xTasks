package com.deone.extrmtasks.tools;

import static com.deone.extrmtasks.tools.Constants.APP_PREFS;

import android.content.Context;
import android.content.SharedPreferences;

public class Sptools {
    private static SharedPreferences sp;
    private static Context appContext;
    private static Sptools instance;

    /**
     *
     * @param applicationContext
     * @return
     */
    public static synchronized Sptools getInstance(Context applicationContext) {
        if (instance == null)
            instance = new Sptools(applicationContext);
        return instance;
    }

    /**
     *
     * @param applicationContext
     */
    private Sptools (Context applicationContext) {
        appContext = applicationContext;
        sp = appContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
    }

    /**
     *
     * @param key
     * @return
     */
    public String readStringData(String key){
        return sp.getString(key, "");
    }

    /**
     *
     * @param key
     * @param defaut
     * @return
     */
    public String readStringData(String key, String defaut){
        return sp.getString(key, ""+defaut);
    }

    /**
     *
     * @param key
     * @param defaut
     * @return
     */
    public int readIntData(String key, int defaut){
        return sp.getInt(key, defaut);
    }

    /**
     *
     * @param key
     * @param defaut
     * @return
     */
    public boolean readBooleanData(String key, boolean defaut){
        return sp.getBoolean(key, defaut);
    }

    /**
     *
     * @param key
     * @param value
     */
    public void writeBooleanData(String key, boolean value){
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(key, value);
        ed.apply();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void writeStringData(String key, String value){
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(key, value);
        ed.apply();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void writeIntData(String key, int value){
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(key, value);
        ed.apply();
    }

    /**
     *
     * @param key
     */
    public void removeData(String key){
        SharedPreferences.Editor ed = sp.edit();
        ed.remove(key);
        ed.apply();
    }

    /**
     *
     */
    public void removeAllData(){
        sp.edit().clear().apply();
    }
}
