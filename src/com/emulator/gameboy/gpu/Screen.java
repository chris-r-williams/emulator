package com.emulator.gameboy.gpu;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public final class Screen extends JPanel {

  private static final long serialVersionUID = -2503328183457416852L;
  private int scale = 2;
  private BufferedImage bufferedImage;
  @SuppressWarnings("unused") // accessor methods for graphics object are unnecessary
  private Graphics graphics;

  /**
   * This is the screen constructor.
   */
  public Screen() {
    bufferedImage = new BufferedImage(160,144,BufferedImage.TYPE_INT_RGB);
    graphics = bufferedImage.getGraphics();
  }

  public void setScale(int scale) {
    this.scale = scale;
  }
  
  /**
   * Updates a scanline in the image buffer that represents the screen.
   * @param line the scanline to be updated
   */
  public void updateLine(int line) {
    // TODO
    // pixels can be set e.g.
    // GUI.screen.bufferedImage.setRGB(cc, rc, ((cc+rc)*5)%256); //Color.BLACK.getRGB() );

  }

  @Override
  public void paint(Graphics graphics) {
    graphics.drawImage(bufferedImage, 0, 0, 160 * scale, 144 * scale, null);
  }

}
