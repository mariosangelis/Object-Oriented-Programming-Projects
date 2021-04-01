
#include "AVL.hpp"
#define MAX_LINE_LEN 128
#define MAX_ENTRIES 64

// use tiny dict as 2nd argument

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
    avl1.add(words[i++]); 
  }
  inf.close();
  
  inf.open(argv[2]);
  i=0;
  while ( inf.getline (line, MAX_LINE_LEN) ) {
    words[i] = string(line);
    //cout << i+1 << ". " << words[i] << endl;
    avl2.add(words[i++]); 
  }
  inf.close();
  
  // Copy constructor
  cout << avl2 << endl;
  cout << avl1 << endl;
  avl2 += avl1;
  cout << avl2 << endl;
}
