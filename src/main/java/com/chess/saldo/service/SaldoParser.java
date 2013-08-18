package com.chess.saldo.service;

import com.chess.saldo.service.ServiceException.Type;
import com.chess.saldo.service.entities.Saldo;
import com.chess.saldo.service.entities.SaldoItem;
import com.chess.saldo.service.entities.SaldoType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaldoParser {

    public static Saldo parsePage(String text) throws ServiceException {
        try {
            JSONObject responseObject = unwrapResponseObject(text);
            validateLogin(responseObject);
            String moneyUsed = "";
            if (responseObject.has("UsageSaldo")) {
                moneyUsed = responseObject.getString("UsageSaldo");
            }
            Saldo saldo = new Saldo(moneyUsed);
            if (responseObject.getBoolean("HasPots")) {
                JSONArray pots = responseObject.getJSONArray("Pots");
                for (int i = 0; i < pots.length(); i++) {
                    JSONObject pot = pots.getJSONObject(i);
                    int balance = pot.getInt("Balance");
                    int total = pot.getInt("Total");
                    SaldoType type = SaldoType.fromString(pot.getString("Type"));
                    saldo.items.put(type, new SaldoItem(balance, total, type));
                }
            }
            return saldo;
        } catch (JSONException e) {
            throw new ServiceException("Ugyldig response fra server. Ser ut som Chess har problemer, prøv igjen senere!", Type.ParseProblem, e);
        }
    }

    private static JSONObject unwrapResponseObject(String text) throws JSONException {
        JSONObject wrapper = new JSONObject(text);
        String rawObj = wrapper.getString("d");
        return new JSONObject(rawObj.replace("\\", ""));
    }

    private static void validateLogin(JSONObject obj) throws ServiceException, JSONException {
        if (!obj.getBoolean("LoginSuccessful")) {
            throw new ServiceException(obj.getString("ErrorMessage"), Type.LoginProblem);
        }
    }

}
