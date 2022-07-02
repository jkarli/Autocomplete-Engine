
import java.util.ArrayList;

public class MyLinkedList {

    DLBNode head;
    int size;

    public MyLinkedList() { //constructor

    }

    public void add(char c) { //adds node to LL
        if (head == null) { //head of LL is null, create head node
            head = new DLBNode(c);
        } else if (c <= head.c) { //adding new node as the head
            DLBNode temp = head;
            head = new DLBNode(c, temp, null, null);
            temp.prevSib = head;
        } else {
            DLBNode curr = head;
            while (c > curr.c && (curr.nextSib != null)) { //iterate until we find the correct spot for node
                curr = curr.nextSib;
            }
            if (c > curr.c) { //adding new node at the end of the LL
                DLBNode newNode = new DLBNode(c, null, curr, null);
                curr.nextSib = newNode;
            } else { //adding in the middle of LL
                DLBNode temp = curr;
                curr = new DLBNode(c, temp, temp.prevSib, null);
                temp.prevSib.nextSib = curr;
                temp.prevSib = curr;
            }
        }
        size++; //tracking size of LL
    }

    public int findChar(char c) { //returns index of given char in LL, -1 if char was not found
        DLBNode curr = head;
        for (int i = 0; i < size; i++) {
            if (curr.c == c) {
                return i;
            } else {
                curr = curr.nextSib;
            }
        }
        return -1;
    }

    public MyLinkedList branchDown(int index) { //branch down at given index
        DLBNode curr = head;
        for (int i = 0; i < index; i++) {
            curr = curr.nextSib;
        }
        curr.child = new MyLinkedList();
        return curr.child;
    }

    public MyLinkedList getChild(char c) { //returns child list rooted at char c, returns null if child list does not exixt
        DLBNode curr = head;
        int index = this.findChar(c);
        if (index != -1) {
            for (int i = 0; i < index; i++) {
                curr = curr.nextSib;
            }
            if (curr.child != null) {
                return curr.child;
            }
        }
        return null;
    }

    //gets predictions from dictionary DLB trie
    //Note: I am only using the ArrayList to store the predictions, it is not part of my DLB trie implementation
    public ArrayList<String> getPredictions(MyLinkedList LL, String s, ArrayList<String> AL, boolean caratFound) {
        DLBNode curr = LL.head;
        for (int i = 0; i < LL.size; i++) {
            if (AL.size() < 5) {
                if (LL.findChar('^') != -1 && caratFound == false) {
                    if (!AL.contains(s)) {
                        AL.add(s);
                    }
                }
                if (curr.child != null) {
                    s += curr.c;
                    getPredictions(curr.child, s, AL, false);
                    caratFound = true;
                    s = s.substring(0, s.length() - 1);
                }
            } else {
                return AL;
            }
            curr = curr.nextSib;
        }
        return AL;
    }

    private class DLBNode {

        char c;
        DLBNode nextSib;
        DLBNode prevSib;
        MyLinkedList child;

        private DLBNode(char c) {
            this.c = c;
        }

        private DLBNode(char c, DLBNode nextSib, DLBNode prevSib, MyLinkedList child) {
            this.c = c;
            this.nextSib = nextSib;
            this.prevSib = prevSib;
            this.child = child;
        }
    }
}
