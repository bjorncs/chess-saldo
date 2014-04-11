package com.chess.saldo;

import android.app.Application;

import com.bcseime.android.chess.saldo2.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import static org.acra.ReportField.ANDROID_VERSION;
import static org.acra.ReportField.APP_VERSION_CODE;
import static org.acra.ReportField.APP_VERSION_NAME;
import static org.acra.ReportField.BUILD;
import static org.acra.ReportField.CUSTOM_DATA;
import static org.acra.ReportField.INSTALLATION_ID;
import static org.acra.ReportField.PACKAGE_NAME;
import static org.acra.ReportField.REPORT_ID;
import static org.acra.ReportField.STACK_TRACE;

@ReportsCrashes(
        formKey = "",
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUri = "http://162.243.199.107:5984/acra-chess/_design/acra-storage/_update/report",
        formUriBasicAuthLogin = "appuser",
        formUriBasicAuthPassword = "cafebabe",
        customReportContent = {APP_VERSION_CODE, APP_VERSION_NAME, BUILD, PACKAGE_NAME, ANDROID_VERSION, CUSTOM_DATA, STACK_TRACE, REPORT_ID, INSTALLATION_ID}
)
public class ChessApplication extends Application {

    private static ChessApplication instance;
    private Tracker analyticsTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        analyticsTracker = GoogleAnalytics.getInstance(this).newTracker(R.xml.analytics);
        instance = this;
    }

    public static ChessApplication getInstance() {
        return instance;
    }

    public Tracker getAnalyticsTracker() {
        return analyticsTracker;
    }
}
