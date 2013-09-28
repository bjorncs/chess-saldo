package com.chess.saldo.service.entities;

import java.util.Map;
import java.util.TreeMap;

public class Saldo {

    public final Map<SaldoType, SaldoItem> items = new TreeMap<SaldoType, SaldoItem>();
    public final String moneyUsed;

    public Saldo(String moneyUsed) {
        this.moneyUsed = moneyUsed;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(String.format("Saldo: {money='%s' items=[", moneyUsed));
        for (SaldoItem item : items.values()) {
            b.append(String.format("%s={balance=%d total=%d} ", item.type.apiName, item.balance, item.total));
        }
        b.append("]}");
        return b.toString();
    }

    public final int parseMoneyUsed() {
        if (moneyUsed.length() == 0) return 0;
        return Math.round(Float.parseFloat(moneyUsed.substring(3).replace(",", ".")));
    }
}
