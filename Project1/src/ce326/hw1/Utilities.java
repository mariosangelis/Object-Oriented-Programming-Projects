/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ce326.hw1;

import java.io.*;
import java.util.*;

/**
 *
 * @author gthanos
 */
public class Utilities {
    
    private static final int ALLOC_STEP = 512;
    
    private static String[] expand(String []array) {
        String []newarray = new String [array.length + ALLOC_STEP];
        for(int i=0; i<array.length; i++)
            newarray[i] = array[i];
        return newarray;
    }
    
    public static String[] readFile(String filepath) {
        Scanner sc;
        
        String [] words = new String[ALLOC_STEP];
        File file = new File(filepath);
        if(!file.exists() || !file.isFile() || !file.canRead())
            return null;
        try {
            sc = new Scanner(new File(filepath));
            int i=0;
            while(sc.hasNext()) {
                if(i%ALLOC_STEP == 0)
                    words = expand(words);
                words[i++] = sc.next();
            }
            return words;
        } catch(FileNotFoundException ex) {
            System.err.println("Unable to open file '"+filepath+"' for reading!");
        }
        return null;
    }
    
    public static boolean writeFile(String str, String filepath) {
        try(PrintWriter wr = new PrintWriter(new File(filepath))) {
            wr.print(str);
        }
        catch(FileNotFoundException ex) {
            System.err.println("Unable to write to file '"+filepath+"'");
            return false;
        }
        return true;
    }
    
    public static boolean dot2png(String filepath) {
        try {
            Process p = Runtime.getRuntime().exec("dot -Tps "+filepath+".dot -o "+filepath+".ps");
            p.waitFor();
        } catch(IOException ex) {
            return false;
        } catch(InterruptedException ex) {
            return false;
        }
        return true;
    }
}
