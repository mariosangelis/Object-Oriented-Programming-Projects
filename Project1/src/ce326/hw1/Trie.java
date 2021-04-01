package ce326.hw1;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Trie {
    private node root;
    private static final int arrayLength=26;
    private static int numOfWords;
    private static char alphabet[];
    
    
    public Trie(String[] words){
        this.root = new node('-');
        numOfWords=0;
        this.alphabet = new char[arrayLength];
        alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        
        for(int i=0;i<words.length;i++){
            if(words[i]==null){break;}
            if(add(words[i])==false){/*System.out.println("Word "+words[i]+" is contained inside structure");*/}; 
        }  
    }
    
    public String toString(){
        return(this.print_preorder(root));
    }
    
    public node getRoot(){
        return this.root;
    }
    
    public int size(){
        return numOfWords;
    }
    
    //This function returns the position of a letter in the English alphabet.
    public int getLetterPosition(char letter){
        
        for(int i=0;i<alphabet.length;i++){
            if(alphabet[i]==letter){
                return i;
            }  
        }
        return -1;
    }
    
    //This function returns true if the word given as an argument is found in the Trie structure.
    //This function is not recursive.
    public boolean contains(String word){
        
        node nextNode;
        nextNode=this.root;
        int pos=0,letterPosition=0;
        char letter;
  
        while(true){
            letter=word.charAt(pos);
            letterPosition=getLetterPosition(letter);
            
            if(nextNode.getChildren()[letterPosition]==null){
                return false;   
            }
            nextNode=nextNode.getChildren()[letterPosition];
            if(pos==word.length()-1 && nextNode.isTerminal()==true){
                break;
            }
            else if(pos==word.length()-1 && nextNode.isTerminal()==false){
                return false;
            }
            
            pos+=1;
        }
        
        return true;
    }
    
    //This function adds the word given as an argument in the Trie strucure.
    //This function is not recursive.
    public boolean add(String word){
        
        //At first, we check if the specified word is contained in the Trie. If we find the word, the function returns false.
        if(this.contains(word)==true){
            return false;
        }
        
        node nextNode;
        nextNode=this.root;
        int pos=0,letterPosition=0;
        char letter;
  
        while(true){
            letter=word.charAt(pos);
            letterPosition=getLetterPosition(letter);
            
            if(nextNode.getChildren()[letterPosition]==null){
                node newNode=new node(letter);
                nextNode.getChildren()[letterPosition]=newNode;     
            }
            nextNode=nextNode.getChildren()[letterPosition];
            if(pos==word.length()-1){
                numOfWords++;
                nextNode.setTerminal();
                break;
            }
            
            pos+=1;
        }
        return true;
    }
    
    //This function creates a string which contains the pre-order form of the Trie structure.
    //This function is recursive.
    public String print_preorder(node nextNode){
        
        String retStr=new String();
        node[] children=nextNode.getChildren();
        
        for(int i=0;i<children.length;i++){
            if(children[i]!=null){
                retStr += " ";
                retStr += children[i].getLetter();
                
                if(children[i].isTerminal()){
                    retStr += "!";
                }
                retStr+=print_preorder(children[i]);
            }
        }
        return retStr;
    }

    //This function creates a string which contains the dot file form of the Trie structure.
    public String toDotString(){
        String retStr="";
        retStr += "graph Trie {\n\t" +this.getRoot().hashCode()+"[label=\"ROOT\" ,shape=circle, color=black]\n";
        retStr += toDotStringRecursive(root);
        retStr += "}";
        return(retStr);
    }
    
    //This function does the same job with the pre order function, but instead of making a string which contains the letters of the nodes, it makes a string
    //which contains the dot representation of the Trie structure.
    //This function is recursive.
    public String toDotStringRecursive(node nextNode){
        
        String retStr=new String();
        node[] children=nextNode.getChildren();
        
        
        for(int i=0;i<children.length;i++){
            if(children[i]!=null){
                
                if(children[i].isTerminal()){
                    retStr += ("\t" + children[i].hashCode() + " [label=\""+children[i].getLetter()+"\", shape=circle, color=red]\n");
                }
                else{
                    retStr += ("\t" + children[i].hashCode() + " [label=\""+children[i].getLetter()+"\", shape=circle, color=black]\n");
                }
                retStr += ("\t" + nextNode.hashCode() + " -- "+children[i].hashCode()+"\n");
                
                retStr+=toDotStringRecursive(children[i]);
            }
        }
        return retStr;
    }
    
    //This function at first calls the "contains" function in order to see if the word given as an argument is contained in the Trie structure.
    //If the word is not contained in the Trie, the function finds all the similar words which differ from the argument's word by "maxDifferentLetters" letters.
    public String[] differBy(String word,int maxDifferentLetters){
        
        if(contains(word)){
            System.out.println(word);
            return null;
        }
        //Create an array of strings and initialize each position to null.
        String[] arrayOfWords=new String[100];

        for(int j=0;j<arrayOfWords.length;j++){
            arrayOfWords[j]=new String();
            arrayOfWords[j]=null;
        }
        //Call the differ_string function. This function will return a list of similar words each of which differs from the argument's word by "maxDifferentLetters" letters.
        differ_string(this.getRoot(),word,word.length(),0,maxDifferentLetters,"",arrayOfWords);
        return arrayOfWords;
    }
    
    //This function at first calls the "contains" function in order to see if the word given as an argument is contained in the Trie structure.
    //If the word is not contained in the Trie, the function finds all the similar words which differ from the argument's word by 1 letter.
    public String[] differByOne(String word){
        
        if(contains(word)){
            System.out.println(word);
            return null;
        }
        //Create an array of strings and initialize each position to null.
        String[] arrayOfWords=new String[100];

        for(int j=0;j<arrayOfWords.length;j++){
            arrayOfWords[j]=new String();
            arrayOfWords[j]=null;
        }
        //Call the differ_string function. This function will return a list of similar words each of which differs from the argument's word by 1 letter.
        differ_string(this.getRoot(),word,word.length(),0,1,"",arrayOfWords);
        return arrayOfWords; 
    }
    
    //Each node calls his not-null children, passing them the string "retStr" as an argument.
    //Each children adds his letter to the string "retStr" and the procedure is repeated recursively.
    //Only the terminal nodes can add the string to the array.
    //This function is recursive.
    public void differ_string(node nextNode,String word,int length,int position,int differentLetters,String retStr,String[] array){
    
        node[] children=nextNode.getChildren();
        String myRetStr=new String();
        int myLength=length,j;
        int myDiffLength=differentLetters;
        int myPosition=position;
        myRetStr=String.valueOf(retStr);
        
        //Add the letter of this node to the string "retStr".
        if(nextNode!=this.getRoot()){
            myRetStr += nextNode.getLetter();
            
            //Check if the node's letter is contained in the word at position "myPosition"
            if(word.charAt(myPosition) != nextNode.getLetter()){
                myDiffLength--;
            }
            myPosition++;
            myLength--;
        }
        
        //This is a terminal node. We just found a similar word, so we add it in the first not empty position of the array.
        if(myLength==0 && nextNode.isTerminal()==true && myDiffLength>=0){
            for(j=0;j<array.length;j++){
                if(array[j]==null){
                    array[j] = String.valueOf(myRetStr);
                    return;
                }
            }
        }
        else if(myDiffLength<0 || myLength==0){return;}
        
        for(int i=0;i<children.length;i++){
            //Call recursively the not-null children.
            if(children[i]!=null){
                differ_string(children[i],word,myLength,myPosition,myDiffLength,myRetStr,array);
            }
        }
    }
    
    //This function returns an array of strings. Each string contains a word which has the same prefix comparing it with the "prefix" string given as an argument. 
    public String[] wordsOfPrefix(String prefix){
        
        int j;
        String[] arrayOfWords=new String[100];

        for(j=0;j<arrayOfWords.length;j++){
            arrayOfWords[j]=new String();
            arrayOfWords[j]=null;
        }
        //Call the getWordsWithSamePrefix function. This function will return a list of words with the same prefix.
        getWordsWithSamePrefix(this.getRoot(),prefix,0,"",arrayOfWords);
        if(arrayOfWords[0]==null){return null;}
        return arrayOfWords;
    }
    
    //Each node calls his not-null children, passing them the string "retStr" as an argument.
    //Each children adds his letter to the string "retStr" and the procedure is repeated recursively.
    //Only the terminal nodes can add the string to the array.
    //This function is recursive.
    public String getWordsWithSamePrefix(node nextNode,String prefix,int position,String retStr,String[] array){
    
        node[] children=nextNode.getChildren();
        String myRetStr=new String();
        int j,myPosition;
        myPosition=position;
        myRetStr=String.valueOf(retStr);
        
        //Compare the letter of this node with the letter found in position "myPosition". If they are the same, add the node's letter to the "retStr"
        //and increase the "myPosition" counter. If they are diffenent, return null.
        //Last,if we have already foun the same prefix, add the node's letter to the "retStr" and don't increase the "myPosition" counter.
        if(nextNode!=this.getRoot() && myPosition < prefix.length() && nextNode.getLetter() == prefix.charAt(myPosition)){
            myRetStr += nextNode.getLetter();
            myPosition++;
            
        }
        else if(nextNode!=this.getRoot() && myPosition >= prefix.length()){
            myRetStr += nextNode.getLetter();
        }
        else if(nextNode!=this.getRoot() && nextNode.getLetter() != prefix.charAt(myPosition)){
            return null;
        }
        
        //This is a terminal node. We just found a word with the same prefix, so we add it in the first not empty position of the array.
        //We do not stop the structure searching, because this node is a terminal node but it is not a leaf.
        if(nextNode.isTerminal()==true && myPosition >= prefix.length()){
            for(j=0;j<array.length;j++){
                if(array[j]==null){
                    array[j] = String.valueOf(myRetStr);
                    break;
                }
            }
        }
        
        for(int i=0;i<children.length;i++){
            //Call recursively the not-null children.
            if(children[i]!=null){
                getWordsWithSamePrefix(children[i],prefix,myPosition,myRetStr,array);
            }
        }
        
        return myRetStr;
    }

}
