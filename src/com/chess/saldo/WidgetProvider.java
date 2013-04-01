package com.chess.saldo;

import java.util.Arrays;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import com.chess.saldo.R;
import com.chess.saldo.service.Saldo;

public class WidgetProvider extends AppWidgetProvider {

	private volatile static PendingIntent alarmIntent;

	public static RemoteViews updateWidgetView(Context context) {
		Saldo saldo = new AppSettings(context).getSaldo();
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		updateSaldoItem(views, R.id.dataProgress, R.id.dataValue, (int) saldo.parseDataLeft(), (int) saldo.parseDataTotal());
		updateSaldoItem(views, R.id.minutesProgress, R.id.minutesValue, saldo.parseMinutesLeft(), saldo.parseMinutesTotal());
		updateSaldoItem(views, R.id.mmsProgress, R.id.mmsValue, saldo.parseMmsLeft(), saldo.parseMmsTotal());
		updateSaldoItem(views, R.id.smsProgress, R.id.smsValue, saldo.parseSmsLeft(), saldo.parseSmsTotal());
		
		Log.d("CHESS_SALDO", "Widget views updated with saldo information");
		if (saldo.parseMoneyUsed() == -1) {
			views.setTextViewText(R.id.cashValue, "-");
		} else {
			views.setTextViewText(R.id.cashValue, Integer.toString((int)saldo.parseMoneyUsed()));
		}
 		Intent i = new Intent(context.getApplicationContext(), MainActivity.class);        
		PendingIntent updateServiceIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.widget_layout, updateServiceIntent);
		return views;
	}
	
	public static void updateAllWidgets(Context context) {
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		RemoteViews view = updateWidgetView(context);
		int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
		for (int appWidgetId : appWidgetIds) {
			manager.updateAppWidget(appWidgetId, view);
		}
	}
	
	private static void updateSaldoItem(RemoteViews remoteViews, int progressId, int textId, int progressValue, int progressMax) {
		if (progressValue == 0 && progressMax == 0) {
			remoteViews.setTextViewText(textId, "-");
		} else {
			remoteViews.setTextViewText(textId, Integer.toString(progressValue));
		}
		remoteViews.setProgressBar(progressId, progressMax, progressValue, false);	
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d("CHESS_SALDO", "OnDeleted() called on widget provider, widgetIds = " + Arrays.toString(appWidgetIds));
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Log.d("CHESS_SALDO", "OnDisabled() called on widget provider");
		if (alarmIntent != null) {
			AlarmManager amng = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			amng.cancel(alarmIntent);
			alarmIntent = null;
		}
		new AppSettings(context).setWidgetEnabled(false);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.d("CHESS_SALDO", "OnEnabled() called on widget provider");
		setUpdateAlarm(context);
		new AppSettings(context).setWidgetEnabled(true);
	}
	
	public static void setUpdateAlarm(Context context) {
		int updateFreq = new AppSettings(context).getUpdateFrequency();
		
		AlarmManager amng = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (alarmIntent != null) {
			Log.d("CHESS_SALDO", "Alarm was already registered, canceling the old one");
			amng.cancel(alarmIntent);
		}

		Intent intent = new Intent(context, UpdateService.class);
		alarmIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Log.d("CHESS_SALDO", "Registering an alarm with update frequency: " + updateFreq);
		amng.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + updateFreq, updateFreq, alarmIntent);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			AppSettings settings = new AppSettings(context);
			boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			boolean shouldUpdate = settings.shouldUpdate();
			Log.d("CHESS_SALDO", String.format("ConnectivityManager.CONNECTIVITY_ACTION: noConnectivity=%b, shouldUpdate=%b", 
					noConnectivity, shouldUpdate));
			if (!noConnectivity && settings.shouldUpdate()) {
				ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = conMgr.getActiveNetworkInfo();
				Log.d("CHESS_SALDO", 
						info == null ? "NetworkInfo = null" : 
							String.format("NetworkInfo: type=%s, connectedOrConnecting=%b, connected=%b", 
									info.getTypeName(), info.isConnectedOrConnecting(), info.isConnected()));
				
				if (info != null && info.isConnectedOrConnecting()) {
					Log.d("CHESS_SALDO", "Invoking update service from wifi listener");
					context.startService(new Intent(context, UpdateService.class));
				}
				
			}	
		} else {
			super.onReceive(context, intent);			
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		new AppSettings(context).setWidgetEnabled(true);
		Log.d("CHESS_SALDO", "OnUpdate() was called for widgets");
		RemoteViews remoteViews = updateWidgetView(context);
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
	}
}
