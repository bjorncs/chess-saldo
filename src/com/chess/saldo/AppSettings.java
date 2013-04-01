package com.chess.saldo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.chess.saldo.service.Saldo;

public class AppSettings {
	
	private final SharedPreferences prefs;
	
	public AppSettings(Context context) {
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public String getUsername() {
		return prefs.getString("username", "");
	}
	
	public void setUsername(String username) {
		Editor editor = prefs.edit();
	    editor.putString("username", username);
		editor.commit();
	}
	
	public String getPassword() {
		return prefs.getString("password", "");
	}
	
	public void setPassword(String password) {
		Editor editor = prefs.edit();
		editor.putString("password", password);
		editor.commit();
	}
	
	public Saldo getSaldo() {
		String saldoStr = prefs.getString("saldo", null);
		if (saldoStr == null) return Saldo.createDefaultSaldo();
		return new Saldo(saldoStr);
	}
	
	public void setSaldo(Saldo saldo) {
		updateLastUpdate();
		if (saldo.isValid()) {
			Editor editor = prefs.edit();
			editor.putString("saldo", saldo.asString());
			editor.commit();
		}
	}
	
	public boolean isUserCredentialsSet() {
		String username = getUsername();
		String password = getPassword();
		return username != null && username.length() > 0 && password != null && password.length() > 0; 
	}
	
	public int getUpdateFrequency() {
		return Integer.parseInt(prefs.getString("UPDATE_FREQ_KEY", "3600000"));
	}
	
	public boolean shouldUpdate() {
		long current = System.currentTimeMillis();
		long last = prefs.getLong("last_update", current);
		long difference = current - last;
		int updateFreq = getUpdateFrequency();
		boolean widgetEnabled = isWidgetEnabled();
		boolean result = widgetEnabled && difference >= updateFreq;
		Log.d("CHESS_SALDO", String.format("ShouldUpdate() = %b (difference=%d, updateFreq=%d, widgetEnabled=%b)", 
				result, difference, updateFreq, widgetEnabled));
		return result;
	}
	
	public void setWidgetEnabled(boolean enabled) {
		Editor editor = prefs.edit();
		editor.putBoolean("widget_enabled", enabled);
		editor.commit();
	}
	
	private boolean isWidgetEnabled() {
		return prefs.getBoolean("widget_enabled", false);
	}
	
	private void updateLastUpdate() {
		Editor editor = prefs.edit();
		editor.putLong("last_update", System.currentTimeMillis());
		editor.commit();
	}

}
