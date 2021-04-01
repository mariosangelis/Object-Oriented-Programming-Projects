#ifndef __AVL_HPP_
#define __AVL_HPP_

#include <iostream>
#include <fstream>
#include <bits/stdc++.h> 
using namespace std;

class AVL {
private:
	class Node {
		Node *parent, *left, *right;
		int height;
		string element;
	
	public:
		Node(const string& e, Node *parent, Node *left, Node *right);
		
		Node*  getParent() const;
		Node*  getLeft() const;
		Node*  getRight() const;
		string getElement() const;
		int    getHeight() const;
		
		void setLeft(Node *ptr);
		void setRight(Node *ptr);
		void setParent(Node *ptr);
		void setElement(string e);
			
		bool isLeft() const;
		bool isRight() const;
		int  rightChildHeight() const;
		int  leftChildHeight() const;
		int  updateHeight();
		bool isBalanced();
	};
private:
	int   size;
	Node* root;
	
public:
	
	class Iterator {
	private:
		std::stack <Node*> stack; 
	public:
		Iterator(const AVL& tree);
		Iterator();
		std::stack<Node*>* getStackReference();
		std::stack<Node*> getStack() const;
		Iterator& operator++();
		Iterator operator++(int a);
		string operator*(); 
		bool operator!=(Iterator it);
		bool operator==(Iterator it);
	};
	
	Iterator begin() const;  
	Iterator end() const;
	
	
	static const int MAX_HEIGHT_DIFF = 1;
	AVL();
	AVL(AVL& tree);
	bool contains(string e);
	bool add(string e);
	Node* insert(Node* node,string e);
	Node* getRoot() const;
	string print2DotFileRecursive(Node* node);
	void left_rotate(Node* node);
	void right_rotate(Node* node);
	bool rmv(string e);
    Node* rmvRecursive(Node* currentNode,string e);
	void print2DotFile(char *filename);
	void pre_order(std::ostream& out) const;
	friend std::ostream& operator<<(std::ostream& out, const AVL& tree); 
	AVL& operator  =(const AVL& avl);
	AVL  operator  +(const AVL& avl);
	AVL& operator +=(const AVL& avl);
	AVL& operator +=(const string& e);
	AVL& operator -=(const string& e);
	AVL  operator  +(const string& e);
	AVL  operator  -(const string& e);
};

#endif
