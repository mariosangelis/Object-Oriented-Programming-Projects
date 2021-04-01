package ce326.hw2;
public class RGBPixel {
    
    private int RGBRepresentation;
    private static final int byteLength = 8;
    
    //Create an RGBPixel object by initializing the red, green and blue values.
    //Put them together inside RGBRepresentation variable using left shifts.
    public RGBPixel(short red,short green,short blue){
        
        RGBRepresentation=0;
        RGBRepresentation = RGBRepresentation | red;
        RGBRepresentation = RGBRepresentation << byteLength;
        RGBRepresentation = RGBRepresentation | green;
        RGBRepresentation = RGBRepresentation << byteLength;
        RGBRepresentation = RGBRepresentation | blue;
        
    }
    
    //Create an RGBPixel object using another RGBPixel object.
    //This is a copy constructor.
    public RGBPixel(RGBPixel pixel){
        
        RGBRepresentation=0;
        RGBRepresentation = RGBRepresentation | pixel.getRed();
        RGBRepresentation = RGBRepresentation << byteLength;
        RGBRepresentation = RGBRepresentation | pixel.getGreen();
        RGBRepresentation = RGBRepresentation << byteLength;
        RGBRepresentation = RGBRepresentation | pixel.getBlue();
        
    }
    
    public short clip(short var){
        if(var < 0){return 0;}
        else if(var > 255){return 255;}
        return var;
    }
    
    //Create an RGBPixel object using a YUVPixel object.
    //Use the YUV to RGB transformation formula.
    public RGBPixel(YUVPixel pixel){
        
        short C = (short) (pixel.getY() - 16);
        short D = (short) (pixel.getU() - 128);
        short E = (short) (pixel.getV() - 128);
        
        short red = clip((short) ((298*C + 409*E + 128) >> 8));
        short green = clip((short) ((298*C - 100*D - 208*E + 128) >> 8));
        short blue = clip((short) ((298*C + 516*D + 128) >> 8));
        
        RGBRepresentation=0;
        RGBRepresentation = RGBRepresentation | red;
        RGBRepresentation = RGBRepresentation << byteLength;
        RGBRepresentation = RGBRepresentation | green;
        RGBRepresentation = RGBRepresentation << byteLength;
        RGBRepresentation = RGBRepresentation | blue;
    }
    
    //This method returns the integer which represents the red,green and blue values of this pixel.
    public int getRGB(){
        return RGBRepresentation;
    }
    
    //This method sets the integer which represents the red,green and blue values of this pixel.
    public void setRGB(int newRGBRepresentation){
        RGBRepresentation = newRGBRepresentation;
    }
    
    //This method sets the integer which represents the red,green and blue values of this pixel.
    public final void setRGB(short red,short green,short blue){
        
        RGBRepresentation=0;
        RGBRepresentation = RGBRepresentation | red;
        RGBRepresentation = RGBRepresentation << byteLength;
        RGBRepresentation = RGBRepresentation | green;
        RGBRepresentation = RGBRepresentation << byteLength;
        RGBRepresentation = RGBRepresentation | blue;
        
    }
    
    //This method returns the red value of this pixel.
    public short getRed(){
        
        int mask = (int) (Math.pow(2,byteLength)) -1;
        mask = mask << 16;
        int ret = RGBRepresentation & mask;
        ret = ret >> 16;

        return(short) (ret); 
    }
    
    //This method returns the green value of this pixel.
    public short getGreen(){
        
        int mask = (int) (Math.pow(2,byteLength)) -1;
        mask = mask << 8;
        int ret = RGBRepresentation & mask;
        ret = ret >> 8;
        
        return(short) (ret); 
    }
    
    //This method returns the blue value of this pixel.
    public short getBlue(){
        
        int mask = (int) (Math.pow(2,byteLength)) -1;
        int ret = RGBRepresentation & mask;
        
        return(short) (ret); 
    }
    
    //This method sets the blue value of this pixel.
    public void setBlue(short newBlue){
        
        setRGB(getRed(),getGreen(),newBlue);
    }
    
    //This method sets the green value of this pixel.
    public void setGreen(short newGreen){
        setRGB(getRed(),newGreen,getBlue());
    }
    
    //This method sets the red value of this pixel.
    public void setRed(short newRed){
        
        setRGB(newRed,getGreen(),getBlue());
        
    }
    
    //This method returns a string which contains the RGB representation of this pixel.
    public String toString(){
        return("R: "+getRed() +" G:"+getGreen()+" B:"+getBlue()); 
    }
    
    

}
