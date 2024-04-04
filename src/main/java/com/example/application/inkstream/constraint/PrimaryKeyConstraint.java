package com.example.application.inkstream.constraint;

import com.example.application.inkstream.record.EventBean;

import java.util.Random;

public abstract class PrimaryKeyConstraint<K,V> implements StreamingConstraint<EventBean<V>>{

    private String description = "PK";
    private EventBean<V> origin;
    private int value1 = new Random().nextInt(10000);
    private int value2 = new Random().nextInt(10000);

    public PrimaryKeyConstraint(EventBean<V> origin) {
        this.origin = origin;
    }

    @Override
    public long checkConstraint(EventBean<V> value){
        if(getRecordKey(value.getValue()).equals(getRecordKey(origin.getValue())))
            return -1;
        else return 0;
    }

    @Override
    public EventBean<V> getOrigin() {
        return origin;
    }

    protected abstract K getRecordKey(V value);

    @Override
    public String getDescription() {
        return description+"_"+this.origin.getTime();
    }
}
