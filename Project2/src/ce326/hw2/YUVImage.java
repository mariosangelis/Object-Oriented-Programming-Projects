package ce326.hw2;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YUVImage {
    
    protected int width;
    protected int height;
    protected YUVPixel [][] pixelArray;
    
    //Create a YUVImage object.
    public YUVImage(int width, int height){
        
        this.width = width;
        this.height = height;
        this.pixelArray = new YUVPixel[height][width];
        
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                pixelArray[i][j] = new YUVPixel((short)16,(short)128,(short)128);
            }
        }
    }
    
    //Creates a YUVImage using another YUVImage object.
    //This is a copy constructor.
    public YUVImage(YUVImage copyImg){
        
        this.width = copyImg.getWidth();
        this.height = copyImg.getHeight();
        this.pixelArray = new YUVPixel[height][width];
       
        
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                pixelArray[i][j] = copyImg.pixelArray[i][j];
            }
        }
    }
    
    //Creates a YUVImage using an RGBImage object.
    //This is a copy constructor.
    public YUVImage(RGBImage RGBImg){
        
        this.width = RGBImg.getWidth();
        this.height = RGBImg.getHeight();
        this.pixelArray = new YUVPixel[height][width];
        
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                pixelArray[i][j] = new YUVPixel(RGBImg.getPixel(i,j));
            }
        }
    }
    
    //Creates a YUV Image by reading a YUV formatted file.
    public YUVImage(java.io.File file) throws FileNotFoundException,UnsupportedFileFormatException{
        
        //Check if the file is YUV formatted.
        /**************************START CHECK**************************/
        if(file.exists()==false || file.canRead()==false || file.isDirectory()==true){
            throw new FileNotFoundException();
        }
        Scanner testScanner = null;
        
        testScanner = new Scanner(file);

        
        String initializationString;
        int yuv_width,yuv_height,Y,U,V;
        
        if(testScanner.hasNext()){
            initializationString = testScanner.next();
            if(initializationString.equals("YUV3")==false){
                throw new UnsupportedFileFormatException("[ERROR]: File is not YUV formatted");
            }
        }
        else{
            throw new UnsupportedFileFormatException("[ERROR]: File is not YUV formatted");      
        }
        if(testScanner.hasNextInt()){
            yuv_width = testScanner.nextInt();
            if(yuv_width<=0){
                throw new UnsupportedFileFormatException("[ERROR]: File is not YUV formatted");
            }
        }
        else{
            throw new UnsupportedFileFormatException("[ERROR]: File is not YUV formatted");      
        }
        if(testScanner.hasNextInt()){
            yuv_height = testScanner.nextInt();
            if(yuv_height<=0){
                throw new UnsupportedFileFormatException("[ERROR]: File is not YUV formatted");
            }
        }
        else{
            throw new UnsupportedFileFormatException("[ERROR]: File is not YUV formatted");      
        }
        
        for(int i=0;i<yuv_height;i++){
            for(int j=0;j<yuv_width;j++){
                for(int k=0;k<3;k++){
                    if(testScanner.hasNext()==false){
                        throw new UnsupportedFileFormatException("[ERROR]: File is not YUV formatted");
                    }
                }   
            }
        }
        
        /**************************END CHECK**************************/
        //Read the YUV formatted file line by line.
        //Initialize width, height and pixel array.
        Scanner inputScanner = null;
        
        try{
            inputScanner = new Scanner(file);
	}
        catch(FileNotFoundException ex){
            System.out.println("FileNotFoundException occured. Termination of the programm.");
            System.exit(0);
        }

        
        initializationString = inputScanner.nextLine();
        yuv_width = inputScanner.nextInt();
        yuv_height = inputScanner.nextInt();


        height = yuv_height;
        width = yuv_width;
        pixelArray = new YUVPixel[yuv_height][yuv_width];
        
        
        for(int i=0;i<yuv_height;i++){
            for(int j=0;j<yuv_width;j++){
                Y = inputScanner.nextInt();
                U = inputScanner.nextInt();
                V = inputScanner.nextInt();
                pixelArray[i][j] = new YUVPixel((short)Y,(short)U,(short)V);    
            }
        }
    }
    
    //Returns a String which contains the YUV format of this image.
    public String toString(){
        StringBuffer str = new StringBuffer("YUV3\n");
        
        str.append(width+" "+height+"\n");
        
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                str.append(pixelArray[i][j].getY()+" "+pixelArray[i][j].getU()+" "+pixelArray[i][j].getV()+"\n");
            }
        }
        
        return str.toString();
    }
        
    //This method writes the YUV format of this image in a file specified by the first argument.
    public void toFile(java.io.File file){
        FileWriter myWriter = null;
        
        try {
            file.createNewFile();
            myWriter = new FileWriter(file);
            myWriter.write(toString());
            myWriter.close();

        } catch (IOException ex) {
            System.out.println("IOException inside YUVImage toFile method");
        }
 
    }
    
    //This method returns the width of this image.
    public int getWidth(){
        return width;
    }
    
    //This method returns the height of this image.
    public int getHeight(){
        return height;
    }
    
    //This method returns the pixel object in position [row][col] of this image.
    public YUVPixel getPixel(int row, int col){
        return (pixelArray[row][col]);
    }
    
    //This method equalizes the image using the "equalize" method of the Histogram class.
    public YUVImage equalize(){
        
        Histogram histogram = new Histogram(this);
        histogram.equalize();
        
        
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                getPixel(i,j).setΥ((short) histogram.getEqualizedLuminocity(getPixel(i,j).getY()));
            }
        }
        
        return this;
    }
}
