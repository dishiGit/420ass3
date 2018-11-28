package ca.mcgill.ecse420.a3;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FineGrainedTest {
    public static void main(String [ ] args)
    {
        OneNode a = new OneNode("a");
        OneNode b = new OneNode("b");
        OneNode c = new OneNode("c");
        OneNode d = new OneNode("d");
        OneNode e = new OneNode("e");
        OneNode f = new OneNode("f");

        a.next = b;
        b.next = c;
        c.next = d;
        d.next = e;
        e.next = f;

        FineGrainedLinkedList mLinkedList = new FineGrainedLinkedList(a);

        class testContains implements Runnable{
            Object mItem;

            testContains(Object item){
                mItem = item;
            }

            @Override
            public void run(){
                for (int i = 0; i<10; i++){
                    mLinkedList.contains(mItem);
                    try{
                        Thread.sleep(200);
                    } catch (Exception e){}

                }

            }
        }

        class tryRemove implements Runnable{
            Object mItem;

            tryRemove(Object item){
                mItem = item;
            }

            @Override
            public void run(){
                for (int i = 0; i<2; i++){
                    mLinkedList.remove(mItem);
                    try{
                        Thread.sleep(200);
                    } catch (Exception e){}
                }
            }
        }


        ExecutorService executor = Executors.newFixedThreadPool(7);
        executor.execute(new testContains("a"));
        executor.execute(new testContains("b"));
        executor.execute(new testContains("c"));
        executor.execute(new testContains("d"));
        executor.execute(new testContains("e"));
        executor.execute(new tryRemove("c"));
        executor.execute(new tryRemove("d"));

        executor.shutdown();
        while (!executor.isTerminated()){};


    }


}
