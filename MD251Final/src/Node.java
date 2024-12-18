public class Node {
    private BuildingID previous;
    private int color;  // White = 0, Grey = 1, Black = 2
    private int distance;   // Distance -1 means infinity

    public Node(BuildingID previous, int color, int distance) {
        this.previous = previous;
        this.color = color;
        this.distance = distance;
    }

    public int getColor(){return this.color;}
    public void chColor(int color){this.color = color;}
    public BuildingID getPrevious(){return this.previous;}
    public void chPrevious(BuildingID previous){this.previous = previous;}
    public int getDistance(){return this.distance;}
    public void chDistance(int distance){this.distance = distance;}
    //public BuildingID getHere() {return this.here;}
    //public void chHere(BuildingID here) {this.here = here;}
}