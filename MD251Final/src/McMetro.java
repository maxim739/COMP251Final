import java.util.*;

/* Acknowledgments
For my understanding of edmonds-karp algorithm I referenced:
    - https://www.w3schools.com/dsa/dsa_algo_graphs_edmondskarp.php

I implemented the merge and sort functions for the bestMetroSystem() method with some research from the
    website JavaTPoint at https://www.javatpoint.com/merge-sort

For the Trie data structure implementation in search and add names the below resources were used for my understanding
    Stringology - Sasha Bell, Tiffany Young
    https://www.geeksforgeeks.org/introduction-to-trie-data-structure-and-algorithm-tutorials/ - Geeks For Geeks
*/

public class McMetro {
    protected Track[] tracks;
    protected HashMap<BuildingID, Building> buildingTable = new HashMap<>();
    protected Tree Trees;

    // You may initialize anything you need in the constructor
    McMetro(Track[] tracks, Building[] buildings) {
        this.tracks = tracks;
        this.Trees = new Tree();
        // Populate buildings table
        if (buildings != null){
            for (Building building : buildings) {
                buildingTable.putIfAbsent(building.id(), building);
            }
        }
    }

    // BFS implementation as seen in class
    ArrayList<Track> bfs(HashMap<BuildingID, ArrayList<BuildingID>> adj, BuildingID start, BuildingID end, HashMap<BuildingID, Node> info, int bottleNeck, HashMap<Track, Integer> capLeft) {
        // return the shortest path between two nodes
        ArrayList<Track> ret = new ArrayList<>();

        if(null == adj.get(start)) {
            return ret;
        } else {
            Node tmp = info.get(start);
            tmp.chDistance(0);
            tmp.chColor(1);
            tmp.chPrevious(null);
        }

        Queue queue = new Queue();
        queue.enqueue(info.get(start));

        while (!(queue.queue.isEmpty())) {
            Node v = queue.dequeue();
            // Find the corresponding building
            BuildingID here = null;
            for(BuildingID b : info.keySet()){
                if (info.get(b) == v) {
                    here = b;
                }
            }
            if (here == null){
                System.out.println("Messed up");
                return ret;
            }
            for (BuildingID build : adj.get(here)){
                Node tmp = info.get(build);
                if (tmp.getColor() == 0) {
                    tmp.chColor(1);
                    queue.enqueue(tmp);
                    tmp.chPrevious(here);
                    tmp.chDistance(info.get(here).getDistance() + 1);
                }
            }
            info.get(here).chColor(2);
        }

        ArrayList<BuildingID> ret2 = new ArrayList<>();
        Node tmp = info.get(end);
        ret2.add(end);
        while (tmp.getPrevious() != null) {
            ret2.add(0,tmp.getPrevious());
            tmp = info.get(tmp.getPrevious());
        }

        int i;
        int j = 1;
        for (i = 0; j < ret2.size(); i++) {
            for (Track tr : tracks) {
                if ((0 == tr.startBuildingId().compareTo(ret2.get(i))) && (0 == tr.endBuildingId().compareTo(ret2.get(j)))) {
                    ret.add(tr);
                }
                if (capLeft.get(tr) < bottleNeck) {
                    bottleNeck = capLeft.get(tr);
                }
            }

            j++;
        }
        return ret;
    }

    // Maximum number of passengers that can be transported from start to end
    int maxPassengers(BuildingID start, BuildingID end) {
        // IF THERE IS NO TRACK CONNECTING THE TWO BUILDINGS, NO ONE CAN TRAVEL BETWEEN THEM
        // Map flow to each track we have
        HashMap<Track, Integer> capLeft = new HashMap<>();
        for (Track tr : capLeft.keySet()) { capLeft.put(tr, tr.capacity()); }   // 0 on all edges to start

        HashMap<BuildingID, ArrayList<BuildingID>> adj = new HashMap<>();
        for (Track t : tracks) {    // Add every track to adjacency list - going to have to get tracks from the end
            if (null == adj.get(t.startBuildingId())) {
                ArrayList<BuildingID> tmp = new ArrayList<>();
                tmp.add(t.endBuildingId());
                adj.put(t.startBuildingId(), tmp);
            } else {
                adj.get(t.startBuildingId()).add(t.endBuildingId());
            }
        }

        HashMap<BuildingID, Node> info = new HashMap<>();
        for (BuildingID b : buildingTable.keySet()) {
            Node tmp = new Node(null, 0, Integer.MAX_VALUE);
            info.put(b, tmp);
        }   // Build a hashmap holding all necessary info

        int bottleNeck = Integer.MAX_VALUE;

        ArrayList<Track> shortest = bfs(adj, start, end, info, bottleNeck, capLeft);
        while (shortest.size() != 0) {
            for (Track tr : shortest) {
                int nw = capLeft.get(tr) - bottleNeck;
                capLeft.put(tr, nw);
                if (nw == 0) {
                    shortest.remove(tr);
                }
            }
            for (Track tr)
        }
        // IN ORDER TO REMOVE TRACKS FROM THE CHECKABLE LIST FOR THE BFS, WE NEED A WAY TO ACCESS TRACKS CONSTANT TIME
        // CREATE FIELD IN ADJACENCY LIST THAT CAN HOLD THE TRACK THAT CONNECTS THE TWO
            // ALLOW ALL FIELDS TO BE ACCESSED CONSTANT TIME - NO MORE FOR LOOPS
            // HashMap<BuildingID, HashMap<BuildingID, Object[4]>>
                //[Track, PrevBuilding, color, distance] - may still need nodes for the graph implementation
                    // In theory this should work

       // min(source, end, trackcap)
        return 0;
    }

    // The implementation for sort and merge was given by https://www.javatpoint.com/merge-sort
    static void sort(Track[] tracks, HashMap<Track, Integer> values, int start, int finish) {
        if (start < finish) {
            int m = (start + finish) / 2;
            sort(tracks, values, start, m);
            sort(tracks, values, m + 1, finish);
            merge(tracks, values, start, m, finish);
        }
    }

    static void merge(Track[] tracks, HashMap<Track, Integer> values, int start, int mid, int finish) {
        int one = mid - start + 1;
        int two = finish - mid;

        Track[] Left = new Track[one];
        for (int i = 0; i < one; i++) {
            //System.out.println("Left: " + i);
            Left[i] = tracks[start + i];
        }

        Track[] Right = new Track[two];
        for (int i = 0; i < two; i++) {
            //System.out.println("Right: " + i);
            Right[i] = tracks[mid + 1 + i];
        }

        int i = 0;
        int j = 0;
        int k = start;

        while ((i < one) && (j < two)) {
            if (values.get(Left[i]) >= values.get(Right[j])) {
                tracks[k] = Left[i];
                i++;
            } else {
                tracks[k] = Right[j];
                j++;
            }
            k++;
        }
        while (i < one) {
            tracks[k] = Left[i];
            i++;
            k++;
        }

        while (j < two) {
            tracks[k] = Right[j];
            j++;
            k++;
        }
    }

    TrackID[] bestMetroSystem() {
        // Implementation of Kruskal's
        // Make sure you impleement so that the tracks are undirected

        HashMap<Track, Integer> value = new HashMap<>();    // Stores the "goodness" of all the tracks

        for (Track tr : tracks) {   // Map the value to each track
            int min = Math.min(buildingTable.get(tr.startBuildingId()).occupants(),
                    Math.min(buildingTable.get(tr.endBuildingId()).occupants(), tr.capacity()));
            value.put(tr, min/tr.cost());
        }

        // Need to use disjoint sets
        ArrayList<Track> ret1 = new ArrayList<>();    // Perhaps won't need
        NaiveDisjointSet<BuildingID> buildings = new NaiveDisjointSet<>();

        // Add all of the buildings to the disjoint set
        for (BuildingID build : buildingTable.keySet()) { buildings.add(build); }

        // Sort the tracks based on their goodness value - impliment merge sort
        Track[] sorted = new Track[tracks.length];
        for (int i = 0; i < tracks.length; i++) { sorted[i] = tracks[i]; }
        sort(sorted, value, 0, (sorted.length-1));
        //if (sorted.length > 1) {sort(sorted, value, 0, sorted.length); }    // Otherwise already sorted
        // Now this array should have TrackID's sorted by goodness
        for (Track tr : sorted) {
            System.out.println("Track: "+tr.id()+" value: "+value.get(tr));
        }

        for (Track tr : sorted) {
            BuildingID a = buildings.find(tr.startBuildingId());
            BuildingID b = buildings.find(tr.endBuildingId());
            if (0 != a.compareTo(b)) {
                // The track connects two buildings that are not a part of the same component
                buildings.union(a, b);  // Union the two components
                ret1.add(tr);   // Add the track to the MST
            }
        }

        int size = ret1.size();
        TrackID[] ret = new TrackID[size];
        for (int i = 0; i < size; i++) { ret[i] = ret1.get(i).id(); }    // Copy all elements from arraylist to array
        return ret;
    }


    void addPassenger(String name) {
        name = name.toLowerCase();
        //System.out.println(name);
        this.Trees.add(name);
    }

    // Do not change this
    void addPassengers(String[] names) {
        for (String s : names) {
            addPassenger(s);
        }
    }

    // Returns all passengers in the system whose names start with firstLetters
    ArrayList<String> searchForPassengers(String firstLetters) {
        firstLetters = firstLetters.toLowerCase();
        ArrayList<String> ret = new ArrayList<>();
        ret = this.Trees.permutations(firstLetters);
        return ret;
    }

    // Implimented these instances of merge and sort using the same resource as before -
        //  - https://www.javatpoint.com/merge-sort
    static void merge1(int[][] table, int start, int mid, int end) {
        //System.out.println("Start: "+start+" Mid: "+mid+" End: "+end);
        int one = mid - start + 1;
        int two = end - mid;
        //System.out.println("One: "+one+" Two: "+two);

        /* if(end == start + 1) {  // We have a 2 element list
                if (table[start][1] > table[end][1]) {
                int[][] temp = new int[1][2];
                temp[0][0] = table[start][0];
                temp[0][1] = table[start][1];
                table[start][0] = table[end][0];
                table[start][1] = table[end][1];
                table[end][0] = temp[0][0];
                table[end][1] = temp[0][1];
            }
            return;
        } */

        int[][] left = new int[one][2];
        for (int x = 0; x < one; x++) {
            left[x][0] = table[x+start][0];
            left[x][1] = table[x+start][1];
        }
        int[][] right = new int[two][2];
        for (int x = 0; x < two; x++) {
            right[x][0] = table[x+mid+1][0];
            right[x][1] = table[x+mid+1][1];
        }

        int i = 0;
        int j = 0;
        int k = start;

        while ((i < one) && (j < two)) {
            //System.out.println("i: "+i+" j: "+j+" k: "+k);
            if (left[i][1] <= right[j][1]) {    // Sort by earliest finishing time
                table[k][0] = left[i][0];
                table[k][1] = left[i][1];
                i++;
            } else {
//                System.out.println("Table len: "+table.length+" k: "+k);
//                System.out.println("Table J len: "+right.length+" j: "+j);
                table[k][0] = right[j][0];
                table[k][1] = right[j][1];
                j++;
            }
            k++;
        }

        while ( i < one) {
            table[k][0] = left[i][0];
            table[k][1] = left[i][1];
            i++;
            k++;
        }

        while ( j < two) {
            table[k][0] = right[j][0];
            table[k][1] = right[j][1];
            j++;
            k++;
        }

    }

    static void sort1(int[][] table, int start, int end) {
        //System.out.println("Table start: "+start+" end: "+end);

//        for (int z = 0; z < table.length; z++) {
//            System.out.println("Employee: "+z+" Start: "+table[z][0]+" End: "+table[z][1]);
//        }

        // If the start and end only allow for two elements, then just sort here and end it
        //if ()

        //if (table.length > 1) {
            if (start < end) {
                int mid = (start + (end-1)) / 2;
                sort1(table, start, mid);
                sort1(table, mid+1, end);
                merge1(table, start, mid, end);
            }
        //}
    }

    static int hireTicketCheckers(int[][] schedule) {
        int ret = 0;
        // Sort the employees by their finishing times schedule[i][1]
        sort1(schedule, 0, schedule.length-1);

        // Now go through and add the first compatible element
        int prevFinishTime = -100001;
        for (int i = 0; i < schedule.length; i++) { // Need to ensure this element is compatible if we add it
            if (schedule[i][0] >= prevFinishTime) { // Add the element
                ret++;
                prevFinishTime = schedule[i][1];
            }
        }
        return ret;
    }
}