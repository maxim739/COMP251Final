import java.util.ArrayList;
import java.util.HashMap;

public class Leaf implements Comparable<Leaf>{
    HashMap<Character, Leaf> children;
    Leaf parent;
    char val;
    boolean end;    // 1 means end, 0 means not end
    Leaf() {
        this.children = new HashMap<>();
        this.end = false;
        this.parent = null;
        //this.val = null;
    }

    public int compareTo(Leaf other) {
        if ((this.children.equals(other.children))&&(this.val == other.val)&&(this.end == other.end)) {
            return 0;
        } else {
            return 1;
        }
    }

    ArrayList<Leaf> getLeaves() {
        ArrayList<Leaf> ret = new ArrayList<>();
        if (end) {  // If we are a leaf, then add to the list
            ret.add(this);  // We add middle Leafs that have children if end of word
        }
        if (children.isEmpty()) {
            return ret; // We have no children to report
        } else {    // We have more children to test
            for (Character c : children.keySet()) {
                ret.addAll(children.get(c).getLeaves());    // For each child test for leaves
            }
        }
        return ret;
    }

    void recurse(ArrayList<String> in) {
        ArrayList<Leaf> leaves = this.getLeaves();
        if (!(leaves.isEmpty())) {  // We have leaves to move up from
            for (Leaf leaf : leaves) {
                String put = "";
                // Start at each end leaf and move up the tree until we get to this
                Leaf tmp = leaf;
                while (!(0 == tmp.compareTo(this))) {
                    put = tmp.val + put;
                    tmp = tmp.parent;
                }
                in.add(put);
            }
        }
    }
}
