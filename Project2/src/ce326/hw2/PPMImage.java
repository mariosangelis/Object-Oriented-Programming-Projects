package ce326.hw2;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PPMImage extends RGBImage{

    public PPMImage() {}
    
    //Creates an RGBImage using another RGBImage object.
    //This is a copy constructor.
    public PPMImage(RGBImage img){
        super(img.getWidth(),img.getHeight(),img.getColorDepth());
        
        for(int i=0;i<img.getHeight();i++){
            for(int j=0;j<img.getWidth();j++){
                setPixel(i,j,img.getPixel(i,j)); 
            }
        }
    }
    
    //Creates an RGBImage using a YUVImage object.
    //This is a copy constructor.
    public PPMImage(YUVImage img){
        this.width = img.getWidth();
        this.height = img.getHeight();
        this.colorDepth = 255;
        this.pixelArray = new RGBPixel[height][width];
        
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                pixelArray[i][j] = new RGBPixel(img.getPixel(i,j));
            }
        }
    }
    
    //Creates an RGB Image by reading a PPM formatted file.
    public PPMImage(java.io.File file) throws FileNotFoundException,UnsupportedFileFormatException{
       
        
        //Check if the file is PPM formatted.
        /**************************START CHECK**************************/
        if(file.exists()==false || file.canRead()==false || file.isDirectory()==true){
            throw new FileNotFoundException();
        }
        
        Scanner testScanner = null;
        
        testScanner = new Scanner(file);

        String initializationString;
        int ppm_width,ppm_height,ppm_colorDepth,red,green,blue;
        
        if(testScanner.hasNext()){
            initializationString = testScanner.next();

            if(initializationString.equals("P3")==false){
                throw new UnsupportedFileFormatException("[ERROR]: File is not PPM formatted");
            }
        }
        else{
            throw new UnsupportedFileFormatException("[ERROR]: File is not PPM formatted");      
        }
        if(testScanner.hasNextInt()){
            ppm_width = testScanner.nextInt();
            if(ppm_width<=0){
                throw new UnsupportedFileFormatException("[ERROR]: File is not PPM formatted");
            }
        }
        else{
            throw new UnsupportedFileFormatException("[ERROR]: File is not PPM formatted");      
        }
        if(testScanner.hasNextInt()){
            ppm_height = testScanner.nextInt();
            if(ppm_height<=0){
                throw new UnsupportedFileFormatException("[ERROR]: File is not PPM formatted");
            }
        }
        else{
            throw new UnsupportedFileFormatException("[ERROR]: File is not PPM formatted");      
        }
        if(testScanner.hasNextInt()){
            ppm_colorDepth = testScanner.nextInt();
            if(ppm_colorDepth<=0 || ppm_colorDepth>MAX_COLORDEPTH){
                throw new UnsupportedFileFormatException("[ERROR]: File is not PPM formatted");
            }
        }
        else{
            throw new UnsupportedFileFormatException("[ERROR]: File is not PPM formatted");      
        }
        
        for(int i=0;i<ppm_height;i++){
            for(int j=0;j<ppm_width;j++){
                for(int k=0;k<3;k++){
                    if(testScanner.hasNext()==false){
                        throw new UnsupportedFileFormatException("[ERROR]: File is not PPM formatted");
                    }
                }   
            }
        }
        
        /**************************END CHECK**************************/
        //Read the PPM formatted file line by line.
        //Initialize width, height, color depth and pixel array.
        Scanner inputScanner = null;
        
        try{
            inputScanner = new Scanner(file);
	}
        catch(FileNotFoundException ex){
            System.out.println("FileNotFoundException occured. Termination of the programm.");
            System.exit(0);
        }

        
        inputScanner.nextLine();
        ppm_width = inputScanner.nextInt();
        ppm_height = inputScanner.nextInt();
        ppm_colorDepth = inputScanner.nextInt();


        height = ppm_height;
        width = ppm_width;
        colorDepth = ppm_colorDepth;
        pixelArray = new RGBPixel[ppm_height][ppm_width];
        
        
        for(int i=0;i<ppm_height;i++){
            for(int j=0;j<ppm_width;j++){
                red = inputScanner.nextInt();
                green = inputScanner.nextInt();
                blue = inputScanner.nextInt();
                pixelArray[i][j] = new RGBPixel((short)red,(short)green,(short)blue);  
            }
        }
    }
    
    
    //Returns a String which contains the PPM format of this image.
    public String toString(){
        StringBuffer str = new StringBuffer("P3\n");
        
        str.append(width+" "+height+"\n");
        str.append(colorDepth+"\n");
        
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                str.append(pixelArray[i][j].getRed()+" "+pixelArray[i][j].getGreen()+" "+pixelArray[i][j].getBlue()+"\n");
            }
        }
        
        return str.toString();
    }
    
    //This method writes the PPM format of this image in a file specified by the first argument.
    public void toFile(java.io.File file) {
        FileWriter myWriter=null;
        
        try {
            file.createNewFile();
            myWriter = new FileWriter(file);
            myWriter.write(toString());
            myWriter.close();
        } catch (IOException ex) {
            System.out.println("IOException inside PPMImage toFile method");
        }
        
    }
     
}
