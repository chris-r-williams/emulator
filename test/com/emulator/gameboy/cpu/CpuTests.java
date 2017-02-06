package com.emulator.gameboy.cpu;

import static org.junit.Assert.assertEquals;

import com.emulator.gameboy.memory.Memory;

import java.lang.reflect.Method;

import org.junit.Test;

public class CpuTests {
  Memory memory = new Memory();
  Cpu tester = new Cpu(memory);

  @Test
  public void unsignedValueTest() {
    byte signedByte = (byte) 0xEE;
    short signedShort = (short) 0xEEEE;

    try {
      // get private methods using reflection
      Method unsignedValueByte = tester.getClass().getDeclaredMethod("unsignedValue", byte.class);
      Method unsignedValueShort = tester.getClass().getDeclaredMethod("unsignedValue", short.class);

      // set methods accessible, invoke, and set methods inaccessible
      unsignedValueByte.setAccessible(true);
      unsignedValueShort.setAccessible(true);
      assertEquals("Unsigned value of byte 0xEE should be 238", (short) 238,
          unsignedValueByte.invoke(tester, signedByte));
      assertEquals("Unsigned value of short 0xEEEE should be 61166", 61166,
          unsignedValueShort.invoke(tester, signedShort));
      unsignedValueByte.setAccessible(false);
      unsignedValueShort.setAccessible(false);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}
