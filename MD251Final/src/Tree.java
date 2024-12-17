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
            if (here.children.get(curr) == null) {  // Dosen't already exist in hashmap, so create new and add to it
                //System.out.println("Adding: "+curr+" because it doesn't exist yet");
                // If not in hashmap then add it to the hashmap
                Leaf tmp = new Leaf();
                tmp.parent = here;
                tmp.val = curr;
                here.children.put(curr, tmp);
            }
            /*else {
                System.out.println("Character: "+curr+" already exists in tree here.");
                System.out.println(here.val+" Is this val");
            }*/
            // Otherwise just move onto the next one
            //System.out.println("Char already exists in the map, don't add: " + curr + " again.");
            here = here.children.get(curr);
        }
        //System.out.println("Done with this word, end is: "+here.val);
        here.end = true;    // Once we are done with the word we just make it an end
        // Ends shouldn't overlap
        //this.printTree();
    }

    void printTree() {
        this.head.printLeaf();
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
        //System.out.println(ret);
        firstLetters = firstLetters.substring(0,1).toUpperCase()+firstLetters.substring(1);
        //System.out.println(firstLetters);

       // ArrayList<String> ret1 = new ArrayList<>();

        for (int x = 0; x < ret.size(); x++) {  // Could make quicker by adding at time we find recursion
            ret.set(x, firstLetters+ret.get(x));
        }

//        for (String st : ret) {
//            ret1.add(firstLetters+st);
//        }
        //System.out.println(ret);

        return ret;
    }

}
