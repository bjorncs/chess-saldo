package com.chess.saldo.service;

import android.content.Context;

import com.bcseime.android.chess.saldo2.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyStore;

public class ChessService {
    private static final int CONN_TIMEOUT = 14 * 1000;
    private static final int SOCKET_TIMEOUT = 24 * 1000;

    private final DefaultHttpClient client;
    private final Context context;
    private String username;
    private String password;

    public ChessService(Context context, String username, String password) {
        this(context);
        this.username = username;
        this.password = password;
    }

    public ChessService(Context context) {
        this.client = createHttpClient();
        this.context = context.getApplicationContext();
    }

    public synchronized Saldo fetchSaldo() throws IOException, ChessServiceException {
        HttpGet request = createRequest();
        HttpResponse response = client.execute(request);
        return parseResponse(response);
    }

    private HttpGet createRequest() {
        HttpGet httpGet = new HttpGet("https://217.68.103.239/mobile/mobile.asmx/GetUsageSaldo");
        httpGet.setHeader("username", username);
        httpGet.setHeader("User-Agent", "");
        httpGet.setHeader("password", password);
        httpGet.setHeader("Content-Type", "application/json; charset=utf-8");
        httpGet.setHeader("X-Requested-With", "XMLHttpRequest");
        httpGet.setHeader("locale", "nb-NO");
        httpGet.setHeader("Host", "minside.chess.no");
        httpGet.setHeader("Connection", "close");
        return httpGet;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private static DefaultHttpClient createHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new CarelessSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            BasicHttpParams basicHttpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(basicHttpParams, CONN_TIMEOUT);
            HttpConnectionParams.setSoTimeout(basicHttpParams, SOCKET_TIMEOUT);

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(basicHttpParams, registry);

            return new DefaultHttpClient(ccm, basicHttpParams);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    private Saldo parseResponse(HttpResponse response) throws IOException, ChessServiceException {
        try {
            String jsonText = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            response.getEntity().consumeContent();
            JSONObject responseObject = unwrapResponseObject(jsonText);
            Saldo saldo = new Saldo(responseObject);
            if (!saldo.isLoginSuccessful()) {
                throw new ChessServiceException(saldo.getErrorMessage());
            }
            return saldo;
        } catch (JSONException e) {
            throw new ChessServiceException(context.getString(R.string.server_down_err_msg), e);
        }
    }

    private static JSONObject unwrapResponseObject(String text) throws JSONException {
        JSONObject wrapper = new JSONObject(text);
        String rawObj = wrapper.getString("d");
        return new JSONObject(rawObj.replace("\\", ""));
    }

}
