package ce326.hw2;
public class YUVPixel {
    
    private int YUVRepresentation;
    private static final int byteLength = 8;
    
    //Create an RGBPixel object by initializing the Y, U and V values.
    //Put them together inside YUVRepresentation variable using left shifts.
    public YUVPixel(short Y, short U, short V){
        
        YUVRepresentation=0;
        YUVRepresentation = YUVRepresentation | Y;
        YUVRepresentation = YUVRepresentation << byteLength;
        YUVRepresentation = YUVRepresentation | U;
        YUVRepresentation = YUVRepresentation << byteLength;
        YUVRepresentation = YUVRepresentation | V;
        
        
    }
    
    //This method sets the integer which represents the Y,U and V values of this pixel.
    public void setYUV(short Y, short U, short V){
        
        YUVRepresentation=0;
        YUVRepresentation = YUVRepresentation | Y;
        YUVRepresentation = YUVRepresentation << byteLength;
        YUVRepresentation = YUVRepresentation | U;
        YUVRepresentation = YUVRepresentation << byteLength;
        YUVRepresentation = YUVRepresentation | V;
    }
    
    //Create a YUVPixel object using another YUVPixel object.
    //This is a copy constructor.
    public YUVPixel(YUVPixel pixel){
        
        YUVRepresentation=0;
        YUVRepresentation = YUVRepresentation | pixel.getY();
        YUVRepresentation = YUVRepresentation << byteLength;
        YUVRepresentation = YUVRepresentation | pixel.getU();
        YUVRepresentation = YUVRepresentation << byteLength;
        YUVRepresentation = YUVRepresentation | pixel.getV();
        
    }
    
    //Create a YUVPixel object using an RGBPixel object.
    //Use the RGB to YUV transformation formula.
    public YUVPixel(RGBPixel pixel){
        
        short Y = (short) (((66*pixel.getRed() + 129*pixel.getGreen() + 25*pixel.getBlue() +128) >> 8) +16);
        short U = (short) ((((-33)*pixel.getRed() - 74*pixel.getGreen() + 112*pixel.getBlue() +128) >> 8) +128);
        short V = (short) (((112*pixel.getRed() - 94*pixel.getGreen() - 18*pixel.getBlue() +128) >> 8) +128);
        
        YUVRepresentation=0;
        YUVRepresentation = YUVRepresentation | Y;
        YUVRepresentation = YUVRepresentation << byteLength;
        YUVRepresentation = YUVRepresentation | U;
        YUVRepresentation = YUVRepresentation << byteLength;
        YUVRepresentation = YUVRepresentation | V;
        
    }
    
    //This method returns the Y value of this pixel.
    public short getY(){
        int mask = (int) (Math.pow(2,byteLength)) -1;
        mask = mask << 16;
        int ret = YUVRepresentation & mask;
        ret = ret >> 16;

        return(short) (ret); 
    }
    
    //This method returns the U value of this pixel.
    public short getU(){
        int mask = (int) (Math.pow(2,byteLength)) -1;
        mask = mask << 8;
        int ret = YUVRepresentation & mask;
        ret = ret >> 8;
        
        return(short) (ret); 
    }
    
    //This method returns the V value of this pixel.
    public short getV(){
        int mask = (int) (Math.pow(2,byteLength)) -1;
        int ret = YUVRepresentation & mask;
        
        return(short) (ret); 
    }
    
    //This method sets the Y value of this pixel.
    public void setΥ(short newY){
        setYUV(newY,getU(),getV());
    }
    
    //This method sets the U value of this pixel.
    public void setU(short newU){
        setYUV(getY(),newU,getV());
    }
    
    //This method sets the V value of this pixel.
    public void setV(short newV){
        setYUV(getY(),getU(),newV);
    }
}
