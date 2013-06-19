package com.chess.saldo;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.chess.saldo.service.ChessSaldoService;
import com.chess.saldo.service.entities.Saldo;

public class UpdateService extends IntentService {

    public static final String UPDATE_COMPLETE_ACTION = "com.chess.saldo.UPDATE_COMPLETE";
    public static final String SHOW_TOAST = "com.chess.saldo.SHOW_TOAST";

    private Handler handler;

    public UpdateService() {
        super("ChessSaldoUpdateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SettingsManager settings = new SettingsManager(this);
        Log.d("CHESS_SALDO", "Update service was invoked by intent.");
        boolean showToast = intent.getBooleanExtra(SHOW_TOAST, false);

        if (!settings.isUserCredentialsSet()) {
            postToast("Chess Saldo:\nBrukernavn eller passord mangler.");
            Log.d("CHESS_SALDO", "Username and/or password missing.");
            return;
        }

        if (showToast) {
            postToast("Chess Saldo: Oppdaterer saldo...");
        }
        try {
            ChessSaldoService service = new ChessSaldoService(settings.getUsername(), settings.getPassword());
            Saldo saldo = service.fetchSaldo();
            settings.setSaldo(saldo);
            Log.d("CHESS_SALDO", String.format("Saldo info updated: data=%f/%f, minutes=%d/%d, mms=%d/%d, sms=%d/%d",
                    saldo.dataLeft, saldo.dataTotal, saldo.minutesLeft, saldo.minutesTotal,
                    saldo.mmsLeft, saldo.mmsTotal, saldo.smsLeft, saldo.smsTotal));
            WidgetProviders.updateAllWidgets(getApplicationContext());

            if (showToast) {
                postToast("Chess Saldo: Saldo oppdatert!");
            }
            Log.d("CHESS_SALDO", "Sending saldo update broadcast.");
            sendBroadcast(new Intent(UPDATE_COMPLETE_ACTION));

        } catch (Exception e) {
            Log.w("CHESS_SALDO", e.toString());
            if (showToast) {
                postToast("Chess Saldo:\n" + e.getMessage());
            }
        }
    }

    private void postToast(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UpdateService.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
