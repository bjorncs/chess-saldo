package com.chess.saldo;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.bcseime.android.chess.saldo2.R;
import com.chess.saldo.service.Saldo;

import java.util.List;

public class WidgetConfigureActivity extends Activity {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = new Settings(getApplicationContext());
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
        Saldo saldo = settings.getSaldo();
        if (saldo == null) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.saldo_missing_title)
                    .setMessage(R.string.saldo_missing_desc)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(new Intent(WidgetConfigureActivity.this, SettingsActivity.class), mAppWidgetId);
                        }
                    })
                    .show();
        } else {
            List<Saldo.Pot> pots = saldo.getPots();
            int size = pots.size() + 1;
            final String[] types = new String[size];
            String[] typesPrettyNames = new String[size];

            types[0] = "money";
            typesPrettyNames[0] = getString(R.string.money_consumption);

            for (int i = 0; i < pots.size(); i++) {
                Saldo.Pot pot = pots.get(i);
                types[i + 1] = pot.type;
                typesPrettyNames[i + 1] = pot.typeDescription;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Type")
                    .setItems(typesPrettyNames, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            settings.setWidgetType(types[i], mAppWidgetId);
                            returnSuccess();
                        }
                    })
                    .show();

        }
    }

    private void returnSuccess() {
        WidgetProviders.updateAllWidgets(this);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

}
