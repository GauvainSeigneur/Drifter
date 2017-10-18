package io.drifterapp.drifter.utils;

/**
 * Created by gse on 13/12/2016.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


public class Keystore {
    private static Keystore store;
    private SharedPreferences SP;
    private static String filename="Keys";

    //Values Saved
    public static final String kFirstTimeHome	                = "firstTimeHome";


    private Keystore(Context context) {
        SP = context.getApplicationContext().getSharedPreferences(filename,0);
    }

    public static Keystore getInstance(Context context) {
        if (store == null) {
            Log.v("Keystore","NEW STORE");
            store = new Keystore(context);
        }
        return store;
    }

    public void putString(String key, String value) {
        //Log.v("Keystore","PUT "+key+" "+value);
        Editor editor;

        editor = SP.edit();
        editor.putString(key, value);
        editor.commit(); // stop everything and save
        // editor.apply();//Keep going and save when you are not busy - Available only in APIs 9 and above.  This is the preferred way of saving.
    }

    //get simply String with only key
    public String getString(String key) {
        return SP.getString(key, null);

    }

    //get String with the random value needed to perform decoding Bitmap in String / compare
    public String getStringComplete(String key, String value) {
        return SP.getString(key, value);

    }

    //Register a boolean state
    public void putState(String key, Boolean value) {
        Editor editor;
        editor = SP.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getState(String key) {
        return SP.getBoolean(key, false); //by default the state is false
    }
    public int getInt(String key) {
        return SP.getInt(key, 0);
    }

    public void putInt(String key, int num) {
        Editor editor;
        editor = SP.edit();

        editor.putInt(key, num);
        editor.commit();
    }

    public Float getFloat(String key) {
        //Log.v("Keystore","GET INT from "+key);
        return SP.getFloat(key, 0.0f);
    }

    public void putFloat(String key, float value) {
        //Log.v("Keystore","PUT INT "+key+" "+String.valueOf(num));
        Editor editor;
        editor = SP.edit();

        editor.putFloat(key, value);
        editor.commit();
    }


    public void clear(){
        Editor editor;
        editor = SP.edit();

        editor.clear();
        editor.commit();
    }

    public void remove(){
        Editor editor;
        editor = SP.edit();

        editor.remove(filename);
        editor.commit();
    }
}
