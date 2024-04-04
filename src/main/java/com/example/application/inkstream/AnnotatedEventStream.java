package com.example.application.inkstream;

import com.example.application.inkstream.record.ConsistencyAnnotatedRecord;
import com.example.application.inkstream.record.EventBean;
import org.streamreasoning.rsp4j.api.operators.s2r.execution.assigner.Consumer;
import org.streamreasoning.rsp4j.api.stream.data.DataStream;

import java.util.LinkedList;
import java.util.List;

public class AnnotatedEventStream<V> implements DataStream<ConsistencyAnnotatedRecord<EventBean<V>>> {

    protected List<Consumer<ConsistencyAnnotatedRecord<EventBean<V>>>> consumers;
    protected String stream_uri;

    public AnnotatedEventStream(String stream_uri) {
        this.stream_uri = stream_uri;
        this.consumers = new LinkedList<>();
    }

    @Override
    public void addConsumer(Consumer<ConsistencyAnnotatedRecord<EventBean<V>>> windowAssigner) {
        consumers.add(windowAssigner);
    }

    @Override
    public void put(ConsistencyAnnotatedRecord<EventBean<V>> vEventStream, long ts) {
        consumers.forEach(graphConsumer -> graphConsumer.notify(vEventStream, ts));
    }

    @Override
    public String getName() {
        return stream_uri;
    }
}
