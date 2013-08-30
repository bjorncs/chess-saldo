package com.chess.saldo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bcseime.android.chess.saldo2.R;
import com.chess.saldo.service.entities.Saldo;
import com.chess.saldo.service.entities.SaldoItem;
import com.chess.saldo.service.entities.SaldoType;

public class MainActivity extends Activity {

    private UpdateCompleteBroadcastReceiver receiver;

    private SettingsManager settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.settings = new SettingsManager(getApplicationContext());
        setContentView(R.layout.main_layout);
        if (!settings.isUserCredentialsSet()) {
            showPreferenceActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(UpdateService.UPDATE_COMPLETE_ACTION);
        receiver = new UpdateCompleteBroadcastReceiver();
        registerReceiver(receiver, filter);
        updateUI();
    }

    @Override
    public void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.settings_item:
                showPreferenceActivity();
                return true;

            case R.id.update_item:
                Intent intent = new Intent(this, UpdateService.class);
                intent.putExtra(UpdateService.SHOW_TOAST, true);
                startService(intent);
                return true;

        }
        return false;
    }

    private void showPreferenceActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Saldo saldo = settings.getSaldo();
                TextView cashTextView = (TextView) findViewById(R.id.cashValue);
                cashTextView.setText(saldo.moneyUsed);
                updateSaldoItem(R.id.main_panel_data, R.id.dataValue, R.id.dataProgress, saldo, SaldoType.DATA, settings);
                updateSaldoItem(R.id.main_panel_minutes, R.id.minutesValue, R.id.minutesProgress, saldo, SaldoType.MINUTES, settings);
                updateSaldoItem(R.id.main_panel_mms, R.id.mmsValue, R.id.mmsProgress, saldo, SaldoType.MMS, settings);
                updateSaldoItem(R.id.main_panel_sms, R.id.smsValue, R.id.smsProgress, saldo, SaldoType.SMS, settings);
            }
        });
    }

    private void updateSaldoItem(int panelId, int textViewId, int progressBarId, Saldo saldo, SaldoType type, SettingsManager settings) {
        ViewGroup panel = (ViewGroup) findViewById(panelId);
        if (saldo.items.containsKey(type)) {
            SaldoItem item = saldo.items.get(type);
            TextView textView = (TextView) findViewById(textViewId);
            ProgressBar progressBar = (ProgressBar) findViewById(progressBarId);
            panel.setVisibility(View.VISIBLE);
            if (item.isUnlimited() && !settings.showFribrukQuota()) {
                progressBar.setVisibility(View.GONE);
                textView.setText("FriBRUK");
            } else {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(item.total);
                progressBar.setProgress(item.balance);
                textView.setText(String.format("%d av %d %s", item.balance, item.total, type.unitSuffix));
            }
        } else {
            panel.setVisibility(View.GONE);
        }
    }

    private class UpdateCompleteBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("CHESS_SALDO", "Received saldo update broadcast.");
            updateUI();
        }
    }
}
