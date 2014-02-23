package com.chess.saldo.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.Assert;
import android.test.ActivityTestCase;

import com.bcseime.android.chess.saldo2.test.R;

public class ChessServiceTest extends ActivityTestCase {

    private String username;
    private String password;

    @Override
    protected void setUp() throws Exception {
        loadUsernameAndPassord();
    }

    private void loadUsernameAndPassord() throws IOException {
        Properties p = new Properties();
        InputStream in = null;
        try {
            in = getInstrumentation().getContext().getResources().openRawResource(R.raw.user_credentials);
            p.load(in);
            username = p.getProperty("username");
            password = p.getProperty("password");
            if (username == null || password == null) {
                throw new IllegalStateException(
                        "Username and/or password is not set in src/instrumentTest/java/res/raw/user_credentials.properties");
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
        }

    }

    public void testGetQuotaInfo() throws Exception {
        Saldo saldo = new ChessService(getInstrumentation().getContext(), username, password).fetchSaldo();
        assertNotNull(saldo);
        assertTrue(saldo.hasPots());
        assertNotSame(saldo.getUsageSaldo(), "-");
        assertFalse(saldo.getPots().isEmpty());
    }

    public void testInvalidUsername() throws IOException {
        try {
            new ChessService(getInstrumentation().getContext(), "invalid_username", password).fetchSaldo();
            Assert.fail("Service should throw exception on wrong username");
        } catch (ChessServiceException e) {
            assertFalse(e.getMessage().isEmpty());
        }
    }

    public void testInvalidPassword() throws IOException {
        try {
            new ChessService(getInstrumentation().getContext(), username, "wrong_password").fetchSaldo();
            Assert.fail("Service should throw exception on wrong password");
        } catch (ChessServiceException e) {
            assertFalse(e.getMessage().isEmpty());
        }
    }

}
