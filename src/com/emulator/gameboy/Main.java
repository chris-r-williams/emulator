package com.emulator.gameboy;

import com.emulator.gameboy.cpu.Cpu;
import com.emulator.gameboy.cpu.CpuTests;
import com.emulator.gameboy.gpu.Gpu;
import com.emulator.gameboy.memory.Memory;
import com.emulator.gui.Gui;

import java.util.ResourceBundle;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public final class Main {
  /**
   * The main method.
   * 
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    // Instantiate objects
    Memory memory = new Memory();
    Cpu cpu = new Cpu(memory);
    Gui gui = new Gui(memory);
    Gpu gpu = new Gpu(memory, cpu, gui.getScreen());

    // Load resources
    ResourceBundle bundle = ResourceBundle.getBundle("resources");
    String tetris = bundle.getString("Tetris");     
    memory.loadRom(tetris);

    // Run tests
    Result result = JUnitCore.runClasses(CpuTests.class);
    if (result.wasSuccessful()) {
      System.out.println("All tests passed.");
    } else {
      for (Failure failure : result.getFailures()) {
        System.out.println(failure.toString());
      }
    }
    
    // Run CPU and GPU
    while (true) {
      cpu.decodeAndExecute(cpu.fetch());
      gpu.step();
    }
  }
}
