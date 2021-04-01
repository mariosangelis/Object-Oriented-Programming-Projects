
#include "AVL.hpp"
#define MAX_LINE_LEN 128
#define MAX_ENTRIES 64

int main(int argc, char *argv[]) {

  AVL avl;
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
    avl = avl + words[i++]; 
    cout << avl << endl;
  }
  inf.close();
  
  cout << avl << endl;
  
  inf.open(argv[2], std::ifstream::in);
  if (!inf.is_open()) {
    cout << "Unable to open file " << argv[2];
    return -1;    
  }
  
  i=0;
  while ( inf.getline(line, MAX_LINE_LEN) ) {
    words[i] = string(line);
    //cout << i+1 << ". " << words[i] << endl;
    avl = avl - words[i++];
    cout << avl << endl;
  }
  inf.close();
}
