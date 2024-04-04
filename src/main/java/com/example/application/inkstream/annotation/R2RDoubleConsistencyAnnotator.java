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

import java.util.*;
import java.util.stream.Stream;

public class R2RDoubleConsistencyAnnotator<V> implements RelationToRelationOperator<Relation<EventBean<V>>,AnnotatedRelation<EventBean<V>>> {

    private LinkedHashMap<Window, ConsistencyGraph<EventBean<V>>> graphs;
    private LinkedHashMap<Window, ConsistencyGraph<EventBean<V>>> graphs2;

    private final long windowSize;
    private final ConstraintFactory<EventBean<V>> constraintFactory;
    private final ConstraintFactory<EventBean<V>> constraintFactory2;
    private final long windowSlide;
    private final long allowedLateness = 0;
    private final long cleanUpCounterMax = 1000;
    private long cleanUpCounter = 0;
    private ConsistencyGraph<EventBean<V>> currentGraph;
    private ConsistencyGraph<EventBean<V>> currentGraph2;

    public ConsistencyGraph<EventBean<V>> getCurrentGraph() {
        return currentGraph.union(currentGraph2);
    }

    public R2RDoubleConsistencyAnnotator(long windowSize, long windowSlide, ConstraintFactory<EventBean<V>> constraintFactory, ConstraintFactory<EventBean<V>> constraintFactory2) {
        this.windowSize = windowSize;
        this.constraintFactory = constraintFactory;
        this.windowSlide = windowSlide;
        this.graphs = new LinkedHashMap<>();
        this.graphs2 = new LinkedHashMap<>();
        this.constraintFactory2 = constraintFactory2;
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
        long windowTimestampStartNew = (long) Math.max(0, Math.ceil(((double)originalValue.getWrappedRecord().getTime()-windowSize)/this.windowSlide)*windowSlide);

//        // Eviction of expired Consistency Graphs
//        if(++cleanUpCounter >= cleanUpCounterMax){
//            expireWindows(windowEndTimestamp);
//            cleanUpCounter = 0;
//        }

        long windowEndTimestamp = windowTimestampStartNew + windowSize;
        Window windowed;

        ConsistencyAnnotatedRecord<EventBean<V>> kConsistencyAnnotatedRecordKeyValue = null;
        while (windowEndTimestamp <= originalValue.getWrappedRecord().getTime()){
            windowEndTimestamp+=windowSlide;
            windowTimestampStartNew+=windowSlide;
        }

        // Add the element to each graph to which it belongs
        boolean assigned = false;
        while (windowTimestampStartNew <= originalValue.getWrappedRecord().getTime() && windowEndTimestamp > originalValue.getWrappedRecord().getTime()) {
            windowed = new Window(windowTimestampStartNew, windowEndTimestamp);
            graphs.computeIfAbsent(windowed, k -> new ConsistencyGraphImpl<>(this.constraintFactory));
            ConsistencyAnnotatedRecord<EventBean<V>> value = ConsistencyAnnotatedRecord.makeCopyOf(originalValue);
            graphs.get(windowed).add(value);
            if (!assigned){
                // return just the first element annotation
                kConsistencyAnnotatedRecordKeyValue = value;
                currentGraph = graphs.get(windowed);
                assigned = true;
            }
            windowEndTimestamp+=windowSlide;
            windowTimestampStartNew+=windowSlide;
        }

        return kConsistencyAnnotatedRecordKeyValue;
    }

    private ConsistencyAnnotatedRecord<EventBean<V>> annotate2(ConsistencyAnnotatedRecord<EventBean<V>> originalValue) {

        // Find the ending timestamp of the related window
        long windowTimestampStartNew = (long) Math.max(0, Math.ceil(((double)originalValue.getWrappedRecord().getTime()-windowSize)/this.windowSlide)*windowSlide);

//        // Eviction of expired Consistency Graphs
//        if(++cleanUpCounter >= cleanUpCounterMax){
//            expireWindows(windowEndTimestamp);
//            cleanUpCounter = 0;
//        }

        long windowEndTimestamp = windowTimestampStartNew + windowSize;
        Window windowed;

        ConsistencyAnnotatedRecord<EventBean<V>> kConsistencyAnnotatedRecordKeyValue = null;
        while (windowEndTimestamp <= originalValue.getWrappedRecord().getTime()){
            windowEndTimestamp+=windowSlide;
            windowTimestampStartNew+=windowSlide;
        }

        // Add the element to each graph to which it belongs
        boolean assigned = false;
        while (windowTimestampStartNew <= originalValue.getWrappedRecord().getTime() && windowEndTimestamp > originalValue.getWrappedRecord().getTime()) {
            windowed = new Window(windowTimestampStartNew, windowEndTimestamp);
            graphs2.computeIfAbsent(windowed, k -> new ConsistencyGraphImpl<>(this.constraintFactory2));
            ConsistencyAnnotatedRecord<EventBean<V>> value = ConsistencyAnnotatedRecord.makeCopyOf(originalValue);
            graphs2.get(windowed).add(value);
            if (!assigned){
                // return just the first element annotation
                kConsistencyAnnotatedRecordKeyValue = value;
                currentGraph2 = graphs2.get(windowed);
                assigned = true;
            }
            windowEndTimestamp+=windowSlide;
            windowTimestampStartNew+=windowSlide;
        }

        return kConsistencyAnnotatedRecordKeyValue;
    }

    @Override
    public Stream<AnnotatedRelation<EventBean<V>>> eval(Stream<Relation<EventBean<V>>> sds) {
        graphs.clear();
        graphs2.clear();

        Stream<AnnotatedRelation<EventBean<V>>> annotatedRelationStream = sds.map(vRelation -> {
            Stream<ConsistencyAnnotatedRecord<EventBean<V>>> consistencyAnnotatedRecordStream = vRelation
                    .getRecords()
                    .sorted(Comparator.comparingLong(EventBean::getTime))
                    .map(vEventBean -> {
                        ConsistencyAnnotatedRecord<EventBean<V>> annotate = annotate(new ConsistencyAnnotatedRecord<>(vEventBean));
                        ConsistencyAnnotatedRecord<EventBean<V>> annotate2 = annotate2(new ConsistencyAnnotatedRecord<>(vEventBean));

                        return annotate.withPolynomial(annotate.getPolynomial().times(annotate2.getPolynomial()));
                    });

            AnnotatedRelation<EventBean<V>> reduce = consistencyAnnotatedRecordStream
                    .reduce(new AnnotatedRelation<>(),
                            (AnnotatedRelation<EventBean<V>> eventBeanAnnotatedRelation, ConsistencyAnnotatedRecord<EventBean<V>> eventBeanConsistencyAnnotatedRecord) -> new AnnotatedRelation<>(eventBeanConsistencyAnnotatedRecord),
                            AnnotatedRelation::union);

            return reduce;
        });
        return annotatedRelationStream;
    }

    @Override
    public TimeVarying<Collection<AnnotatedRelation<EventBean<V>>>> apply(SDS<Relation<EventBean<V>>> sds) {
        return null;
    }

    @Override
    public SolutionMapping<AnnotatedRelation<EventBean<V>>> createSolutionMapping(AnnotatedRelation<EventBean<V>> result) {
        return null;
    }

    public List<ConsistencyGraph<EventBean<V>>> getCurrentGraphs() {
        return new ArrayList<>(graphs.values());
    }
}
