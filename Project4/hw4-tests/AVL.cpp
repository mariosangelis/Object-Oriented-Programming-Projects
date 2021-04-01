//Angelis Marios, AEM : 2406
//This is an avl tree implementation
#include "AVL.hpp"
#include <string.h>
using namespace std;


//This is the node constructor
AVL::Node::Node(const string& e, Node *parent, Node *left, Node *right){
	
	element.assign(e);
	this->height=1;
	this->parent = parent;
	this->left = left;
	this -> right = right;
}


AVL::Node* AVL::Node::getParent() const{
	return this->parent;
}

AVL::Node* AVL::Node::getLeft() const{
	return this->left;
}

AVL::Node* AVL::Node::getRight() const{
	return this->right;
}


string  AVL::Node::getElement() const{
	return this->element;
}

int AVL::Node::getHeight() const{
	return height;
}

void AVL::Node::setLeft(AVL::Node *ptr){
	this->left=ptr;
}

void AVL::Node::setRight(AVL::Node *ptr){
	this->right=ptr;
}

void AVL::Node::setParent(AVL::Node *ptr){
	this->parent=ptr;
}

void AVL::Node::setElement(string e){
	element.assign(e);
}

//Returns true if this node is a left child
bool AVL::Node::isLeft() const{
	
	if(this->getParent()==nullptr){return false;}
	
	if(this->getParent()->getLeft()==this){
		return true;
	}
	else{
		return false;
	}
}

//Returns true if this node is a right child
bool AVL::Node::isRight() const{
	
	if(this->getParent()==nullptr){return false;}
	
	if(this->getParent()->getRight()==this){
		return true;
	}
	else{
		return false;
	}
}

//Returns the height of the right children
int AVL::Node::rightChildHeight() const{
	return this->getRight()->getHeight();
}

//Returns the height of the left children
int AVL::Node::leftChildHeight() const{
	return this->getLeft()->getHeight();
}

//Update height function
int AVL::Node::updateHeight(){
	int max;
	
	//This is a leaf
	if(getLeft()==nullptr && getRight()==nullptr){
		height=1;
		return height;
	}
	
	if(getLeft()==nullptr){
		//Height is the height of the right child increased by 1
		height=getRight()->getHeight()+1;
		return height;
	}
	else if(getRight()==nullptr){
		//Height is the height of the left child increased by 1
		height=getLeft()->getHeight()+1;
		return height;
	}
	else{
		//Height is the max height of the right and the left child increased by 1
		max=getRight()->getHeight();
		
		if(max < getLeft()->getHeight()){
			max = getLeft()->getHeight();
		}
		height=max+1;
	}
	return height;
}

//This function checks if the node is balanced following the AVL rules
bool AVL::Node::isBalanced(){
	
	if(getLeft()==nullptr && getRight()==nullptr){
		//This is a leaf
		return true;
	}
	
	if(getLeft()==nullptr){
		//Check only if the right child's height is larger than 1
		if(getRight()->getHeight() <= 1){return true;}
		else{return false;}
	}
	if(getRight()==nullptr){
		//Check only if the left child's height is larger than 1
		if(getLeft()->getHeight() <= 1){return true;}
		else{return false;}
	}
	
	if(abs(getLeft()->getHeight() - getRight()->getHeight()) <= 1){return true;}
	return false;
}

//This is the default AVL constructor
AVL::AVL(){
	size=0;
	root=nullptr;
}

//This is the AVL copy constructor
AVL::AVL(AVL& tree){
	size=0;
	root=nullptr;
	
	//Add all the elements of the avl tree given as an argument to the current tree
	for(Iterator it = tree.begin(); it != tree.end();it++) {
		this->add(*it);
	}
	
}

//This function returns true if the string e is contained inside the AVL tree structure
bool AVL::contains(string e){
	Node* currentNode = root;
	
	if(size==0){return false;}
	
	while(1){
		int ret = currentNode->getElement().compare(e);
		
		if(ret==0){
			return true;
		}
		else if (ret<0){
			if(currentNode->getRight()==nullptr){break;}
			currentNode = currentNode->getRight();
		}
		else if(ret>0){
			if(currentNode->getLeft()==nullptr){break;}
			currentNode = currentNode->getLeft();
		}
	}
	
	return false;
}

//This function describes a right rotation

/*     y                               x
      / \     Right Rotation          /  \
     x   T3   - - - - - - - >        T1   y 
    / \                                  / \
   T1  T2                               T2  T3
*/
void AVL::right_rotate(Node* node){
	
	Node* temp_node = node->getLeft()->getRight();
		
	node->getLeft()->setRight(node);
	node->getLeft()->setParent(node->getParent());
	if(node->getParent()!=nullptr){
		if(node->getParent()->getLeft()==node){
			node->getParent()->setLeft(node->getLeft());
		}
		else{
			node->getParent()->setRight(node->getLeft());
		}
		
	}
	
	node->setParent(node->getLeft());
	node->setLeft(temp_node);
	if(temp_node!=nullptr){
		temp_node->setParent(node);
	}
	if(root==node){root=node->getParent();}
	
	node->updateHeight();
	node->getParent()->updateHeight();
	
	
}

//This function describes a left rotation

/*     y                               x
      / \                             /  \
     x   T3                          T1   y 
    / \       < - - - - - - -            / \
   T1  T2     Left Rotation            T2  T3
*/

void AVL::left_rotate(Node* node){
	
	Node* temp_node = node->getRight()->getLeft();
		
	node->getRight()->setLeft(node);
	node->getRight()->setParent(node->getParent());
	if(node->getParent()!=nullptr){
		if(node->getParent()->getLeft()==node){
			
			node->getParent()->setLeft(node->getRight());
		}
		else{
			node->getParent()->setRight(node->getRight());
		}
	}
	
	node->setParent(node->getRight());
	node->setRight(temp_node);
	if(temp_node!=nullptr){
		temp_node->setParent(node);
	}
	
	if(root==node){root=node->getParent();}
	
	node->updateHeight();
	node->getParent()->updateHeight();
	
}

//This is the recursive remove function. At first, find the node to delete, by moving left or right after comparing each node's sting with "e" string given as an argument. 
AVL::Node* AVL::rmvRecursive(Node* currentNode,string e){
	
	int ret = currentNode->getElement().compare(e);
	
	if(ret==0){
		//This is the node to delete
		//cout << "delete node is " << currentNode->getElement() << endl;
		
		if(currentNode->getLeft()==nullptr && currentNode->getRight()==nullptr){
			//cout << "This is a leaf " << currentNode->getElement() << endl;
			
			//This is a leaf node
			size--;
			
			//This is the last node. Set root to the null pointer
			if(currentNode==root){
				root=nullptr;
				return nullptr;
			}
			
			if(currentNode->getParent()->getRight()==currentNode){
				//Set the parent's right pointer to null
				currentNode->getParent()->setRight(nullptr);
			}
			else{
				//Set the parent's left pointer to null
				currentNode->getParent()->setLeft(nullptr);
			}
			
			return nullptr;
		}
		else if(currentNode->getLeft()!=nullptr && currentNode->getRight()==nullptr){
			//cout << "Right child is null" << endl;
			
			//The node to delete has only a left child
			size--;
			//Swap locally the root node with the current node's left child
			if(currentNode==root){
				root=currentNode->getLeft();
				currentNode->getLeft()->setParent(nullptr);
				return nullptr;
			}
			
			if(currentNode->getParent()->getRight()==currentNode){
				//Set the parent's right pointer to current node's left child
				currentNode->getParent()->setRight(currentNode->getLeft());
				//Set current node's parent pointer to the parent of the current node
				currentNode->getLeft()->setParent(currentNode->getParent());
			}
			else{
				//Set the parent's left pointer to current node's left child
				currentNode->getParent()->setLeft(currentNode->getLeft());
				//Set current node's parent pointer to the parent of the current node
				currentNode->getLeft()->setParent(currentNode->getParent());
			}
			return nullptr;
		}
		else if(currentNode->getLeft()==nullptr && currentNode->getRight()!=nullptr){
			//cout << "Left child is null" << endl;
			//The node to delete has only a right child
			
			//Current node is the root of the avl tree
			if(currentNode==root){
				//Find the most left node of the right subtree below the current node
				Node* swapNode = currentNode->getRight();
			
				while(1){
					if(swapNode->getLeft()==nullptr){
						break;
					}
					swapNode = swapNode->getLeft();
				}
				//cout << "swap node is " << swapNode->getElement() << endl;
				
				//Remove the most left node of the right subtree below the current node
				rmv(swapNode->getElement());
				//Swap the elements
				currentNode->setElement(swapNode->getElement());
			}
			else{
				size--;
				
				if(currentNode->getParent()->getRight()==currentNode){
					//Set the parent's right pointer to current node's right child
					currentNode->getParent()->setRight(currentNode->getRight());
					//Set current node's parent pointer to the parent of the current node
					currentNode->getRight()->setParent(currentNode->getParent());
				}
				else{
					//Set the parent's left pointer to current node's right child
					currentNode->getParent()->setLeft(currentNode->getRight());
					//Set current node's parent pointer to the parent of the current node
					currentNode->getRight()->setParent(currentNode->getParent());
				}
				return nullptr;
			}
		}
		else{
			//cout << "None child is null" << endl;
			
			//The node to delete has a right and a left child
			//Find the most left node of the right subtree below the current node
			Node* swapNode = currentNode->getRight();
			
			while(1){
				if(swapNode->getLeft()==nullptr){
					break;
				}
				swapNode = swapNode->getLeft();
			}
			//cout << "swap node is " << swapNode->getElement() << endl;
			
			//Remove the most left node of the right subtree below the current node
			rmv(swapNode->getElement());
			//Swap the elements
			currentNode->setElement(swapNode->getElement());
		}
	}
	else if (ret<0){
		//Go right
		rmvRecursive(currentNode->getRight(),e);
	}
	else if(ret>0){
		//Go left
		rmvRecursive(currentNode->getLeft(),e);
	}
	//Update node's height
	currentNode->updateHeight();
	
	//Check if this node is balanced
	bool is_balanced = currentNode->isBalanced();
	
	if(is_balanced==true){
		//This node is balanced
		return currentNode;
	}
	
	if(currentNode->getLeft()==nullptr && currentNode->getRight()==nullptr){
		return currentNode;
	}
	
	//Let z be the current unbalanced node, y be the larger height child of z and x be the larger height child of y.
	Node* y;
	int max_height;
	if(currentNode->getLeft()==nullptr){
		y=currentNode->getRight();
	}
	else if(currentNode->getRight()==nullptr){
		y=currentNode->getLeft();
	}
	else{
		max_height=currentNode->getRight()->getHeight();
		if(max_height<currentNode->getLeft()->getHeight()){
			y=currentNode->getLeft();
		}
		else{
			y=currentNode->getRight();
		}
	}
	
	
	Node* x;
	max_height=0;
	if(y->getLeft()==nullptr){
		x=y->getRight();
	}
	else if(y->getRight()==nullptr){
		x=y->getLeft();
	}
	else{
		//y node has a left and a right child
		//Give priority to simple rotations instead of double rotations
		if(y==currentNode->getLeft()){
			max_height=y->getLeft()->getHeight();
			if(max_height < y->getRight()->getHeight()){
				x=y->getRight();
			}
			else{
				x=y->getLeft();
			}
		}
		else if(y==currentNode->getRight()){
			max_height=y->getRight()->getHeight();
			if(max_height < y->getLeft()->getHeight()){
				x=y->getLeft();
			}
			else{
				x=y->getRight();
			}
		}
	}
	
	if(is_balanced==false && y==currentNode->getRight() && x==y->getLeft()){
		//cout << "This is a right left" << endl;
		//This is the right left case
/*         z                            z                            x
          / \                          / \                          /  \ 
        T1   y   Right Rotate (y)    T1   x      Left Rotate(z)   z      y
            / \  - - - - - - - - ->     /  \   - - - - - - - ->  / \    / \
            x   T4                      T2   y                  T1  T2  T3  T4
           / \                              /  \
         T2   T3                           T3   T4
*/		
		right_rotate(currentNode->getRight());
		left_rotate(currentNode);
	}
	else if(is_balanced==false && y==currentNode->getRight() && x==y->getRight()){
		//cout << "This is a right right " << endl;
		//This is the right right case
/*      z                                y
       /  \                            /   \ 
      T1   y     Left Rotate(z)       z      x
          /  \   - - - - - - - ->    / \    / \
         T2   x                     T1  T2 T3  T4
             / \
           T3  T4
*/		
		left_rotate(currentNode);
	}
	else if(is_balanced==false  && y==currentNode->getLeft() && x==y->getRight()){
		//cout << "This is a left right " << endl;
		//This is the left right case
/*           z                               z                           x
            / \                            /   \                        /  \ 
           y   T4  Left Rotate (y)        x    T4  Right Rotate(z)    y      z
          / \      - - - - - - - - ->    /  \      - - - - - - - ->  / \    / \
        T1   x                          y    T3                    T1  T2 T3  T4
            / \                        / \
          T2   T3                    T1   T2
*/		
		left_rotate(currentNode->getLeft());
		right_rotate(currentNode);
		
	}
	else if(is_balanced==false  && y==currentNode->getLeft() && x==y->getLeft()){
		//cout << "This is a left left " << endl;
		//This is the left left case
/*               z                                      y 
                / \                                   /   \
               y   T4      Right Rotate (z)          x      z
              / \          - - - - - - - - ->      /  \    /  \ 
             x   T3                               T1  T2  T3  T4
            / \
          T1   T2
*/		
		right_rotate(currentNode);
	}
	
	return currentNode;
}

//This function removes the node which contains the "e" string from the avl
bool AVL::rmv(string e){
	
	if(contains(e)==false){return false;}
	
	//Call the remove recursive function
	rmvRecursive(root,e);
	return true;
}

//This is the recursive insert function. At first, find the correct place to insert the new node by moving left or right after comparing each node's sting with "e" string given as an
//argument. 
AVL::Node* AVL::insert(Node* node,string e){
	
	//This is the correct place to insert the new node.
	if(node==nullptr){
		Node* newNode = new Node(e,nullptr,nullptr,nullptr);
		size++;
		return newNode;
	}
	
	int ret = node->getElement().compare(e);
	Node *newNode;
	
	if (ret<0){
		//Go right
		newNode=insert(node->getRight(),e);
		if(node->getRight()==nullptr){
			node->setRight(newNode);
			newNode->setParent(node);
		}
	}
	else if(ret>0){
		//Go left
		newNode=insert(node->getLeft(),e);
		if(node->getLeft()==nullptr){
			node->setLeft(newNode);
			newNode->setParent(node);
		}
	}
	
	//Update node's height
	node->updateHeight();
	
	//Check if this node is balanced
	bool is_balanced = node->isBalanced();
	
	if(is_balanced==false && newNode==node->getRight() && node->getRight()->getElement().compare(e)>0){
		//cout << "This is a right left" << endl;
		//This is the right left case
/*         z                            z                            x
          / \                          / \                          /  \ 
        T1   y   Right Rotate (y)    T1   x      Left Rotate(z)   z      y
            / \  - - - - - - - - ->     /  \   - - - - - - - ->  / \    / \
            x   T4                      T2   y                  T1  T2  T3  T4
           / \                              /  \
         T2   T3                           T3   T4
*/		
		right_rotate(node->getRight());
		left_rotate(node);
	}
	else if(is_balanced==false && newNode==node->getRight() && node->getRight()->getElement().compare(e)<0){
		//cout << "This is a right right " << endl;
		//This is the right right case
/*      z                                y
       /  \                            /   \ 
      T1   y     Left Rotate(z)       z      x
          /  \   - - - - - - - ->    / \    / \
         T2   x                     T1  T2 T3  T4
             / \
           T3  T4
*/		
		left_rotate(node);
	}
	else if(is_balanced==false && newNode==node->getLeft() && node->getLeft()->getElement().compare(e)<0){
		//cout << "This is a left right " << endl;
		//This is the left right case
/*           z                               z                           x
            / \                            /   \                        /  \ 
           y   T4  Left Rotate (y)        x    T4  Right Rotate(z)    y      z
          / \      - - - - - - - - ->    /  \      - - - - - - - ->  / \    / \
        T1   x                          y    T3                    T1  T2 T3  T4
            / \                        / \
          T2   T3                    T1   T2
*/
		left_rotate(node->getLeft());
		right_rotate(node);
		
	}
	else if(is_balanced==false && newNode==node->getLeft() && node->getLeft()->getElement().compare(e)>0){
		//cout << "This is a left left " << endl;
		//This is the left left case
/*               z                                      y 
                / \                                   /   \
               y   T4      Right Rotate (z)          x      z
              / \          - - - - - - - - ->      /  \    /  \ 
             x   T3                               T1  T2  T3  T4
            / \
          T1   T2
*/
          
		right_rotate(node);
	}
	return node;
}


//This function creates a new node in the avl tree
bool AVL::add(string e){
	
	//If the tree is empty, initialize the root
	if(size==0){
		Node* newNode = new Node(e,nullptr,nullptr,nullptr);
		root = newNode;
		size++;
		return true;
	}
	
	if(contains(e)==true){return false;}
	//Call insert recursive function
	insert(root,e);
	
	return true;
}

//This function returns the root of the avl tree
AVL::Node* AVL::getRoot() const{
	return root;
}

//This function prints to the out stream the avl tree using pre order traversal
void AVL::pre_order(std::ostream& out) const{
	
	for(AVL::Iterator it = this->begin(); it != this->end(); ++it) {
		out << *it << " ";
	}
	
}

//This function prints each node of the avl tree in a file specified by "filename" by following the pre order traversal rule
string AVL::print2DotFileRecursive(AVL::Node* node){
	
	string retStr ="";
	const void * address = static_cast<const void*>(node);
	std::stringstream ss;
	ss << address;  
	std::string name = ss.str();
	if(node!=root){
		retStr.append("\t" + node->getElement() + " [label=\""+node->getElement()+"\", shape=circle, color=black]\n");
	}
	if(node->getLeft()!=nullptr){
		
		const void * address = static_cast<const void*>(node->getLeft());
		std::stringstream ss;
		ss << address;  
		std::string name2 = ss.str();
		
		retStr.append("\t" +node->getElement()+ " -- "+node->getLeft()->getElement()+"\n");
		retStr.append(print2DotFileRecursive(node->getLeft()));
	}
	if(node->getRight()!=nullptr){
		const void * address = static_cast<const void*>(node->getRight());
		std::stringstream ss;
		ss << address;  
		std::string name2 = ss.str();
		
		retStr.append("\t" +node->getElement()+ " -- "+node->getRight()->getElement()+"\n");
		
		retStr.append(print2DotFileRecursive(node->getRight()));
	}
	
	return retStr;
	
}

//This function creates prints the avl tree in a file specified by "filename".
void AVL::print2DotFile(char *filename){
	
	if(root==nullptr){return;}
	
	const void * address = static_cast<const void*>(root);
	std::stringstream ss;
	ss << address;  
	std::string name = ss.str();
	
	string str = "graph Trie {\n\t" +root->getElement()+" [label=\""+root->getElement()+"\" ,shape=circle, color=black]\n";
	
	//Call print2DotFileRecursive function to append each node in the file
	str.append(print2DotFileRecursive(root));
	
	str.append("}");
	ofstream myfile;
	myfile.open (filename);
	myfile << str;
	myfile.close();
	
}

//This is the + overloaded method
AVL AVL::operator+(const AVL& avl){
	AVL newavl;
	
	//Create a new avl tree
	//At first, copy all the nodes of the left tree to the new avl tree
	for(Iterator it = this->begin(); it != this->end();it++) {
		newavl.add(*it);
	}
	
	//After that, copy all the nodes of the right tree to the new avl tree
	for(Iterator it2 = avl.begin(); it2 != avl.end(); it2++) {
		newavl.add(*it2);
	}
	return newavl;
}

//This is the += overloaded method
AVL& AVL::operator+=(const AVL& avl){
	
	//Copy all the nodes of the tree specified by the argument, to the new avl tree
	for(Iterator it2 = avl.begin(); it2 != avl.end(); it2++) {
		add(*it2);
	}
	return *this;
}

//This is the = overloaded method
AVL& AVL::operator=(const AVL& avl){
	
	//Delete all the nodes from the left tree
	while(1){
		if(root==nullptr){break;}
		for(Iterator it = this->begin(); it != this->end(); it++) {
			this->rmv(*it);
		}
	}
	
	//Copy all the nodes of the tree specified by the argument, to the left tree
	for(Iterator it = avl.begin(); it != avl.end(); it++) {
		this->add(*it);
	}
	
	return *this;
}

//This is the += overloaded method
AVL& AVL::operator+=(const string& e){
	
	if(contains(e)==true){return *this;}
	//Add the "e" node in the avl tree
	add(e);
	return *this;
}

//This is the -= overloaded method
AVL& AVL::operator-=(const string& e){
	
	if(contains(e)==false){return *this;}
	//Remove the "e" node from the avl tree
	rmv(e);
	return *this;
}

//This is the + overloaded method
AVL AVL::operator+(const string& e){
	
	//Create a new avl tree
	AVL newtree(*this);
	//Add the "e" node in the avl tree
	newtree.add(e);
	return newtree;
}

//This is the - overloaded method
AVL AVL::operator-(const string& e){
	
	//Create a new avl tree
	AVL newtree(*this);
	//Remove the "e" node from the avl tree
	newtree.rmv(e);
	return newtree;
}

//This is the << overloaded method
std::ostream& operator<<(std::ostream& out, const AVL& tree){
	
	if(tree.getRoot()==nullptr){return out;}
	
	//Print the avl tree to the out stream by following the pre order rule
	tree.pre_order(out);
	out.flush();
	return out;
}

//This is the default constructor for the iterator
AVL::Iterator::Iterator(){}

//This is the iterator constructor giving as an argument an avl tree
AVL::Iterator::Iterator(const AVL& tree){
	
	//Push into the iterator's stack the root node of the avl tree. 
	stack.push(tree.getRoot());
	
}

//This is the iterator's * overloaded method
string AVL::Iterator::operator*(){
	
	Node* node;
	//Perform a "top" call to get the top element from the iterator's stack
	node=stack.top();
	
	return node->getElement();
	
}

//This is the iterator's ++ overloaded method
//Dynamically increase the iterator position by loading new nodes into the iterator's stack
// ++it
AVL::Iterator& AVL::Iterator::operator++(){
	
	Node* node;
	node=stack.top();
	stack.pop();
	
	if(node->getRight()!=nullptr){
		stack.push(node->getRight());
	}
	if(node->getLeft()!=nullptr){
		stack.push(node->getLeft());
	}
	if(stack.empty()){
		stack.push(nullptr);
	}
	
	return *this;
	
}

//This is the iterator's == overloaded method
//Check if the iterator which calls this method shows in the same address as the iterator which is provided as the first argument
bool AVL::Iterator::operator==(AVL::Iterator it){

	Node* node;
	node=stack.top();
	
	std::stack<AVL::Node*>* stack2 = it.getStackReference();
	if(node==stack2->top()){
		return true;
	}
	else{return false;}
	
}

//This is the iterator's != overloaded method
bool AVL::Iterator::operator!=(AVL::Iterator it){
	return !(this->operator==(it));
}

//This is the iterator's ++ overloaded method
// it++
AVL::Iterator AVL::Iterator::operator++(int a){
	
	Iterator newIterator;
	//Create a new iterator
	
	//Create a copy of the stack of the iterator object which called this method
	std::stack<AVL::Node*> copystack = stack;
	std::stack<AVL::Node*>* newstack = newIterator.getStackReference();
	
	//Copy all the stack from this iterator to the new iterator
	while(copystack.empty()==false){
		
		Node* node;
		node=copystack.top();
		copystack.pop();
		newstack->push(node);
		
	}
	
	//Dynamically increase the iterator position by loading new nodes into the iterator's stack
	Node* node;
	node=stack.top();
	stack.pop();
	
	if(node->getRight()!=nullptr){
		stack.push(node->getRight());
	}
	if(node->getLeft()!=nullptr){
		stack.push(node->getLeft());
	}
	if(stack.empty()){
		stack.push(nullptr);
	}
	
	//Return the new iterator
	return newIterator;
}

//Create a new iterator by calling the Iterator constructor giving him the avl tree as an argument
AVL::Iterator AVL::begin() const{
	
	Iterator newIterator(*this);
	return newIterator;
}

//Create a new iterator which contains only the nullptr inside his stack
AVL::Iterator AVL::end() const{
	
	Iterator newIterator;
	std::stack<AVL::Node*>* stack = newIterator.getStackReference();
	stack->push(nullptr);
	
	return newIterator;
}

//This function returns the address of the iterator's stack
std::stack<AVL::Node*>* AVL::Iterator::getStackReference(){
	return &stack;
}

//This function returns an instance of the iterator's stack
std::stack<AVL::Node*> AVL::Iterator::getStack() const{
	return stack;
}


