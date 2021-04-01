package ce326.hw1;
public class node {
    
    private boolean isTerminal;
    private char letter;
    private node[] children;
    private static final int arrayLength=26;
    
    
    public node(char letter){
        this.letter=letter;
        
        this.children=new node[26];
        for(int i=0;i<arrayLength;i++){
            this.children[i]=null;
        }
    }
    
    //This method sets theprovate variable "isTerminal" to 1.
    public void setTerminal(){
        this.isTerminal=true;
    }
    
    //This method returns true if this node is a terminal node
    public boolean isTerminal(){
        return(this.isTerminal);
    }
    
    //This method returns the letter of this node. 
    public char getLetter(){
        return(this.letter);
    }
    
    //This method returns the children array of this node. 
    public node[] getChildren(){
        return(this.children);
    }
}
