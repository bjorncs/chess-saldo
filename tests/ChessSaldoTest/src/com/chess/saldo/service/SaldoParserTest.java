/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chess.saldo.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.test.ActivityTestCase;

import com.chess.saldo.test.R;

/**
 *
 * @author Bjorncs
 */
public class SaldoParserTest extends ActivityTestCase {

   public void testParsingMinSide() throws FileNotFoundException, IOException, ServiceException {
      loadHtmlFile(R.raw.test1);
      loadHtmlFile(R.raw.test2);
      loadHtmlFile(R.raw.test3);
      loadHtmlFile(R.raw.test4);
   }

   private void loadHtmlFile(int rid) throws FileNotFoundException, IOException, ServiceException {
      BufferedReader reader = new BufferedReader(new InputStreamReader(getInstrumentation().getContext().getResources().openRawResource(rid)));
      StringBuilder builder = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
          builder.append(line);
      }
      reader.close();
      String text = builder.toString();
      Saldo saldo = SaldoParser.parsePage(text);
      checkSaldoInformation(saldo);
   }

   private void checkSaldoInformation(Saldo saldo) {
      assertNotNull(saldo.moneyUsage);
      if (!saldo.moneyUsage.equals(Saldo.NOT_AVAILABLE)) {
         assertTrue(saldo.parseMoneyUsed() >= 0 && saldo.parseMoneyUsed() <= 1000);
      }
      assertNotNull(saldo.remainingData);
      if (!saldo.remainingData.equals(Saldo.NOT_AVAILABLE)) {
         assertTrue(saldo.parseDataLeft() >= 0 && saldo.parseDataLeft() <= 1000);
         assertTrue(saldo.parseDataTotal() > 0 && saldo.parseDataTotal() <= 1000);
      }
      assertNotNull(saldo.remainingMinutes);
      if (!saldo.remainingMinutes.equals(Saldo.NOT_AVAILABLE)) {
         assertTrue(saldo.parseMinutesLeft() >= 0 && saldo.parseMinutesLeft() <= 1000);
         assertTrue(saldo.parseMinutesTotal() > 0 && saldo.parseMinutesTotal() <= 1000);
      }
      assertNotNull(saldo.remainingMms);
      if (!saldo.remainingMms.equals(Saldo.NOT_AVAILABLE)) {
         assertTrue(saldo.parseMmsLeft() >= 0 && saldo.parseMmsLeft() <= 1000);
         assertTrue(saldo.parseMmsTotal() > 0 && saldo.parseMmsTotal() <= 1000);
      }
      assertNotNull(saldo.remainingSms);
      if (!saldo.remainingSms.equals(Saldo.NOT_AVAILABLE)) {
         assertTrue(saldo.parseSmsLeft() >= 0 && saldo.parseSmsLeft() <= 1000);
         assertTrue(saldo.parseSmsTotal() > 0 && saldo.parseSmsTotal() <= 1000);
      }
   }
}
