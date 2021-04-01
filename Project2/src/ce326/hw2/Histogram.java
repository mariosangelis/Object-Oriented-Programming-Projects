package ce326.hw2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Histogram {
    
    private double [] histogramArray;
    private int width;
    private int height;
    
    //This method creates the histogram of the YUV image specified by the argument.
    public Histogram(YUVImage img){
        
        histogramArray = new double[236];
        
        for(int i=0;i<236;i++){
            histogramArray[i] = 0;
        }
        width = img.getWidth();
        height = img.getHeight();
        
        
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                for(int k=0;k<236;k++){
                    if(img.getPixel(i,j).getY() == k){
                        histogramArray[k]++;
                        break;
                    }
                }
            }
        }
         
    }
    
    //This method computes the probability mass function of all the pixels in this image.
    //After that, it computes the cumulative distributive function of all the pixels in this image.
    //Last, it computes the new equalized value for each luminocity value.
    public void equalize(){
        
        double sum=0;
        int maxLuminocity = 235;
        
        for(int i=0;i<236;i++){
            histogramArray[i] = histogramArray[i]/(height*width);
        }
        
        for(int i=0;i<236;i++){
            sum += histogramArray[i];
            histogramArray[i] = sum;
        }
        for(int i=0;i<236;i++){
            histogramArray[i] = maxLuminocity*histogramArray[i];
        } 
    }
    
    //This method returns a string which contains the "histogram" format of this image.
    public String toString(){
        
        StringBuilder ret = new StringBuilder("");
        
        for(int i=0;i<236;i++){
            ret.append(i);
            
            for(int j=0;j<((int)(histogramArray[i]/1000));j++){
                ret.append("#");
            }
            for(int j=0;j<((int)((int)(histogramArray[i]%1000))/100);j++){
                ret.append("$");
            }
            for(int j=0;j<((int)(histogramArray[i]%100));j++){
                ret.append("*");
            }
            ret.append("\n");   
        }
        return ret.toString();
    }
    
    //This method writes the "histogram" format of this image in a file specified by the first argument.
    public void toFile(File file) throws IOException{
        
        if (file.createNewFile()) {
            //System.out.println("File created: " + file.getName());
        } 
        else {
            //System.out.println("File already exists.");
        }
        FileWriter myWriter = new FileWriter(file);
        myWriter.write(toString());
        myWriter.close();
    }
    
    //This method returns the new equalized luminocity value in the position specified by the first argument.
    public short getEqualizedLuminocity(int luminocity){
        return (short) histogramArray[luminocity];
    }
    
}

