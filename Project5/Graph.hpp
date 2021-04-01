
#ifndef _GRAPH_HPP_ 
#define _GRAPH_HPP_
using namespace std;
#include <iostream>
#include <list>


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

#endif
