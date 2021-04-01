package ce326.hw2;
/*The UnsupportedFileFormatException class is a descendant of the java.lang.Exception class.
An exception to this type is created when we try to read an image file that is different
from what we are trying to read. The class has two constructors:
	public UnsupportedFileFormatException ()
	public UnsupportedFileFormatException (String msg) */

public class UnsupportedFileFormatException extends Exception{
    public UnsupportedFileFormatException(){
            super("Anappropriate type of file");
    }

    public UnsupportedFileFormatException(String msg){
            super(msg);
    }
}

