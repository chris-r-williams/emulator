package com.emulator.gui;

import com.emulator.gameboy.gpu.Screen;
import com.emulator.gameboy.memory.Memory;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;

public final class Gui implements ActionListener {
  private JFrame frame;
  private JMenuBar menuBar;
  private JMenu fileMenu;
  private JMenu windowSizeMenu;
  private JMenuItem fileLoadMenuItem;
  private JMenuItem exitMenuItem;
  private JCheckBoxMenuItem nativeResMenuItem;
  private JCheckBoxMenuItem twoXMenuItem;
  private JCheckBoxMenuItem threeXMenuItem;
  private Screen screen;
  private JFileChooser fileChooser;
  int defaultScale;

  /**
   * This is the GUI constructor.
   * 
   * @param memory the instance of memory
   */
  public Gui(Memory memory) {
    defaultScale = 2;
    frame = new JFrame();
    menuBar = new JMenuBar();
    fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new FileFilter() {

      @Override
      public String getDescription() {
        return "GameBoy ROM files (*.gb)";
      }

      @Override
      public boolean accept(File file) {
        if (file.isDirectory()) {
          return true;
        } else {
          String filename = file.getName().toLowerCase();
          return filename.endsWith(".gb");
        }
      }
    });

    frame.setTitle("RES·ÆMVLATVR");

    // build the file menu
    fileMenu = new JMenu("File");
    menuBar.add(fileMenu);

    fileLoadMenuItem = new JMenuItem(new AbstractAction("Load ROM File...") {
      private static final long serialVersionUID = -6346481368590593920L;
      @Override
      public void actionPerformed(ActionEvent event) {
        int status = fileChooser.showOpenDialog(fileChooser.getParent());
        if (status == JFileChooser.APPROVE_OPTION) {
          String rom = fileChooser.getSelectedFile().getAbsolutePath();
          memory.loadRom(rom);
        }
      }
    });
    fileMenu.add(fileLoadMenuItem);

    exitMenuItem = new JMenuItem(new AbstractAction("Exit") {
      private static final long serialVersionUID = -6346481368590593920L;
      @Override
      public void actionPerformed(ActionEvent event) {
        Runtime.getRuntime().exit(0);
      }
    });
    fileMenu.add(exitMenuItem);

    // build the window size menu
    windowSizeMenu = new JMenu("Window Size");
    menuBar.add(windowSizeMenu);

    nativeResMenuItem = new JCheckBoxMenuItem(new AbstractAction("Native resolution") {
      private static final long serialVersionUID = -6346481368590593920L;
      @Override
      public void actionPerformed(ActionEvent event) {
        nativeResMenuItem.setSelected(true);
        twoXMenuItem.setSelected(false);
        threeXMenuItem.setSelected(false);
        rescale(1);
      }
    });
    windowSizeMenu.add(nativeResMenuItem);

    twoXMenuItem = new JCheckBoxMenuItem(new AbstractAction("2x") {
      private static final long serialVersionUID = 8742418629499563748L;
      @Override
      public void actionPerformed(ActionEvent event) {
        nativeResMenuItem.setSelected(false);
        twoXMenuItem.setSelected(true);
        threeXMenuItem.setSelected(false);
        rescale(2);
      }
    });
    twoXMenuItem.setSelected(true);
    windowSizeMenu.add(twoXMenuItem);

    threeXMenuItem = new JCheckBoxMenuItem(new AbstractAction("3x") {
      private static final long serialVersionUID = 8722542849292474606L;
      @Override
      public void actionPerformed(ActionEvent event) {
        nativeResMenuItem.setSelected(false);
        twoXMenuItem.setSelected(false);
        threeXMenuItem.setSelected(true);
        rescale(3);
      }
    });
    windowSizeMenu.add(threeXMenuItem);

    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setJMenuBar(menuBar);
    screen = new Screen();
    frame.getContentPane().add(screen);
    frame.getContentPane().setPreferredSize(new Dimension(160 * defaultScale, 144 * defaultScale));
    frame.pack();
    frame.setVisible(true);
  }

  private void rescale(int scale) {
    screen.setScale(scale);
    screen.repaint();
    frame.getContentPane().setPreferredSize(new Dimension(160 * scale, 144 * scale));
    frame.pack();
  }

  public Screen getScreen() {
    return screen;
  }

  @Override
  public void actionPerformed(ActionEvent arg0) {
    // TODO Auto-generated method stub

  }
}
