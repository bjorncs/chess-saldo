package com.chess.saldo;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.chess.saldo.R;

public class AppPreferenceActivity extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.widget_prefs);
	}

}
