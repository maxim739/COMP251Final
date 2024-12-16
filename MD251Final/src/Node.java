public class Node {
    private Building previous;
    private int color;
    private int distance;

    public Node(Building previous, int color, int distance) {
        this.previous = previous;
        this.color = color;
        this.distance = distance;
    }

    public int getColor(){return this.color;}
    public void chColor(int color){this.color = color;}
    public Building getPrevious(){return this.previous;}
    public void chPrevious(Building previous){this.previous = previous;}
    public int getDistance(){return this.distance;}
    public void chDistance(int distance){this.distance = distance;}




}