package com.chess.saldo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.bcseime.android.chess.saldo2.R;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_screen);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "widget.updatefreq":
                ConnectivityBroadcastReceiver.setUpdateAlarm(getApplicationContext());
                break;
            case "username":
            case "password":
                String username = sharedPreferences.getString("username", "");
                String password = sharedPreferences.getString("password", "");
                if (username.length() > 0 && password.length() > 0) {
                    Intent intent = new Intent(this, UpdateService.class);
                    intent.putExtra(UpdateService.SHOW_TOAST, true);
                    startService(intent);
                }
                break;

            case "show_fribruk":
            case "show_consumption":
                WidgetProviders.updateAllWidgets(this);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}
