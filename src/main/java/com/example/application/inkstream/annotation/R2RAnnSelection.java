package com.example.application.inkstream.annotation;

import com.example.application.inkstream.record.ConsistencyAnnotatedRecord;
import com.example.application.inkstream.record.EventBean;
import org.streamreasoning.rsp4j.api.operators.r2r.RelationToRelationOperator;
import org.streamreasoning.rsp4j.api.querying.result.SolutionMapping;
import org.streamreasoning.rsp4j.api.sds.SDS;
import org.streamreasoning.rsp4j.api.sds.timevarying.TimeVarying;

import java.util.Collection;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class R2RAnnSelection<V> implements RelationToRelationOperator<AnnotatedRelation<EventBean<V>>,AnnotatedRelation<EventBean<V>>> {

    Predicate<ConsistencyAnnotatedRecord<EventBean<V>>> predicate;

    public R2RAnnSelection(Predicate<ConsistencyAnnotatedRecord<EventBean<V>>> predicate) {
        this.predicate = predicate;
    }

    @Override
    public Stream<AnnotatedRelation<EventBean<V>>> eval(Stream<AnnotatedRelation<EventBean<V>>> sds) {
        return sds.map(new Function<AnnotatedRelation<EventBean<V>>, AnnotatedRelation<EventBean<V>>>() {
            @Override
            public AnnotatedRelation<EventBean<V>> apply(AnnotatedRelation<EventBean<V>> eventBeanAnnotatedRelation) {
                return new AnnotatedRelation<>(eventBeanAnnotatedRelation.annotatedRecords.stream()
                        .filter(predicate).collect(Collectors.toList()));
            }
        });
    }

    @Override
    public TimeVarying<Collection<AnnotatedRelation<EventBean<V>>>> apply(SDS<AnnotatedRelation<EventBean<V>>> sds) {
        return null;
    }

    @Override
    public SolutionMapping<AnnotatedRelation<EventBean<V>>> createSolutionMapping(AnnotatedRelation<EventBean<V>> result) {
        return null;
    }
}
