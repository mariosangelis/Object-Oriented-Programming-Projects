package ce326.hw2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.*;
    public class ImageProcessing {
        JFrame frame;
        JPanel mainPanel;
        JLabel imgLabel;
        JScrollPane scrollPane;

        PPMImage ppmImg;
        YUVImage yuvImg;
        BufferedImage img;
        String imgFilename;

        JMenu saveMenu;
        JMenu actionMenu;
  
  public static final int MINIMUM_WIDTH = 500;
  public static final int MINIMUM_HEIGHT = 400;

  public ImageProcessing() {
    frame = new JFrame("CE325 HW3");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainPanel = new JPanel(new BorderLayout());
    frame.setContentPane(mainPanel);
    
    imgLabel = new JLabel();
    imgLabel.setPreferredSize(new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT));
    scrollPane = new JScrollPane(imgLabel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    mainPanel.add(scrollPane, BorderLayout.CENTER);
    addMenu();
    frame.pack();
    frame.setVisible(true);
  }   

  public static void main(String []args) {    
    ImageProcessing frame = new ImageProcessing();
  }
  
  public void enableMenu() {
    if(!saveMenu.isEnabled())
      saveMenu.setEnabled(true);
    if(!actionMenu.isEnabled())
      actionMenu.setEnabled(true);
  }
  
  public void addMenu() {
    JMenuBar menuBar = new JMenuBar();
    
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic(KeyEvent.VK_F);
    menuBar.add(fileMenu);
    
    JMenu openMenu = new JMenu("Open");
    openMenu.setMnemonic(KeyEvent.VK_O);
    JMenuItem ppmOpen = new JMenuItem("PPM File");
    ppmOpen.setMnemonic(KeyEvent.VK_P);        
    openMenu.add(ppmOpen);
    JMenuItem yuvOpen = new JMenuItem("YUV File");
    yuvOpen.setMnemonic(KeyEvent.VK_Y);
    openMenu.add(yuvOpen);
    JMenuItem otherOpen = new JMenuItem("Other Format");
    otherOpen.setMnemonic(KeyEvent.VK_Y);
    openMenu.add(otherOpen);
    fileMenu.add(openMenu);
    
    ppmOpen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new PPMFileFilter());
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          try {
            ppmImg = new PPMImage(selectedFile);
            img = RGBImage2BufferedImage(ppmImg);
            imgLabel.setIcon(new ImageIcon(img));
            enableMenu();
          } catch(FileNotFoundException ex) {
            JOptionPane.showMessageDialog(
               frame,
               "File not found!",
               "ERROR",
               JOptionPane.ERROR_MESSAGE);
          }catch(UnsupportedFileFormatException ex) {
            JOptionPane.showMessageDialog(
               frame,
               ex.getMessage(),
               "ERROR: File format",
               JOptionPane.ERROR_MESSAGE);
          }
          
        }
      }
    });
    
    yuvOpen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new YUVFileFilter());
        fileChooser.addChoosableFileFilter(new PPMFileFilter());
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          try {
            yuvImg = new YUVImage(selectedFile);
            ppmImg = new PPMImage(new RGBImage(yuvImg));
            img = RGBImage2BufferedImage(ppmImg);
            imgLabel.setIcon(new ImageIcon(img));
            enableMenu();
          }catch(FileNotFoundException ex) {
            JOptionPane.showMessageDialog(
               frame,
               "File not found!",
               "ERROR",
               JOptionPane.ERROR_MESSAGE);
          }catch(UnsupportedFileFormatException ex) {
            JOptionPane.showMessageDialog(
               frame,
               ex.getMessage(),
               "ERROR: File format",
               JOptionPane.ERROR_MESSAGE);
          }
          
        }
      }
    });
    
    otherOpen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          try {
            img = ImageIO.read(selectedFile);
            imgLabel.setIcon(new ImageIcon(img));
            ppmImg = new PPMImage(BufferedImage2RGBImage(img));
            enableMenu();
          }catch(FileNotFoundException ex) {
            JOptionPane.showMessageDialog(
               frame,
               "File not found!",
               "ERROR",
               JOptionPane.ERROR_MESSAGE);
          }catch(IOException ex) {
            JOptionPane.showMessageDialog(
               frame,
               ex.getMessage(),
               "ERROR: IO Error",
               JOptionPane.ERROR_MESSAGE);
          }
          
        }
      }
    });
    
    saveMenu = new JMenu("Save");
    saveMenu.setMnemonic(KeyEvent.VK_S);
    saveMenu.setEnabled(false);
    JMenuItem ppmSave = new JMenuItem("PPM File");
    ppmSave.setMnemonic(KeyEvent.VK_P);        
    saveMenu.add(ppmSave);
    JMenuItem yuvSave = new JMenuItem("YUV File");
    yuvSave.setMnemonic(KeyEvent.VK_Y);        
    saveMenu.add(yuvSave);
    fileMenu.add(saveMenu);
    
    ppmSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new PPMFileFilter());
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          if(selectedFile.exists()) {
            int answer = JOptionPane.showConfirmDialog(
                            frame, "File exists! Overwrite ?",
                            "Warning",
                            JOptionPane.YES_NO_OPTION);
            if(answer != JOptionPane.YES_OPTION)
              return;
          }
          ppmImg.toFile(selectedFile);
        }
      }
    });
    
    yuvSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new YUVFileFilter());
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          if(selectedFile.exists()) {
            int answer = JOptionPane.showConfirmDialog(
                            frame, "File exists! Overwrite ?",
                            "Warning",
                            JOptionPane.YES_NO_OPTION);
            if(answer != JOptionPane.YES_OPTION)
              return;
          }
          yuvImg = new YUVImage(ppmImg);
          yuvImg.toFile(selectedFile);
        }
      }
    });
    
    actionMenu = new JMenu("Tools");
    actionMenu.setEnabled(false);
    actionMenu.setMnemonic(KeyEvent.VK_A);    
    menuBar.add(actionMenu);
    JMenuItem grayscale = new JMenuItem("Grayscale");    
    JMenuItem incsize = new JMenuItem("Increase Size");
    JMenuItem decsize = new JMenuItem("Decrease Size");
    JMenuItem rotate = new JMenuItem("Rotate Clocwise");
    JMenuItem equalize = new JMenuItem("Equalize Histogram");
    JMenu stackMenu = new JMenu("Stacking Algorithm");
    JMenuItem stackDir = new JMenuItem("Select directory..");
    stackMenu.add(stackDir);
    actionMenu.add(grayscale);
    actionMenu.add(incsize);
    actionMenu.add(decsize);
    actionMenu.add(rotate);
    actionMenu.add(equalize);
    actionMenu.add(stackMenu);
    
    grayscale.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        ppmImg.grayscale();
        img = RGBImage2BufferedImage(ppmImg);
        imgLabel.setIcon(new ImageIcon(img));
      }
    });
    
    incsize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        ppmImg.doublesize();
        img = RGBImage2BufferedImage(ppmImg);
        imgLabel.setIcon(new ImageIcon(img));
      }
    });
    
    decsize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        ppmImg.halfsize();
        img = RGBImage2BufferedImage(ppmImg);
        imgLabel.setIcon(new ImageIcon(img));
      }
    });
    
    rotate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        ppmImg.rotateClockwise();
        img = RGBImage2BufferedImage(ppmImg);
        imgLabel.setIcon(new ImageIcon(img));
      }
    });
    
    equalize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        yuvImg = new YUVImage(ppmImg);          
        yuvImg.equalize();
        ppmImg = new PPMImage(yuvImg);
        img = RGBImage2BufferedImage(ppmImg);
        imgLabel.setIcon(new ImageIcon(img));
      }
    });
    
    stackDir.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new PPMFileFilter());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          try {
            File selectedFile = fileChooser.getSelectedFile();
            PPMImageStacker stacker = new PPMImageStacker(selectedFile);
            stacker.stack();
            ppmImg = new PPMImage(stacker.getStackedImage());
            img = RGBImage2BufferedImage(ppmImg);
            imgLabel.setIcon(new ImageIcon(img));
          }catch(FileNotFoundException ex) {
            JOptionPane.showMessageDialog(
               frame,
               ex.getMessage(),
               "ERROR",
               JOptionPane.ERROR_MESSAGE);
          }catch(UnsupportedFileFormatException ex) {
            JOptionPane.showMessageDialog(
               frame,
               ex.getMessage(),
               "ERROR",
               JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    });

    
    frame.setJMenuBar(menuBar);
  }
  
  public static BufferedImage RGBImage2BufferedImage(RGBImage img) {
    BufferedImage bimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
    for(int y=0; y<img.getHeight(); y++) {
      for(int x=0; x<img.getWidth(); x++) {
        bimg.setRGB(x, y, (new Color(img.getPixel(y,x).getRGB())).getRGB() );
      }
    }
    return bimg;
  }
  
  public static RGBImage BufferedImage2RGBImage(BufferedImage img) {
    RGBImage rgbImg = new RGBImage(img.getWidth(), img.getHeight(), RGBImage.MAX_COLORDEPTH);    
    for(int y=0; y<rgbImg.getHeight(); y++) {
      for(int x=0; x<rgbImg.getWidth(); x++) {
        Color c = new Color(img.getRGB(x,y));
        rgbImg.setPixel(y, x, new RGBPixel((short)c.getRed(), (short)c.getGreen(),(short)c.getBlue()));
      }
    }
    return rgbImg;
  }
}
