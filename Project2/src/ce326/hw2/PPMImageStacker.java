package ce326.hw2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class PPMImageStacker {
    
    private File directory;
    private RGBImage stackedImage;
    
    //Initialize directory.
    //Check if the dir file specified by the argument is a directory.
    public PPMImageStacker(java.io.File dir){
        
        if(dir.exists()==false){
            System.out.println("[ERROR] Directory"+dir.getName()+" does not exist!");
            System.exit(0);
        }
        
        if(dir.isDirectory()==false){ 
            System.out.println("[ERROR]"+dir.getName()+" is not a directory!");
            System.exit(0);
        }
        this.directory = dir;

    }
    
    //Create a list which contains each file of the directory which we previously initialized in the constructor.
    //Apply the stacking algorithm.
    public void stack() throws UnsupportedFileFormatException, FileNotFoundException{
        
        ArrayList <PPMImage> array = new ArrayList <PPMImage>();
        
        for (final File fileEntry : directory.listFiles()) {
            array.add(new PPMImage(fileEntry)); 
        }
        
        int tempWidth = array.get(0).getWidth();
        int tempHeight = array.get(0).getHeight();
        int tempColorDepth = array.get(0).getColorDepth();
        int sumRed = 0;
	int sumGreen = 0;
	int sumBlue = 0;
        int i,j,k;
        
        
        stackedImage = new RGBImage(tempWidth,tempHeight,tempColorDepth);
        for(i=0;i<tempHeight;i++){
            for(j=0;j<tempWidth;j++){
                for(k=0;k<array.size();k++){
                    sumRed += array.get(k).getPixel(i,j).getRed();
                    sumGreen += array.get(k).getPixel(i,j).getGreen();
                    sumBlue += array.get(k).getPixel(i,j).getBlue();
                }
                
                stackedImage.getPixel(i,j).setRed((short) (sumRed/array.size()));
                stackedImage.getPixel(i,j).setGreen((short) (sumGreen/array.size()));
                stackedImage.getPixel(i,j).setBlue((short) (sumBlue/array.size()));
                sumRed = 0;
                sumGreen = 0;
                sumBlue = 0;
            }
        }
    }
    
    //Call the stack method, create a PPM image object from the RGB stacked image and return this PPM image object.
    public PPMImage getStackedImage() throws UnsupportedFileFormatException, FileNotFoundException{
        stack();
        
        PPMImage ret = new PPMImage(stackedImage);
        return ret;
        
    }
    
}
