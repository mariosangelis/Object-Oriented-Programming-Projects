package hw3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ModalCutWindow {
    
    private static boolean choice;
    public ModalCutWindow(JFrame frame){
        
        //This is a dialog window.
        JDialog modal = new JDialog(frame,Dialog.ModalityType.DOCUMENT_MODAL);
        JButton yes;
        JButton no;
        
        ImageIcon icon = new ImageIcon("./build-10158.png");
        Image image = icon.getImage();
        modal.setIconImage(image);
        modal.setPreferredSize(new Dimension(400,200));
        modal.setSize(new Dimension(400,200));
        modal.setLocationRelativeTo(frame);
        
        modal.setTitle("Copy-Paste modal window");
        modal.setLayout(new BorderLayout());
        //We use 2 different layouts. Modal dialog panel has BorderLayout and each subpanel(paneltop,panelbottom) has centered FlowLayout.
        JPanel paneltop = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel panelbottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paneltop.setBackground(new Color(163, 194, 194));
        panelbottom.setBackground(new Color(163, 194, 194));
        
        JLabel label = new JLabel("File exists. Do you want to replace it ?");
        yes= new JButton("Yes Replace");
        yes.setBackground(Color.red);
        yes.setFocusPainted(false);
        no= new JButton("Cancel");
        no.setBackground(new Color(0, 153, 51));
        no.setFocusPainted(false);
        
        ActionListener buttonlistener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                JButton src = (JButton) e.getSource();
                if(src.getActionCommand().equals("Yes Replace")){
                    choice=true;
                    modal.dispose();
                }
                else{
                    choice=false;
                    modal.dispose();
                }
            }  
        };
       
        yes.addActionListener(buttonlistener);
        no.addActionListener(buttonlistener);
        paneltop.add(label);
        panelbottom.add(yes);
        panelbottom.add(no);
        modal.add(paneltop,BorderLayout.NORTH);
        modal.add(panelbottom,BorderLayout.CENTER);
        modal.addWindowListener(closeWindow);
        modal.setResizable(true);
        modal.pack();
        modal.setVisible(true);
    }
    
    private static WindowListener closeWindow = new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            choice=false;
            e.getWindow().dispose();
        }
    };

    public boolean getChoice(){
        return this.choice;
    }
}
