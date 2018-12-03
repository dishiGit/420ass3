
package ca.mcgill.ecse420.a3.q3;
import java.lang.reflect.Array;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;


public class BoundedLockBasedQueue<T> {

    private T[] itemArray; //Array based queue
    private int head; //Head index
    private int tail; //Tail index
    private AtomicInteger size; //Size queue

    //Head and tail locks used to allow parallelism 
    private Lock deqLock;
    private Lock enqLock;

    //Lock conditions 
    private Condition notEmptyCondition;
    private Condition notFullCondition;

    public BoundedLockBasedQueue(int capacity) {
        //Initialize queue variables
        itemArray = (T[]) new Object[capacity];
        size = new AtomicInteger(0);
        head = 0;
        tail = 0;

        //Initialize locks and respective conditions
        deqLock = new ReentrantLock();
        enqLock = new ReentrantLock();
        notEmptyCondition = deqLock.newCondition();
        notFullCondition = enqLock.newCondition();
    }

    public void Enqueue(T item) throws InterruptedException{

        boolean mustWakeDequeuers = false;

        enqLock.lock();
        try {
            //check and wait till there is space in queue
            while(size.get() == itemArray.length) {
                try {
                    notFullCondition.await();
                } catch(InterruptedException e) {
                    System.out.println(e);
                }
            }

                /*Add item to queue. Wrap around needed to 
                add item to proper index once queue is almost full*/
            itemArray[(tail)%itemArray.length] = item;
            tail++;

            //Increment size and check if it used to be empty
            if(size.getAndIncrement() == 0){
                mustWakeDequeuers = true;
            }

        } finally {
            enqLock.unlock();
        }
        if(mustWakeDequeuers){
            //Acquire head lock and attempt to signal all
            deqLock.lock();
            try{
                notEmptyCondition.signalAll();

            }finally{
                deqLock.unlock();
            }
        }
    }


    public T Dequeue() throws InterruptedException {
        boolean mustWakeEnqueuers = false;
        deqLock.lock();
        T result

        try {
            //Check if queue is empty and wait until something is in it
            while(size.get() == 0) {
                try {
                    notEmptyCondition.await();
                } catch(InterruptedException e) {
                    System.out.println(e);

                }
            }

            //Increment head index and return previous head value
            result = itemArray[head%itemArray.length;];
            head++;

            if (size.getAndDecrement() == capacity) {
                mustWakeEnqueuers = true;
            }

        }finally {
            deqLock.unlock();
        }
        //If queue is no longer full singal all waiting enqueuers
        if(mustWakeEnqueuers){
            enqLock.lock();
            try{

                notFullCondition.signalAll();

            }finally{
                enqLock.unlock();
            }
        }
        //return previous value of head
        return result;
    }
}