package ce326.hw2;

public class RGBImage implements Image{
    protected int width;
    protected int height;
    protected int colorDepth;
    protected RGBPixel [][] pixelArray;
    public static int MAX_COLORDEPTH = 255;
    
    public RGBImage(){}
    
    //Create an RGBImage object.
    public RGBImage(int width, int height, int colordepth){
        
        this.width = width;
        this.height = height;
        this.colorDepth = colordepth;
        this.pixelArray = new RGBPixel[height][width];
        
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                pixelArray[i][j] = new RGBPixel((short)0,(short)0,(short)0);
            }
        }
    }
    
    //Create an RGBImage object using another RGBImage object.
    //This is a copy constructor.
    public RGBImage(RGBImage copyImg){
        this.width = copyImg.getWidth();
        this.height = copyImg.getHeight();
        this.colorDepth = copyImg.getColorDepth();
        this.pixelArray = new RGBPixel[height][width];
        
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                RGBPixel pixel= copyImg.getPixel(i,j);
                this.pixelArray[i][j] = new RGBPixel(pixel.getRed(),pixel.getGreen(),pixel.getBlue());
            }
        }
    }
    
    //Create an RGBImage object using another YUVImage object.
    //This is a copy constructor.
    public RGBImage(YUVImage copyImg){
        
        this.width = copyImg.getWidth();
        this.height = copyImg.getHeight();
        this.colorDepth = 255;
        this.pixelArray = new RGBPixel[height][width];
        
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                this.pixelArray[i][j] = new RGBPixel(copyImg.getPixel(i,j));
            }
        }
    }
    
    //This method returns the width of this image.
    public int getWidth(){
        return width;
    }
    
    //This method height the width of this image.
    public int getHeight(){
        return height;
    }
    
    //This method color Depth the width of this image.
    public int getColorDepth(){
        return colorDepth;
    }
            
    //This method returns the pixel object in position [row][col] of this image.
    public RGBPixel getPixel(int row, int col){
        return (pixelArray[row][col]);
    }
    
    //This method sets the pixel object in position [row][col] of this image using another "RGBPixel" pixel.
    public void setPixel(int row, int col,RGBPixel pixel){
        pixelArray[row][col].setRed(pixel.getRed());
        pixelArray[row][col].setGreen(pixel.getGreen());
        pixelArray[row][col].setBlue(pixel.getBlue());
    }

    //This method transforms the image to greyscale form.
    @Override
    public void grayscale(){
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                short grey = (short) (pixelArray[i][j].getRed()*0.3 + pixelArray[i][j].getGreen()*0.59 + pixelArray[i][j].getBlue()*0.11);
                
                pixelArray[i][j].setRed(grey); 
                pixelArray[i][j].setGreen(grey); 
                pixelArray[i][j].setBlue(grey); 
            }
        }
    }

    //This method creates a double size image.
    @Override
    public void doublesize() {
        
        RGBPixel [][] newImage = new RGBPixel[2*height][2*width];
        
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
              
                newImage[2*row][2*col] = pixelArray[row][col];
                newImage[(2*row)+1][2*col] = pixelArray[row][col];
                newImage[2*row][(2*col)+1] = pixelArray[row][col];
                newImage[(2*row)+1][(2*col)+1] = pixelArray[row][col];
            }
        }
        
        pixelArray = newImage;
        newImage = null;
        this.height = height*2;
        this.width = width*2;
        
    }

    //This method creates a half size image.
    @Override
    public void halfsize() {
        RGBPixel [][] newImage = new RGBPixel[height/2][width/2];
        
        for(int i=0;i<height/2;i++){
            for(int j=0;j<width/2;j++){
                newImage[i][j] = new RGBPixel((short)0,(short)0,(short)0);
            }
        }
        
        for(int row=0;row < (height/2);row++){
            for(int col=0;col< (width/2);col++){
                int sumRGB_red = pixelArray[2*row][2*col].getRed() + pixelArray[(2*row)+1][2*col].getRed() + pixelArray[2*row][(2*col)+1].getRed() + pixelArray[(2*row)+1][(2*col)+1].getRed();
                int sumRGB_green = pixelArray[2*row][2*col].getGreen() + pixelArray[(2*row)+1][2*col].getGreen() + pixelArray[2*row][(2*col)+1].getGreen() + pixelArray[(2*row)+1][(2*col)+1].getGreen();
                int sumRGB_blue = pixelArray[2*row][2*col].getBlue() + pixelArray[(2*row)+1][2*col].getBlue() + pixelArray[2*row][(2*col)+1].getBlue() + pixelArray[(2*row)+1][(2*col)+1].getBlue();
                newImage[row][col].setRed((short) (sumRGB_red/4));
                newImage[row][col].setGreen((short) (sumRGB_green/4));
                newImage[row][col].setBlue((short) (sumRGB_blue/4));
                
            }
        }
        this.height = height/2;
        this.width = width/2;
        pixelArray = newImage;
        newImage = null;
    }

    //This method rotates the image by 90 degrees clockwise.
    @Override
    public void rotateClockwise() {
        RGBPixel [][] newImage = new RGBPixel[width][height];
        
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                newImage[col][height-1-row] = pixelArray[row][col]; 
            }
        }
        
        pixelArray = newImage;
        newImage = null;
        int temp = height;
        height=width;
        width=temp;
    }
    
}
