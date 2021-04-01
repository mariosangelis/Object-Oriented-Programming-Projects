package hw3;
import java.io.File;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ModalRenameWindow {
    
    private static String newname;
    private static String oldName;
    
    public ModalRenameWindow(JFrame frame,String oldname){
        oldName=oldname;
        //This is a dialog window.
        JDialog modal = new JDialog(frame,Dialog.ModalityType.DOCUMENT_MODAL);
        
        JTextField name;
        modal.setTitle("Rename modal window");
        ImageIcon icon = new ImageIcon("./delete.png");
        File newfile = new File("./delete.png");
        
        if(newfile.exists()==false){}
        
        Image image = icon.getImage();
        modal.setIconImage(image);
        modal.setPreferredSize(new Dimension(400,200));
        modal.setSize(new Dimension(400,200));
        modal.setLocationRelativeTo(frame);
        
        //We use 2 different layouts. 
        //Modal dialog panel has BorderLayout. Paneltop has centered FlowLayout and panelBottom has left FlowLayout(It contains a textfield and a button).
        modal.setLayout(new BorderLayout());
        JPanel paneltop = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel panelbottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paneltop.setBackground(new Color(163, 194, 194));
        panelbottom.setBackground(new Color(163, 194, 194));
        
        name = new JTextField(oldname);
        JLabel label = new JLabel("Give the new name");
        name.setText(oldname);
        name.setPreferredSize(new Dimension(200,40));
        name.setBackground(Color.WHITE);
        name.setAlignmentY(100);
        
        //Textfiled's Key Listener.
        name.addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    newname = name.getText();
                    modal.dispose();
                }
            }
        });
        
        JButton renameButton = new JButton("Rename");
        renameButton.setBackground(new Color(0, 153, 51));
        renameButton.setFocusPainted(false);
        
        //Button Action Listener.
        renameButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                newname = name.getText();
                modal.dispose();
            }
        });
        
        
        paneltop.add(label);
        panelbottom.add(name);
        panelbottom.add(renameButton);

        modal.add(paneltop,BorderLayout.NORTH);
        modal.add(panelbottom,BorderLayout.CENTER);
        modal.addWindowListener(closeWindow);
        modal.setResizable(true);
        modal.pack();
        modal.setVisible(true);
    }
    
    private static WindowListener closeWindow = new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            newname=oldName;
            e.getWindow().dispose();
        }
    };
    
    
    public String getNewName(){
        return this.newname;
    }
    
}
