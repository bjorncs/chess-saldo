/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chess.saldo.service;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 *
 * @author Bjorncs
 */
public class Saldo implements Serializable {

	public static final String NOT_AVAILABLE = "-";

    public final String moneyUsage;
    public final String remainingSms;
    public final String remainingMms;
    public final String remainingData;
    public final String remainingMinutes;

    public Saldo(String moneyUsage, String remainingSms, String remainingMms, String remainingData, String remainingMinutes) {
        this.moneyUsage = moneyUsage;
        this.remainingSms = remainingSms;
        this.remainingMms = remainingMms;
        this.remainingData = remainingData;
        this.remainingMinutes = remainingMinutes;
    }

    public Saldo(String serializedSaldo) {
        try {
            String[] urlEncodedInfo = serializedSaldo.split("&");
            this.moneyUsage = URLDecoder.decode(urlEncodedInfo[0], "UTF-8");
            this.remainingData = URLDecoder.decode(urlEncodedInfo[1], "UTF-8");
            this.remainingSms = URLDecoder.decode(urlEncodedInfo[2], "UTF-8");
            this.remainingMms = URLDecoder.decode(urlEncodedInfo[3], "UTF-8");
            this.remainingMinutes = URLDecoder.decode(urlEncodedInfo[4], "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Saldo createDefaultSaldo() {
        return new Saldo("kr 3,52", "150 av 150", "10 av 10", "150 MB av 150 MB", "150 min av 150 min");
    }

    public double parseMoneyUsed() {
        if (moneyUsage.equals(Saldo.NOT_AVAILABLE)) {
            return -1;
        } else if (moneyUsage.equalsIgnoreCase("Henter...")) {
            return 0;
        } else {
            return Double.parseDouble(moneyUsage.substring(3).replace(",", "."));
        }
    }

    public String getMoneyUsed() {
        if (moneyUsage.equals(Saldo.NOT_AVAILABLE)) {
            return "-";
        } else if (moneyUsage.equalsIgnoreCase("Henter...")) {
            return "-";
        } else {
            return moneyUsage;
        }
    }

    public int parseSmsTotal() {
        return (int) parseQuotaString(remainingSms, "", 1);
    }

    public int parseSmsLeft() {
        return (int)parseQuotaString(remainingSms, "", 0);
    }

    public int parseMmsTotal() {
        return (int)parseQuotaString(remainingMms, "", 1);
    }

    public int parseMmsLeft() {
        return (int)parseQuotaString(remainingMms, "", 0);
    }

    public double parseDataTotal() {
        return parseQuotaString(remainingData, "MB", 1);
    }

    public double parseDataLeft() {
        return parseQuotaString(remainingData, "MB", 0);
    }

    public int parseMinutesTotal() {
        return (int) parseQuotaString(remainingMinutes, "min", 1);
    }

    public int parseMinutesLeft() {
        return (int) parseQuotaString(remainingMinutes, "min", 0);
    }

    public boolean isValid() {
        try {
            parseMoneyUsed();
            parseDataLeft();
            parseDataTotal();
            parseMinutesLeft();
            parseMinutesTotal();
            parseMmsLeft();
            parseMmsTotal();
            parseSmsLeft();
            parseSmsTotal();
            return true;
        } catch (Exception exp) {
            return false;
        }
    }

    //Parse strings like '146 av 150' and '150 min av 150 min'
    private static double parseQuotaString(String quotaStr, String unit, int valueIndex) {
        if (quotaStr.equals(Saldo.NOT_AVAILABLE)) {
            return 0;
        }
        if (unit.length() > 0) {
            quotaStr = quotaStr.replace(" " + unit, "");
        }
        quotaStr = quotaStr.replace(" av", "").replace(",", ".").split(" ")[valueIndex];
        return Double.parseDouble(quotaStr);
    }

    public String asString() {
        try {
            return String.format(
                    "%s&%s&%s&%s&%s",
                    URLEncoder.encode(moneyUsage, "UTF-8"),
                    URLEncoder.encode(remainingData, "UTF-8"),
                    URLEncoder.encode(remainingSms, "UTF-8"),
                    URLEncoder.encode(remainingMms, "UTF-8"),
                    URLEncoder.encode(remainingMinutes, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Saldo other = (Saldo) obj;
        if ((this.moneyUsage == null) ? (other.moneyUsage != null) : !this.moneyUsage.equals(other.moneyUsage)) {
            return false;
        }
        if ((this.remainingSms == null) ? (other.remainingSms != null) : !this.remainingSms.equals(other.remainingSms)) {
            return false;
        }
        if ((this.remainingMms == null) ? (other.remainingMms != null) : !this.remainingMms.equals(other.remainingMms)) {
            return false;
        }
        if ((this.remainingData == null) ? (other.remainingData != null) : !this.remainingData.equals(other.remainingData)) {
            return false;
        }
        if ((this.remainingMinutes == null) ? (other.remainingMinutes != null) : !this.remainingMinutes.equals(other.remainingMinutes)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.moneyUsage != null ? this.moneyUsage.hashCode() : 0);
        hash = 59 * hash + (this.remainingSms != null ? this.remainingSms.hashCode() : 0);
        hash = 59 * hash + (this.remainingMms != null ? this.remainingMms.hashCode() : 0);
        hash = 59 * hash + (this.remainingData != null ? this.remainingData.hashCode() : 0);
        hash = 59 * hash + (this.remainingMinutes != null ? this.remainingMinutes.hashCode() : 0);
        return hash;
    }
}
