package com.chess.saldo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chess.saldo.R;
import com.chess.saldo.service.ChessSaldoService;
import com.chess.saldo.service.Saldo;

public class MainActivity extends Activity {

	private static final int SHOW_PREFERENCES = 1337;
	
	private AppSettings settings;
	private volatile UpdateTask updateTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.settings = new AppSettings(getApplicationContext());
		setContentView(R.layout.main_layout);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!settings.isUserCredentialsSet()) {
			showPreferenceActivity();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		fetchData();
		updateUI();
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case R.id.settings_item:
				showPreferenceActivity();
				return true;

			case R.id.update_item:
				fetchData();
				return true;
				
		}
		return false;
	}
	
	private void fetchData() {
		if (updateTask == null || updateTask.getStatus() == AsyncTask.Status.FINISHED) {
			updateTask = new UpdateTask();
			updateTask.execute((Void)null);
		}
	}
	
	private void showPreferenceActivity() {
		Intent intent = new Intent(this, AppPreferenceActivity.class);
		startActivityForResult(intent, SHOW_PREFERENCES);
	}
	
	private void updateUI() {
		Saldo saldo = settings.getSaldo();
		TextView cashTextView = (TextView) findViewById(R.id.cashValue);
		cashTextView.setText(saldo.getMoneyUsed());
		updateSaldoItem(R.id.dataValue, R.id.dataProgress, (int)saldo.parseDataLeft(), (int) saldo.parseDataTotal(), "MB");
		updateSaldoItem(R.id.minutesValue, R.id.minutesProgress, saldo.parseMinutesLeft(), saldo.parseMinutesTotal(), "min");
		updateSaldoItem(R.id.mmsValue, R.id.mmsProgress, saldo.parseMmsLeft(), saldo.parseMmsTotal(), "mms");
		updateSaldoItem(R.id.smsValue, R.id.smsProgress, saldo.parseSmsLeft(), saldo.parseSmsTotal(), "sms");
		
	}
	
	private void updateSaldoItem(int textViewId, int progressBarId, int progressValue, int progressMax, String unit) {
		ProgressBar progressBar = (ProgressBar) findViewById(progressBarId);
		TextView textView = (TextView) findViewById(textViewId);
		progressBar.setMax(progressMax);
		progressBar.setProgress(progressValue);
		if (progressValue == 0 && progressMax == 0) {
			textView.setText("-");
		} else {
			textView.setText(String.format("%d av %d %s", progressValue, progressMax, unit));
		}
	}
	
	private class UpdateTask extends AsyncTask<Void, Void, Exception> {

		private final ProgressDialog dialog;
		
		private UpdateTask() {
			dialog = ProgressDialog.show(MainActivity.this, "", "Laster ned saldo...");
			dialog.show();
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
			try  {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			} catch (Exception ex) {}
			if (exp != null) {
				String errorMsg = "Chess Saldo:\n" + exp.getClass().getSimpleName() + ": " + exp.getMessage();
				Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
			}
			WidgetProvider.updateAllWidgets(getApplicationContext());
			updateUI();
		}
	}

}
