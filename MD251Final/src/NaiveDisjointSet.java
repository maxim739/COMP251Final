import java.util.*;

public class NaiveDisjointSet<T> {
    HashMap<T, T> parentMap = new HashMap<>();
    HashMap<T, Integer> rank = new HashMap<>();

    void add(T element) { parentMap.put(element, element); rank.put(element, 0); }

    T find(T a) {
        while (parentMap.get(a) != parentMap.get(parentMap.get(a))) {   // While we aren't at the head of a component
            parentMap.put(a, parentMap.get(a));
        }
        return parentMap.get(a);
    }

    int getRank(T a) { return this.rank.get(a);}

    void union(T a, T b) {
        T a_parent = this.find(a);
        T b_parent = this.find(b);

        if (a_parent != b_parent) {     // They are in different sets
            if (rank.get(a_parent) < rank.get(b_parent)) {  // The rank of a is lower
                parentMap.put(a_parent, b_parent);  // The parent of a is now the parent of b
                rank.put(b_parent, (rank.get(b_parent) + 1));   // Increase b's rank
            } else {    // Rank of b is lower
                parentMap.put(b_parent, a_parent);  // parent of b is set to parent of a
                rank.put(a_parent, (rank.get(a_parent) + 1));   // The rank of a is increased by one
            }
        }
    }
}
