package com.example.application.polyflow.stream;

import org.streamreasoning.polyflow.api.operators.s2r.execution.assigner.Consumer;
import org.streamreasoning.polyflow.api.stream.data.DataStream;

import java.util.ArrayList;
import java.util.List;

public class DataStreamImpl<X> implements DataStream<X> {

    String URI;
    protected List<Consumer<X>> consumers = new ArrayList<>();

    public DataStreamImpl(String streamURI){
        this.URI = streamURI;
    }
    @Override
    public void addConsumer(Consumer<X> windowAssigner) {
        if(!consumers.contains(windowAssigner))
            this.consumers.add(windowAssigner);
    }

    @Override
    public void put(X row, long ts) {
        consumers.forEach(c -> c.notify(this, row, ts));
    }

    @Override
    public String getName() {
        return URI;
    }
}
