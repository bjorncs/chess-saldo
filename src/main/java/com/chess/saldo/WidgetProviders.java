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
import com.chess.saldo.service.Saldo;

import java.util.Arrays;

public abstract class WidgetProviders extends AppWidgetProvider {

    public static class Large extends WidgetProviders {

        private static RemoteViews updateWidgetView(Context context) {
            Settings settings = new Settings(context);
            Saldo saldo = settings.getSaldo();
            RemoteViews root = new RemoteViews(context.getPackageName(), R.layout.large_widget_layout);

            if (saldo != null) {
                root.removeAllViews(R.id.pot_container);
                RemoteViews moneyView = new RemoteViews(context.getPackageName(), R.layout.widget_pot_item);
                setMoneyConsumption(context, moneyView, saldo.getUsageSaldoAsInt());
                root.addView(R.id.pot_container, moneyView);

                if (saldo.hasPots()) {
                    boolean showFribruk = settings.showFribruk();
                    for (Saldo.Pot pot : saldo.getPots()) {
                        RemoteViews divider = new RemoteViews(context.getPackageName(), R.layout.widget_divider);
                        root.addView(R.id.pot_container, divider);

                        RemoteViews potView = new RemoteViews(context.getPackageName(), R.layout.widget_pot_item);
                        setPot(context, potView, pot, showFribruk);
                        root.addView(R.id.pot_container, potView);
                    }
                }

                Log.d("CHESS_SALDO", "Widget root updated with saldo information");
            }

            Intent i = new Intent(context.getApplicationContext(), MainActivity.class);
            PendingIntent updateServiceIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            root.setOnClickPendingIntent(R.id.pot_container, updateServiceIntent);
            return root;
        }

        @Override
        protected RemoteViews getUpdatedRemoteView(Context context, int widgetId) {
            return updateWidgetView(context);
        }
    }

    public static class Small extends WidgetProviders {

        private static RemoteViews updateWidgetView(Context context, int widgetId) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.small_widget_layout);
            Settings settings = new Settings(context);
            Saldo saldo = settings.getSaldo();
            String type = settings.getWidgetType(widgetId);

            if (saldo != null) {
                if (type.equalsIgnoreCase("money")) {
                    setMoneyConsumption(context, rv, saldo.getUsageSaldoAsInt());
                } else {
                    Saldo.Pot pot = saldo.getPot(type);
                    if (pot != null) {
                        setPot(context, rv, pot, settings.showFribruk());
                    }
                }
            }

            Intent i = new Intent(context.getApplicationContext(), MainActivity.class);
            PendingIntent updateServiceIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.pot_container, updateServiceIntent);
            return rv;
        }

        @Override
        protected RemoteViews getUpdatedRemoteView(Context context, int widgetId) {
            return updateWidgetView(context, widgetId);
        }
    }

    // Progress bar container is workaround for issue 11040 (https://code.google.com/p/android/issues/detail?id=11040)
    private static void setMoneyConsumption(Context context, RemoteViews rv, int moneyConsumption) {
        rv.setViewVisibility(R.id.pot_progress_container, View.GONE);
        rv.setTextViewText(R.id.pot_value, moneyConsumption != -1 ? Integer.toString(moneyConsumption) : "-");
        rv.setTextViewText(R.id.pot_unit, context.getString(R.string.kroner_unit));
    }

    // Progress bar container is workaround for issue 11040 (https://code.google.com/p/android/issues/detail?id=11040)
    private static void setPot(Context context, RemoteViews rv, Saldo.Pot pot, boolean showFribruk) {
        if (pot.freeUsage && !showFribruk) {
            rv.setTextViewText(R.id.pot_value, context.getString(R.string.fribruk_widget));
            rv.setViewVisibility(R.id.pot_progress_container, View.GONE);
        } else {
            rv.setTextViewText(R.id.pot_value, Integer.toString(pot.balance));
            rv.setProgressBar(R.id.pot_progress, pot.total, pot.balance, false);
        }
        rv.setTextViewText(R.id.pot_unit, pot.unit);
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
