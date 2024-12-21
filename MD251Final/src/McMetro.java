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
    boolean bfs(HashMap<BuildingID, Node> map, HashMap<Track, Integer> currFlow, BuildingID start, BuildingID end) {
        // Make sure we can't accidentally remove the start node
        // Need to clean the bfs from last time
        for (BuildingID id : map.keySet()) {
            Node x = map.get(id);
            x.distance = Integer.MAX_VALUE;
            x.color = 0;
            x.previous = null;
            x.pTrack = null;
        }

        // Start at the start node
        Node tmp = map.get(start);
        tmp.chDistance(0);
        tmp.chColor(1);

        // Main BFS component
        Queue queue = new Queue();
        queue.enqueue(tmp);
        while (!(queue.queue.isEmpty())) {
            Node v = queue.dequeue();
            for (BuildingID build : v.nextBuild) {
                Node tmp2 = map.get(build);
                if (tmp2.color == 0) {
                    tmp2.color = 1;
                    queue.enqueue(tmp2);
                    tmp2.distance = v.distance + 1;
                    // previous of this node is v
                    int index = tmp2.prevBuild.indexOf(v.here);
                    tmp2.previous = tmp2.prevBuild.get(index);
                    tmp2.pTrack = tmp2.prevTrack.get(index);
                }
            }
            v.color = 2;
        }

        // calculate the path - if we can't reach the start from the end, return false
        ArrayList<Track> tracksOnPath = new ArrayList<>();
        int bottleNeck = Integer.MAX_VALUE;

        Node what = map.get(end);

        while (null !=  what.previous) {
            Track preTrack = what.pTrack;
            tracksOnPath.addFirst(preTrack);
            int check = preTrack.capacity() - currFlow.get(preTrack);
            if (check < bottleNeck) { bottleNeck = check; }

            what = map.get(what.previous);
        }

        //for(Track tr : tracksOnPath){System.out.println(tr.id());}

        if (0 != what.here.compareTo(start)) {
            return false;
        }

        // Remove all the nodes that are no longer needed - capacity is same as flow - need to also clean distances/prev
        Node st = map.get(start);
        int z = 0;
        while (0 != buildingTable.get(st.here).compareTo(buildingTable.get(end))) {
            int num = st.nextTrack.indexOf(tracksOnPath.get(z));
            Track now = tracksOnPath.get(z);
            int space = currFlow.get(now) + bottleNeck;
            if (now.capacity() - space <= 0) {
                // We need to remove it and move on
                Node next = map.get(st.nextBuild.get(num));
                st.nextBuild.remove(num);
                st.nextTrack.remove(num);

                st = next;
            } else {
                // Just add to the flow and move on
                st = map.get(st.nextBuild.get(num));
            }
            currFlow.put(now, space);
            z++;
        }

        return true;
    }

    // Maximum number of passengers that can be transported from start to end
    int maxPassengers(BuildingID start, BuildingID end) {
        HashMap<BuildingID, Node> map = new HashMap<>();
        HashMap<Track, Integer> currFlow = new HashMap<>();

        // Initialize the graph using an adjacency list
        for (Track tr : tracks) {
            Node tmp = null;
            if(map.get(tr.startBuildingId()) == null){
                tmp = new Node(tr.startBuildingId(), null, 0, Integer.MAX_VALUE);
            } else {
                tmp = map.get(tr.startBuildingId());
            }
            tmp.addNext(tr.endBuildingId(), tr);
            map.put(tr.startBuildingId(), tmp);

            Node tmp2 = null;
            if (map.get(tr.endBuildingId()) == null) {
                tmp2 = new Node(tr.endBuildingId(), null, 0, Integer.MAX_VALUE);
            } else {
                tmp2 = map.get(tr.endBuildingId());
            }
            tmp2.addPrev(tr.startBuildingId(), tr);
            map.put(tr.endBuildingId(), tmp2);

            currFlow.put(tr, 0);
        }

        if(map.get(start) == null || map.get(end) == null){
            return 0;
        }

        boolean check = bfs(map, currFlow, start, end);
        while (check) {
            check = bfs(map, currFlow, start, end);
        }

        int ret = 0;

        for (Track tr : map.get(end).prevTrack) {
            ret = ret + currFlow.get(tr);
        }

        return Math.min(ret, Math.min(buildingTable.get(start).occupants(), buildingTable.get(end).occupants()));
    }

    // Researched how to implement sort and merge from https://www.javatpoint.com/merge-sort
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
            Left[i] = tracks[start + i];
        }

        Track[] Right = new Track[two];
        for (int i = 0; i < two; i++) {
            Right[i] = tracks[mid + 1 + i];
        }

        int i = 0;
        int j = 0;
        int k = start;

        while ((i < one) && (j < two)) {
            if (0 <= values.get(Left[i]).compareTo(values.get(Right[j]))) {
            //if (values.get(Left[i]) >= values.get(Right[j])) {
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
        HashMap<Track, Integer> value = new HashMap<>();    // Stores the "goodness" of all the tracks

        for (Track tr : tracks) {   // Map the value to each track
            int min = Math.min(buildingTable.get(tr.startBuildingId()).occupants(),
                    Math.min(buildingTable.get(tr.endBuildingId()).occupants(), tr.capacity()));
            value.put(tr, min/tr.cost());
        }

        // Need to use disjoint sets
        ArrayList<Track> ret1 = new ArrayList<>();    // Perhaps won't need
        NaiveDisjointSet<BuildingID> buildings = new NaiveDisjointSet<>();

        // Add all the buildings to the disjoint set
        for (BuildingID build : buildingTable.keySet()) { buildings.add(build); }

        // Sort the tracks based on their goodness value - implement merge sort
        Track[] sorted = new Track[tracks.length];
        for (int i = 0; i < tracks.length; i++) { sorted[i] = tracks[i]; }
        sort(sorted, value, 0, (sorted.length-1));

        // Now this array should have TrackID's sorted by goodness

        /*for (Track tr : sorted) {
            System.out.println("Track: "+tr.id()+" value: "+value.get(tr));
        }*/

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
        return this.Trees.permutations(firstLetters);
    }

    // Implemented these instances of merge and sort using the same resource as before -
        //  - https://www.javatpoint.com/merge-sort
    static void merge1(int[][] table, int start, int mid, int end) {
        int one = mid - start + 1;
        int two = end - mid;

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
            if (left[i][1] <= right[j][1]) {    // Sort by earliest finishing time
                table[k][0] = left[i][0];
                table[k][1] = left[i][1];
                i++;
            } else {
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
        if (start < end) {
            int mid = (start + (end-1)) / 2;
            sort1(table, start, mid);
            sort1(table, mid+1, end);
            merge1(table, start, mid, end);
        }
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