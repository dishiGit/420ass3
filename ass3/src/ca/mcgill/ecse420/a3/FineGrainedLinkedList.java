package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedLinkedList {
    Node head;

    class Node extends ReentrantLock {
        int key;
        Node next;

        Node(Object item){
            key = item.hashCode();
        }
    }

    public void remove(Object item){
        int key = item.hashCode();
        Node pred = null;
        Node curr = head;
        try{
            while (curr.key != key){
                if (pred.isLocked()) pred.unlock();
                pred = curr;
                curr = pred.next;
                curr.lock();
                //TODO
            }
        } finally {
            curr.unlock();
            pred.unlock();
        }
    }

    public boolean contains(Object item){
        Node ptr = head;
        Node unlockPtr = head;
        try{
            while (ptr != null){
                ptr.lock();
                int key = item.hashCode();
                if (ptr.key == key) return true;
                ptr = ptr.next;
            }
            return false;
        } finally{
            while(unlockPtr != ptr && head != null){
                Node n = unlockPtr.next;
                unlockPtr.unlock();
                unlockPtr = n;
            }
        }

    }
}
