import java.util.ArrayList;

public class Queue {
    ArrayList<Node> queue;
    Queue() {
        this.queue = new ArrayList<>();
    }

    void enqueue(Node in){
        // Need to insert at proper index in queue
        //System.out.println("Enqueue node: "+in.here);
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
        //System.out.println("Dequeue: "+ret+" distance: "+ret.distance);
        queue.removeFirst();
        return ret;
    }
}
