package com.chess.saldo.service.entities;

/**
 * Created by bjorncs on 10.06.13.
 */
public enum SaldoType {
    MONEY("Forbruk", "KR"),
    SMS("SMS", "SMS"),
    MMS("MMS", "MMS"),
    MINUTES("Minutter", "MIN"),
    DATA("Data", "MB");

    public final String prettyName;
    public final String widgetName;

    SaldoType(String prettyName, String widgetName) {
        this.prettyName = prettyName;
        this.widgetName = widgetName;
    }

    public static String[] prettyNames() {
        String[] names = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            names[i] = values()[i].prettyName;
        }
        return names;
    }

    public static String[] widgetName() {
        String[] names = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            names[i] = values()[i].widgetName;
        }
        return names;
    }

    public static SaldoType fromString(String name) {
        for (SaldoType value : values()) {
            if (value.prettyName.equals(name)) return value;
        }
        throw new IllegalArgumentException("Invalid saldo type: " + name);
    }
}
