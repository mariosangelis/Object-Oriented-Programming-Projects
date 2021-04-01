
#include "AVL.hpp"
#include <unistd.h>
#define MAX_LINE_LEN 128
#define MAX_ENTRIES 64

int main(int argc, char *argv[]) {

  AVL avl1, avl2;
  if(argc<2) {
    cout << "\nInsufficient number of arguments. Exiting..." << endl;
    return 0;
  }
    
  ifstream inf(argv[1]);
  char line[MAX_LINE_LEN]; 
  string words[MAX_ENTRIES];
 
  if (!inf.is_open()) {
    cout << "Unable to open file " << argv[1];
    return -1;    
  }
  
  int i=0;
  while ( inf.getline (line, MAX_LINE_LEN) ) {
    words[i] = string(line);
    //cout << i+1 << ". " << words[i] << endl;
    avl1.add(words[i]);
    avl2.add(words[i++]); 
  }
  inf.close();
  int size = i;
  
  
  //cout << avl2 << endl;
  
  //avl1.print2DotFile("printdot.dot");
  
  srand(123456789);
  for(int i=0; i<size>>3; i++) {
    string str = words[rand()%size];
    //cout << "rmv: " << str << endl;
    avl1.rmv(str);
  }
  
  //cout << avl1 << endl;
  
  // Assignement operator
  avl2 = avl1;
  cout << avl1 << endl;
  cout << avl2 << endl;
}
