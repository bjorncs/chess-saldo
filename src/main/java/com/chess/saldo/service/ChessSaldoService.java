package com.chess.saldo.service;

import com.chess.saldo.service.entities.Saldo;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

public class ChessSaldoService {
    private static final int CONN_TIMEOUT = 14 * 1000;
    private static final int SOCKET_TIMEOUT = 24 * 1000;


    private String username;
    private String password;
    private final DefaultHttpClient client;

    public ChessSaldoService(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    public ChessSaldoService() {
        this.client = createHttpClient();
    }

    public synchronized Saldo fetchSaldo() throws IOException, ServiceException {
        HttpGet request = createRequest();
        HttpResponse response = client.execute(request);

        return parseResponse(response);
    }

    private Saldo parseResponse(HttpResponse response) throws IOException, ServiceException {
        String htmlText = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
        response.getEntity().consumeContent();
        return SaldoParser.parsePage(htmlText);
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
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

}
