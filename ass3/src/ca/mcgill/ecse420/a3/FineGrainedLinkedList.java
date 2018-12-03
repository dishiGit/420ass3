package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedLinkedList {
    OneNode head;

    FineGrainedLinkedList (){
        head = null;
    }

    FineGrainedLinkedList (OneNode h){
        head = h;
    }



    public boolean remove(Object item){
        int key = item.hashCode();
        OneNode pred = head;
        pred.lock();
        OneNode curr = pred.next;
        try{
            while (curr !=null){
                curr.lock();
                if (curr.key == key){
                    pred.next = curr.next;
                    System.out.print("Item removed: " + String.valueOf(item) + "\n");
                    return true;
                }
                pred.unlock();
                pred = curr;
                curr = pred.next;
            }
            System.out.print("Item DNE, failed to remove: " + String.valueOf(item) + "\n");
            return false;
        } finally {
            if (curr != null) curr.unlock();
            if (pred != null) pred.unlock();
        }
    }

    public boolean contains(Object item){
        OneNode ptr = head;
        OneNode unlockPtr = head;
        try{
            while (ptr != null){
                ptr.lock();
                int key = item.hashCode();
                if (ptr.key == key) {
                    System.out.print("Found: " + String.valueOf(item) + "\n");
                    return true;
                }
                ptr = ptr.next;
            }
            System.out.print("Failed to find: " + String.valueOf(item) + "\n");
            return false;
        } finally{
            while(unlockPtr != null){

                unlockPtr.unlock();
                if (unlockPtr == ptr ) break;
                unlockPtr = unlockPtr.next;
            }
        }

    }
}
