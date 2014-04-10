package com.chess.saldo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.bcseime.android.chess.saldo2.R;
import com.chess.saldo.service.Saldo;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends Activity {

    private UpdateCompleteBroadcastReceiver receiver;
    private Settings settings;
    @InjectView(R.id.main) LinearLayout viewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.settings = new Settings(getApplicationContext());
        setContentView(R.layout.main_layout);
        ButterKnife.inject(this);
        if (!settings.isUserCredentialsSet()) {
            showPreferenceActivity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(UpdateService.UPDATE_COMPLETE_ACTION);
        receiver = new UpdateCompleteBroadcastReceiver();
        registerReceiver(receiver, filter);
        updateSaldo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.settings_item:
                showPreferenceActivity();
                return true;

            case R.id.update_item:
                updateSaldo();
                return true;

        }
        return false;
    }

    private void updateSaldo() {
        Intent intent = new Intent(this, UpdateService.class);
        intent.putExtra(UpdateService.SHOW_TOAST, true);
        startService(intent);
    }

    private void showPreferenceActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void updateUI() {
        Saldo saldo = settings.getSaldo();
        boolean showFribruk = settings.showFribruk();
        if (saldo != null) {
            viewContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(this);

            // Add money consumption
            View moneyView = inflater.inflate(R.layout.pot_item, viewContainer, false);
            PotViewHolder moneyHolder = new PotViewHolder(moneyView);
            moneyHolder.progress.setVisibility(View.GONE);
            moneyHolder.name.setText(R.string.money_consumption);
            moneyHolder.value.setText(saldo.getUsageSaldo());
            viewContainer.addView(moneyView);

            List<Saldo.Pot> pots = saldo.getPots();
            for (Saldo.Pot pot : pots) {
                View potView = inflater.inflate(R.layout.pot_item, viewContainer, false);
                PotViewHolder potHolder = new PotViewHolder(potView);
                potHolder.name.setText(pot.typeDescription);
                if (pot.freeUsage && !showFribruk) {
                    potHolder.value.setText(R.string.fribruk);
                    potHolder.progress.setVisibility(View.GONE);
                } else {
                    potHolder.progress.setMax(pot.total);
                    if (settings.showConsumption()) {
                        potHolder.value.setText(String.format("%d of %d %s", pot.total - pot.balance, pot.total, pot.unit));
                        potHolder.progress.setProgress(pot.total - pot.balance);
                    } else {
                        potHolder.value.setText(String.format("%d of %d %s", pot.balance, pot.total, pot.unit));
                        potHolder.progress.setProgress(pot.balance);
                    }
                }
                viewContainer.addView(potView);
            }
        }
    }


    private class UpdateCompleteBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("CHESS_SALDO", "Received saldo update broadcast.");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUI();
                }
            });
        }
    }
}
