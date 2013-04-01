package com.chess.saldo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.chess.saldo.R;
import com.chess.saldo.service.ChessSaldoService;
import com.chess.saldo.service.Saldo;

public class ConfigureActivity extends Activity {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private AppSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = new AppSettings(getApplicationContext());
        setResult(RESULT_CANCELED);

        // Set the view layout resource to use.
        setContentView(R.layout.config_layout);

        addOKButton();
        addCancelButton();
        addSettingsButton();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    private void addSettingsButton(){
        Button settingsButton = (Button) findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent settingsActivity = new Intent(getBaseContext(), AppPreferenceActivity.class);
                startActivity(settingsActivity);
            }
        });
    }

    private void addOKButton() {
        Button okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new OnClickListener() {
            // @Override
            public void onClick(View v) {
                if (!settings.isUserCredentialsSet()) {
                    Toast.makeText(getApplicationContext(),
                            "Chess Saldo:\nBrukernavn eller passord mangler.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    ConnectionTask connTask = new ConnectionTask();
                    connTask.execute((Void) null);
                }
            }

        });
    }

    private void addCancelButton() {
        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new OnClickListener() {
            // @Override
            public void onClick(View v) {
                ConfigureActivity.this.onBackPressed();
            }
        });
    }

    private class ConnectionTask extends AsyncTask<Void, Void, Exception> {

        private ProgressDialog progressDialog;

        private ConnectionTask() {
            try {
                this.progressDialog = ProgressDialog
                        .show(ConfigureActivity.this, "Kobler til",
                                "Validerer brukernavn og passord", true, true);
            } catch (Exception e) {
            }
        }

        @Override
        protected Exception doInBackground(Void... dummy) {
            try {
                ChessSaldoService service = new ChessSaldoService(settings.getUsername(), settings.getPassword());
                Saldo saldo = service.fetchSaldo();
                settings.setSaldo(saldo);
                return null;
            } catch (Exception e) {
                return e;
            } finally {
                try  {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (Exception ex) {}
            }
        }

        @Override
        protected void onPostExecute(Exception exp) {
            if (exp != null) {
                String errorMsg = "Chess Saldo:\n" + exp.getClass().getSimpleName() + ": " + exp.getMessage();
                Toast.makeText(ConfigureActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }

            Intent intent = new Intent(getApplicationContext(),	UpdateService.class);
            startService(intent);

            WidgetProvider.setUpdateAlarm(getApplicationContext());
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    }

}
