package hw3;
import java.io.FileNotFoundException;

public class HW3 {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    //Create an object of the SwingClass.
                    new SwingClass();
                } 
                catch (FileNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }
}
