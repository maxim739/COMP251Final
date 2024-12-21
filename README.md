# McGill COMP251 Final Coding Project
A nice alternative to a written final:
For this project I implimented 5 methods as a way to prove my knowledge of Algorithms and Data Structures for the McGill course. The assignment along with the boilerplate was created by the professors and TAs for examination purposes, and the test cases were created by some very nice peers.

# Max Passengers
In this method I implimented the Edmonds - Karp algorithm to find the maximum flow of a network between two "Buildings" - nodes on a graph, connected by "Tracks" - edges between the nodes. 
To impliment the graph I represented the nodes and edges with an adjacency list, and then found the shortest path between the two nodes by repetedly calculating the shortest path between the two nodes using a Breadth-First search, and then going over the edges on the shortest path and creating a residual graph for the Edmonds-Karp algorithm.
After the BFS couldn't find any more avaliable paths between the two nodes, the flow had been maximized between the two nodes, and the maximum flow was returned.

# Best Metro System
This method called for a minimum spanning tree given a node and edge set. I implimented kruskal's algorithm, which required an implimentation of a Disjoint Set data structure. For the disjoint set, I implimented path compression for finding elements, and union-by-rank for the union operation.
The algorithm works by sorting the edges based on some "goodness" value, and then adding the edge to the MST if it connects two elements that don't belong to the same disjoint set.

# Add/Search For Passengers
Adding: Names are added to a "trie" data structure, which impliments each character as a node in a prefix tree. If two names share the same prefix, they will share that same route down the tree.
Searching: Given a prefix to look for, that path down a tree was searched, and every path from that prefix node down to a lead was returned. If the prefix doesn't match any path down the tree, no names were returned.

# Hire Ticket Checkers
Given a 2D array representing employees and their start and end times for work, return the maximum number of employees that could work. 
This was an implimentation of a Greedy Algorithm that added employees to the schedule by the earliest finishing time compatible with the previous employee added, and then returning the size of the list of employees.

Overall, this assignment was a good representation of the material we learned in class, which was fun to write (especially compared the alternative - studying and writing a final).