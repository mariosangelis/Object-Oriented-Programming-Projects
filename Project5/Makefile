
build: Graph.hpp GraphUI.hpp
	g++ -Wall -g -std=c++11 GraphString.cpp -o gstr
	g++ -Wall -g -std=c++11 GraphStudent.cpp -o gstudent
	g++ -Wall -g -std=c++11 GraphInteger.cpp -o gint
	
bfs1:
	./gstr < tests/bfs1.in > bfs1.out
	diff -urN tests/bfs1.std bfs1.out
bfs2:
	./gstr < tests/bfs2.in > bfs2.out 
	diff -urN tests/bfs2.std bfs2.out
bfs3:
	./gstr < tests/bfs3.in > bfs3.out 
	diff -urN tests/bfs3.std bfs3.out
dfs1:
	./gstr < tests/dfs1.in > dfs1.out
	diff -urN tests/dfs1.std dfs1.out
dfs2:
	./gstr < tests/dfs2.in > dfs2.out 
	diff -urN tests/dfs2.std dfs2.out
dfs3:
	./gstr < tests/dfs3.in > dfs3.out 
	diff -urN tests/dfs3.std dfs3.out
mst1:
	./gstr < tests/mst1.in > mst1.out
	diff -urN tests/mst1.std mst1.out
mst2:
	./gstr < tests/mst2.in > mst2.out
	diff -urN tests/mst2.std mst2.out
mst3:
	./gstudent < tests/mst3.in > mst3.out
	diff -urN tests/mst3.std mst3.out
dijkstra1:
	./gstr < tests/dijkstra1.in > dijkstra1.out
	diff -urN tests/dijkstra1.std dijkstra1.out
dijkstra2:
	./gstr < tests/dijkstra2.in > dijkstra2.out
	diff -urN tests/dijkstra2.std dijkstra2.out
dijkstra3:
	./gstr < tests/dijkstra3.in > dijkstra3.out
	diff -urN tests/dijkstra3.std dijkstra3.out
dijkstra4:
	./gstudent < tests/dijkstra4.in > dijkstra4.out
	diff -urN tests/dijkstra4.std dijkstra4.out
bellman-ford1:
	./gstr < tests/bellman-ford1.in > bellman-ford1.out
	diff -urN tests/bellman-ford1.std bellman-ford1.out
bellman-ford2:
	./gstr < tests/bellman-ford2.in > bellman-ford2.out
	diff -urN tests/bellman-ford2.std bellman-ford2.out
bellman-ford3:
	./gstr < tests/bellman-ford3.in > bellman-ford3.out
	diff -urN tests/bellman-ford3.std bellman-ford3.out
	
run: bfs1 bfs2 bfs3 dfs1 dfs2 dfs3 mst1 mst2 mst3 dijkstra1 dijkstra2 dijkstra3 dijkstra4 bellman-ford1 bellman-ford2 bellman-ford3
