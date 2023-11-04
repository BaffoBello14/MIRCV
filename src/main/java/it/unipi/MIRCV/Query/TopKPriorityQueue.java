package it.unipi.MIRCV.Query;

import java.util.Comparator;
import java.util.PriorityQueue;

public class TopKPriorityQueue<E> extends PriorityQueue<E> {
    private final int maxSize;
    public TopKPriorityQueue(int maxSize, Comparator<?super E>comparator){
        super(maxSize,comparator);
        this.maxSize=maxSize;
    }
    @Override
    public boolean offer(E e){
        if(size()>=maxSize){
            E top=peek();
            if(comparator().compare(e,top)<0){
                poll();
                super.offer(e);
                return true;
            }
            return false;
        }else {
            return super.offer(e);
        }
    }
}
