package com.chess.saldo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

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
        if (key.equals("widget.updatefreq")) {
            ConnectivityBroadcastReceiver.setUpdateAlarm(getApplicationContext());
        } else if (key.equals("username") || key.equals("password")) {
            String username = sharedPreferences.getString("username", "");
            String password = sharedPreferences.getString("password", "");
            if (username.length() > 0 && password.length() > 0) {
                Intent intent = new Intent(this, UpdateService.class);
                intent.putExtra(UpdateService.SHOW_TOAST, true);
                startService(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}
