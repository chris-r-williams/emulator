package com.emulator.gameboy.cpu;

/**
 * This interface provides the essentials for implementing any CPU which may
 * eventually be used in this (potentially multi-system) emulator.
 * 
 * @author Chris R. Williams
 *
 */
interface AbstractCpu {
  /**
   * Decodes the opcode and executes the instruction.
   * 
   * @param opcode the opcode to decode
   * @throws UnsupportedOperationException for unimplemented opcodes
   */
  public void run();
}
