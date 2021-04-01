//Angelis Marios
//AEM:2406
#ifndef _GRAPH_HPP_ 
#define _GRAPH_HPP_
using namespace std;
#include <iostream>
#include <list>
#include <fstream>
#include <queue> 
#include <iterator>
#include <algorithm>    // std::find
#include <map>
#define MAX_INT 10000000


class NegativeGraphCycle : public std::exception{
	
	public:
		NegativeGraphCycle(){}
		
		void what(){
			cout << "Negative Graph Cycle!" << endl;
		}
		
	};

template<typename T>
struct Edge {
	T from;
	T to;
	int dist;
	Edge(T f, T t, int d): from(f), to(t), dist(d) {}
	bool operator==(const Edge<T>& e) const{
		if(from==e.from && to==e.to){
			return true;
		}
		return false;
	}
	
};

template<typename T>
std::ostream& operator<<(std::ostream& out, const Edge<T>& e) {
	out << e.from << " -- " << e.to << " (" << e.dist << ")";
	return out;
}

template <typename T>
class Graph {
	
	class Node {
		T object;
		int hashCode;
		bool isNull;
		
	public:
		list<struct Edge<T>> connectionList;
		Node();
		~Node();
		int HashCode();
		void sethashCode(int hash);
		Node(const T& info);
		T getObject();
		bool getNullNodeBit();
		void setNullNodeBit(bool option);
	};
	
private:
	
	Node* AdjacencyArray;
	int capacity;
	int size;
	bool isDirectedGraph;
	void expand_table();
	void shrink_table();  
  
public:
	void printAll();
	Graph(bool isDirectedGraph = true, int capacity = 2);
	bool contains(const T& info);
	int getTotalNodes();
	bool addVtx(const T& info);
	bool rmvVtx(const T& info);
	bool addEdg(const T& from, const T& to, int distance);
	bool rmvEdg(const T& from, const T& to);
	list<T> dfs(const T& info) const;
	list<T> dfsRecursive(const T& info,std::list<T>& dfslist) const;
	list<T> bfs(const T& info) const;
	list<Edge<T>> mst();
	
	bool print2DotFile(const char *filename) const;
	list<T> dijkstra(const T& from, const T& to);
	list<T> bellman_ford(const T& from, const T& to);
  
};

int hashnum=0;

template <typename T>
Graph<T>::Node::Node(const T& info){
	
	hashCode=hashnum;
	hashnum++;
	object=info;
	isNull=false;
}

template <typename T>
int Graph<T>::Node::HashCode(){
	
	return this->hashCode;
}

template <typename T>
Graph<T>::Node::Node(){
	
	isNull=true;
}

template <typename T>
bool Graph<T>::Node::getNullNodeBit(){
	
	return isNull;
}

template <typename T>
void Graph<T>::Node::setNullNodeBit(bool option){
	
	isNull = option;
}

template <typename T>
T Graph<T>::Node::getObject(){
	
	return object;
}

template <typename T>
Graph<T>::Node::~Node(){
	
	//cout << "Node destructor" <<endl;
}

template <typename T>
void Graph<T>::Node::sethashCode(int hash){
	
	this->hashCode = hash;
}

//This is the constructor of the graph.
template <typename T>
Graph<T>::Graph(bool isDirectedGraph, int capacity){
	
	this->capacity = capacity;
	this->size=0;
	this->isDirectedGraph=isDirectedGraph;
	
	//Allocate memory for the Adjacency Array.
	AdjacencyArray = new Node[capacity];
}

//This function returns the number of functional nodes inside the table "AdjacencyArray".
//All these nodes will have the "isNull" bit assigned to false.  
template <typename T>
int Graph<T>::Graph::getTotalNodes(){
	int i,total_nodes=0;
	
	for(i=0;i<size;i++){
		if(AdjacencyArray[i].getNullNodeBit()==false){
			total_nodes++;
		}
	}
	
	return total_nodes;
}

//This function returns true if there is a node which contains the object given as the first argument inside the table "AdjacencyArray".
template <typename T>
bool Graph<T>::contains(const T& info){
	int i;
	
	for(i=0;i<size;i++){
		if(AdjacencyArray[i].getObject()==info){
			return true;
		}
	}
	return false;
	
}

//This function adds a new Vertex to the graph.
template <typename T>
bool Graph<T>::addVtx(const T& info){
	
	if(contains(info)==true){
		return false;
	}
	
	//newNode is located in stack memory.
	Node newNode(info);
	if(size==capacity){
		expand_table();
	}
	
	//AdjacencyArray is located in heap memory.
	//During this initialization, for each position inside this table, the Node constructor will be called.
	//In the next command, the default operator = will be called. It will copy all the data from the newNode (located in stack) to the AdjacencyArray[size] (located in heap).
	
	AdjacencyArray[size]=newNode;
	size++;
	
	return true;
}

//This function removes a vertex from the graph.
template <typename T>
bool Graph<T>::rmvVtx(const T& info){
	
	if(contains(info)==false){
		return false;
	}
	
	int i;
	
	for(i=0;i<size;i++){
		//Find the object specified by the first argument
		if(AdjacencyArray[i].getObject()==info){
			while(1){
				//Remove all the edges
				auto it = AdjacencyArray[i].connectionList.begin();
				for (;it != AdjacencyArray[i].connectionList.end();it++) {
					//cout << "remove " << info.get() << " -> " << (*it).to.get() << endl;
					rmvEdg(info,(*it).to);
					break;
				}
				if(it == AdjacencyArray[i].connectionList.end()){break;}
			}
			break;
		}
	}
	
	//If this graph is a directed graph, remove all the edges which have as their destination (to) the object info.
	if(isDirectedGraph==true){
		for(i=0;i<size;i++){
			if(AdjacencyArray[i].getNullNodeBit()==false && AdjacencyArray[i].getObject()!=info ){
				auto it = AdjacencyArray[i].connectionList.begin();
				for (;it != AdjacencyArray[i].connectionList.end();it++) {
					//If the destination is the object "info", remove the edge.
					if((*it).to==info){
						rmvEdg(AdjacencyArray[i].getObject(),info);
						break;
					}
				}
			}
		}
	}
	
	//Set the "isNull" bit of the onject "info" to true, in order to cancel this node.
	for(i=0;i<size;i++){
		if(AdjacencyArray[i].getObject()==info){
			AdjacencyArray[i].setNullNodeBit(true);
			break;
		}
	}
	
	//The shrink_table function will automatically resize the "AdjacencyArray" table.
	if(capacity > 4*getTotalNodes()){
		shrink_table();
	}
	
	return true;
}

//This function duplicates the capacity of the "AdjacencyArray" table..
template <typename T>
void Graph<T>::expand_table(){
	
	int i;
	capacity*=2;
	Node* tempAdjacencyArray = new Node[capacity];
	
	for(i=0;i<size;i++){
		tempAdjacencyArray[i]=AdjacencyArray[i];
	}
	
	delete[] AdjacencyArray;
	AdjacencyArray=tempAdjacencyArray;
	
}

//This function reduces the capacity of the "AdjacencyArray" table by half and resizes the nodes, in order to cover the empty positions.
template <typename T>
void Graph<T>::shrink_table(){
	
	int i,pos=0;
	capacity = capacity/2;
	hashnum=0;
	Node* tempAdjacencyArray = new Node[capacity];
	
	for(i=0;i<capacity*2;i++){
		if(AdjacencyArray[i].getNullNodeBit()==false){
			tempAdjacencyArray[pos] = AdjacencyArray[i];
			tempAdjacencyArray[pos].sethashCode(hashnum);
			hashnum++;
			pos++;
		}
	}
	
	size=pos;
	delete[] AdjacencyArray;
	AdjacencyArray=tempAdjacencyArray;
	
}

//This function adds a new edge to the graph.
template <typename T>
bool Graph<T>::addEdg(const T& from, const T& to, int distance){
	
	if(contains(from)==false || contains(to)==false){
		return false;
	}
	
	int i,insert_flag=0;
	struct Edge<T> newEdge(from,to,distance);
	
	for(i=0;i<size;i++){
		if(AdjacencyArray[i].getNullNodeBit()==false && AdjacencyArray[i].getObject()==from){
			
			auto it = AdjacencyArray[i].connectionList.begin();
			for (;it != AdjacencyArray[i].connectionList.end();it++) {
				if((*it).to == to){
					//There is the same edge, return false.
					return false;
				}
			}
			
			int j,list_item_hash,new_node_hash;
			
			for(j=0;j<size;j++){
				if(AdjacencyArray[j].getNullNodeBit()==false && AdjacencyArray[j].getObject()==to){
					list_item_hash = AdjacencyArray[j].HashCode();
					break;
				}
			}
			
			it = AdjacencyArray[i].connectionList.begin();
			for (;it != AdjacencyArray[i].connectionList.end();it++) {
				
				for(j=0;j<size;j++){
					if(AdjacencyArray[j].getNullNodeBit()==false && AdjacencyArray[j].getObject()==(*it).to){
						new_node_hash = AdjacencyArray[j].HashCode();
						break;
					}
				}
				
				if(list_item_hash < new_node_hash){
					insert_flag=1;
					AdjacencyArray[i].connectionList.insert(it,newEdge);
					break;
				}
				
			}
			
			if(insert_flag==0){
				AdjacencyArray[i].connectionList.push_back(newEdge);
			}
			
			//If this grap is a directed graph, add the reverse node.
			if(isDirectedGraph==false){
				addEdg(to,from,distance);
			}
			
			break;
		}
	}
	
	return true;
}

//This function prints the "AdjacencyArray" table.
template <typename T>
void Graph<T>::printAll(){
	int i;
	
	for(i=0;i<size;i++){
		if(AdjacencyArray[i].getNullNodeBit()==false){
	
			cout << "Node " << AdjacencyArray[i].getObject().get() << " : ";
			auto it2 = AdjacencyArray[i].connectionList.begin();
			for (;it2 != AdjacencyArray[i].connectionList.end();it2++) {
				cout << (*it2).to.get() << "[" << (*it2).dist <<"] ";
			}
			cout << endl;
		}
	}
}

//This function removes an edge ftom the graph.
template <typename T>
bool Graph<T>::rmvEdg(const T& from, const T& to){
	
	int i;
	
	for(i=0;i<size;i++){
		if(AdjacencyArray[i].getNullNodeBit()==false && AdjacencyArray[i].getObject()==from){
			auto it = AdjacencyArray[i].connectionList.begin();
			for (;it != AdjacencyArray[i].connectionList.end();it++) {
				if((*it).to == to){
					
					AdjacencyArray[i].connectionList.remove(*it);
					
					//If this grap is a directed graph, delete the reverse node.
					if(isDirectedGraph==false){
						rmvEdg(to,from);
					}
					return true;
				}
			}
			return false;
		}
	}
	
	return true;
}

//This function creates a dot file in order to graphically represent the graph.
template <typename T>
bool Graph<T>::print2DotFile(const char *filename) const{
	
	string str;
	int i,first=0;
	
	for(i=0;i<size;i++){
		if(AdjacencyArray[i].getNullNodeBit()==false){
			if(first==0){
				//str = "graph Trie {\n\t" + AdjacencyArray[i].getObject().get() +" [label=\""+AdjacencyArray[i].getObject().get()+"\" ,shape=circle, color=black]\n";
				first=1;
				auto it2 = AdjacencyArray[i].connectionList.begin();
				for (;it2 != AdjacencyArray[i].connectionList.end();it2++) {
				//	str.append("\t" +(*it2).from.get()+ " -- " + (*it2).to.get()+ "\n");
				}
			}
			else{
				//str.append("\t" + AdjacencyArray[i].getObject().get() + " [label=\""+AdjacencyArray[i].getObject().get()+"\", shape=circle, color=black]\n");
				auto it2 = AdjacencyArray[i].connectionList.begin();
				for (;it2 != AdjacencyArray[i].connectionList.end();it2++) {
				//	str.append("\t" +(*it2).from.get()+ " -- " + (*it2).to.get()+ "\n");
				}
			}
		}
	}
	str.append("}");
	
	ofstream myfile;
	myfile.open (filename,ios::trunc);
	myfile << str;
	myfile.close();
	
	return true;
}

//This function computed the dfs[Depth first traversal].
template <typename T>
list<T> Graph<T>::dfs(const T& info) const{
	
	list<T> dfsList;
	//Call the dfs recursive function.
	dfsList=dfsRecursive(info,dfsList);
	
	return dfsList;
}

//This is the dfs recursive function. Notice that the second argument is a reference, in order to make changes to the same object(no copy constructor called).
template <typename T>
list<T> Graph<T>::dfsRecursive(const T& info,std::list<T>& dfslist) const {
	
	auto searchIterator = dfslist.begin();
	for (;searchIterator != dfslist.end();searchIterator++) {
		if((*searchIterator)==info){
			break;
		}
	}
	if(searchIterator == dfslist.end()){
		//Add the "info" object in the list.
		dfslist.push_back(info);
	}
	
	int i;
	
	for(i=0;i<size;i++){
		if(AdjacencyArray[i].getObject()==info){
			//
			auto it2 = AdjacencyArray[i].connectionList.begin();
			for (;it2 != AdjacencyArray[i].connectionList.end();it2++) {
				
				searchIterator = dfslist.begin();
				for (;searchIterator != dfslist.end();searchIterator++) {
					if((*searchIterator)==(*it2).to){
						break;
					}
				}
				if(searchIterator == dfslist.end()){
					//If the "to" object is not contained to the dfs list, call the dfsRecursive function and pass it as the first argument.
					dfsRecursive((*it2).to,dfslist);
				}
			}
			break;
		}
	}
	
	return dfslist;
}

//This function computed the bfs[Breadth first traversal].
template <typename T>
list<T> Graph<T>::bfs(const T& info) const{
	
	list<T> bfsList;
	list<T> fifo;
	
	T temp_info = info;
	
	int i;
	auto searchIterator = bfsList.begin();
	auto fifoIterator = fifo.begin();
	
	while(1){
		
		//Add the "temp_info" object in the list.
		bfsList.push_back(temp_info);
		for(i=0;i<size;i++){
			if(AdjacencyArray[i].getObject()==temp_info){
				auto it2 = AdjacencyArray[i].connectionList.begin();
				for (;it2 != AdjacencyArray[i].connectionList.end();it2++) {
					
					searchIterator = bfsList.begin();
					for (;searchIterator != bfsList.end();searchIterator++) {
						if((*searchIterator)==(*it2).to){
							break;
						}
					}
					
					fifoIterator = fifo.begin();
					for (;fifoIterator != fifo.end();fifoIterator++) {
						if((*fifoIterator)==(*it2).to){
							break;
						}
					}
					
					//If the "to" object is not in either the dfs list or the fifo queue, push it to the back of the fifo queue.
					if(searchIterator == bfsList.end() && fifoIterator==fifo.end()){
						fifo.push_back((*it2).to);
					}
				}
				break;
			}
		}
		if(fifo.empty()==false){
			//Temp info is the first element of the fifo queue.
			temp_info=fifo.front();
			fifo.pop_front();
		}
		else{break;}
	}
	return bfsList;
}


//This function implements the Dijkstra algorithm in order to compute the shortest paths from the object given as the first argument to the object given as the second argument.
template <typename T>
list<T> Graph<T>::dijkstra(const T& from, const T& to){
	
	list<T> dijkstraList;
	list<T> retList;
	map <int,int> hashMap;     
	map <int,int> prevMap;    
	T fromObject = from;
	
	Node curr;
	
	int i=0,exit_flag;
	auto searchIterator = dijkstraList.begin();
	std::map<int,int>::iterator hashMapIterator,prevMapIterator;
	auto connectionListIterator = AdjacencyArray[i].connectionList.begin();
	
	//hashMap includes the hash code of each Node of the graph in the first column and the total cost to each node getting started from the the vertex "from" given as the first argument in the second column
	//prevMap includes the hash code of each Node of the graph in the first column and the previous node in the shortest path produced by dijkstra's algorithm in the second column.
	
	//Set hash Map and prev hashMap
	for(i=0;i<size;i++){
		if(AdjacencyArray[i].getNullNodeBit()==false){
			if(AdjacencyArray[i].getObject()==to){
				curr=AdjacencyArray[i];
			}
			
			if(AdjacencyArray[i].getObject()==from){
				hashMap.insert({AdjacencyArray[i].HashCode(),0});
				prevMap.insert({AdjacencyArray[i].HashCode(),0});
			}
			else{
				hashMap.insert({AdjacencyArray[i].HashCode(),MAX_INT});
				prevMap.insert({AdjacencyArray[i].HashCode(),-1});
			}
		}
	}
	
	while(1){
		for(i=0;i<size;i++){
			if(AdjacencyArray[i].getNullNodeBit()==false && AdjacencyArray[i].getObject()==fromObject){
				
				//Insert the fromObject in the dijkstra list.
				dijkstraList.push_back(fromObject);
				//fromDistance is a pointer to the from node inside hash map.
				std::map<int,int>::iterator fromDistance;
				fromDistance = hashMap.find(AdjacencyArray[i].HashCode());
				
				connectionListIterator = AdjacencyArray[i].connectionList.begin();
				for (;connectionListIterator != AdjacencyArray[i].connectionList.end();connectionListIterator++) {
					
					exit_flag=0;
					
					int j;
					for(j=0;j<size;j++){
						if(AdjacencyArray[j].getNullNodeBit()==false && AdjacencyArray[j].getObject()==(*connectionListIterator).to){
							
							searchIterator = find(dijkstraList.begin(), dijkstraList.end(), AdjacencyArray[j].getObject());
							
							//Do not update the nodes that have already been examined.
							if(searchIterator != dijkstraList.end()){
								exit_flag=1;
							}
							
							break;
						}
					}
					if(exit_flag==1){continue;}
					
					//toDistance is a pointer to the node which specifies the destination of this edge inside hash map.
					std::map<int,int>::iterator toDistance;
					toDistance = hashMap.find(AdjacencyArray[j].HashCode());
					
					//fromDistance->second is the least cost to the fromObject.
					//(*connectionListIterator).dist is the cost of this edge.
					//toDistance->second is the cost calculated so far to the node which specifies the destination of this edge.
					if(fromDistance->second + (*connectionListIterator).dist < toDistance->second){
						//Update the distance to the destination node inside hash map.
						//Update the previous node of the the destination node inside prev map.
						toDistance->second = fromDistance->second + (*connectionListIterator).dist;
						prevMapIterator = prevMap.find(toDistance->first);
						prevMapIterator->second=fromDistance->first;
					}
				}
				break;
			}
		}
		
		int min = MAX_INT;
		
		//Select the object with the minimum cost from the second column of the hashMap as the fromObject.
		//The new fromObject should not be included in the dijkstraList.
		hashMapIterator=hashMap.begin();
		for (;hashMapIterator!=hashMap.end(); ++hashMapIterator){
			if(hashMapIterator->second < min){
				searchIterator = dijkstraList.begin();
				for (;searchIterator != dijkstraList.end();searchIterator++) {
					if((*searchIterator)==AdjacencyArray[hashMapIterator->first].getObject()){
						break;
					}
				}
				if(searchIterator == dijkstraList.end()){
					min = hashMapIterator->second;
					fromObject=AdjacencyArray[hashMapIterator->first].getObject();
				}
			}
		}
		if(min==MAX_INT){break;}
	}
	
	//prevMapIterator = prevMap.begin();
	//cout << "iteration " << j << endl;
	//for (;prevMapIterator != prevMap.end();prevMapIterator++) {
	//    cout << prevMapIterator->first << " -> " << prevMapIterator->second << endl;
	// }
	//cout << "-----------------------------------------------------------------" << endl;
	
	
	//Compute the ret list which specifies the path from the node given as the first argument to the node given as the second argument
	//At first, curr is the node  which specifies the final destination ("to") object.
	
	retList.clear();
	while(1){
		
		//Find the previous node inside the prev map.
		prevMapIterator=prevMap.find(curr.HashCode());
		
		//There is not previous node. The algorithm will return an empty list.
		if(prevMapIterator->second==-1){
			retList.clear();
			return retList;
		}
		
		//Add to the front the current node.
		retList.push_front(curr.getObject());
		//Found the "from" node, dijkstra is finished.
		if(curr.getObject()==from){break;}
		
		//prevMapIterator is the first node of the graph. The algorithm will return an empty list.
		if(prevMapIterator->first==prevMapIterator->second){break;}
		//Set the current node's previous node as the current node.
		curr=AdjacencyArray[prevMapIterator->second];
		
	}
	
	//Find from node
	for(i=0;i<size;i++){
		if(AdjacencyArray[i].getNullNodeBit()==false){
			if(AdjacencyArray[i].getObject()==from){
				curr=AdjacencyArray[i];
				break;
			}
		}
	}
	
	//If the first <T> element of retList is different than the "from" element, return an empty list.
	if((*retList.begin()) != curr.getObject()){
		retList.clear();
	}

	
	return retList;
}

//This function implements the Bellman Ford algorithm in order to compute the shortest paths from the object given as the first argument to the object given as the second argument.
template <typename T>
list<T> Graph<T>::bellman_ford(const T& from, const T& to){
	
	list<T> BFList;
	list<T> retList;
	map <int,int> hashMap;     
	map <int,int> prevMap;
	NegativeGraphCycle exception;
	
	Node curr;
	
	int i=0,total_nodes,j;
	std::map<int,int>::iterator hashMapIterator,prevMapIterator;
	auto connectionListIterator = AdjacencyArray[i].connectionList.begin();
	
	total_nodes=getTotalNodes();
		
	//hashMap includes the hash code of each Node of the graph in the first column and the total cost to each node getting started from the the vertex "from" given as the first argument in the second column
	//prevMap includes the hash code of each Node of the graph in the first column and the previous node in the shortest path produced by dijkstra's algorithm in the second column.
	
	//Set hash Map and prev hashMap
	for(i=0;i<size;i++){
		if(AdjacencyArray[i].getNullNodeBit()==false){
			
			if(AdjacencyArray[i].getObject()==to){
				curr=AdjacencyArray[i];
			}
			if(AdjacencyArray[i].getObject()==from){
				hashMap.insert({AdjacencyArray[i].HashCode(),0});
				prevMap.insert({AdjacencyArray[i].HashCode(),0});
			}
			else{
				hashMap.insert({AdjacencyArray[i].HashCode(),MAX_INT});
				prevMap.insert({AdjacencyArray[i].HashCode(),-1});
			}
		}
	}
	
	//Run this loop n+1 times, where n is the number of the functional nodes.
	//During the last loop, the algorithm will detect negative loops inside the graph.
	for(j=0;j<=total_nodes;j++){
		
		for(i=0;i<size;i++){
			if(AdjacencyArray[i].getNullNodeBit()==false){
				
				//fromDistance is a pointer to the from node inside hash map.
				std::map<int,int>::iterator fromDistance;
				fromDistance = hashMap.find(AdjacencyArray[i].HashCode());
				
				connectionListIterator = AdjacencyArray[i].connectionList.begin();
				for (;connectionListIterator != AdjacencyArray[i].connectionList.end();connectionListIterator++) {
					
					int k;
					for(k=0;k<size;k++){
						if(AdjacencyArray[k].getNullNodeBit()==false && AdjacencyArray[k].getObject()==(*connectionListIterator).to){
							break;
						}
					}
					
					//toDistance is a pointer to the node which specifies the destination of this edge inside hash map.
					std::map<int,int>::iterator toDistance;
					toDistance = hashMap.find(AdjacencyArray[k].HashCode());
					prevMapIterator = prevMap.find(toDistance->first);
					
					//fromDistance->second is the least cost to the fromObject.
					//(*connectionListIterator).dist is the cost of this edge.
					//toDistance->second is the cost calculated so far to the node which specifies the destination of this edge.
					if(toDistance->second == MAX_INT && fromDistance->second != MAX_INT){
						
						//This is the last loop. Check for negative loops inside the graph.
						if(j==total_nodes){
							throw exception;
						}
						//Update the distance to the destination node inside hash map.
						//Update the previous node of the the destination node inside prev map.
						toDistance->second = fromDistance->second + (*connectionListIterator).dist;
						prevMapIterator->second=fromDistance->first;
					}
					else if(toDistance->second > (fromDistance->second + (*connectionListIterator).dist)){
						//Update the distance to the destination node inside hash map.
						//Update the previous node of the the destination node inside prev map.
						toDistance->second = fromDistance->second + (*connectionListIterator).dist;
						prevMapIterator->second=fromDistance->first;
						
						//This is the last loop. Check for negative loops inside the graph.
						if(j==total_nodes){
							throw exception;
						}
					}
				}
			}
		}
	}
	
	i=0;
	
	//Compute the ret list which specifies the path from the node given as the first argument to the node given as the second argument
	//At first, curr is the node  which specifies the final destination ("to") object.
	while(1){
		//If there is not a path from the "from" object to the "to" object, return an empty list.
		if(i==getTotalNodes()){
			retList.clear();
			break;
		}
		
		i++;
		//Find the previous node inside the prev map.
		prevMapIterator=prevMap.find(curr.HashCode());
		
		//There is not previous node. The algorithm will return an empty list.
		if(prevMapIterator->second==-1){
			retList.clear();
			return retList;
		}
		//Add to the front the current node.
		retList.push_front(curr.getObject());
		
		//Found the "from" node, bellman ford is finished.
		if(curr.getObject()==from){break;}
		
		//prevMapIterator is the first node of the graph. The algorithm will return an empty list.
		if(prevMapIterator->first==prevMapIterator->second){
			retList.clear();
			break;
		}
		//Set the current node's previous node as the current node.
		curr=AdjacencyArray[prevMapIterator->second];	
	}
	
	return retList;
}

//This function implements the Prim algorithm in order to compute the minimum spanning tree of the graph.
//This is a greedy alorithm.
template <typename T>
list<Edge<T>> Graph<T>::mst(){
	
	list<Edge<T>> retList;
	list<T> mstList;
	
	//If the graph is directed, return an empty list.
	if(isDirectedGraph==true){
		return retList;
	}
	
	map <int,int> hashMap;     
	int i=0,set_flag=0,exit_flag;
	T fromObject,prevFromObject;
	auto searchIterator = mstList.begin();
	std::map<int,int>::iterator hashMapIterator;
	auto connectionListIterator = AdjacencyArray[i].connectionList.begin();
	
	//hashMap includes the hash code of each Node of the graph in the first column and the cost of the previous edge.
	
	//Set hash Map.
	for(i=0;i<size;i++){
		if(AdjacencyArray[i].getNullNodeBit()==false){
			//Set fromObject because this function has not arguments.
			if(set_flag==0){
				fromObject = AdjacencyArray[i].getObject();
				set_flag=1;
			}
			
			if(i==0){
				hashMap.insert({AdjacencyArray[i].HashCode(),0});
			}
			else{
				hashMap.insert({AdjacencyArray[i].HashCode(),MAX_INT});
			}
		}
	}
	
	while(1){
		for(i=0;i<size;i++){
			if(AdjacencyArray[i].getNullNodeBit()==false && AdjacencyArray[i].getObject()==fromObject){
				
				//Insert the fromObject in the mst list.
				mstList.push_back(fromObject);
				
				connectionListIterator = AdjacencyArray[i].connectionList.begin();
				for (;connectionListIterator != AdjacencyArray[i].connectionList.end();connectionListIterator++) {
					
					int j;
					exit_flag=0;
					
					for(j=0;j<size;j++){
						if(AdjacencyArray[j].getNullNodeBit()==false && AdjacencyArray[j].getObject()==(*connectionListIterator).to){
							searchIterator = find(mstList.begin(), mstList.end(), AdjacencyArray[j].getObject());
							
							//Do not update the nodes that have already been examined.
							if(searchIterator != mstList.end()){
								exit_flag=1;
							}
							
							break;
						}
					}
					if(exit_flag==1){continue;}
					
					//toDistance is a pointer to the node which specifies the destination of this edge inside hash map.
					std::map<int,int>::iterator toDistance;
					toDistance = hashMap.find(AdjacencyArray[j].HashCode());
					
					//toDistance->second is the cost calculated so far to the node which specifies the destination of this edge.
					if(toDistance->second > (*connectionListIterator).dist){
						//Update the distance to the destination node inside hash map.
						toDistance->second = (*connectionListIterator).dist;
					}
				}
				break;
			}
		}
		
		int min = MAX_INT;
		
		//Select the object with the minimum cost from the second column of the hashMap as the fromObject.
		//The new fromObject should not be included in the mstList.
		hashMapIterator=hashMap.begin();
		for (;hashMapIterator!=hashMap.end(); ++hashMapIterator){
			if(hashMapIterator->second < min){
				searchIterator = mstList.begin();
				for (;searchIterator != mstList.end();searchIterator++) {
					if((*searchIterator)==AdjacencyArray[hashMapIterator->first].getObject()){
						break;
					}
				}
				if(searchIterator == mstList.end()){
					min = hashMapIterator->second;
					fromObject=AdjacencyArray[hashMapIterator->first].getObject();
				}
			}
		}
		if(min==MAX_INT){break;}
		else{
			//retList is a list which contains edges and not T objects.
			int break_flag=0;
			
			//Find the edge which has the same distance with the min distance. Maybe there are more than one edges with the same distance and the same destination.
			//Check graph_05 as an example.
			for(i=0;i<size;i++){
				if(AdjacencyArray[i].getNullNodeBit()==false){
					
					//The candidate object should be included inside the mstList.
					searchIterator=find(mstList.begin(),mstList.end(),AdjacencyArray[i].getObject());
					if(searchIterator==mstList.end()){
						continue;
					}
					
					//Maybe found the correct object. Check the edge list (connectionList).
					connectionListIterator = AdjacencyArray[i].connectionList.begin();
					for (;connectionListIterator != AdjacencyArray[i].connectionList.end();connectionListIterator++) {
						//If there is an edge with the following characteristics, put it in the retList.
						//Edge characteristics : The "to" object of this edge should be the fromObject.
						//                       The distance of this edge should be the same as the computed min distance to the fromObject.                   
						
						if((*connectionListIterator).to==fromObject && (*connectionListIterator).dist==min){
							retList.push_back(*connectionListIterator);
							break_flag=1;
							break;
						}
					}
					if(break_flag==1){
						break;
					}
				}
			}
		}
	}
	
	auto retList_iterator = retList.begin();
	
	Edge<T>* insertElement;
	int min_cost=MAX_INT;
	list<Edge<T>> finalList;
	auto finalListIterator = finalList.begin();
	
	for(i=0;i < (int)retList.size();i++){
		retList_iterator = retList.begin();
		for (;retList_iterator!=retList.end(); ++retList_iterator){
			
			finalListIterator = find(finalList.begin(),finalList.end(),*retList_iterator);
			if(finalListIterator!=finalList.end()){continue;}
			
			if((*retList_iterator).dist < min_cost){
				
				min_cost = (*retList_iterator).dist;
				insertElement = &(*retList_iterator);
				
			}
		}
		finalList.push_back(*insertElement);
		min_cost=MAX_INT;
	}
	
	finalListIterator = finalList.begin();
	int j;
	T swapElement;
	
	
	for (;finalListIterator!=finalList.end(); ++finalListIterator){
		
		for(i=0;i<size;i++){
			if(AdjacencyArray[i].getNullNodeBit()==false && AdjacencyArray[i].getObject()==(*finalListIterator).from){
				break;
			}
		}
		for(j=0;j<size;j++){
			if(AdjacencyArray[j].getNullNodeBit()==false && AdjacencyArray[j].getObject()==(*finalListIterator).to){
				break;
			}
		}
		
		if(AdjacencyArray[j].HashCode() < AdjacencyArray[i].HashCode()){
			swapElement = (*finalListIterator).to;
			(*finalListIterator).to = (*finalListIterator).from;
			
			(*finalListIterator).from = swapElement;
			
		}
	}
	
	return finalList;
}

#endif
