import java.util.ArrayList;

public class Tree {
    // Sort of like a linked list, except you can point to multiple children
    // Each character in the hashmap points to a new leaf
    // The indexes store the type of char being represented
    Leaf head;

    Tree() {
        this.head = new Leaf();
        this.head.parent = null;
    }

    void add(String name) {
        Leaf here = this.head;  // Start at the one belonging to McMetro
        for (int i = 0; i < name.length(); i++) {
            // For each character we need to see if it already exists in the hashmap
            char curr = name.charAt(i);
            if (here.children.get(curr) == null) {
                // If not in hashmap then add it to the hashmap
                Leaf tmp = new Leaf();
                tmp.parent = here;
                tmp.val = curr;
                here.children.put(curr, tmp);
            }
            // Otherwise just move onto the next one
            here = here.children.get(curr);
        }
        here.end = true;    // Once we are done with the word we just make it an end
        // Ends shouldn't overlap
    }

    ArrayList<String> permutations(String firstLetters) {
        // Need to return every permutation from point that firstLetters leaves off at and
            // add them with the firstLetters prefix to the return string
        // If nothing matches, return an empty string
        ArrayList<String> ret = new ArrayList<>();
        Leaf tmp = this.head;
        for (char c : firstLetters.toCharArray()) {
            if (tmp.children.get(c) == null) {  // The character is not in the hashmap
                // We have not reached the end of the prefix, so we need to return an empty list
                return ret;
            } else {
                tmp = tmp.children.get(c);  // Move on down the line
            }
        }

        // Done with prefix - start getting permutations from this point
        // Could do with a recursive function, but those tend to get long
        tmp.recurse(ret);

        return ret;
    }

}
