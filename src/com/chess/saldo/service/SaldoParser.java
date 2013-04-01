/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chess.saldo.service;

import com.chess.saldo.service.ServiceException.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Bjorncs
 */
public class SaldoParser {

   public static Saldo parsePage(String text) throws ServiceException {
      validateLogin(text);
      String moneyUsage = parseMoneyUsage(text);
      String remainingSms = parseRemainingSms(text);
      String remainingMms = parseRemainingMms(text);
      String remainingData = parseRemainingData(text);
      String remainingMinutes = parseRemainingMinutes(text);
      if (moneyUsage.equals(Saldo.NOT_AVAILABLE) &&
              remainingSms.equals(Saldo.NOT_AVAILABLE) &&
              remainingMms.equals(Saldo.NOT_AVAILABLE) &&
              remainingData.equals(Saldo.NOT_AVAILABLE) &&
              remainingMinutes.equals(Saldo.NOT_AVAILABLE)) {
         ServiceException exp = new ServiceException("Kunne ikke finne noe saldo-informasjon", Type.ParseProblem);
         exp.setHtmlText(text);
         throw exp;
      }
      Saldo saldo = new Saldo(moneyUsage, remainingSms, remainingMms, remainingData, remainingMinutes);
      return saldo;
   }

   private static void validateLogin(String text) throws ServiceException {
      if (!text.contains("WelcomeMessage") && !text.contains("dnn_ctr546_UserInformationBar_HyperLinkLoggedInAs")) {
         ServiceException exp = new ServiceException("Kunne ikke logge inn. Er brukernavn/passord riktig?", Type.LoginProblem);
         exp.setHtmlText(text);
         throw exp;
      }
   }

   private static String findPattern(String text, String pattern) throws ServiceException {
      Pattern p = Pattern.compile(pattern);
      Matcher match = p.matcher(text);
      if (!match.find()) {
         return Saldo.NOT_AVAILABLE;
      }
      return match.group(1);
   }

   private static String parseMoneyUsage(String text) throws ServiceException {
      return findPattern(text, "dnn_ctr546_UserInformationBar_LabelSaldoUsage\">(.+?)</span>");
   }

   private static String parseRemainingSms(String text) throws ServiceException {
      return findPattern(text, "frie SMS</h4>(.+?)</a>");
   }

   private static String parseRemainingMms(String text) throws ServiceException {
      return findPattern(text, "frie MMS</h4>(.+?)</a>");
   }

   private static String parseRemainingData(String text) throws ServiceException {
      return findPattern(text, "fri datatrafikk</h4>(.+?)</a>");
   }

   private static String parseRemainingMinutes(String text) throws ServiceException {
      return findPattern(text, "frie samtaler</h4>(.+?)</a>");
   }
}
