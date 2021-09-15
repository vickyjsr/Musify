package com.example.musify;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveSharedPreferences {
    Context context;
    SharedPreferences sharedPreferences;

    public SaveSharedPreferences(Context context)
    {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("apreferences",Context.MODE_PRIVATE);

    }
    public void setState(boolean b)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("editor",b);
        editor.apply();
    }

    public boolean getState()
    {
        return sharedPreferences.getBoolean("editor",false);
    }




}
