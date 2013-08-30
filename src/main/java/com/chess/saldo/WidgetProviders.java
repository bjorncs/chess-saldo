package com.chess.saldo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import com.bcseime.android.chess.saldo2.R;
import com.chess.saldo.service.entities.Saldo;
import com.chess.saldo.service.entities.SaldoItem;
import com.chess.saldo.service.entities.SaldoType;

import java.util.Arrays;

/**
 * Created by bjorncs on 08.06.13.
 */
public abstract class WidgetProviders extends AppWidgetProvider {

    public static class Large extends WidgetProviders {

        private static RemoteViews updateWidgetView(Context context) {
            SettingsManager settings = new SettingsManager(context);
            Saldo saldo = settings.getSaldo();
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.large_widget_layout);
            updateSaldoItem(views, R.id.dataProgress, R.id.dataProgressContainer, R.id.dataValue, saldo, SaldoType.DATA, settings);
            updateSaldoItem(views, R.id.minutesProgress, R.id.minutesProgressContainer, R.id.minutesValue, saldo, SaldoType.MINUTES, settings);
            updateSaldoItem(views, R.id.mmsProgress, R.id.mmsProgressContainer, R.id.mmsValue, saldo, SaldoType.MMS, settings);
            updateSaldoItem(views, R.id.smsProgress, R.id.smsProgressContainer, R.id.smsValue, saldo, SaldoType.SMS, settings);

            Log.d("CHESS_SALDO", "Widget views updated with saldo information");
            if (saldo.moneyUsed.isEmpty()) {
                views.setTextViewText(R.id.cashValue, "-");
            } else {
                views.setTextViewText(R.id.cashValue, Integer.toString(saldo.parseMoneyUsed()));
            }
            Intent i = new Intent(context.getApplicationContext(), MainActivity.class);
            PendingIntent updateServiceIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_layout, updateServiceIntent);
            return views;
        }

        @Override
        protected RemoteViews getUpdatedRemoteView(Context context, int widgetId) {
            return updateWidgetView(context);
        }
    }

    public static class Small extends WidgetProviders {

        private static RemoteViews updateWidgetView(Context context, int widgetId) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.small_widget_layout);

            SettingsManager settings = new SettingsManager(context);
            Saldo saldo = settings.getSaldo();
            SaldoType type = settings.getWidgetType(widgetId);

            remoteViews.setTextViewText(R.id.lblSaldoType, type.unitSuffix);
            if (type == SaldoType.MONEY) {
                remoteViews.setTextViewText(R.id.lblSaldoValue, Integer.toString(saldo.parseMoneyUsed()));
                remoteViews.setViewVisibility(R.id.pgrSaldoContainer, View.INVISIBLE);
            } else {
                updateSaldoItem(remoteViews, R.id.pgrSaldo, R.id.pgrSaldoContainer, R.id.lblSaldoValue, saldo, type, settings);
            }

            Intent i = new Intent(context.getApplicationContext(), MainActivity.class);
            PendingIntent updateServiceIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_layout, updateServiceIntent);
            return remoteViews;
        }

        @Override
        protected RemoteViews getUpdatedRemoteView(Context context, int widgetId) {
            return updateWidgetView(context, widgetId);
        }
    }

    // Progress bar containter is workaround for issue 11040 (https://code.google.com/p/android/issues/detail?id=11040)
    private static void updateSaldoItem(RemoteViews remoteViews, int progressId, int progressContainerId, int textId, Saldo saldo, SaldoType type, SettingsManager settings) {
        if (saldo.items.containsKey(type)) {
            SaldoItem item = saldo.items.get(type);
            if (item.isUnlimited() && !settings.showFribrukQuota()) {
                remoteViews.setTextViewText(textId, "FRI");
                remoteViews.setViewVisibility(progressContainerId, View.INVISIBLE);
            } else {
                remoteViews.setTextViewText(textId, Integer.toString(item.balance));
                remoteViews.setViewVisibility(progressContainerId, View.VISIBLE);
                remoteViews.setProgressBar(progressId, item.total, item.balance, false);
            }
        } else {
            remoteViews.setTextViewText(textId, "-");
            remoteViews.setViewVisibility(progressContainerId, View.INVISIBLE);
        }
    }


    public static void updateAllWidgets(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        RemoteViews largeView = Large.updateWidgetView(context);
        int[] largeIds = manager.getAppWidgetIds(new ComponentName(context, Large.class));
        for (int appWidgetId : largeIds) {
            manager.updateAppWidget(appWidgetId, largeView);
        }


        int[] smallIds = manager.getAppWidgetIds(new ComponentName(context, Small.class));
        for (int appWidgetId : smallIds) {
            RemoteViews smallView = Small.updateWidgetView(context, appWidgetId);
            manager.updateAppWidget(appWidgetId, smallView);
        }
    }

    public static boolean hasWidgets(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        return manager.getAppWidgetIds(new ComponentName(context, Large.class)).length > 0 ||
                manager.getAppWidgetIds(new ComponentName(context, Small.class)).length > 0;
    }

    protected abstract RemoteViews getUpdatedRemoteView(Context context, int widgetId);

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d("CHESS_SALDO", "OnDeleted() called on widget provider, widgetIds = " + Arrays.toString(appWidgetIds));
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d("CHESS_SALDO", "OnDisabled() called on widget provider");
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d("CHESS_SALDO", "OnEnabled() called on widget provider");
        ConnectivityBroadcastReceiver.setUpdateAlarm(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d("CHESS_SALDO", "OnUpdate() was called for widgets");
        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = getUpdatedRemoteView(context, widgetId);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
