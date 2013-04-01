/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chess.saldo.service;

import junit.framework.Assert;
import android.test.ActivityTestCase;

/**
 *
 * @author Bjorncs
 */
public class SaldoTest extends ActivityTestCase {
   
   public void testSerialization() {
      Saldo saldo = new Saldo("?=?", "åæø", "mms", "kake_1234567", "^^^^");
      String asString = saldo.asString();
      Saldo deserializedSaldo = new Saldo(asString);
      Assert.assertEquals(saldo, deserializedSaldo);
   }
   
}
