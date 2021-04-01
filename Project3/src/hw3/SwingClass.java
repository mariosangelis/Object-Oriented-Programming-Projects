package hw3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class SwingClass {
    
    public static int WIDTH=1100;
    public static int HEIGHT=700;
    private JFrame frame;
    private JMenuBar menuBar;
    private JMenu file_menu,edit_menu,search_menu;
    private JPanel westPanel,eastPanel,centerPanel,pathPanel,northPanel,searchPanel;
    private StringBuilder Breadcrumb;
    private boolean SearchFieldOn;
    private FileNode []filenodes;
    private FileNode markedNode;
    private FileNode copyPasteNode;
    private boolean cutFlag=false;
    private boolean copyFlag=false;
    private boolean pasteFlag=false;
    private String currentPath;
    public String newname;
    public boolean choice;
    private boolean PropertiesFlag;
    private File []filelist;
    private int max_width_dimension=0;
    private int max_height_dimension=0;
    private JScrollPane centerPanelPane;
    private JScrollPane westPanelPane;
    private JPopupMenu editPopUpMenu;
    private String favouritesPath;
    private Map<String,String> favouritesMap;
    private String homePath;
    private final int smallestFavouritesWidth = 350;
    private JPopupMenu FavouritesPopUpMenu;
    private JButton markedFavouritesButton;
    private LinkedList<String> searchList;
    private static Thread searchThread;
    private JButton searchButton;
    private String separator;
    private String splitSeparator;
    private static MySemaphore lock;
    private String searchPath;
    private JPanel searchcenterPanel;
    private JScrollPane searchcenterPanelPane;
    private static int searchFlag=0;
    private static int stopsearchFlag=0;
    private boolean isMac=false;
    
    public SwingClass() throws FileNotFoundException{
    
        frame = new JFrame("File Explorer");
        ImageIcon icon = new ImageIcon("./build-10158.png");
        Image image = icon.getImage();
        frame.setIconImage(image);
        frame.setLayout(new BorderLayout());
        
        lock = new MySemaphore(1);
        //Set horizontal navigation bar.
        setMenuItems();     
        homePath=System.getProperty("user.home").toString();
        
        //Set the frame body.
        setFrameBody();
        
        //The updateCenterPanel will call the updateBreadCrumb function.
        updateCenterPanel(homePath);
        
        //Initialize favourites map.
        favouritesMap = new HashMap<>();
        favouritesPath = currentPath + separator + "java-file-browser";
        File favourites = new File(favouritesPath);
        searchList = new LinkedList<>();
        
        
        if(favourites.exists()==false){
            PropertiesFlag=false;
            //System.out.println("MAIN :Favourites directory does not exist");
            //Call the updateWestPanel function in order to insert the home favourite.
            updateWestPanel();
        }
        else{
            //Call the updateWestPanel function in order to load the saved favourites.
            favouritesPath = currentPath + separator + "java-file-browser"+separator+"properties.xml";
            PropertiesFlag=true;
            updateWestPanel();
        }
        
        frame.addWindowListener(closeWindow);
        frame.setResizable(true);
        frame.pack();
        frame.setVisible(true);
    }
    
    private static WindowListener closeWindow = new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            JFrame src = (JFrame) e.getSource();
            
            lock.down();
            if(stopsearchFlag==1 || searchFlag==1){
                searchThread.stop();
            }
            lock.up();
            src.dispose();
        }
    };
    
    //This is a JMenuItem listener for the items in the horizontal menu navigation bar.
    ActionListener menuItemListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e){

            String menuString = e.getActionCommand();
            
            if(menuString.equals("New Window") ) {
                //Create a new object from this class.
                lock.down();
                if(searchFlag==1){
                    lock.up();
                }
                else{
                    lock.up();
                    try { 
                        new SwingClass();
                    } 
                    catch (FileNotFoundException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
            else if(menuString.equals("Exit") ) {
                //Close the current frame.
                lock.down();
                if(stopsearchFlag==1 || searchFlag==1){
                    searchThread.stop();
                }
                lock.up();
                frame.dispose();
            }
            else if(menuString.equals("Rename")){
                //Only one flag can be valid at that time.
                copyFlag=false;
                cutFlag=false;
                pasteFlag=false; 
                
                //Create a modal in order to take the new name of the file.
                ModalRenameWindow modal = new ModalRenameWindow(frame,markedNode.getFileLabel().getText());
                newname = modal.getNewName();
                
                File newfile = new File(currentPath+separator+newname);
                //Rename the file specified by the markedNode.
                if(markedNode.getFileLink().renameTo(newfile)){}
                else{
                    System.out.println("Rename failed");
                }
                //The renamed node must be marked after the rename procedure.
                markedNode.setFileName(newname);
                markedNode.setFileLink(newfile);
                markedNode.setFileLabel();
                //Update the center panel.
                try {
                    updateCenterPanel(currentPath);
                } 
                catch (FileNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
                //Disable the edit menu.
                edit_menu.setEnabled(false);
            }
            else if(menuString.equals("Cut")){
                //Only one flag can be valid at that time.
                //Specify the cut node.
                cutFlag=true;
                copyFlag=false;
                pasteFlag=false;
                copyPasteNode=markedNode;
                //Disable the edit menu.
                edit_menu.setEnabled(false);
            }
            else if(menuString.equals("Copy")){
                //Only one flag can be valid at that time.
                //Specify the copy node.
                copyFlag=true;
                cutFlag=false;
                pasteFlag=false; 
                copyPasteNode=markedNode;
                //Disable the edit menu.
                edit_menu.setEnabled(false);
            }
            else if(menuString.equals("Paste")){
                boolean cancelFlag=false;
                if((cutFlag==true || copyFlag==true) && pasteFlag==false){
                    int do_not_delete_flag=0;
                    //Copy/Cut file is a directory.
                    if(copyPasteNode.getFileLink().isDirectory()){
                        
                        File newdirectory;
                        //Suppose that we want to copy the directory "html-css-files" from the directory C://mario to the directory C://mario//Desktop
                        //We go to the directory C://mario and we press a right click over the directory Desktop. We select paste on the pop up menu.
                        //The file must be transferred to the directory C://mario//Desktop//
                        //In the previous example, markedNode is Desktop, currentPath is C://mario, copyPasteNode is ?html-css-files".
                        if(markedNode.getFileLink().getAbsolutePath().equals(currentPath+separator+markedNode.getFileName()) && markedNode.getFileLink().isDirectory()){
                            newdirectory = new File(currentPath+separator+markedNode.getFileName()+separator+copyPasteNode.getFileName());
                        }
                        else{
                            newdirectory = new File(currentPath+separator+copyPasteNode.getFileName());
                        }
                        
                        if(newdirectory.exists()){
                            //If the "new" directory exists, create a modal window to confirm the procedure.
                            ModalCutWindow modal = new ModalCutWindow(frame);
                            choice = modal.getChoice();
                            if(choice==true){
                                
                                if(newdirectory.getAbsolutePath().equals(copyPasteNode.getFileLink().getAbsolutePath())){
                                    //Copy paste to the same folder. Dont do anything.
                                    do_not_delete_flag=1;
                                }
                                else{
                                    //Delete the existing directory using the recursive deleteDirectory function.
                                    deleteDirectory(newdirectory);
                                    if(favouritesMap.containsKey(newdirectory.getName())){
                                        deleteFavourite(newdirectory.getName());
                                    }
                                    //Make the new directory and copy all the files from the Copy/Cut directory to the new directory using the recursive copyDirectory function.
                                    newdirectory.mkdir();
                                    try {
                                        copyDirectory(copyPasteNode.getFileLink(),newdirectory);
                                    } 
                                    catch (IOException ex) {
                                        System.out.println(ex.getMessage());
                                    }
                                }
                            }
                            cancelFlag=choice;  
                        }
                        else{
                            cancelFlag=true;
                            //The "new" directory does not exist.
                            //Make the new directory and copy all the files from the Copy/Cut directory to the new directory using the recursive copyDirectory function.
                            newdirectory.mkdir();
                            try {
                                copyDirectory(copyPasteNode.getFileLink(),newdirectory);
                            } 
                            catch (IOException ex) {
                                System.out.println(ex.getMessage());
                            } 
                        } 
                    }
                    else{
                        //Copy-Cut file is a file.
                        
                        File newfile;
                        
                        //Suppose that we want to copy the file "image.jpg" from the directory C://mario to the directory C://mario//Desktop
                        //We go to the directory C://mario and we press a right click over the directory Desktop. We select paste on the pop up menu.
                        //The file must be transferred to the directory C://mario//Desktop
                        //In the previous example, markedNode is Desktop, currentPath is C://mario, copyPasteNode is "image.jpg".
                        
                        if(markedNode.getFileLink().getAbsolutePath().equals(currentPath+markedNode.getFileName()) && markedNode.getFileLink().isDirectory()){
                            newfile = new File(currentPath+markedNode.getFileName()+separator+copyPasteNode.getFileName());  
                        }
                        else{
                            newfile = new File(currentPath+copyPasteNode.getFileName());
                        }
                        
                        if(newfile.exists()){
                            //If the "new" file exists, create a modal window to confirm the procedure.
                            ModalCutWindow modal = new ModalCutWindow(frame);
                            choice = modal.getChoice();
                            if(choice==true){
                                
                                if(newfile.getAbsolutePath().equals(copyPasteNode.getFileLink().getAbsolutePath())){
                                    //Copy paste to the same folder. Dont do anything.
                                    do_not_delete_flag=1;
                                }
                                else{
                                    //Delete the existing file.
                                    newfile.delete();
                                    newfile = new File(currentPath+copyPasteNode.getFileName());
                                    //Copy the Copy/Cut file's data to the new file.
                                    try {
                                        copyFile(copyPasteNode.getFileLink(),newfile);
                                        centerPanel.updateUI();
                                    } 
                                    catch (IOException ex) {
                                        System.out.println(ex.getMessage());
                                    }
                                }
                            }
                            cancelFlag=choice;
                        }
                        else{
                            //The "new" file does not exist.
                            //Copy the Copy/Cut file's data to the new file.
                            cancelFlag=true;
                            try {
                                copyFile(copyPasteNode.getFileLink(),newfile);
                            } 
                            catch (IOException ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                    }
                    //If the cutFlag is enabled, delete the previous file.
                    if(cutFlag==true && cancelFlag==true){
                        if(copyPasteNode.getFileLink().isDirectory()){
                            if(do_not_delete_flag==0){
                                deleteDirectory(copyPasteNode.getFileLink());
                                //If the directory is included in the favourite links, delete it from favourites panel.
                                if(favouritesMap.containsKey(copyPasteNode.getFileName())){
                                    //System.out.println("Cut directory, cut it from the favourites");
                                    deleteFavourite(copyPasteNode.getFileName());
                                }
                            }
                        }
                        else{
                            if(do_not_delete_flag==0){
                                if(copyPasteNode.getFileLink().delete()) {} 
                                else{System.out.println("Failed to delete the file"); }
                            }
                        }
                    }
                    pasteFlag=true; 
                    cutFlag=false;
                    copyFlag=false;
                    
                    //Update the center panel.
                    try {
                        updateCenterPanel(currentPath);
                    } 
                    catch (FileNotFoundException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                //Disable the edit menu.
                edit_menu.setEnabled(false);
            }
            else if(menuString.equals("Delete")){
                //Create a modal window to confirm the procedure.
                ModalDeleteWindow modal = new ModalDeleteWindow(frame);
                choice = modal.getChoice();
                if(choice==true){
                    if(markedNode.getFileLink().isDirectory()){
                        deleteDirectory(markedNode.getFileLink());
                        //If the directory is included in the favourite links, delete it from favourites panel.
                        if(favouritesMap.containsKey(markedNode.getFileName())){
                            //System.out.println("Delete directory, delete it from the favourites");
                            deleteFavourite(markedNode.getFileName());
                        }
                    }
                    else{
                        if(markedNode.getFileLink().delete()==false){
                            System.out.println("Failed to delete the file");
                        }
                    }
                    
                    //Update the center panel.
                    try {
                        updateCenterPanel(currentPath);
                    } 
                    catch (FileNotFoundException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                //Disable the edit menu.
                edit_menu.setEnabled(false);
            }
            else if(menuString.equals("Properties")){
                //Create a modal to show the properties.
                PropertiesModalWindow modal = new PropertiesModalWindow(frame,markedNode.getFileLink().getAbsolutePath());  
                //Disable the edit menu.
                edit_menu.setEnabled(false);
            }
            else if(menuString.equals("Add to Favourites")){
                if(PropertiesFlag==false){
                    //Set favourites function will create the xml file which includes the favourites.
                    setFavourites();
                    PropertiesFlag=true;
                }
                addToFavourites(markedNode.getFileLink());
                //Disable the edit menu.
                edit_menu.setEnabled(false);
            }
        }
    };
    
    //This function creates the horizontal navigation bar.
    public void setMenuItems(){
        
        menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setPreferredSize(new Dimension(WIDTH,30));
        file_menu=new JMenu("File");
        file_menu.setBackground(Color.WHITE);
        file_menu.setOpaque(true);
        //file_menu.setPreferredSize(new Dimension(50, 40));
        file_menu.setBackground(new Color(102, 179, 255));
        
        //Control the color of the "file_menu" submenu during mouse roll over.
        file_menu.addMouseListener(new MouseListener(){

            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {
                file_menu.setBackground(new Color(102, 179, 255));
                edit_menu.setBackground(Color.WHITE);
                search_menu.setBackground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                file_menu.setBackground(new Color(102, 179, 255));
                edit_menu.setBackground(Color.WHITE);
                search_menu.setBackground(Color.WHITE);
            }
        });
        
        JMenuItem menuItem = new JMenuItem("New Window");
        menuItem.addActionListener(menuItemListener);
        file_menu.add(menuItem);
        menuItem = new JMenuItem("Exit");
        menuItem.addActionListener(menuItemListener);
        file_menu.add(menuItem);
        
        edit_menu=new JMenu("Edit");
        //edit_menu.setPreferredSize(new Dimension(50, 40));
        edit_menu.setBackground(Color.WHITE);
        
        //Control the color of the "edit_menu" submenu during mouse roll over.
        edit_menu.addMouseListener(new MouseListener(){

            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {
                edit_menu.setBackground(new Color(102, 179, 255));
                file_menu.setBackground(Color.WHITE);
                search_menu.setBackground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                file_menu.setBackground(new Color(102, 179, 255));
                edit_menu.setBackground(Color.WHITE);
                search_menu.setBackground(Color.WHITE);
            }
            
        });
        
        edit_menu.setEnabled(false);
        edit_menu.setOpaque(true);
        menuItem = new JMenuItem("Cut");
        menuItem.addActionListener(menuItemListener);
        edit_menu.add(menuItem);
        menuItem = new JMenuItem("Copy");
        menuItem.addActionListener(menuItemListener);
        edit_menu.add(menuItem);
        menuItem = new JMenuItem("Paste");
        menuItem.addActionListener(menuItemListener);
        edit_menu.add(menuItem);
        menuItem = new JMenuItem("Rename");
        menuItem.addActionListener(menuItemListener);
        edit_menu.add(menuItem);
        menuItem = new JMenuItem("Delete");
        menuItem.addActionListener(menuItemListener);
        edit_menu.add(menuItem);
        menuItem = new JMenuItem("Add to Favourites");
        menuItem.addActionListener(menuItemListener);
        edit_menu.add(menuItem);
        menuItem = new JMenuItem("Properties");
        menuItem.addActionListener(menuItemListener);
        edit_menu.add(menuItem);
        search_menu=new JMenu("Search");
        search_menu.setBackground(Color.WHITE);
        search_menu.setOpaque(true);
        //search_menu.setPreferredSize(new Dimension(50, 40));
        SearchFieldOn=true;
        
        
        //Control the color of the "search_menu" submenu during mouse roll over.
        search_menu.addMouseListener(new MouseListener(){

            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                //Enable-Disable the north panel.
                if (e.getButton() == MouseEvent.BUTTON1){
                    if(SearchFieldOn==true){
                        searchPanel.setVisible(false);
                        SearchFieldOn=false;
                    }
                    else{
                        searchPanel.setVisible(true);
                        SearchFieldOn=true;
                    }
                } 
            }

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {
                search_menu.setBackground(new Color(102, 179, 255));
                file_menu.setBackground(Color.WHITE);
                edit_menu.setBackground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                file_menu.setBackground(new Color(102, 179, 255));
                edit_menu.setBackground(Color.WHITE);
                search_menu.setBackground(Color.WHITE);
            }

        });
        
        //Create the "edit" pop up menu.
        createPopUpMenu();
        //Create the "favourites" pop up menu.
        createFavouritesPopUpMenu();
        
        menuBar.add(file_menu);
        menuBar.add(edit_menu);
        menuBar.add(search_menu);
        frame.setJMenuBar(menuBar);
    }
    
    public void setFrameBody(){
        frame.getContentPane().setBackground(Color.WHITE);
        
        //This is the favourites panel. It is included in a JScrollpane.
        westPanel = new JPanel(new BorderLayout());
        westPanel.setBackground(Color.WHITE);
        westPanel.setPreferredSize(new Dimension(260,HEIGHT)); 
        westPanelPane = new JScrollPane(westPanel);
        westPanelPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        westPanelPane.getVerticalScrollBar().setBackground(new Color(230, 230, 230));
        westPanelPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        westPanelPane.getVerticalScrollBar().setUnitIncrement(10);
        
        //Add the west jscrollbar pane to the west side of the frame.
        frame.add(westPanelPane,BorderLayout.WEST);
        
        //This is the east panel. It contains the northpanel and the center panel.
        //North panel contains the search Panel and the path panel.
        //Search panel contains a textfield and a button.
        //Path panel includes the breadcrumb.
        //------------------------------------------  }}
        //                    |    SearchPanel     |  }}
        //                    |---------------------  }} northpanel[}}]
        //                    |    Pathpanel       |  }}
        //                    |---------------------  }}
        //      Westpanel     |                    |  }  eastpanel[}]
        //                    |                    |  }
        //                    |    Centerpanel     |  }
        //                    |                    |  }
        //                    |                    |  }
        //------------------------------------------  }
        eastPanel = new JPanel();
        eastPanel.setLayout(new BorderLayout());
        eastPanel.setBackground(Color.WHITE);
        northPanel = new JPanel();
        northPanel.setBackground(Color.WHITE);
        northPanel.setLayout(new BorderLayout());
        
        searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        JTextField textField = new JTextField("",60);
        textField.setPreferredSize(new Dimension(27,27));
        
        searchPanel.add(textField);
        searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(90,27));
        searchButton.setFocusPainted(false);
        searchPanel.add(searchButton);
        
        //This is the action listener for the search button.
        searchButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton src = (JButton) e.getSource();
                
                if(src.getText().equals("Search")){
                    src.setText("Stop");
                    //The search button was pressed. Take text from textfield and call search function.
                    String token = textField.getText();
                    //Clear the textfield's text.
                    textField.setText("");
                    //Create a new thread. Pass him the search token and the currentPath.
                    searchThread = new Thread(new SearchThread(token));
                    searchThread.start();
                    
                }
                else if(src.getText().equals("Stop")){
                    src.setText("Search");
                    //Send an interrupt to the search thread.
                    searchThread.interrupt();
                }
            }
        });
        
        pathPanel = new JPanel();
        pathPanel.setBackground(Color.WHITE);
        pathPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        //updateBreadcrumb(System.getProperty("user.home").toString());
        //Set the breadcrumb to user's home directory.
        updateBreadcrumb(homePath);
        
        //Add the search panel to the north side of the north panel.
        //Add the path panel to the south side of the north panel.
        northPanel.add(searchPanel,BorderLayout.NORTH);
        northPanel.add(pathPanel,BorderLayout.SOUTH);
        northPanel.setVisible(true);
        
        //Add the north panel to the north side of the east panel.
        eastPanel.add(northPanel,BorderLayout.NORTH);
        centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setPreferredSize(new Dimension(WIDTH,HEIGHT*100));

        centerPanelPane = new JScrollPane(centerPanel);
        centerPanelPane.setBackground(Color.red);
        centerPanelPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        centerPanelPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //Increase the scrollbar speed.
        centerPanelPane.getVerticalScrollBar().setUnitIncrement(10);
        centerPanelPane.getVerticalScrollBar().setBackground(new Color(230, 230, 230));
        
        //This is a component listener for the centerpanel. If the centerpanel is being resized, a component event is occured. 
        //We call the updateCenterPanel function to recompute the width-height of the center panel.
        centerPanelPane.addComponentListener(new ComponentListener(){

            @Override
            public void componentResized(ComponentEvent e) {
                centerPanel.removeAll();
                centerPanel.updateUI();
                try {
                    updateCenterPanel(currentPath);
                } 
                catch (FileNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {}

            @Override
            public void componentShown(ComponentEvent e) {}

            @Override
            public void componentHidden(ComponentEvent e) {} 
        });
        
        eastPanel.setPreferredSize(new Dimension(WIDTH,HEIGHT));
        //Add the center jscrollbar pane to the center side of the east panel.
        eastPanel.add(centerPanelPane,BorderLayout.CENTER);
        //Add the east panel to the center side of the frame.
        frame.add(eastPanel,BorderLayout.CENTER);
    }
    
    //This is the breadcrumb mouse listener.
    MouseListener breadCrumbMouseListener = new MouseListener(){

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            //Set the colors of the pressed button.
            JButton src = (JButton) e.getSource();
            src.setBackground(new Color(204, 245, 255));
            
            //Split the breadcrumb stringbuilder.
            String []token = Breadcrumb.toString().split(">");
            int i;
            
            if(System.getProperty("os.name").split(" ")[0].equals("Linux") || isMac==true){
                currentPath="/";
            }
            else{
                currentPath="";
            }
            
            //Set the currentPath.
            for(i=0;i<token.length;i++){
                currentPath+=token[i];
                if(token[i].equals(src.getText())){break;}
                currentPath+=separator;
             
            }
            if(currentPath.charAt(currentPath.length()-1)!=separator.charAt(0)){
                currentPath+=separator;
            }
            //Update the breadcrumb with the new currentPath.
            updateBreadcrumb(currentPath);
            try {
                updateCenterPanel(currentPath);
            } 
            catch (FileNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {
            //Set the colors of the button.
            JButton src = (JButton) e.getSource();
            src.setBackground(new Color(204, 245, 255));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //Set the colors of the button.
            JButton src = (JButton) e.getSource();
            src.setBackground(Color.WHITE);
        }
    };
    
    //This is the updateBreadcrumb function.
    public void updateBreadcrumb(String filepath){
        
        lock.down();
        if(searchFlag==1 && Breadcrumb.toString().equals("Search Results (loading...)")==false){
            Breadcrumb = new StringBuilder("Search Results (loading...)");
            pathPanel.removeAll();
            JButton button = new JButton("Search Results (loading...)");
            button.setFont(new Font("Courier",Font.BOLD,15));
            button.setOpaque(true);
            button.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            button.setBackground(Color.WHITE);
            button.setFocusPainted(false);
            pathPanel.add(button);
            pathPanel.updateUI();
            lock.up();
            return;
        }
        else if(searchFlag==1 && Breadcrumb.toString().equals("Search Results (loading...)")==true){
            Breadcrumb = new StringBuilder("Search Results");
            pathPanel.removeAll();
            JButton button = new JButton("Search Results");
            button.setFont(new Font("Courier",Font.BOLD,15));
            button.setOpaque(true);
            button.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            button.setBackground(Color.WHITE);
            button.setFocusPainted(false);
            pathPanel.add(button);
            pathPanel.updateUI();
            lock.up();
            return;
        }
        
        if(System.getProperty("os.name").split(" ")[0].equals("Windows")){
            //System.out.println("This is a windows OS");
            separator="\\";
            splitSeparator = "\\\\";
        }
        else if(System.getProperty("os.name").split(" ")[0].equals("Linux")){
            //System.out.println("This is a linux OS");
            separator="/";
            splitSeparator="/";
        }
        else{
            //System.out.println("This is a MAC OS");
            separator="/";
            splitSeparator="/";
            isMac=true;
        }
        
        currentPath = filepath;
        String []token = currentPath.split(splitSeparator);
        int i;

        Breadcrumb = new StringBuilder("");
        pathPanel.removeAll();
        pathPanel.updateUI();
        
        FileNode node = new FileNode(currentPath);
        markedNode = node;
        
        
        for(i=0;i<token.length;i++){
            if((System.getProperty("os.name").split(" ")[0].equals("Linux") && i==0) || (isMac==true && i==0)){
                continue;
            }
            //For each token, create a button and add it to the path panel.
            JButton button = new JButton(token[i]);
            button.addMouseListener(breadCrumbMouseListener);
            button.setFont(new Font("Courier",Font.BOLD,15));
            button.setOpaque(true);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            button.setBackground(Color.WHITE);
            pathPanel.add(button);
            Breadcrumb.append(token[i]);
            if(i!=token.length-1){
                Breadcrumb.append(">");
                JLabel label = new JLabel(">");
                pathPanel.add(label);
            }
        }
        pathPanel.updateUI();
        lock.up();
    }
    
    public void setFavourites(){
        //This function is called only once.
        favouritesPath = homePath + separator+"java-file-browser";
        File favourites = new File(favouritesPath);
        
        //Check is the "java-file-browser" directory exists.
        if(favourites.exists() && favourites.isDirectory()){
            //System.out.println("Favourites directory exists");
        }
        else{
            //Create the directory.
            favourites.mkdir();
        }
        
        favouritesPath += separator+"properties.xml";
        File properties = new File(favouritesPath);
        
        //Check is the properties.xml file inside the "java-file-browser" directory exists.
        if(properties.exists() && properties.canRead() && properties.isFile()){
            //System.out.println("Favourites file exists");
        }
        else{
            try {
                //Create the file. Initialize the xml headers.
                properties.createNewFile();
                FileWriter writer = new FileWriter(properties);
                
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                writer.write("\n<favourites>");
                writer.write("\n</favourites>");
                //Close the writer in order to avoid conflicts during delete.
                writer.flush();
                writer.close();
            } 
            catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    
    public void addToFavourites(File node){
        
        //We can not add a file into favourites.
        if(node.isDirectory()==false){
            System.out.println("Can not add to favourites. This is a file");
            return;
        }
        
        File properties = new File(favouritesPath);
        try {
            Scanner scan = new Scanner(properties);
            
            //Check if there is a favourite with the same name.
            while(scan.hasNext()){
                String token = scan.next();
                if(token.startsWith("name")){
                    String name = token.replaceFirst("name","");
                    name = name.replace('"',' ');
                    name = name.replace('=',' ');
                    name = name.trim();
                    if(name.equals(node.getName())){
                        System.out.println("Favourite exists");
                        scan.close();
                        return;
                    }    
                } 
            }
            scan.close();
            Scanner scan2 = new Scanner(properties);
            
            //Create a duplicate file.
            //Copy all the previous xml data to the new file.
            //Add the new favourite.
            //Delete the old file.
            //Rename the new file to "properties.xml".
            File writefile = new File(favouritesPath+"2");
            writefile.createNewFile();
            FileWriter writer = new FileWriter(writefile);
            
            while(scan2.hasNext()){
                String token = scan2.next();
                if(token.equals("</favourites>")){
                    //Add the new favourite.
                    writer.write('\t'+"<directory name="+'"'+node.getName()+'"'+" path="+'"'+node.getAbsolutePath()+'"'+" />"+'\n');
                    writer.write("</favourites>");
                    writer.flush();
                    //Close the writer in order to avoid conflicts during delete.
                    writer.close();
                    break;
                }
                else{
                    //Copy all the previous xml data to the new file.
                    if(token.equals("<favourites>")==true){
                        writer.write(token);
                        writer.write('\n');
                    }
                    else if(token.startsWith("<?xml")==true){
                        writer.write(token+" ");   
                    }
                    else{
                        if(token.charAt(0)=='<'){
                            writer.append('\t');
                            writer.write(token+" ");
                        }
                        if(token.charAt(token.length()-1)=='>'){
                            writer.write(token);
                            writer.append('\n');    
                        }
                        else if(token.charAt(0)!='<' && token.charAt(token.length()-1)!='>'){
                            writer.write(token+" ");
                        }
                    }
                }
            }
            scan2.close();
            writer.close();
            
            //Delete the old file.
            if(properties.delete()==false){
                System.out.println("delete failed");
            }
            else{
                //Rename the new file to "properties.xml".
                properties = new File(favouritesPath);
                writefile.renameTo(properties);
            }
            //Update the west panel.
            updateWestPanel();
        } 
        catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } 
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    
    public void updateWestPanel(){
           
        westPanel.removeAll();
        westPanel.updateUI();
        //Initialize the favourites map.
        favouritesMap = new HashMap<>();
        //West panel has a left flow layout manager.
        //Add a label to the center of the westPanel.
        JLabel favouritesLabel = new JLabel("Favourites Files or Directories",SwingConstants.CENTER);
        favouritesLabel.setForeground(new Color(0, 138, 230));
        favouritesLabel.setFont(new Font("Courier",Font.BOLD,15));
        westPanel.add(favouritesLabel,BorderLayout.NORTH);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        //This is a MouseListener for each favourites button.
        MouseListener favouritesButtonListener = new MouseListener(){

            @Override
            public void mouseClicked(MouseEvent e) {
                lock.down();
                if(searchFlag==1){
                    lock.up();
                    return;
                }
                lock.up();
                
                JButton src = (JButton) e.getSource();
                src.setBackground(new Color(204, 245, 255));
            
                if(SwingUtilities.isLeftMouseButton(e)){
                    try {
                        //If the user make a left click in a button inside the west panel, we must update the breadcrumb and the center panel in order to load
                        //the files from the path specified by this button.
                        String path;
                        path=favouritesMap.get(src.getText());
                        
                        //-------------------------------------------------------------------------------------------------------
                        
                        updateBreadcrumb(path);
                        updateCenterPanel(path);
                    } 
                    catch (FileNotFoundException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                JButton src = (JButton) e.getSource();
                src.setBackground(new Color(204, 245, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JButton src = (JButton) e.getSource();
                src.setBackground(Color.WHITE);
            }
            
            private void showPopup(MouseEvent e) {
                //This pop up menu includes only the "Delete option".
                if (e.isPopupTrigger()) {
                    JButton src = (JButton) e.getSource();
                    markedFavouritesButton = src;
                    //Can not delete the home favourite.
                    if(markedFavouritesButton.getText().equals("Home")){
                        System.out.println("Can not delete Desktop favourite");
                    }
                    else{
                        FavouritesPopUpMenu.show(e.getComponent(),e.getX(), e.getY());
                    }
                }
            }
        };
        
        String name;
        String path;
        
        //West panel always includes the home favourite button.
        JButton button = new JButton("Home");
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(smallestFavouritesWidth,30));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.addMouseListener(favouritesButtonListener);
        button.setFont(new Font("Courier",Font.BOLD,15));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        
        //Add the <name,path> to the favourites hash map.
        favouritesMap.put("Home",homePath);
        panel.add(button);
        panel.setBackground(Color.WHITE);
        
        //If this flag is false, the properties.xml file does not occur.
        //After inserting the home favourtie button, update the graphical environment.
        if(PropertiesFlag==false){
            westPanel.add(panel,BorderLayout.CENTER);
            westPanel.setPreferredSize(new Dimension(300,HEIGHT));
            westPanel.updateUI();
            lock.down();
            if(searchFlag==0){
                eastPanel.updateUI();
                centerPanelPane.updateUI();
                centerPanel.updateUI();
                lock.up();
            }
            lock.up();

            return;
        }
        
        File properties = new File(favouritesPath);
        
        try {
            //Read the xml file and find the longest path (max_width variable). Also, initialize the favouritesMap.
            Scanner scan = new Scanner(properties);
            int max_width=0;
            
            while(scan.hasNext()){
                String token = scan.next();
                name="";
                path="";
                if(token.startsWith("name")){
                    //A name maybe include spaces (ex Wireless Communications).
                    if(token.charAt(token.length()-1)=='"'){
                        //This name does not include spaces (ex html-css-files).
                        name = token.replaceFirst("name","");
                        name = name.replace('"',' ');
                        name = name.replace('=',' ');
                        name = name.trim();
                    }
                    else{
                        //This name includes spaces(ex Wireless Communications).
                        name = token.replaceFirst("name","");
                        name = name.replace('"',' ');
                        name = name.replace('=',' ');
                        name = name.trim();
                        while(true){
                            token = scan.next();
                            if(token.charAt(token.length()-1)=='"'){
                                token = token.replace('"',' ');
                                token = token.trim();
                                name += " ";
                                name += token;
                                break;
                            }
                            else{
                                token = token.trim();
                                name += " ";
                                name += token;
                            }
                        }
                    }
                    //Set the max width variable
                    if(name.length()>max_width){
                        max_width=name.length();
                    }
                    token = scan.next();
                    
                    //Find the specified path for the previous name.
                    if(token.startsWith("path")){
                        //A path maybe include spaces (ex C:\Users\mario\Desktop\html-css-files\Wireless Communications).
                        if(token.charAt(token.length()-1)=='"'){
                            //This path does not include spaces (ex C:\Users\mario\Desktop\html-css-files).
                            path = token.replaceFirst("path=","");
                            path = path.replace('"',' ');
                            //path = path.replace('/',' ');
                            //path = path.replace('>',' ');
                            //path = path.replace('=',' ');
                            path = path.trim();
                        }
                        else{
                            //This path includes spaces (ex C:\Users\mario\Desktop\html-css-files\Wireless Communications).
                            path = token.replaceFirst("path=","");
                            path = path.replace('"',' ');
                            //path = path.replace('/',' ');
                            //ath = path.replace('>',' ');
                            //path = path.replace('=',' ');
                            path = path.trim();
                            while(true){
                                token = scan.next();
                                if(token.charAt(token.length()-1)=='"'){
                                    token = token.replace('"',' ');
                                    token = token.trim();
                                    //token.replace('/',' ');
                                    token.replace('>',' ');
                                    path += " ";
                                    path += token;
                                    break;
                                }
                                else{
                                    token = token.trim();
                                    path += " ";
                                    path += token;
                                }
                            }
                        }
                    }
                    //Add the <name,path> to the favourites hash map.
                    favouritesMap.put(name,path);
                }
            }
            
            scan.close();
            scan = new Scanner(properties);
            
            //Read again the xml file to set the west panel.
            while(scan.hasNext()){
                String token = scan.next();
                name="";
                if(token.startsWith("name")){
                    //A name maybe include spaces (ex Wireless Communications).
                    if(token.charAt(token.length()-1)=='"'){
                        //This name does not include spaces (ex html-css-files).
                        name = token.replaceFirst("name","");
                        name = name.replace('"',' ');
                        name = name.replace('=',' ');
                        name = name.trim();
                    }
                    else{
                        //This name includes spaces(ex Wireless Communications).
                        name = token.replaceFirst("name","");
                        name = name.replace('"',' ');
                        name = name.replace('=',' ');
                        name = name.trim();
                        while(true){
                            token = scan.next();
                            if(token.charAt(token.length()-1)=='"'){
                                token = token.replace('"',' ');
                                token = token.trim();
                                name += " ";
                                name += token;
                                break;
                            }
                            else{
                                token = token.trim();
                                name += " ";
                                name += token;
                            }
                        }
                    }
                    //Create a button and add it to the west panel.
                    button = new JButton(name);
                    button.setHorizontalAlignment(SwingConstants.LEFT);
                    button.setPreferredSize(new Dimension(Math.max(smallestFavouritesWidth,max_width*10),30));
                    button.setBackground(Color.WHITE);
                    button.setFocusPainted(false);
                    button.addMouseListener(favouritesButtonListener);
                    button.setFont(new Font("Courier",Font.BOLD,15));
                    button.setOpaque(true);
                    button.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
                    panel.add(button);
                    panel.setBackground(Color.WHITE);
                } 
            }
            scan.close();
            
            //Update the west panel's preferred size. The minimum width dimension is smallestFavouritesWidth = 300.
            //The width dimension is the max of {300,max_width*10}.
            //The height dimension is the number of the favourites multiplied by the height of each button (36 including the border spacing).
            //The height of the jscrollbar westPanelPane is stable. Change only the height of the west Panel and the scroll bar will be updated.
            westPanel.setPreferredSize(new Dimension(Math.max(smallestFavouritesWidth,max_width*10),favouritesMap.size()*36));
            westPanelPane.setPreferredSize(new Dimension(Math.max(smallestFavouritesWidth,max_width*10),HEIGHT));
            westPanel.add(panel,BorderLayout.CENTER);
            westPanel.updateUI();
            lock.down();
            if(searchFlag==0){
                eastPanel.updateUI();
                centerPanelPane.updateUI();
                centerPanel.updateUI();
                lock.up();
            }
            lock.up();
            
        }
        catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
        
    public void updateCenterPanel(String filepath) throws FileNotFoundException{

        lock.down();
        if(stopsearchFlag==1){
            stopsearchFlag=0;
            searchcenterPanel.setVisible(false);
            searchcenterPanelPane.setVisible(false);
            //Restore center panel
            centerPanel.setVisible(true);
            centerPanelPane.setVisible(true);
            eastPanel.add(centerPanelPane,BorderLayout.CENTER);
            centerPanel.updateUI();
            centerPanelPane.updateUI();
            searchButton.setEnabled(true);
            //Initialize the list.
            searchList = new LinkedList<>();
            searchThread.stop();
            
        }
        else if(searchFlag==1){
            lock.up();
            return;
        }
        lock.up();
        
        File currentFolder = new File(filepath);
        
        if(currentFolder.exists()==false || currentFolder.canRead()==false || currentFolder.isFile()){
            lock.up();
            throw new FileNotFoundException();
        }
        
        centerPanel.removeAll();
        centerPanel.updateUI();
        //This is a list which contains all the files of the current directory. 
        filelist = null;
        filelist = currentFolder.listFiles();
        int i,j=0,filenodes_length=0;
        
        if(filelist == null){
            lock.up();
            return;
        }
        
        max_width_dimension=0;
        
        for(i=0;i<filelist.length;i++){
            File file = new File(filelist[i].getAbsolutePath());
            if(file.canRead()==true){filenodes_length++;}
            
        }
        filenodes = new FileNode[filenodes_length];
        
        
        
        //Initialize filenodes list by creating a FileNode element for each file.
        //Find the max width of all the labels.
        for(i=0;i<filelist.length;i++){
            
            filenodes[j] = new FileNode(filelist[i].getAbsolutePath());
            if(filenodes[j].getFileLabel()==null){
                continue;
            }
            if(filenodes[j].getFileLabel().getPreferredSize().width > max_width_dimension){
                max_width_dimension = filenodes[j].getFileLabel().getPreferredSize().width;
                max_height_dimension = filenodes[j].getFileLabel().getPreferredSize().height;
            }
            j++;
        }
        max_width_dimension+=20;
        
        //This is the MouseListener for each label.
        MouseListener labelListener = new MouseListener(){

            @Override
            public void mouseClicked(MouseEvent e) {
                
                if(SwingUtilities.isLeftMouseButton(e)){
                    //This is a left mouse click.
                    if(e.getClickCount()==2){
                        //This is a double left mouse click.
                        String tempPath="";
                        JLabel label = (JLabel) e.getSource();
                        
                        //Update the current path.
                        tempPath=currentPath;
                        if(currentPath.charAt(currentPath.length()-1)!=separator.charAt(0)){
                            currentPath+=separator;
                        }
                        currentPath+=label.getText();
                        currentPath+=separator;

                        File temp = new File(currentPath);
                        //The clicked label specifies a directory, so update the breadcrumb and update center panel.
                        if(temp.isDirectory()){
                            updateBreadcrumb(currentPath);
                            try {
                                updateCenterPanel(currentPath);
                            } 
                            catch (FileNotFoundException ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                        else{
                            //The clicked label specifies a file and not a directory.
                            //Reset the current path and open the file.
                            currentPath=tempPath;
                            
                            try {
                                Desktop.getDesktop().open(temp);
                            } 
                            catch (IOException ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                        edit_menu.setEnabled(false);
                    }
                    else{
                        //This is a single left mouse click.
                        JLabel label = (JLabel) e.getSource();
                        int i;
                        //Reset the color of the previous marked label.
                        if(markedNode!=null &&  markedNode.getFileLabel().equals(label)==false){
                            markedNode.getFileLabel().setBackground(Color.WHITE);
                        }
                        
                        //Mark this node.
                        for(i=0;i<filenodes.length;i++){
                            if(filenodes[i].getFileLabel().equals(label)){
                                markedNode = filenodes[i];
                                break;
                            }
                        }
                        //Set the color of the new marked label.
                        label.setBackground(new Color(0, 204, 255));
                        edit_menu.setEnabled(true);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //Set the color of the label.
                JLabel label = (JLabel) e.getSource();
                
                if(label.getBackground().equals(new Color(0, 204, 255))){}
                else{
                    label.setBackground(new Color(204, 245, 255));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //Set the color of the label.
                JLabel label = (JLabel) e.getSource();
                if(label.getBackground().equals(new Color(204, 245, 255))){
                    label.setBackground(Color.WHITE);
                }
            }
            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JLabel label = (JLabel) e.getSource();
                    int i;
                    if(markedNode!=null &&  markedNode.getFileLabel().equals(label)==false){
                        markedNode.getFileLabel().setBackground(Color.WHITE);
                    }

                    for(i=0;i<filenodes.length;i++){
                        if(filenodes[i].getFileLabel().equals(label)){
                            markedNode = filenodes[i];
                            break;
                        }
                    }
                    label.setBackground(new Color(0, 204, 255));
                    edit_menu.setEnabled(true);
                    editPopUpMenu.show(e.getComponent(),e.getX(), e.getY());
                }
            }
        };

        int total_height,final_rows;
        double total_rows;
        //Calculate the number of total rows 
        //centerPanelPane.getWidth() is adaptive. CenterPanelPane has a component listener.
        //(double) (centerPanelPane.getWidth()/max_width_dimension) indicates how much labels a row will have.
        //total_rows indicates how much rows the center panel will have.
        total_rows = (double)filelist.length / (double) (centerPanelPane.getWidth()/max_width_dimension);
        final_rows = (int) Math.ceil(total_rows);
        total_height = final_rows * max_height_dimension;
        
        centerPanel.setPreferredSize(new Dimension(centerPanelPane.getWidth(),total_height));
        for(i=0;i<filenodes_length;i++){
            //Add all the labels to the center panel. Each element will have "max_width_dimension" width.
            filenodes[i].getFileLabel().setPreferredSize(new Dimension(max_width_dimension,max_height_dimension));
            filenodes[i].getFileLabel().addMouseListener(labelListener);
            filenodes[i].getFileLabel().setBackground(Color.WHITE);
            filenodes[i].getFileLabel().setOpaque(true);
            centerPanel.add(filenodes[i].getFileLabel());
        }
        lock.up();
    }
    
    //This is the simple file copy function.
    //We use FileInputStream and FileOutputStream in order to copy the data from the oldfile to the newfile.
    public void copyFile(File oldfile,File newfile) throws FileNotFoundException, IOException{
        
        FileInputStream in = new FileInputStream(oldfile);
        FileOutputStream out = new FileOutputStream(newfile);
        byte []array=new byte[256];
        int readBytes;
        
        while(in.available()>0){
            readBytes = in.read(array);
            out.write(array,0,readBytes);
        }
        out.flush();
        //Close the references in order to avoid collisions during the delete function.
        in.close();
        out.close();
    }
    
    //This is the recursive copy directory function.
    public void copyDirectory(File olddirectory,File newdirectory) throws FileNotFoundException, IOException{
        
        File []filelist = olddirectory.listFiles();
        int i;

        for(i=0;i<filelist.length;i++){
            
            if(filelist[i].isDirectory()){
                File newdir = new File(newdirectory.getAbsolutePath()+separator+filelist[i].getName());
                newdir.mkdir();
                copyDirectory(filelist[i],newdir);
            }
            else{
                File newfile = new File(newdirectory.getAbsolutePath()+separator+filelist[i].getName());
                try {
                    copyFile(filelist[i],newfile);
                } 
                catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }
    
    //This is the recursive delete directory function.
    public void deleteDirectory(File dir){
        
        File []filelist = dir.listFiles();
        int i;

        for(i=0;i<filelist.length;i++){
            
            if(filelist[i].isDirectory()){
                deleteDirectory(filelist[i].getAbsoluteFile());
            }
            else{
                if(filelist[i].delete()){
                    //System.out.println("Deleted successfully file");
                }
                else{
                    System.out.println("Could not delete file");
                }
            }
        }
        if(dir.delete()){
            //System.out.println("Delete directory ok");
        }
        else{
            System.out.println("Could not delete directory");
        }
    }
    
    public void search(String token,String path){
       
        //This function searches for token inside the directory specified by the path argument.
        //This is a recursive function.
        //The search thread calls this function.
        if(token.charAt(token.length()-1)=='>'){
            //The search text is "name" <type>
            //tokens[0] is the name.
            //tokens[1] is the type.
            String[] tokens = new String[2];
            tokens = token.split(" ");
            tokens[0] = tokens[0].toLowerCase();
            tokens[1] = tokens[1].replaceAll("<","");
            tokens[1] = tokens[1].replaceAll(">","");

            File dir = new File(path);
            
            //Load all the files from this directory.
            File []filelist = dir.listFiles();
            
            int i,j;
            String fileType = null;

            if(filelist!=null){ 
                for(i=0;i<filelist.length;i++){
                    //Check for an interrupt in each iteration. If there is an interrupt, that means that the user pressed the stop button.
                    //If there is an interrupt, the search function returns recursively.
                    if (!Thread.currentThread().isInterrupted()){
                        String[] nameTokens;
                        
                        //Find the type of this file.
                        nameTokens = filelist[i].getName().split("\\.");

                        if(nameTokens[0]!=null){
                            fileType=nameTokens[nameTokens.length-1];
                            //If the token is included in the file name and the file has the same type, add the path to the searchlist.
                            if(filelist[i].getName().toLowerCase().contains(tokens[0]) && fileType.equals(tokens[1])){
                                searchList.add(filelist[i].getAbsolutePath());
                            }
                        }
                        //If this file is a directory, call again the recursive search function.
                        if(filelist[i].isDirectory()){
                            search(token,filelist[i].getAbsolutePath());
                        }
                    }
                    else{
                        //Return because of an interrupt.
                        return;
                    }
                }
            }
        }
        else{

            token = token.toLowerCase();
            File dir = new File(path);
            
            //Load all the files from this directory.

            File []filelist = dir.listFiles();
            int i;
            if(filelist!=null){
                for(i=0;i<filelist.length;i++){
                    //Check for an interrupt in each iteration. If there is an interrupt, that means that the user pressed the stop button.
                    //If there is an interrupt, the search function returns recursively.
                    if (!Thread.currentThread().isInterrupted()){
                        //If the token is included in the file name, add the path to the searchlist.
                        if(filelist[i].getName().toLowerCase().contains(token)){
                            searchList.add(filelist[i].getAbsolutePath());
                        }
                        //If this file is a directory, call again the recursive search function.
                        if(filelist[i].isDirectory()){
                            search(token,filelist[i].getAbsolutePath());
                        }
                    }
                    else{
                        //Return because of an interrupt.
                        return;
                    }
                }
            }
        }
    }
    
    public void createPopUpMenu(){
        
        //This is the pop up menu. It is the same with the "edit" submenu inside the horizontal navbar.
        editPopUpMenu = new JPopupMenu();
        editPopUpMenu.setPreferredSize(new Dimension(200,200));
        editPopUpMenu.setEnabled(false);
        editPopUpMenu.setOpaque(true);
        JMenuItem menuItem = new JMenuItem("Cut");
        //menuItem.setBackground(Color.WHITE);
        menuItem.addActionListener(menuItemListener);
        editPopUpMenu.add(menuItem);
        menuItem = new JMenuItem("Copy");
        menuItem.addActionListener(menuItemListener);
        editPopUpMenu.add(menuItem);
        menuItem = new JMenuItem("Paste");
        menuItem.addActionListener(menuItemListener);
        editPopUpMenu.add(menuItem);
        menuItem = new JMenuItem("Rename");
        menuItem.addActionListener(menuItemListener);
        editPopUpMenu.add(menuItem);
        menuItem = new JMenuItem("Delete");
        menuItem.addActionListener(menuItemListener);
        editPopUpMenu.add(menuItem);
        menuItem = new JMenuItem("Add to Favourites");
        menuItem.addActionListener(menuItemListener);
        editPopUpMenu.add(menuItem);
        menuItem = new JMenuItem("Properties");
        menuItem.addActionListener(menuItemListener);
        editPopUpMenu.add(menuItem);
    }
    
    ActionListener FavouritesMenuItemListener = new ActionListener(){

        @Override
        public void actionPerformed(ActionEvent e) {
            //Delete the favourite specified by the markedFavouritesButton.
            //markedFavouritesButton has been set inside westpanel's button listener.
            deleteFavourite(markedFavouritesButton.getText());
        }
    };
    
    public void createFavouritesPopUpMenu(){
        
        //This is the favourites pop up menu.
        FavouritesPopUpMenu = new JPopupMenu();
        FavouritesPopUpMenu.setPreferredSize(new Dimension(200,30));
        FavouritesPopUpMenu.setEnabled(false);
        FavouritesPopUpMenu.setOpaque(true);
        
        JMenuItem menuItem = new JMenuItem("Delete");
        menuItem.addActionListener(FavouritesMenuItemListener);
        FavouritesPopUpMenu.add(menuItem);
    }
    
    public void deleteFavourite(String favourite){
        
        try {
           
            String deletename = favourite;
            String name;
            File properties = new File(favouritesPath);
            Scanner scan = new Scanner(properties);
            //Create a duplicate file.
            //Copy all the previous xml data to the new file.
            //Add the new favourite.
            //Delete the old file.
            //Rename the new file to "properties.xml".
            File writefile = new File(favouritesPath+"2");
            writefile.createNewFile();
            FileWriter writer = new FileWriter(writefile);
            
            //Read the xml file.
            while(scan.hasNext()){
                String token = scan.next();
                name="";
                
                if(token.startsWith("<directory")){
                    token = scan.next();
                    //A name maybe include spaces (ex Wireless Communications).
                    if(token.charAt(token.length()-1)=='"'){
                        //This name does not include spaces (ex html-css-files).
                        name = token.replaceFirst("name","");
                        name = name.replace('"',' ');
                        name = name.replace('=',' ');
                        name = name.trim();
                    }
                    else{
                        //This name includes spaces(ex Wireless Communications).
                        name = token.replaceFirst("name","");
                        name = name.replace('"',' ');
                        name = name.replace('=',' ');
                        name = name.trim();
                        while(true){
                            token = scan.next();
                            if(token.charAt(token.length()-1)=='"'){
                                token = token.replace('"',' ');
                                token = token.trim();
                                name += " ";
                                name += token;
                                break;
                            }
                            else{
                                token = token.trim();
                                name += " ";
                                name += token;
                            }
                        }
                    }
                    
                    //Check if the previous name is the same with the name to delete.
                    if(name.equals(deletename)){
                        //Skip the path (do not write it to the new file).
                        while(true){
                            token=scan.next();
                            if(token.endsWith("/>")){
                                break;
                            }
                        }
                    }
                    else{
                        String path="";
                        while(true){
                            token=scan.next();
                            path += " ";
                            path += token;   
                            if(token.endsWith("/>")){
                                break;
                            }
                        }
                        writer.write('\t'+"<directory name="+'"'+name+'"'+path+'\n');
                    }
                }
                else{
                    //Copy all the previous xml data to the new file.
                    if(token.equals("</favourites>")){
                        writer.write("</favourites>");
                        writer.flush();
                        
                        break;
                    }
                    else{
                        if(token.equals("<favourites>")==true){
                            writer.write(token);
                            writer.write('\n');    
                        }
                        else if(token.startsWith("<?xml")==true){
                            writer.write(token+" ");
                        }
                        else{
                            if(token.charAt(0)=='<'){
                                writer.append('\t');
                                writer.write(token+" ");
                            }
                            if(token.charAt(token.length()-1)=='>'){
                                writer.write(token);
                                writer.append('\n');    
                            }
                            else if(token.charAt(0)!='<' && token.charAt(token.length()-1)!='>'){
                                writer.write(token+" ");
                            }
                        }
                    }
                }
            }
            scan.close();
            writer.close();
            
            //Delete the old file.
            if(properties.delete()==false){
                System.out.println("delete failed");
            }
            else{
            //Rename the new file to "properties.xml".
                properties = new File(favouritesPath);
                writefile.renameTo(properties);
            }
            //Update the west panel.
            updateWestPanel();
        }
        catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    

    public class SearchThread extends Thread {

        private String searchToken;
        private MySemaphore mtx;
        
        
        public SearchThread(String token) {
            this.searchToken=token;
            mtx = new MySemaphore(0);
        }

        public void run() {
            
            //Thread can call the search function now.
            
            search(this.searchToken,currentPath);
            searchButton.setText("Search");
            searchButton.setEnabled(false);
            lock.down();
            searchFlag = 1;
            lock.up();
            updateBreadcrumb("");
            //Search list includes the paths computed by the search function.
            if(searchList.size()!=0){
                //Create a SearchModalWindow to display the paths computed by the search function. 
                //SearchModalWindow modal = new SearchModalWindow(searchList,mtx);
                setSearchWindow();
                //Wait for search to finish.
                mtx.down();
                searchButton.setEnabled(true);
                //Initialize the list.
                searchList = new LinkedList<>();
                //SearchModalWindow object increased the semaphore, so the search function is over.
                //modal.getPath();
                //Get the selected path from the modal. If this path is a directory, update the breadcrump. If it is a file, open it.
                if(searchPath!=null){
                    File searchFile = new File(searchPath);
                    if(searchFile.isDirectory()){
                        
                        updateBreadcrumb(searchPath);
                        try {
                            updateCenterPanel(searchPath);
                        } 
                        catch (FileNotFoundException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                    else{
                        updateBreadcrumb(currentPath);
                        try {
                            Desktop.getDesktop().open(searchFile);
                        } 
                        catch (IOException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                }
            }
        }
        
        public void setSearchWindow(){
            searchcenterPanel = new JPanel();
            centerPanel.setVisible(false);
            pathPanel.setEnabled(false);
            centerPanelPane.setVisible(false);
            
            pathPanel.updateUI();
            centerPanel.updateUI();
            centerPanelPane.updateUI();
            searchcenterPanelPane = new JScrollPane(searchcenterPanel);
            searchcenterPanelPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            searchcenterPanelPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            //Increase the scrollbar speed.
            searchcenterPanelPane.getVerticalScrollBar().setUnitIncrement(10);
            searchcenterPanelPane.getVerticalScrollBar().setBackground(new Color(230, 230, 230));
            
            searchcenterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

            //This is a MouseListener for the search button.
            MouseListener SearchButtonListener = new MouseListener(){

                @Override
                public void mouseClicked(MouseEvent e) {
                    JButton src = (JButton) e.getSource();
                    src.setBackground(new Color(204, 245, 255));

                    if(SwingUtilities.isLeftMouseButton(e)){
                        //This is a left mouse click. Update the path variable and close the modal.
                        lock.down();
                        if(searchFlag==0){
                            lock.up();
                            searchPath=src.getText();
                            searchcenterPanel.setVisible(false);
                            searchcenterPanelPane.setVisible(false);
                            //Restore center panel
                            centerPanel.setVisible(true);
                            centerPanelPane.setVisible(true);
                            eastPanel.add(centerPanelPane,BorderLayout.CENTER);
                            centerPanel.updateUI();
                            centerPanelPane.updateUI();
                            eastPanel.updateUI();
                            lock.down();
                            searchFlag=0;
                            lock.up();
                            mtx.up();
                        }
                        else{
                            lock.up();
                        }
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {}

                @Override
                public void mouseReleased(MouseEvent e) {}

                @Override
                public void mouseEntered(MouseEvent e) {
                    //Update the color of the pressed button.
                    JButton src = (JButton) e.getSource();
                    src.setBackground(new Color(204, 245, 255));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    //Update the color of the pressed button.
                    JButton src = (JButton) e.getSource();
                    src.setBackground(Color.WHITE);
                }
            };

            int i,max_width=0;

            //Calculate the max width of the search list's elements.
            for(i=0;i<searchList.size();i++){
                if(searchList.get(i).length()>max_width){
                    max_width = searchList.get(i).length();
                }
            }
            searchcenterPanelPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            searchcenterPanelPane.getVerticalScrollBar().setBackground(new Color(230, 230, 230));
            searchcenterPanelPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            searchcenterPanelPane.getVerticalScrollBar().setUnitIncrement(10);
            //The height of the jscroll pane is stable.
            //Change only the dimension of the panel.
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            searchcenterPanelPane.setPreferredSize(new Dimension(Math.min(max_width*10,(int) screenSize.getWidth()),500)); 
            searchcenterPanel.setPreferredSize(new Dimension(Math.min(max_width*10,(int) screenSize.getWidth()),searchList.size()*36)); 
            
            //searchcenterPanel.setPreferredSize(new Dimension(max_width*10,800));
            searchcenterPanel.setBackground(Color.WHITE);
            
            
            eastPanel.add(searchcenterPanelPane,BorderLayout.CENTER);
            //Create a button for each path
            for(i=0;i<searchList.size();i++){
                JButton button = new JButton(searchList.get(i));
                button.setHorizontalAlignment(SwingConstants.LEFT);
                button.setPreferredSize(new Dimension(max_width*10,30));
                button.setBackground(Color.WHITE);
                button.setFocusPainted(false);
                button.addMouseListener(SearchButtonListener);
                button.setFont(new Font("Courier",Font.BOLD,15));
                button.setOpaque(true);
                button.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
                searchcenterPanel.add(button);
            }
            searchcenterPanel.updateUI();
            searchcenterPanelPane.updateUI();
            eastPanel.updateUI();
            updateBreadcrumb("");
            lock.down();
            stopsearchFlag=1;
            searchFlag = 0;
            lock.up();
        }
    }
}

