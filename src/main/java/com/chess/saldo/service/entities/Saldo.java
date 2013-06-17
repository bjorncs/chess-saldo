package com.chess.saldo.service.entities;

public class Saldo {

    public final int smsTotal;
    public final int smsLeft;
    public final int mmsTotal;
    public final int mmsLeft;
    public final int minutesTotal;
    public final int minutesLeft;
    public final float dataTotal;
    public final float dataLeft;
    public final float moneyUsed;
    public final String strMoneyUsed;

    public Saldo(int smsTotal, int smsLeft, int mmsTotal, int mmsLeft, int minutesTotal, int minutesLeft, float dataTotal, float dataLeft, float moneyUsed, String strMoneyUsed) {
        this.smsTotal = smsTotal;
        this.smsLeft = smsLeft;
        this.mmsTotal = mmsTotal;
        this.mmsLeft = mmsLeft;
        this.minutesTotal = minutesTotal;
        this.minutesLeft = minutesLeft;
        this.dataTotal = dataTotal;
        this.dataLeft = dataLeft;
        this.moneyUsed = moneyUsed;
        this.strMoneyUsed = strMoneyUsed;
    }

    public int getProgressFor(SaldoType type) {
        switch (type) {
            case DATA:
                return (int) dataLeft;
            case MINUTES:
                return minutesLeft;
            case MMS:
                return mmsLeft;
            case MONEY:
                return (int)moneyUsed;
            case SMS:
                return smsLeft;
            default:
                throw new RuntimeException("Undefined case for saldo type: " + type);
        }
    }

    public int getMaxFor(SaldoType type) {
        switch (type) {
            case DATA:
                return (int) dataTotal;
            case MINUTES:
                return minutesTotal;
            case MMS:
                return mmsTotal;
            case MONEY:
                return (int) moneyUsed;
            case SMS:
                return smsTotal;
            default:
                throw new RuntimeException("Undefined case for saldo type: " + type);
        }
    }
}
