package com.example.application.inkstream.annotation;

import com.example.application.inkstream.cgraph.ConsistencyGraph;
import com.example.application.inkstream.cgraph.ConsistencyGraphImpl;
import com.example.application.inkstream.constraint.ConstraintFactory;
import com.example.application.inkstream.record.ConsistencyAnnotatedRecord;
import com.example.application.inkstream.record.EventBean;
import org.streamreasoning.rsp4j.api.operators.r2r.RelationToRelationOperator;
import org.streamreasoning.rsp4j.api.querying.result.SolutionMapping;
import org.streamreasoning.rsp4j.api.sds.SDS;
import org.streamreasoning.rsp4j.api.sds.timevarying.TimeVarying;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

public class R2RConsistencyAnnotatorStock<V> implements RelationToRelationOperator<Relation<EventBean<V>>,AnnotatedRelation<EventBean<V>>> {

    private LinkedHashMap<Window, ConsistencyGraph<EventBean<V>>> graphs;
    private final long windowSize;
    private final ConstraintFactory<EventBean<V>> constraintFactory;
    private final long windowSlide;
    private final long allowedLateness = 0;
    private final long cleanUpCounterMax = 1000;
    private long cleanUpCounter = 0;


    public R2RConsistencyAnnotatorStock(long windowSize, long windowSlide, ConstraintFactory<EventBean<V>> constraintFactory) {
        this.windowSize = windowSize;
        this.constraintFactory = constraintFactory;
        this.windowSlide = windowSlide;
        this.graphs = new LinkedHashMap<>();
    }

    private void expireWindows(long ts){
        if (ts - windowSize - allowedLateness > 0){
            graphs.entrySet().removeIf(windowedLinkedHashMapEntry -> windowedLinkedHashMapEntry.getKey().end() < ts - windowSize - allowedLateness);
        }
    }

    public void add(ConsistencyAnnotatedRecord<EventBean<V>> value, long windowEndTimestamp) {
        if(++cleanUpCounter >= cleanUpCounterMax){
            expireWindows(windowEndTimestamp-1);
            cleanUpCounter = 0;
        }

        long windowStartTimestamp = Math.max(windowEndTimestamp - windowSize,0);
        windowEndTimestamp = Math.max(windowEndTimestamp, windowSize);
        Window window;

        do {
            window = new Window(windowStartTimestamp, windowEndTimestamp);
            graphs.computeIfAbsent(window, k -> new ConsistencyGraphImpl<>(this.constraintFactory));
            graphs.get(window).add(value);
            windowStartTimestamp+=windowSlide;
            windowEndTimestamp+=windowSlide;
        } while (windowStartTimestamp <= value.getWrappedRecord().getTime());
    }


    private ConsistencyAnnotatedRecord<EventBean<V>> annotate(ConsistencyAnnotatedRecord<EventBean<V>> originalValue) {

        // Find the ending timestamp of the related window
        long windowEndTimestamp = (long) Math.ceil(((double)originalValue.getWrappedRecord().getTime())/this.windowSlide)*windowSlide;

        // Eviction of expired Consistency Graphs
        if(++cleanUpCounter >= cleanUpCounterMax){
            expireWindows(windowEndTimestamp);
            cleanUpCounter = 0;
        }

        long windowStartTimestamp = Math.max(windowEndTimestamp - windowSize,0);
        windowEndTimestamp = Math.max(windowEndTimestamp, windowSize);
        Window windowed;

        ConsistencyAnnotatedRecord<EventBean<V>> kConsistencyAnnotatedRecordKeyValue = null;
        while (windowEndTimestamp <= originalValue.getWrappedRecord().getTime()){
            windowStartTimestamp+=windowSlide;
            windowEndTimestamp+=windowSlide;
        }

        // Add the element to each graph to which it belongs
        boolean assigned = false;
        while (windowStartTimestamp <= originalValue.getWrappedRecord().getTime() && windowEndTimestamp > originalValue.getWrappedRecord().getTime()) {
            windowed = new Window(windowStartTimestamp, windowEndTimestamp);
            graphs.computeIfAbsent(windowed, k -> new ConsistencyGraphImpl<>(this.constraintFactory));
            ConsistencyAnnotatedRecord<EventBean<V>> value = ConsistencyAnnotatedRecord.makeCopyOf(originalValue);
            graphs.get(windowed).add(value);
            if (!assigned){
                // return just the first element annotation
                kConsistencyAnnotatedRecordKeyValue = value;
                assigned = true;
            }
            windowStartTimestamp+=windowSlide;
            windowEndTimestamp+=windowSlide;
        }

        return kConsistencyAnnotatedRecordKeyValue;
    }

    @Override
    public Stream<AnnotatedRelation<EventBean<V>>> eval(Stream<Relation<EventBean<V>>> sds) {
        return sds.map(vRelation -> {
            Stream<ConsistencyAnnotatedRecord<EventBean<V>>> consistencyAnnotatedRecordStream = vRelation
                    .getRecords()
                    .map(vEventBean -> annotate(new ConsistencyAnnotatedRecord<>(vEventBean)));


            AnnotatedRelation<EventBean<V>> reduce = consistencyAnnotatedRecordStream
                    .reduce(new AnnotatedRelation<>(),
                            (AnnotatedRelation<EventBean<V>> eventBeanAnnotatedRelation, ConsistencyAnnotatedRecord<EventBean<V>> eventBeanConsistencyAnnotatedRecord) -> new AnnotatedRelation<>(eventBeanConsistencyAnnotatedRecord),
                            AnnotatedRelation::union);



            return reduce;
        });
    }

    private class Avg {
        private double sum = 0.0;
        private int count = 1;

        public Avg(double sum) {
            this.sum = sum;
        }
    }



    @Override
    public TimeVarying<Collection<AnnotatedRelation<EventBean<V>>>> apply(SDS<Relation<EventBean<V>>> sds) {
        return null;
    }

    @Override
    public SolutionMapping<AnnotatedRelation<EventBean<V>>> createSolutionMapping(AnnotatedRelation<EventBean<V>> result) {
        return null;
    }
}
