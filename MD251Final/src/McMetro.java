import java.util.*;
import java.lang.Math.*;

/* Acknowledgments
I implemented the merge and sort functions for the bestMetroSystem() method with some research from the
    website JavaTPoint at https://www.javatpoint.com/merge-sort
*/

public class McMetro {
    protected Track[] tracks;
    protected HashMap<BuildingID, Building> buildingTable = new HashMap<>();

    // You may initialize anything you need in the constructor
    McMetro(Track[] tracks, Building[] buildings) {
       this.tracks = tracks;
        // Put the init for the passengers here
            // Arraylist? look at the imp on page
       // Populate buildings table
       for (Building building : buildings) {
           buildingTable.putIfAbsent(building.id(), building);
       }
    }

    BuildingID dequeue(HashMap<BuildingID, Node> hash, ArrayList<BuildingID> queue) {
        BuildingID ret = null;
        //int buildingID = -1;
        int lowVal = -1;

        for (BuildingID build : queue) {
            int thisDist = hash.get(build).getDistance();
            if (thisDist < lowVal || lowVal == -1) {
                lowVal = thisDist;
                //buildingID = this.buildingTable.get(build).retId();
                ret = build;
            }
        }


        try {
            queue.remove(ret);
            //System.out.println("Removed: "+ret.toString());
        } catch (Exception e) {
            System.err.println("The buildingID: "+ret+" does not exist in the HashMap.");
        }
        /*System.out.println("Coming out of dequque: ");
        System.out.println("Hash is: "+hash);
        System.out.println("Ret is: " + ret);*/
        return ret;     // return the closest building
    }

    ArrayList<BuildingID> shortestPath(BuildingID source, BuildingID sink, HashMap<BuildingID, Building> buildings, ArrayList<Track> tracks ) {
        // we are going to run the BFS to get the shortest distance from source to sink
        ArrayList<BuildingID> ret = new ArrayList<>();   // Return array
        HashMap<BuildingID, Node> properties = new HashMap<>();     // New Hashmap from buildingID to it's properties
        // Colors White = 0, Grey = 1, Black = 2
        for (BuildingID id : buildings.keySet()) {   // Initialize the array we are going to use
            Node node = new Node(null, 0, -1);
            properties.put(id, node);
            /*System.out.println("ID: "+id.getIdNum()+" -> Color: "+node.getColor()+" Dist: "+node.getDistance());
            System.out.println("ID hashcode: " +System.identityHashCode(id));
            System.out.println("id node hashcode: "+node);
            System.out.println(properties.get(id).getColor());*/
            //newlin
        }
        // Now we need to identify the source node - make it's distance 0 and the color grey
        // We keep the previous building as null
        properties.get(source).chColor(1);
        properties.get(source).chDistance(0);

        // Make the priority queue
        ArrayList<BuildingID> queue = new ArrayList<>();  // Made a new arraylist type to dequeue

        // Start BFS while loop
        queue.add(source);
        while (!(queue.isEmpty())) {
            // Dequeue a buildingID
            BuildingID v = this.dequeue(properties, queue);
            /*System.out.println("v hashcode: "+System.identityHashCode(v));
            System.out.println(properties);
            System.out.println("v node hashcode: "+properties.get(v));
            System.out.println(properties.get(v).getColor());*/
            //System.out.println("In shortest path while loop");

            // For all neighbors of v - we need to find all outgoing nodes of v and create a list of
                // all neighbors
            ArrayList<BuildingID> neighbors = new ArrayList<>();
            for (Track tr : tracks) { if (tr.startBuildingId() == v) { neighbors.add(tr.endBuildingId()); } }
            // Now go through each neighbor as BFS would do
            for (BuildingID neigh : neighbors) {
                //System.out.println("Neighbor: "+neigh.getIdNum());
                if (properties.get(neigh).getColor() == 0) {
                    properties.get(neigh).chColor(1);
                    queue.add(neigh);
                    //System.out.println("Adding: "+neigh.getIdNum()+" to queue");
                    properties.get(neigh).chDistance(properties.get(neigh).getDistance() + 1);
                    properties.get(neigh).chPrevious(buildingTable.get(v));
                }
            }
            properties.get(v).chColor(2);
        }
        // Now we should have all of the nodes labelled with their proper distances and colors
        // Look from sink to source with previous' to construct an arraylist of the shortest path
        BuildingID prev = properties.get(sink).getPrevious().id();
        //BuildingID prev = null;
        /*try { prev = properties.get(sink).getPrevious().id(); }
        catch (NullPointerException e) { return ret; }*/
        //System.out.println("Prev is: "+prev+" Source is: "+source);
        ret.addFirst(sink);
        while (null != properties.get(prev).getPrevious()) {
            ret.addFirst(prev);
            prev = properties.get(prev).getPrevious().id();
        }
        //System.out.println("Shortest Path is: "+ret);
        return ret;
    }

    ArrayList<Track> getTracks(ArrayList<BuildingID> builds, BuildingID source) {
        ArrayList<Track> ret = new ArrayList<>();
        if(1 == builds.size()) {
            for (Track tr : this.tracks) {
                if (tr.startBuildingId().getIdNum() == source.getIdNum()) {
                    ret.add(tr);
                    break;
                }
            }
        } else {
            for (int i = 0; i != builds.size(); i++) {
                //System.out.println(i + "th element");
                for (Track tr : this.tracks) {
                    if (tr.startBuildingId().getIdNum() == builds.get(i).getIdNum() &&
                            tr.endBuildingId().getIdNum() == builds.get(i + 1).getIdNum()) {
                        ret.add(tr);
                        break;
                    }
                }
            }
        }
        return ret;
    }

    // Maximum number of passengers that can be transported from start to end
    int maxPassengers(BuildingID start, BuildingID end) {
        ArrayList<Track> augTracks = new ArrayList<>();
        for (Track tr : tracks) {augTracks.add(tr);}    // Create arraylist deepish copy of tracks so we can augment
        HashMap<BuildingID, Building> augBuilding = new HashMap<>();
        for (BuildingID id : buildingTable.keySet()) {  // Create a deep(ish) copy of the array so we can modify it
            augBuilding.put(id, buildingTable.get(id));
        }
        HashMap<Track, Integer> currFlow = new HashMap<>();
        for (Track tr : tracks) { currFlow.put(tr, 0); }    // Each track now contains a currFlow field
        // When currFlow = capacity, remove it from the array we feed into the BFS

        // Do the Edmonds-Karp algorithm
        ArrayList<BuildingID> shortest = this.shortestPath(start, end,  augBuilding, augTracks);    // The first BFS
        while (!(shortest.isEmpty())) {
            // Adjust flow values and remove pipes from BFS when they have no more flow avaliable
            // Bottleneck calc
            int bottleNeck = -1;
            ArrayList<Track> currTracks= getTracks(shortest, start);
            //System.out.println("Currtracks: "+currTracks);

            // Calculate the bottleneck value of the track
            for (Track tr : currTracks) {
                int poten = tr.capacity() - currFlow.get(tr);   // Avaliable max flow
                if (poten < bottleNeck || bottleNeck == -1) { bottleNeck = poten; }
            }
            //System.out.println(bottleNeck);
            if (bottleNeck == -1) {System.out.println("Bottleneck is till -1 :("); return -1;}

            // Aug all tracks on the path - remove them if they have no more capacity left
            for (Track tr : currTracks) {
                currFlow.put(tr, currFlow.get(tr) + bottleNeck);    // Now update the avaliable flow of the track
                if (tr.capacity() == currFlow.get(tr)) { augTracks.remove(tr); }    // Remove if no more cap left
            }

            // Calculate new BFS for the new iteration of the while loop
            shortest = this.shortestPath(start, end, augBuilding, augTracks);
        }
        // After loop we should have updated flow values for each track in the tracks array
        // Look through the tracks going into the sink to see how much flow is being passed

        // When the array is null, check the outgoing flow from source or incoming flow from sink
        int flow = -1;
        for (Track tr : tracks) {
            if (tr.endBuildingId() == end) {
                if (flow == -1) { flow = 0; }
                int i = currFlow.get(tr);
                flow += i;
            }
        }
        return flow;    // The maximum incoming flow to the sink
    }

    // The implementation for sort and merge was given by https://www.javatpoint.com/merge-sort
    static void sort(Track[] tracks, HashMap<Track, Integer> values, int start, int finish) {
        if (start < finish) {
            int m = (start + (finish-1)) / 2;
            sort(tracks, values, start, m);
            sort(tracks, values, m + 1, finish);
            merge(tracks, values, start, m, finish);
        }
    }

    static void merge(Track[] tracks, HashMap<Track, Integer> values, int start, int mid, int finish) {
        /*if (1 == finish - start) {
            // We are in the trivial case
            int v1 = values.get(tracks[start]);
            int v2 = values.get(tracks[finish]);
            if (v1 < v2) {
                Track temp = tracks[start];
                tracks[start] = tracks[finish];
                tracks[finish] = temp;
            }   // Otherwise we are in the right order
        } else {*/
            int one = mid - start + 1;
            int two = finish - mid;

            //System.out.println(tracks + " Len: " + tracks.length);
            //System.out.println("Start: " + start + " Mid: " + mid + " Fin: " + finish);
            //System.out.println("One : " + one + " Two: " + two);

            Track[] Left = new Track[one];
            for (int i = 0; i < one; i++) {
                System.out.println("Left: " + i);
                Left[i] = tracks[start + i];
            }

            Track[] Right = new Track[two];
            for (int i = 0; i < two; i++) {
                System.out.println("Right: " + i);
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
                    tracks[k] = Right[i];
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
        //}
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
        sort(sorted, value, 0, sorted.length-1);
        //if (sorted.length > 1) {sort(sorted, value, 0, sorted.length); }    // Otherwise already sorted
        // Now this array should have TrackID's sorted by goodness

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

        // TODO: your implementation here
        // Add a passenger to the arraylist that we created in the initialization
        // We need to create a prefix tree somehow
        // We make a new tree for each first character, space complexity not the greatest here
        // Need to make a new private class tree datatype thing
        // Adding another passenger w the same name will not change tree
        // Make all uppercase, then return just first upper, the rest lower (int the list)
        // Look at java util treemap()
        // Use our disjoint set class
    }

    // Do not change this
    void addPassengers(String[] names) {
        for (String s : names) {
            addPassenger(s);
        }
    }

    // Returns all passengers in the system whose names start with firstLetters
    ArrayList<String> searchForPassengers(String firstLetters) {
        // TODO: your implementation here
        // Create a new arraylist
        // look for the largest tree with these letters, then add all permutations of the largest tree to the arraylist
        // Return empty list when null or no string is entered

        return new ArrayList<>();
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
