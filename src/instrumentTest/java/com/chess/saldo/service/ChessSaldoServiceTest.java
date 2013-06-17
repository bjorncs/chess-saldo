package com.chess.saldo.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.bcseime.android.chess.saldo2.test.R;
import com.chess.saldo.service.entities.Saldo;
import junit.framework.Assert;
import android.test.ActivityTestCase;

import com.chess.saldo.service.ServiceException.Type;

public class ChessSaldoServiceTest extends ActivityTestCase {

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
		Saldo saldo = new ChessSaldoService(username, password).fetchSaldo();
		assertNotNull(saldo);
        assertNotNull(saldo.strMoneyUsed);
	}

	public void testInvalidUsername() throws IOException {
		try {
			new ChessSaldoService("invalid_username", password).fetchSaldo();
			Assert.fail("Service should throw exception on wrong username");

		} catch (ServiceException ex) {
			Assert.assertEquals(Type.LoginProblem, ex.getType());
		}
	}

	public void testInvalidPassword() throws IOException {
		try {
			new ChessSaldoService(username, "wrong_password").fetchSaldo();
			Assert.fail("Service should throw exception on wrong password");
		} catch (ServiceException ex) {
			Assert.assertEquals(Type.LoginProblem, ex.getType());
		}

	}

}
