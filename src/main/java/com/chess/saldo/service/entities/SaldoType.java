package com.chess.saldo.service.entities;

/**
 * Created by bjorncs on 10.06.13.
 */
public enum SaldoType {
    MONEY("Forbruk", "Money", "KR"),
    SMS("SMS", "SMS", "SMS"),
    MMS("MMS", "MMS", "MMS"),
    MINUTES("Minutter", "CallTime", "MIN"),
    DATA("Data", "GPRS", "MB");

    public final String prettyName;
    public final String apiName;
    public final String unitSuffix;

    SaldoType(String prettyName, String apiName, String unitSuffix) {
        this.prettyName = prettyName;
        this.apiName = apiName;
        this.unitSuffix = unitSuffix;
    }

    public static String[] prettyNames() {
        String[] names = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            names[i] = values()[i].prettyName;
        }
        return names;
    }

    public static SaldoType fromString(String name) {
        for (SaldoType value : values()) {
            if (value.apiName.equals(name)) return value;
        }
        throw new IllegalArgumentException("Invalid saldo type: " + name);
    }

    public static SaldoType fromPrettyString(String name) {
        for (SaldoType value : values()) {
            if (value.prettyName.equals(name)) return value;
        }
        throw new IllegalArgumentException("Invalid saldo type: " + name);
    }
}
