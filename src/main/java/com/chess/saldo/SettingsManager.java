package com.chess.saldo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.chess.saldo.service.entities.Saldo;
import com.chess.saldo.service.entities.SaldoType;

public class SettingsManager {

    private final SharedPreferences prefs;

    public SettingsManager(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getUsername() {
        return prefs.getString("username", "");
    }

    public String getPassword() {
        return prefs.getString("password", "");
    }

    public Saldo getSaldo() {
        int smsTotal = prefs.getInt("saldo.smsTotal", -1);
        int smsLeft = prefs.getInt("saldo.smsLeft", -1);
        int mmsTotal = prefs.getInt("saldo.mmsTotal", -1);
        int mmsLeft = prefs.getInt("saldo.mmsLeft", -1);
        int minutesTotal = prefs.getInt("saldo.minutesTotal", -1);
        int minutesLeft = prefs.getInt("saldo.minutesLeft", -1);
        float dataTotal = prefs.getFloat("saldo.dataTotal", -1);
        float dataLeft = prefs.getFloat("saldo.dataLeft", -1);
        float moneyUsed = prefs.getFloat("saldo.moneyUsed", -1);
        String strMoneyUsed = prefs.getString("saldo.strMoneyUsed", "");

        Saldo saldo = new Saldo(
                smsTotal, smsLeft,
                mmsTotal, mmsLeft,
                minutesTotal, minutesLeft,
                dataTotal, dataLeft,
                moneyUsed, strMoneyUsed);
        return saldo;
    }

    public void setSaldo(Saldo saldo) {
        updateLastUpdate();
        Editor editor = prefs.edit();
        editor.putInt("saldo.smsTotal", saldo.smsTotal);
        editor.putInt("saldo.smsLeft", saldo.smsLeft);
        editor.putInt("saldo.mmsTotal", saldo.mmsTotal);
        editor.putInt("saldo.mmsLeft", saldo.mmsLeft);
        editor.putInt("saldo.minutesTotal", saldo.minutesTotal);
        editor.putInt("saldo.minutesLeft", saldo.minutesLeft);
        editor.putFloat("saldo.dataTotal", saldo.dataTotal);
        editor.putFloat("saldo.dataLeft", saldo.dataLeft);
        editor.putFloat("saldo.moneyUsed", saldo.moneyUsed);
        editor.putString("saldo.strMoneyUsed", saldo.strMoneyUsed);
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

    public void setWidgetType(SaldoType type, int widgetId) {
        Editor editor = prefs.edit();
        editor.putString("widget." + widgetId + ".type", type.prettyName);
        editor.commit();
    }

    public SaldoType getWidgetType(int widgetId) {
        return SaldoType.fromString(prefs.getString("widget." + widgetId + ".type", SaldoType.MONEY.prettyName));
    }

    private void updateLastUpdate() {
        Editor editor = prefs.edit();
        editor.putLong("service.lastupdate", System.currentTimeMillis());
        editor.commit();
    }

}
