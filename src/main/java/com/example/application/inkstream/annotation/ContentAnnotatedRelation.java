package com.example.application.inkstream.annotation;

import com.example.application.inkstream.cgraph.ConsistencyGraph;
import com.example.application.inkstream.cgraph.ConsistencyGraphImpl;
import com.example.application.inkstream.constraint.ConstraintFactory;
import com.example.application.inkstream.record.ConsistencyAnnotatedRecord;
import org.streamreasoning.rsp4j.api.secret.content.Content;
import org.streamreasoning.rsp4j.api.secret.time.Time;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Stream;

public class ContentAnnotatedRelation<I>  implements Content<I,AnnotatedRelation<I>> {

    private final ConstraintFactory<I> constraintFactory;
    private Time instance;
    private List<I> preprocessed;
    private long lastUpdate = -1;

    public ContentAnnotatedRelation(Time instance, ConstraintFactory<I> constraintFactory) {
        this.instance = instance;
        this.preprocessed = new ArrayList<>();
        this.constraintFactory = constraintFactory;
    }

    @Override
    public int size() {
        return preprocessed.size();
    }

    @Override
    public void add(I e) {
        preprocessed.add(e);
        lastUpdate = instance.getAppTime();
    }

    @Override
    public Long getTimeStampLastUpdate() {
        return lastUpdate;
    }

    @Override
    public AnnotatedRelation<I> coalesce() {
        ConsistencyGraph<I> consistencyGraph = new ConsistencyGraphImpl<>(constraintFactory);
        return preprocessed.stream()
                .map(vEventBean -> consistencyGraph.add(new ConsistencyAnnotatedRecord<>(vEventBean)))
                .reduce(new AnnotatedRelation<>(),
                        (AnnotatedRelation<I> eventBeanAnnotatedRelation, ConsistencyAnnotatedRecord<I> eventBeanConsistencyAnnotatedRecord) -> new AnnotatedRelation<>(eventBeanConsistencyAnnotatedRecord),
                        AnnotatedRelation::union);
    }
}
