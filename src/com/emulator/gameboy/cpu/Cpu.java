package com.emulator.gameboy.cpu;

import com.emulator.gameboy.memory.Memory;

public final class Cpu implements AbstractCpu {
  private int programCounter;
  private int stackPointer;
  private Flag flagZ;
  private Flag flagN;
  private Flag flagH;
  private Flag flagC;
  private byte registerA = 0x01;
  private byte registerB = 0x00;
  private byte registerC = 0x13;
  private byte registerD = 0x00;
  private byte registerE = (byte) 0xd8; // a 0x01, f 0xb0, b 0x00, c 0x13, d 0x00, e 0xd8 
  private short registersHl = 0x014d; // hl 0x014d
  @SuppressWarnings("unused") // no accessor methods for timerM are necessary
  private int timerM;
  private int lastInstructionTime;
  private Memory memory;

  /**
   * This class implements the GameBoy CPU.
   * @param memory a reference to the memory instance
   */
  public Cpu(Memory memory) {
    programCounter = 0x0000; // 0x0100;
    stackPointer = 0x0000; // 0xFFFE;
    //flags = (byte) 0x00; // 0b10110000; // ZNHC0000
    timerM = 0;
    lastInstructionTime = 0;
    this.memory = memory;
    flagZ = new Flag();
    flagN = new Flag();
    flagH = new Flag();
    flagC = new Flag();
  }
  
  /**
   * Returns the unsigned value of a signed byte.
   * 
   * @param signed the signed byte
   * @return the unsigned byte value
   */
  public short unsignedValue(byte signed) {
    return (short) (signed & 0xFF);
  }

  /**
   * Returns the unsigned value of a signed word.
   * 
   * @param signed the signed word
   * @return the unsigned word value
   */
  public int unsignedValue(short signed) {
    return (int) (signed & 0xFFFF);
  }

  private static class Flag {

    private boolean isSet = false;
    
    /**
     * Sets the Zero Flag.
     */
    public void set() {
      //flags |= 0b10000000;
      isSet = true;
    }

    /**
     * Resets the Zero Flag.
     */
    public void reset() {
      //flags &= ~0b10000000;
      isSet = false;
    }

    /**
     * Tests the Zero Flag.
     */
    public boolean isSet() {
      //return (flags & 0b10000000) == 0b10000000;
      return isSet;
    }
  }

  @Override
  /**
   * Fetches the opcode at the memory address in the program counter.
   * 
   * @return the opcode
   */
  public void run() {
    while (true) {
      decodeAndExecute(fetch());
    }
  }
  
  public byte fetch() {
    return memory.getByteAt(programCounter);
  }

  /**
   * Decodes and executes an instruction.
   * 
   * @param opcode the opcode of the instruction
   * @throws UnsupportedOperationException if the instruction is not implemented
   */
  public void decodeAndExecute(byte opcode) throws UnsupportedOperationException {
    System.out.println(String.format("Decode: 0x%02X", unsignedValue(opcode)));
    switch (unsignedValue(opcode)) {
      case 0x00:
        // NOP
        programCounter++;
        break;
      case 0x05:
        decB();
        break;
      case 0x06:
        ldBD8();
        break;
      case 0x0D:
        decC();
        break;
      case 0x0E:
        ldCD8();
        break;
      case 0x11:
        ldDeD16();
        break;
      case 0x1A:
        ldA_De_();
        break;
      case 0x20:
        jrNzR8();
        break;
      case 0x21:
        ldHlD16();
        break;
      case 0x31:
        ldSpD16();
        break;
      case 0x32:
        ld_Hlm_A();
        break;
      case 0x3E:
        ldAD8();
        break;
      case 0x4F:
        ldCa();
        break;
      case 0x77:
        ld_Hl_A();
        break;
      case 0xAF:
        xorA();
        break;
      case 0xC3:
        jpA16();
        break;
      case 0xC5:
        pushBc();
        break;
      case 0xCB:
        // CB prefixed instruction
        cb();
        decodeAndExecuteCb(fetch());
        break;
      case 0xCD:
        callA16();
        break;
      case 0xE0:
        ldh_A8_A();
        break;
      case 0xE2:
        ld_C_A();
        break;
      case 0xFB:
        ei();
        break;
      default:
        throw new UnsupportedOperationException("Unimplemented instruction: "
          + String.format("0x%02X", unsignedValue(memory.getByteAt(programCounter))));
    }
  }

  private void decodeAndExecuteCb(byte opcode) {
    switch (unsignedValue(opcode)) {
      case 0x11:
        rlC();
        break;
      case 0x7C:
        bit7H();
        break;
      default:
        throw new UnsupportedOperationException("Unimplemented CB instruction: "
          + String.format("0x%02X", unsignedValue(memory.getByteAt(programCounter))));
    }
  }

  /*
   * The CPU instruction implementations
   */
  
  // 0x05 DEC B
  protected void decB() {
    registerB = (byte) ((unsignedValue(registerB) - 1) % 256);
    boolean halfCarryBefore = unsignedValue(registerB) > 0x0F ? true : false; 
    if (registerB == 0) {
      flagZ.set();
    } else {
      flagZ.reset();
    }
    flagN.set();
    boolean halfCarryAfter = unsignedValue(registerB) > 0x0F ? true : false; 
    if (halfCarryBefore == halfCarryAfter) {
      flagH.reset();
    } else {
      flagH.set();
    }
    timerM++;
    lastInstructionTime = 1;
    programCounter++;
  }

  // 0x06 LD B,d8
  protected void ldBD8() {
    registerB = memory.getByteAt(programCounter + 1);
    timerM += 2;
    lastInstructionTime = 2;
    programCounter += 2;
  }

  // 0x0D DEC C
  protected void decC() {
    registerC--;
    boolean halfCarryBefore = unsignedValue(registerB) > 0x0F ? true : false; 
    if (registerC == 0) {
      flagZ.set();
    } else {
      flagZ.reset();
    }
    flagN.set();
    boolean halfCarryAfter = unsignedValue(registerB) > 0x0F ? true : false; 
    if (halfCarryBefore == halfCarryAfter) {
      flagH.reset();
    } else {
      flagH.set();
    }
    timerM++;
    lastInstructionTime = 1;
    programCounter++;
  }

  // 0x0E LD C,d8
  protected void ldCD8() {
    registerC = memory.getByteAt(programCounter + 1);
    timerM += 2;
    lastInstructionTime = 2;
    programCounter += 2;
  }

  // 0x11 LD DE,d16
  protected void ldDeD16() {
    registerD = memory.getByteAt(programCounter + 1); // TODO check endianness
    registerE = memory.getByteAt(programCounter + 2);
    timerM += 3;
    lastInstructionTime = 3;
    programCounter += 3;
  }

  // 0x1A LD A,(DE)
  protected void ldA_De_() {
    registerA = memory.getByteAt((registerD << 8) + registerE); // TODO check endianness
    timerM += 2;
    lastInstructionTime = 2;
    programCounter++;
  }

  // 0x20 JR NZ,r8
  protected void jrNzR8() {
    if (!flagZ.isSet()) {
      programCounter += memory.getByteAt(programCounter + 1);
      timerM += 3;
      lastInstructionTime = 3;
    } else {
      timerM += 2;
      lastInstructionTime = 2;
    }
    programCounter += 2;
  }

  // 0x21 LD HL,d16
  protected void ldHlD16() {
    registersHl = memory.getWordAt(programCounter + 1);
    timerM += 3;
    lastInstructionTime = 3;
    programCounter += 3;
  }

  // 0x31 LD SP,d16
  protected void ldSpD16() {
    stackPointer = unsignedValue(memory.getWordAt(programCounter + 1));
    timerM += 3; 
    lastInstructionTime = 3;
    programCounter += 3;
  }
  
  // 0x32 LD (HL-),A
  protected void ld_Hlm_A() {
    memory.setByte(unsignedValue(registersHl),registerA);
    registersHl--;
    timerM += 2;
    lastInstructionTime = 2;
    programCounter++;
  }

  // 0x3E LD A,d8
  protected void ldAD8() {
    registerA = memory.getByteAt(programCounter + 1);
    timerM += 2;
    lastInstructionTime = 2;
    programCounter += 2;
  }

  // 0x4F LD C,A
  protected void ldCa() {
    registerC = registerA;
    timerM++;
    lastInstructionTime = 1;
    programCounter++;
  }

  // 0x77 LD (HL),A
  protected void ld_Hl_A() {
    memory.setByte(unsignedValue(registersHl), registerA);
    timerM += 2;
    lastInstructionTime = 2;
    programCounter++;
  }
  
  // 0xAF XOR A
  protected void xorA() {
    registerA = 0x00;
    flagZ.set();
    flagN.reset();
    flagH.reset();
    flagC.reset();
    timerM++;
    lastInstructionTime = 1;
    programCounter++;
  }

  // 0xC3 JP a16
  protected void jpA16() {
    timerM += 4;
    lastInstructionTime = 4;
    programCounter = unsignedValue(memory.getWordAt(programCounter + 1));
  }

  // 0xC5 PUSH BC
  protected void pushBc() {
    memory.setWord(stackPointer, (short) ((registerB << 8) + registerC)); // TODO check endianness
    stackPointer += 2;
    timerM += 4;
    lastInstructionTime = 4;
    programCounter++;
  }

  // 0xCB PREFIX CB
  protected void cb() {
    //mTimer++; lastInstructionTime = 1; // TODO already accounted for?
    programCounter++;
  }

  // 0xCD CALL a16
  protected void callA16() {
    // write address of next instruction to stack
    memory.setWord(stackPointer, memory.getWordAt(programCounter + 3));

    // decrement by 2 bytes (word size)
    stackPointer -= 2;

    // set PC to function address
    programCounter = memory.getWordAt(programCounter + 1);

    timerM += 3;
    lastInstructionTime = 3;
  }

  // 0xE0 LDH (a8),A
  protected void ldh_A8_A() {
    memory.setByte(0xFF00 + unsignedValue(registerA), registerA);
    timerM += 3;
    lastInstructionTime = 3;
    programCounter += 2;
  }

  // 0xE2 LD (C),A
  protected void ld_C_A() {
    registerA = memory.getByteAt(unsignedValue(registerC));
    timerM += 2;
    lastInstructionTime = 2;
    programCounter += 2;
  }
  
  // 0xFB EI
  protected void ei() {
    memory.setByte(0xFFFF, (byte) 0xFF);
    timerM++;
    lastInstructionTime = 1;
    programCounter++;
  }

  // 0xCB11 RL C
  protected void rlC() {
    if (unsignedValue(registerC) >= 0x80) {
      flagC.set();
    } else {
      flagC.reset();
    }
    registerC = (byte) (registerC << 1);
    if (registerC == 0) {
      flagZ.set();
    } else {
      flagZ.reset();
    }
    flagN.reset();
    flagH.reset();
    timerM += 2;
    lastInstructionTime = 2;
    programCounter ++;
  }

  // 0xCB7C BIT 7,H
  protected void bit7H() {
    if (memory.checkBit(memory.getByteAt(programCounter + 1), 7)) {
      flagZ.set();
    } else {
      flagZ.reset();
    }
    flagN.reset();
    flagH.set();
    timerM += 2;
    lastInstructionTime += 2;
    programCounter++;
  }

  public int getLastInstructionTime() {
    return lastInstructionTime;
  }
}
