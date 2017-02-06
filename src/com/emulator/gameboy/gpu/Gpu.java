package com.emulator.gameboy.gpu;

import com.emulator.gameboy.cpu.Cpu;
import com.emulator.gameboy.memory.Memory;

public final class Gpu {
  private int mode = 0;
  private int modeClock = 0;
  private int currScanline = 0;
  private int mapOffset = 0;
  private int scanlineOffset = 0;
  @SuppressWarnings("unused") // accessor methods for screenOffset are unnecessary
  private int screenOffset = 0;
  private Memory memory;
  private Cpu cpu;
  private Screen screen;

  /**
   * This is the GPU constructor.
   * 
   * @param memory the instance of memory
   * @param cpu the instance of the CPU
   * @param screen the instance of the screen
   */
  public Gpu(Memory memory, Cpu cpu, Screen screen) {
    this.memory = memory;
    this.screen = screen;
    this.cpu = cpu;
  }

  /**
   * Step the GPU.
   */
  public void step() {
    modeClock += (cpu.getLastInstructionTime());

    switch (mode) { // OAM read mode, scanline active
      case 2:
        if (modeClock >= 20) {
          // Enter scanline mode 3
          modeClock = 0;
          mode = 3;
        }
        break; // VRAM read mode, scanline active// Treat end of mode 3 as end of scanline
      case 3:
        if (modeClock >= 43) {
          // Enter hblank
          modeClock = 0;
          mode = 0;

          // Write a scanline to the framebuffer
          renderScanline();
          // test rendering a scanline
          screen.updateLine(currScanline);
        }
        break;

        // Hblank
        // After the last hblank, push the screen data to canvas
      case 0:
        if (modeClock >= 51) {
          modeClock = 0;
          currScanline++;

          if (currScanline == 143) {
            // Enter vblank
            mode = 1;
            //GUI.screen.repaint(); TODO
          } else {
            mode = 2;
          }
        }
        break;

        // Vblank (10 lines)
      case 1:
        if (modeClock >= 114) {
          modeClock = 0;
          currScanline++;

          if (currScanline > 153) {
            // Restart scanning modes
            mode = 2;
            currScanline = 0;
          }
        }
        break;
      default:
        throw new UnsupportedOperationException("Unsupported GPU mode");
    }
  }

  private void renderScanline() {
    // VRAM offset for the tile map
    if (memory.checkBit(memory.getByteAt(0xFF40), 3)) { // FF40 is GPU reg, bit 3 is bgmap flag
      mapOffset = 0x1C00;
    } else {
      mapOffset = 0x1800;
    }

    int scrollY = unsignedValue(memory.getByteAt(0xFF42)); // scroll Y register
    int scrollX = unsignedValue(memory.getByteAt(0xFF43)); // scroll X register
    int currScanline = unsignedValue(memory.getByteAt(0XFF44)); // current scanline register

    // Which line of tiles to use in the map
    mapOffset += ((currScanline + scrollY) & 255) >> 3;

    // Which tile to start with in the map line
    scanlineOffset = (scrollY >> 3);

    // Which line of pixels to use in the tiles
    //int y = (currScanline + scrollY) & 7; TODO

    // Where in the tileline to start
    int posX = scrollX & 7;

    // Where to render on the screen
    screenOffset = currScanline * 160 * 4;

    // Read tile index from the background map
    //int[] color = new int[4]; // TODO
    int tile = unsignedValue(memory.getByteAt(0x8000 + mapOffset + scanlineOffset));

    // If the tile data set in use is #1, the
    // indices are signed; calculate a real tile offset
    if (memory.checkBit(memory.getByteAt(0xFF40), 4) && tile < 128) { //FF40 is gpu reg bit 4 bgtile
      tile += 256;
    }

    for (int i = 0; i < 160; i++) {
      // Re-map the tile pixel through the palette
      //color = GPU._pal[GPU._tileset[tile][y][x]]; TODO

      // Plot the pixel to canvas

      //GPU._scrn.data[screenOffset+0] = color[0];
      //GPU._scrn.data[screenOffset+1] = color[1];
      //GPU._scrn.data[screenOffset+2] = color[2];
      //GPU._scrn.data[screenOffset+3] = color[3];
      //GUI.screen.bufferedImage.setRGB(cc, currScanline, color[0]); //Color.BLACK.getRGB() ); TODO
      screenOffset += 4;

      // When this tile ends, read another
      posX++;
      if (posX == 8) {
        posX = 0;
        scanlineOffset = (scanlineOffset + 1) & 31;
        tile = unsignedValue(memory.getByteAt(0x8000 + mapOffset + scanlineOffset));
        if (memory.checkBit(memory.getByteAt(0xFF40), 4) && tile < 128) {
          tile += 256;
        }
      }
    }
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
    return (short) (signed & 0xFFFF);
  }
}
