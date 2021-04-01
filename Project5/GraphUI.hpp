#include "Graph.cpp"
#include <bits/stdc++.h> 

#ifndef _GRAPH_UI_
#define _GRAPH_UI_
using namespace std;


template <typename T>
int graphUI() {
	
	string option, line;
	int distance;
	bool isDirectedGraph = false;
	
	
	cin >> option;
	if(!option.compare("digraph")){
		isDirectedGraph = true;
	}
	Graph<T> g(isDirectedGraph);
	
	while(true) {
		
		std::stringstream stream;
		cin >> option;
		
		if(!option.compare("av")) {
			getline(std::cin, line);
			stream << line;
			
			T vtx(stream);
			
			if(g.addVtx(vtx)){
				cout << "av " << vtx << " OK\n";
			}
			else{
				cout << "av " << vtx << " NOK\n";
			}
		}
		else if(!option.compare("rv")) {
			
			getline(std::cin, line);
			stream << line;
			
			T vtx(stream);
			
			if(g.rmvVtx(vtx)){
				cout << "rv " << vtx << " OK\n";
			}
			else{
				cout << "rv " << vtx << " NOK\n";
			}
		}
		else if(!option.compare("ae")) {
			stringstream from,to;
			string token;
			int tokens_num=0;
			vector <string> tokens; 
			
			getline(std::cin, line);
			stream << line;
			

			while(stream >> token ) {
				tokens_num++;
				tokens.push_back(token);
			}
			
			if(tokens_num==5){
				//tokens[1] is the "from" name
				//tokens[2] is the "from" AEM
				//tokens[3] is the "to" name
				//tokens[4] is the "to" AEM
				//tokens[5] is the distance
				from << tokens[0];
				from << " ";
				from << tokens[1];
				to << tokens[2];
				to << " ";
				to << tokens[3];
				distance = stoi(tokens[4]);
			}
			else{
				from.str(tokens[0]);
				to.str(tokens[1]);
				distance = stoi(tokens[2]);
			}
			
			T from_info(from);
			T to_info(to);
			
			if(g.addEdg(from_info,to_info,distance)){
				cout << "ae " << from_info << " " << to_info << " OK\n";
			}
			else{
				cout << "ae " << from_info << " " << to_info << " NOK\n";
			}
		}
		else if(!option.compare("re")) {
			
			stringstream from,to;
			string token;
			
			vector <string> tokens; 
			
			getline(std::cin, line);
			stream << line;
			int tokens_num=0;
			
			while(stream >> token ) {
				tokens_num++;
				tokens.push_back(token);
			}
			
			if(tokens_num==4){
				//tokens[1] is the "from" name
				//tokens[2] is the "from" AEM
				//tokens[3] is the "to" name
				//tokens[4] is the "to" AEM
				from << tokens[0];
				from << " ";
				from << tokens[1];
				
				to << tokens[2];
				to << " ";
				to << tokens[3];
			}
			else{
				from.str(tokens[0]);
				to.str(tokens[1]);
			}
			
			T from_info(from);
			T to_info(to);
			
			if(g.rmvEdg(from_info,to_info)){
				cout << "re " << from_info << " " << to_info << " OK\n";
			}
			else{
				cout << "re " << from_info << " " << to_info << " NOK\n";
			}
		}
		else if(!option.compare("dot")) {
			
			char filename[256];
			
			cin.getline(filename,256);
		
			if(g.print2DotFile(filename)){
				cout << "dot " << filename << " OK\n";
			}
			else{
				cout << "dot " << filename << " NOK\n";
			}
		}
		else if(!option.compare("bfs")) {
		
			getline(std::cin, line);
			stream << line;
			
			T startNode(stream);
			
			list<T> bfsList = g.bfs(startNode);
		
			cout << "\n----- BFS Traversal -----\n";
			auto it = bfsList.begin();
			for (;it != bfsList.end();it++) {
				it++;
				if(it==bfsList.end()){
					it--;
					cout << (*it);
					break;
				}
				it--;
				cout << (*it) << " -> ";
			}
			cout << "\n-------------------------\n";
		}
		else if(!option.compare("dfs")) {
			
			getline(std::cin, line);
			stream << line;
			
			T startNode(stream);
			list<T> dfsList = g.dfs(startNode);
			
			cout << "\n----- DFS Traversal -----\n";
			auto it = dfsList.begin();
			for (;it != dfsList.end();it++) {
				it++;
				if(it==dfsList.end()){
					it--;
					cout << (*it);
					break;
				}
				it--;
				
				cout << (*it) << " -> ";
			}
			cout << "\n-------------------------\n";
		}
		else if(!option.compare("dijkstra")) {
			stringstream from,to;
			string token;
			vector <string> tokens; 
			
			getline(std::cin, line);
			stream << line;
			int tokens_num=0;
			
			while(stream >> token ) {
				tokens_num++;
				tokens.push_back(token);
			}
			
			if(tokens_num==4){
				//tokens[1] is the "from" name
				//tokens[2] is the "from" AEM
				//tokens[3] is the "to" name
				//tokens[4] is the "to" AEM
				from << tokens[0];
				from << " ";
				from << tokens[1];
				
				to << tokens[2];
				to << " ";
				to << tokens[3];
			}
			else{
				from.str(tokens[0]);
				to.str(tokens[1]);
			}
			
			
			T from_info(from);
			T to_info(to);
			list<T> dijkstraList = g.dijkstra(from_info,to_info);
			
			cout << "Dijkstra (" << from_info << " - " << to_info << "): ";
			
			auto it = dijkstraList.begin();
			for (;it != dijkstraList.end();it++) {
				it++;
				if(it==dijkstraList.end()){
					it--;
					cout << (*it) << endl;
					break;
				}
				it--;
				
				cout << (*it) << ", ";
			}
			
			if(dijkstraList.empty()==true){
				cout << endl;
			}
			
		}
		else if(!option.compare("bellman-ford")) {
			
			stringstream from,to;
			string token;
			vector <string> tokens; 
			
			getline(std::cin, line);
			stream << line;
			int tokens_num=0;
			
			while(stream >> token ) {
				tokens_num++;
				tokens.push_back(token);
			}
			
			if(tokens_num==4){
				//tokens[1] is the "from" name
				//tokens[2] is the "from" AEM
				//tokens[3] is the "to" name
				//tokens[4] is the "to" AEM
				from << tokens[0];
				from << " ";
				from << tokens[1];
				
				to << tokens[2];
				to << " ";
				to << tokens[3];
			}
			else{
				from.str(tokens[0]);
				to.str(tokens[1]);
			}
			
			list<T> BFList;
			
			T from_info(from);
			T to_info(to);
			
			cout << "Bellman-Ford (" << from_info << " - " << to_info << "): ";
			
			try{
				BFList = g.bellman_ford(from_info,to_info);
			}
			catch(NegativeGraphCycle exception){
				exception.what();
				continue;
			}
			
			
			if(BFList.empty()==true){
				//cout << "List is empty" << endl;
				continue;
				
			}
			
			auto it = BFList.begin();
			for (;it != BFList.end();it++) {
				it++;
				if(it==BFList.end()){
					it--;
					cout << (*it) << endl;
					break;
				}
				it--;
				
				cout << (*it) << ", ";
			}
		
		}
		else if(!option.compare("mst")) {
			
			int sum=0;
			list<Edge<T>> mstList;
			mstList = g.mst();
			
			
			if(mstList.empty()==true){continue;}
			
			cout << "\n--- Min Spanning Tree ---\n";
			
			auto mstListIterator = mstList.begin();
			for (;mstListIterator!=mstList.end(); ++mstListIterator){
				
				cout << (*mstListIterator).from << " -- " << (*mstListIterator).to << " (" << (*mstListIterator).dist << ")" << endl;
				sum +=(*mstListIterator).dist;
				
			}
			
			cout << "MST Cost: " << sum << endl;
		}
		else if(!option.compare("q")) {
			//cout << "bye bye...\n";
			return 0;
		}
		else if(!option.compare("#")) {
			string line;
			getline(cin,line);
			//cout << "Skipping line: " << line << endl;
		}
		else {
			cout << "INPUT ERROR\n";
			return -1;
		}
		
		//g.printAll();
		
	}
return -1;  
}

#endif
