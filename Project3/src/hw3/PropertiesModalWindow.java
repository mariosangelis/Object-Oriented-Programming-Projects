package hw3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PropertiesModalWindow {

    private final int labelWidth = 150; 
    
    public PropertiesModalWindow(JFrame frame,String path){
        
        FileNode node = new FileNode(path);
        //This is a dialog window.
        JDialog modal = new JDialog(frame,Dialog.ModalityType.DOCUMENT_MODAL);
        
        modal.setTitle("Properties modal window");
        ImageIcon icon = new ImageIcon("./build-10158.png");
        Image image = icon.getImage();
        modal.setIconImage(image);
        modal.setPreferredSize(new Dimension(node.getFileLink().getAbsolutePath().length()*10+200,400));
        modal.setSize(new Dimension(node.getFileLink().getAbsolutePath().length()*10+200,400));
        modal.setLocationRelativeTo(frame);
        modal.setLayout(new BorderLayout());        
        
        //This linepanel includes the icon.
        JPanel linepanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linepanel.setBackground(Color.WHITE);
        JLabel label = new JLabel();
        icon = node.getImageIcon();
        label.setIcon(icon);
        linepanel.add(label);
        modal.add(linepanel,BorderLayout.NORTH);
        
        //This linepanel includes all the other properties of the file.
        linepanel = new JPanel(new GridLayout(0,1));
        linepanel.setBackground(Color.WHITE);
        linepanel.setPreferredSize(new Dimension(300,350));
        
        //This subpanel includes the name priorities of the file.
        JPanel linesubpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linesubpanel.setBackground(Color.WHITE);
        
        label = new JLabel("Name :");
        label.setPreferredSize(new Dimension(labelWidth,20));
        label.setFont(new Font("Courier",Font.BOLD,15));
        linesubpanel.add(label);
        label = new JLabel(node.getFileName());
        label.setFont(new Font("Courier",Font.CENTER_BASELINE,12));
        linesubpanel.add(label);
        linepanel.add(linesubpanel);
        
        //This subpanel includes the location (path) priorities of the file.
        linesubpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linesubpanel.setBackground(Color.WHITE);
        label = new JLabel("Location :");
        label.setPreferredSize(new Dimension(labelWidth,20));
        label.setFont(new Font("Courier",Font.BOLD,15));
        linesubpanel.add(label);
        label = new JLabel(node.getFileLink().getAbsolutePath());
        label.setFont(new Font("Courier",Font.CENTER_BASELINE,12));
        linesubpanel.add(label);
        linepanel.add(linesubpanel);
        
        //This subpanel includes the size priorities of the file.
        linesubpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linesubpanel.setBackground(Color.WHITE);
        label = new JLabel("Size on disk: ");
        label.setPreferredSize(new Dimension(labelWidth,20));
        label.setFont(new Font("Courier",Font.BOLD,15));
        linesubpanel.add(label);
        if(node.getFileLink().isFile()){
            label = new JLabel(String.valueOf(node.getFileLink().length()+" bytes"));
        }
        else{
            label = new JLabel(String.valueOf(DirectorySize(node.getFileLink())+" bytes"));
        }
        
        label.setFont(new Font("Courier",Font.CENTER_BASELINE,12));
        linesubpanel.add(label);
        linepanel.add(linesubpanel);
        
        //This subpanel includes the last modification time of the file.
        linesubpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linesubpanel.setBackground(Color.WHITE);
        label = new JLabel("Modified: ");
        label.setPreferredSize(new Dimension(labelWidth,20));
        label.setFont(new Font("Courier",Font.BOLD,15));
        linesubpanel.add(label);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        label = new JLabel(sdf.format( node.getFileLink().lastModified()));
        label.setFont(new Font("Courier",Font.CENTER_BASELINE,12));
        linesubpanel.add(label);
        linepanel.add(linesubpanel);
        
        modal.add(linepanel,BorderLayout.CENTER);
        
        //This panel includes the attribute priorities of the file.
        linepanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        //This subpanel includes 3 checkboxes, read,write and execute.
        linesubpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linepanel.setBackground(Color.WHITE);
        linesubpanel.setBackground(Color.WHITE);
        label = new JLabel("Attributes: ");
        linepanel.add(label);
        
        //Read checkbox.
        JCheckBox read = new JCheckBox("Read");
        read.setBackground(Color.WHITE);
        read.setFocusPainted(false);
        read.setBorder(BorderFactory.createEmptyBorder(0,20,0,0));
        
        //Write checkbox.
        JCheckBox write = new JCheckBox("Write");
        write.setBackground(Color.WHITE);
        write.setFocusPainted(false);
        
        //Execute checkbox.
        write.setBorder(BorderFactory.createEmptyBorder(0,20,0,0));
        JCheckBox execute = new JCheckBox("Execute");
        execute.setBackground(Color.WHITE);
        execute.setFocusPainted(false);
        execute.setBorder(BorderFactory.createEmptyBorder(0,20,0,0));
        
        //Set attributes visibility.
        if(node.getFileLink().canRead()){
            if(node.getFileLink().setReadable(false)==false){
                read.setEnabled(false);
                read.setSelected(true);
            }
            else{
                node.getFileLink().setReadable(true);
                read.setEnabled(true);
                read.setSelected(true);
            }
        }
        else{
            if(node.getFileLink().setReadable(true)==false){
                read.setEnabled(false);
                read.setSelected(false);
            }
            else{
                node.getFileLink().setReadable(false);
                read.setEnabled(true);
                read.setSelected(false);
            }
        }
        
        if(node.getFileLink().canWrite()){
            if(node.getFileLink().setWritable(false)==false){
                write.setEnabled(false);
                write.setSelected(true);
            }
            else{
                node.getFileLink().setWritable(true);
                write.setEnabled(true);
                write.setSelected(true);
            }
        }
        else{
            if(node.getFileLink().setWritable(true)==false){
                write.setEnabled(false);
                write.setSelected(false);
            }
            else{
                node.getFileLink().setWritable(false);
                write.setEnabled(true);
                write.setSelected(false);
            }
        }
        
        if(node.getFileLink().canExecute()){
            if(node.getFileLink().setExecutable(false)==false){
                execute.setEnabled(false);
                execute.setSelected(true);
            }
            else{
                node.getFileLink().setExecutable(true);
                execute.setEnabled(true);
                execute.setSelected(true);
            }
        }
        else{
            if(node.getFileLink().setExecutable(true)==false){
                execute.setEnabled(false);
                execute.setSelected(false);
            }
            else{
                node.getFileLink().setExecutable(false);
                execute.setEnabled(true);
                execute.setSelected(false);
            }
        }
        
        //This is an ItemListener for each checkbox.
        ItemListener checkboxlistener = new ItemListener(){

            @Override
            public void itemStateChanged(ItemEvent e) {
                
                JCheckBox src = (JCheckBox) e.getSource();
                
                if(e.getStateChange()==ItemEvent.DESELECTED){
                    //Deselected state change.
                    if(src==read){
                        if(node.getFileLink().setReadable(false,false)==false){
                            System.out.println("setReadable false failed");
                        }
                    }
                    else if(src==write){
                        if(node.getFileLink().setWritable(false)==false){
                            System.out.println("setWritable false failed");
                        }
                    }
                    else{
                        if(node.getFileLink().setExecutable(false)==false){
                            System.out.println("setExecutable false failed");
                        }
                    }
                }
                else{
                    //Selected state change.
                    if(src==read){
                        node.getFileLink().setReadable(true);
                    }
                    else if(src==write){
                        node.getFileLink().setWritable(true);
                    }
                    else{
                        node.getFileLink().setExecutable(true);
                    }
                }
            }
        };
        read.addItemListener(checkboxlistener);
        write.addItemListener(checkboxlistener);
        execute.addItemListener(checkboxlistener);
        
        label.setFont(new Font("Courier",Font.BOLD,15));
        linesubpanel.add(read);
        linesubpanel.add(write);
        linesubpanel.add(execute);
        linepanel.add(linesubpanel);

        modal.add(linepanel,BorderLayout.SOUTH);
        modal.addWindowListener(closeWindow);
        modal.setResizable(true);
        modal.pack();
        modal.setVisible(true);
    }
    
    private static WindowListener closeWindow = new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            e.getWindow().dispose();
        }
    };
    
    //This function calculates the size of a directory.
    //This function is recursive.
    public static long DirectorySize(File directory) {
        long length = 0;
        File []filelist = directory.listFiles();
        int i;
        
        if(filelist == null){
            return 0;
        }

        for(i=0;i<filelist.length;i++){
            if(filelist[i].isDirectory()){
                length += DirectorySize(filelist[i]);
            }
            else{
                length += filelist[i].length();
            }
        }
        return length;
    }
}
