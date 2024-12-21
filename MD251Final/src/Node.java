import java.util.ArrayList;

public class Node {
    public BuildingID here;
    public BuildingID previous;
    public Track pTrack;
    public ArrayList<Track> prevTrack;
    public ArrayList<BuildingID> prevBuild;
    public ArrayList<Track> nextTrack;

    public ArrayList<BuildingID> nextBuild;
    public int color;  // White = 0, Grey = 1, Black = 2
    public int distance;   // Distance -1 means infinity

    public Node(BuildingID here, BuildingID prev, int color, int distance) {
        this.here = here;
        this.previous = prev;
        this.color = color;
        this.distance = distance;
        this.prevTrack = new ArrayList<>();
        this.pTrack = null;
        this.prevBuild = new ArrayList<>();
        this.nextBuild = new ArrayList<>();
        this.nextTrack = new ArrayList<>();
    }

    public void addNext(BuildingID next, Track bridge) {
        nextBuild.addFirst(next);
        nextTrack.addFirst(bridge);
    }

    public void addPrev(BuildingID prev, Track bridge){
        prevBuild.addFirst(prev);
        prevTrack.addFirst(bridge);
    }

    public void chColor(int color){this.color = color;}
    public int getDistance(){return this.distance;}
    public void chDistance(int distance){this.distance = distance;}
}