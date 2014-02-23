package com.chess.saldo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.chess.saldo.service.Saldo;

import org.json.JSONException;
import org.json.JSONObject;

public class Settings {

    private final SharedPreferences prefs;

    public Settings(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getUsername() {
        return prefs.getString("username", "");
    }

    public String getPassword() {
        return prefs.getString("password", "");
    }

    public Saldo getSaldo() {
        try {
            String json = prefs.getString("saldo", null);
            if (json == null) return null;
            return new Saldo(new JSONObject(json));
        } catch (JSONException e) {
            return null;
        }
    }

    public void setSaldo(Saldo saldo) {
        updateLastUpdate();
        Editor editor = prefs.edit();
        editor.putString("saldo", saldo.getRawData().toString());
        editor.commit();
    }

    public boolean isUserCredentialsSet() {
        String username = getUsername();
        String password = getPassword();
        return username != null && username.length() > 0 && password != null && password.length() > 0;
    }

    public int getUpdateFrequency() {
        return Integer.parseInt(prefs.getString("widget.updatefreq", "3600000"));
    }

    public long getLastUpdate() {
        return prefs.getLong("service.lastupdate", 0);
    }

    public void setWidgetType(String type, int widgetId) {
        Editor editor = prefs.edit();
        editor.putString("widget." + widgetId + ".type", type);
        editor.commit();
    }

    public String getWidgetType(int widgetId) {
        return prefs.getString("widget." + widgetId + ".type", "?");
    }

    public boolean showFribruk() {
        return prefs.getBoolean("show_fribruk", true);
    }

    private void updateLastUpdate() {
        Editor editor = prefs.edit();
        editor.putLong("service.lastupdate", System.currentTimeMillis());
        editor.commit();
    }

}
