package com.chess.saldo;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.chess.saldo.service.ChessSaldoService;
import com.chess.saldo.service.entities.Saldo;
import com.chess.saldo.service.entities.SaldoType;

public class WidgetConfigureActivity extends Activity {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private SettingsManager settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = new SettingsManager(getApplicationContext());
        setResult(RESULT_CANCELED);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        if (settings.isUserCredentialsSet()) {
            if (isSmallWidget()) {
                createListDialog();
            } else {
                returnSuccess();
            }
        } else {
            startActivityForResult(new Intent(this, SettingsActivity.class), mAppWidgetId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isSmallWidget()) {
            createListDialog();
        } else {
            returnSuccess();
        }
    }

    private boolean isSmallWidget() {
        AppWidgetManager mgr = AppWidgetManager.getInstance(this);
        AppWidgetProviderInfo info = mgr.getAppWidgetInfo(mAppWidgetId);
        return info.provider.compareTo(new ComponentName(this, WidgetProviders.Small.class)) == 0;
    }

    private void createListDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Type")
                .setItems(SaldoType.prettyNames(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SaldoType type = SaldoType.values()[i];
                        SettingsManager mgr = new SettingsManager(WidgetConfigureActivity.this);
                        mgr.setWidgetType(type, mAppWidgetId);
                        returnSuccess();
                    }
                })
                .show();
    }

    private void returnSuccess() {
        WidgetProviders.updateAllWidgets(this);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

}
