package com.chess.saldo.service;

import com.chess.saldo.service.ServiceException.Type;
import com.chess.saldo.service.entities.Saldo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaldoParser {

    public static Saldo parsePage(String text) throws ServiceException {
        validateLogin(text);
        String moneyUsage = findMoneyUsage(text);
        String remainingSms = findRemainingSms(text);
        String remainingMms = findRemainingMms(text);
        String remainingData = findRemainingData(text);
        String remainingMinutes = findRemainingMinutes(text);

        if (moneyUsage == null && remainingSms == null && remainingMms == null &&
                remainingData == null && remainingMinutes == null) {
            throw new ServiceException("Kunne ikke finne noe saldo-informasjon", Type.ParseProblem);
        }

        int smsTotal = -1;
        int smsLeft = -1;
        int mmsTotal = -1;
        int mmsLeft = -1;
        int minutesTotal = -1;
        int minutesLeft = -1;
        float dataTotal = -1;
        float dataLeft = -1;
        float moneyUsed = -1;
        String strMoneyUsed = "";

        if (moneyUsage != null) {
            moneyUsed = parseMoneyUsed(moneyUsage);
            strMoneyUsed = parseMoneyUsedStr(moneyUsage);
        }

        if (remainingSms != null) {
            smsLeft = parseSmsLeft(remainingSms);
            smsTotal = parseSmsTotal(remainingSms);
        }

        if (remainingMms != null) {
            mmsLeft = parseMmsLeft(remainingMms);
            mmsTotal = parseMmsTotal(remainingMms);
        }

        if (remainingData != null) {
            dataLeft = parseDataLeft(remainingData);
            dataTotal = parseDataTotal(remainingData);
        }

        if (remainingMinutes != null) {
            minutesLeft = parseMinutesLeft(remainingMinutes);
            minutesTotal = parseMinutesTotal(remainingMinutes);
        }

        return new Saldo(smsTotal, smsLeft, mmsTotal, mmsLeft, minutesTotal, minutesLeft, dataTotal, dataLeft, moneyUsed, strMoneyUsed);
    }

    private static void validateLogin(String text) throws ServiceException {
        if (!text.contains("WelcomeMessage") && !text.contains("dnn_ctr546_UserInformationBar_HyperLinkLoggedInAs")) {
            throw new ServiceException("Kunne ikke logge inn. Er brukernavn/passord riktig?", Type.LoginProblem);
        }
    }

    private static String findPattern(String text, String pattern) throws ServiceException {
        Pattern p = Pattern.compile(pattern);
        Matcher match = p.matcher(text);
        if (!match.find()) {
            return null;
        }
        return match.group(1);
    }

    private static String findMoneyUsage(String text) throws ServiceException {
        return findPattern(text, "dnn_ctr546_UserInformationBar_LabelSaldoUsage\">(.+?)</span>");
    }

    private static String findRemainingSms(String text) throws ServiceException {
        return findPattern(text, "frie SMS</h4>(.+?)</a>");
    }

    private static String findRemainingMms(String text) throws ServiceException {
        return findPattern(text, "frie MMS</h4>(.+?)</a>");
    }

    private static String findRemainingData(String text) throws ServiceException {
        return findPattern(text, "fri datatrafikk</h4>(.+?)</a>");
    }

    private static String findRemainingMinutes(String text) throws ServiceException {
        return findPattern(text, "frie samtaler</h4>(.+?)</a>");
    }

    private static float parseMoneyUsed(String moneyUsage) {
        if (moneyUsage.equalsIgnoreCase("Henter...")) {
            return -1;
        } else {
            return Float.parseFloat(moneyUsage.substring(3).replace(",", "."));
        }
    }

    private static String parseMoneyUsedStr(String moneyUsage) {
        if (moneyUsage.equalsIgnoreCase("Henter...")) {
            return "";
        } else {
            return moneyUsage;
        }
    }

    private static int parseSmsTotal(String remainingSms) {
        return (int) parseQuotaString(remainingSms, "", 1);
    }

    private static int parseSmsLeft(String remainingSms) {
        return (int)parseQuotaString(remainingSms, "", 0);
    }

    private static int parseMmsTotal(String remainingMms) {
        return (int)parseQuotaString(remainingMms, "", 1);
    }

    private static int parseMmsLeft(String remainingMms) {
        return (int)parseQuotaString(remainingMms, "", 0);
    }

    private static float parseDataTotal(String remainingData) {
        return parseQuotaString(remainingData, "MB", 1);
    }

    private static float parseDataLeft(String remainingData) {
        return parseQuotaString(remainingData, "MB", 0);
    }

    private static int parseMinutesTotal(String remainingMinutes) {
        return (int) parseQuotaString(remainingMinutes, "min", 1);
    }

    private static int parseMinutesLeft(String remainingMinutes) {
        return (int) parseQuotaString(remainingMinutes, "min", 0);
    }

    private static float parseQuotaString(String quotaStr, String unit, int valueIndex) {
        if (unit.length() > 0) {
            quotaStr = quotaStr.replace(" " + unit, "");
        }
        quotaStr = quotaStr.replace(" av", "").replace(",", ".").split(" ")[valueIndex];
        return Float.parseFloat(quotaStr);
    }
}
