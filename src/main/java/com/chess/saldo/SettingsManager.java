package com.chess.saldo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.chess.saldo.service.entities.Saldo;
import com.chess.saldo.service.entities.SaldoItem;
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
        String money = prefs.getString("saldo.money", "");
        Saldo saldo = new Saldo(money);
        for (SaldoType type : SaldoType.values()) {
            if (prefs.getBoolean("saldo."+type.apiName, true)) {
                int balance = prefs.getInt("saldo."+type.apiName+".balance", 0);
                int total = prefs.getInt("saldo."+type.apiName+".total", 0);
                saldo.items.put(type, new SaldoItem(balance, total, type));
            }
        }
        return saldo;
    }

    public void setSaldo(Saldo saldo) {
        updateLastUpdate();
        Editor editor = prefs.edit();
        editor.putString("saldo.money", saldo.moneyUsed);
        // Remove all saldo items
        for (SaldoType type : SaldoType.values()) {
            editor.putBoolean("saldo."+type.apiName, false);
        }
        // Add all saldo items from saldo object
        for (SaldoItem item : saldo.items.values()) {
            editor.putBoolean("saldo."+item.type.apiName, true);
            editor.putInt("saldo."+item.type.apiName+".balance", item.balance);
            editor.putInt("saldo."+item.type.apiName+".total", item.total);
        }
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
        return SaldoType.fromPrettyString(prefs.getString("widget." + widgetId + ".type", SaldoType.MONEY.prettyName));
    }

    private void updateLastUpdate() {
        Editor editor = prefs.edit();
        editor.putLong("service.lastupdate", System.currentTimeMillis());
        editor.commit();
    }

}
