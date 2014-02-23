package com.chess.saldo.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Saldo {

    public Saldo(JSONObject rawData) {
        this.rawData = rawData;
    }

    private final JSONObject rawData;

    public String getUsageSaldo() {
        return rawData.optString("UsageSaldo", "-");
    }

    public int getUsageSaldoAsInt() {
        String usage = getUsageSaldo();
        if (usage.length() < 4) return -1;
        return Math.round(Float.parseFloat(usage.substring(3).replace(",", ".")));
    }

    public boolean hasPots() {
       return rawData.optBoolean("HasPots", false);
    }

    public String getErrorMessage() {
        return rawData.optString("ErrorMessage", "");
    }

    public boolean isLoginSuccessful() {
        return rawData.optBoolean("LoginSuccessful", false);
    }

    public List<Pot> getPots() {
        List<Pot> pots = new ArrayList<>();
        if (hasPots())
        {
            JSONArray jsonPots = rawData.optJSONArray("Pots");
            for (int i = 0; i < jsonPots.length(); i++) {
                JSONObject jsonPot = jsonPots.optJSONObject(i);
                pots.add(toPot(jsonPot));
            }
        }
        return pots;
    }

    public Pot getPot(String type) {
        if (!hasPots()) return null;
        JSONArray jsonPots = rawData.optJSONArray("Pots");
        for (int i = 0; i < jsonPots.length(); i++) {
            JSONObject jsonPot = jsonPots.optJSONObject(i);
            if (jsonPot.optString("Type").equalsIgnoreCase(type)) {
                return toPot(jsonPot);
            }
        }
        return null;
    }

    private Pot toPot(JSONObject jsonPot) {
        return new Pot(
                jsonPot.optInt("Balance", 0),
                jsonPot.optInt("Total", 0),
                jsonPot.optString("Type", "?"),
                jsonPot.optString("TypeDescription", "-"),
                jsonPot.optString("Unit", "?"),
                jsonPot.optBoolean("FreeUsage", false),
                jsonPot.optInt("SortNo", 1)
        );
    }

    public JSONObject getRawData() {
        return rawData;
    }

    public static class Pot {
        public final int balance;
        public final int total;
        public final String type;
        public final String typeDescription;
        public final String unit;
        public final boolean freeUsage;
        public final int sortNo;

        public Pot(int balance, int total, String type, String typeDescription, String unit, boolean freeUsage, int sortNo) {
            this.balance = balance;
            this.total = total;
            this.type = type;
            this.typeDescription = typeDescription;
            this.unit = unit;
            this.freeUsage = freeUsage;
            this.sortNo = sortNo;
        }
    }
}
