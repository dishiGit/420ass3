package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.ReentrantLock;

public class OneNode extends ReentrantLock {
    int key;
    OneNode next;

    OneNode(Object item){
        key = item.hashCode();
        next = null;
    }
}
