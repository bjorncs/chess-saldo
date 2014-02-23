package com.chess.saldo;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.bcseime.android.chess.saldo2.R;
import com.chess.saldo.service.ChessService;
import com.chess.saldo.service.Saldo;

public class UpdateService extends IntentService {

    public static final String UPDATE_COMPLETE_ACTION = "com.chess.saldo.UPDATE_COMPLETE";
    public static final String SHOW_TOAST = "com.chess.saldo.SHOW_TOAST";

    private Handler handler;
    private Settings settings;
    private ChessService service;

    public UpdateService() {
        super("ChessSaldoUpdateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        settings = new Settings(this);
        service = new ChessService(this, settings.getUsername(), settings.getPassword());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("CHESS_SALDO", "Update service was invoked by intent.");
        boolean showToast = intent.getBooleanExtra(SHOW_TOAST, false);

        if (!settings.isUserCredentialsSet()) {
            postToast(getString(R.string.credentials_missing));
            Log.d("CHESS_SALDO", "Username and/or password missing.");
            return;
        }

        if (showToast) {
            postToast(getString(R.string.updating));
        }
        try {
            service.setUsername(settings.getUsername());
            service.setPassword(settings.getPassword());
            Saldo saldo = service.fetchSaldo();
            settings.setSaldo(saldo);
            Log.d("CHESS_SALDO", String.format("Saldo info updated: %s", saldo.toString()));
            WidgetProviders.updateAllWidgets(getApplicationContext());

            if (showToast) {
                postToast(getString(R.string.update_finished));
            }
            Log.d("CHESS_SALDO", "Sending saldo update broadcast.");
            sendBroadcast(new Intent(UPDATE_COMPLETE_ACTION));

        } catch (Exception e) {
            Log.w("CHESS_SALDO", e.toString());
            if (showToast) {
                postToast(e.getMessage());
            }
        }
    }

    private void postToast(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UpdateService.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
