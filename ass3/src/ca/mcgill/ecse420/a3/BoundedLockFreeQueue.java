package ca.mcgill.ecse420.a3;

import java.util.concurrent.atomic.AtomicInteger;

public class BoundedLockFreeQueue<T> {

    private T[] items;
    private AtomicInteger head = new AtomicInteger(0);
    private AtomicInteger tail = new AtomicInteger(0);
    private AtomicInteger tailVal= new AtomicInteger(0);
    private AtomicInteger space;

    public BoundedLockFreeQueue(int capacity) {
        items = (T[]) new Object[capacity];
        space = new AtomicInteger(capacity);
    }

    public void enq(T item) throws InterruptedException {
        int lc = space.get();
        while (lc <= 0 || !space.compareAndSet(lc, lc - 1))
            lc = space.get();

        int t = tail.getAndIncrement();
        items[t % items.length] = item;

        while (tailVal.compareAndSet(t, t + 1));
        System.out.println("ADDED "+item);
    }

    public T deq() throws InterruptedException {
        int h = head.getAndIncrement();

        while (h >= tailVal.get());
        T item = items[h % items.length];
        space.incrementAndGet();

        System.out.println("REMOVED " + item);
        return item;
    }
}