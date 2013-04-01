package com.chess.saldo;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.chess.saldo.service.ChessSaldoService;
import com.chess.saldo.service.Saldo;

public class UpdateService extends Service {

	private static volatile WidgetUpdater widgetUpdater;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleCommand(intent);
		return Service.START_NOT_STICKY;
	}

	private void handleCommand(Intent intent) {
		Log.d("CHESS_SALDO", "Update service was invoked by intent");
		if (widgetUpdater == null
				|| widgetUpdater.getStatus() == AsyncTask.Status.FINISHED) {
			Log.d("CHESS_SALDO", "Update thread is not running, starting a new thread");
			widgetUpdater = new WidgetUpdater();
			widgetUpdater.execute((Void) null);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class WidgetUpdater extends AsyncTask<Void, Void, Exception> {

		private final AppSettings settings = new AppSettings(getApplicationContext());

		private WidgetUpdater() {
			if (!settings.isUserCredentialsSet()) {
				Toast.makeText(getApplicationContext(),
						"Chess Saldo:\nBrukernavn eller passord mangler.",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected Exception doInBackground(Void... dummy) {
			try {
				
				ChessSaldoService service = new ChessSaldoService(settings.getUsername(), settings.getPassword());
				Saldo saldo = service.fetchSaldo();
				settings.setSaldo(saldo);
				Log.d("CHESS_SALDO", String.format("Saldo info updated: data=%f/%f, minutes=%d/%d, mms=%d/%d, sms=%d/%d", 
						saldo.parseDataLeft(), saldo.parseDataTotal(), saldo.parseMinutesLeft(), saldo.parseMinutesTotal(), 
						saldo.parseMmsLeft(), saldo.parseMmsTotal(), saldo.parseSmsLeft(), saldo.parseSmsTotal()));
				
			} catch (Exception e) {
				return e;
			}
			return null;

		}

		@Override
		protected void onPostExecute(Exception exp) {
			try {
				WidgetProvider.updateAllWidgets(getApplicationContext());
				if (exp != null) {
					 Log.w("CHESS_SALDO", exp.toString());
				}
			} finally {
				stopSelf();
			}
		}
	}
}
