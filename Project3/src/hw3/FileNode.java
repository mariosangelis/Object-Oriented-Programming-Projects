package hw3;

import java.awt.FlowLayout;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class FileNode {
    
    private String filename;
    private File fileLink;
    private JLabel fileLabel;
    private ImageIcon icon;
    
    public FileNode(String path){
        
        File file = new File(path);
        if(file.canRead()==false){return;}
        
        this.filename=file.getName();
        this.fileLink=file;
        
        //Create a file label. It has an image and a name.
        fileLabel = new JLabel();
        fileLabel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        if(this.fileLink.isDirectory()){
            //Put the image of a folder because this file is a directory.
            String image_filepath = "./icons/folder.png";
            icon = new ImageIcon(image_filepath);
            fileLabel.setIcon(icon);
        }
        else{
            //Find the suitable image for this file.
            String []token = filename.split("\\.");
            String image_filepath = "./icons/"+token[token.length-1] +".png";
            
            File imagefile = new File(image_filepath);
            //If the type of the file is unrecognized, choose the "question.png" image.
            if(imagefile.exists()==false){
                image_filepath = "./icons/question.png";
            }
            
            icon = new ImageIcon(image_filepath);
            fileLabel.setIcon(icon);
        }
        fileLabel.setText(this.fileLink.getName());
    }
    
    public JLabel getFileLabel(){
        return this.fileLabel;
    }
    
    public ImageIcon getImageIcon(){
        return this.icon;
    }

    public File getFileLink(){
        return this.fileLink;
    }
    
    public String getFileName(){
        return this.filename;
    }
    
    public void setFileLink(File newLink){
        this.fileLink = newLink;
    }
    
    public void setFileName(String newname){
        this.filename=newname;
    }
    
    public void setFileLabel(){
        //This file can be renamed. Also the type of this file may be changed during the rename procedure. 
        ImageIcon icon;
        //Create a file label. It has an image and a name.
        fileLabel = new JLabel();
        fileLabel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        if(this.fileLink.isDirectory()){
            //Put the image of a folder because this file is a directory.
            String image_filepath = "./icons/folder.png";
            icon = new ImageIcon(image_filepath);
            fileLabel.setIcon(icon);
        }
        else{
            //Find the suitable image for this file.
            String []token = filename.split("\\.");
            String image_filepath = "./icons/"+token[token.length-1] +".png";
            
            File imagefile = new File(image_filepath);
            //If the type of the file is unrecognized, choose the "question.png" image.
            if(imagefile.exists()==false){
                image_filepath = "./icons/question.png";
            }
            
            icon = new ImageIcon(image_filepath);
            fileLabel.setIcon(icon);
        }
        fileLabel.setText(this.fileLink.getName());
     }
}
