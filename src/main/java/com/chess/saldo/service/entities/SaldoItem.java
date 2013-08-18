package com.chess.saldo.service.entities;

/**
 * Created by bjorncs on 8/18/13.
 */
public class SaldoItem {
    public final int balance;
    public final int total;
    public final SaldoType type;

    public SaldoItem(int balance, int total, SaldoType type) {
        this.balance = balance;
        this.total = total;
        this.type = type;
    }

    public final float getProgress() {
        return total > 0 ? balance / (float) total : 0;
    }

    public boolean isUnlimited() {
        return total == 20000;
    }
}
