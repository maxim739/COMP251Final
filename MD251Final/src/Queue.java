import java.util.ArrayList;

public class Queue {
    ArrayList<Node> queue;
    Queue() {
        this.queue = new ArrayList<>();
    }

    void enqueue(Node in){
        // Need to insert at proper index in queue
        // fuck it - do it quick - search through list and add in order
        int index = 0;
        for(Node n : queue){
            if(in.getDistance() < n.getDistance()) {
                break;
            } else {
                index++;
            }
        }
        queue.add(index, in);
    }
    Node dequeue(){
        // Return the node at the shortest distance and remove it from the list
        Node ret = queue.getFirst();
        queue.removeFirst();
        return ret;
    }
}
