package com.emulator.gameboy.memory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Memory {
  /* the GameBoy had 16k of memory, mapped as follows:
   * 
   * $FFFF Interrupt Enable Flag
   * $FF80-$FFFE Zero Page - 127 bytes
   * $FF00-$FF7F Hardware I/O Registers
   * $FEA0-$FEFF Unusable Memory
   * $FE00-$FE9F OAM - Object Attribute Memory
   * $E000-$FDFF Echo RAM - Reserved, Do Not Use
   * $D000-$DFFF Internal RAM - Bank 1-7 (switchable - CGB only)
   * $C000-$CFFF Internal RAM - Bank 0 (fixed)
   * $A000-$BFFF Cartridge RAM (If Available)
   * $9C00-$9FFF BG Map Data 2
   * $9800-$9BFF BG Map Data 1
   * $8000-$97FF Character RAM
   * $4000-$7FFF Cartridge ROM - Switchable Banks 1-xx
   * $0150-$3FFF Cartridge ROM - Bank 0 (fixed)
   * $0100-$014F Cartridge Header Area
   * $0000-$00FF Restart and Interrupt Vectors
   *
   * memory map source: http://gameboy.mongenel.com/dmg/asmmemmap.html
   */

  private byte[] memoryArray = new byte[0xFFFF + 1];
  private ByteBuffer memory;

  /**
   * Check if a bit is set.
   * 
   * @param flags a byte of bit flags
   * @param bit which bit to check
   * @return is the bit set
   */
  public boolean checkBit(byte flags, int bit) {
    if ((flags & (1 << bit)) == 1) {
      return true;
    }
    return false;
  }

  public byte getByteAt(int address) {
    return memory.get(address);
  }

  public short getWordAt(int address) {
    return memory.getShort(address);
  }

  public void setByte(int address, byte value) {
    memory.put(address, value);
  }

  public void setWord(int address, short value) {
    memory.putShort(address, value);
  }

  /**
   * Load a ROM into memory.
   * 
   * @param path the path of the ROM
   */
  public void loadRom(String path) {
    try {
      byte[] rom = Files.readAllBytes(Paths.get(path));
      for (int i = 0; i < rom.length; i++) {
        memoryArray[i] = rom[i]; 
      }
      memory = ByteBuffer.wrap(memoryArray);
      memory.order(ByteOrder.LITTLE_ENDIAN);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
