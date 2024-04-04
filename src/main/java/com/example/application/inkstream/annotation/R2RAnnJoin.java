package com.example.application.inkstream.annotation;

import com.example.application.inkstream.record.EventBean;
import org.streamreasoning.rsp4j.api.operators.r2r.RelationToRelationOperator;
import org.streamreasoning.rsp4j.api.querying.result.SolutionMapping;
import org.streamreasoning.rsp4j.api.sds.SDS;
import org.streamreasoning.rsp4j.api.sds.timevarying.TimeVarying;

import java.util.Collection;
import java.util.stream.Stream;

public class R2RAnnJoin<V> implements RelationToRelationOperator<AnnotatedRelation<EventBean<V>>,AnnotatedRelation<EventBean<V>>> {


    @Override
    public Stream<AnnotatedRelation<EventBean<V>>> eval(Stream<AnnotatedRelation<EventBean<V>>> sds) {
        return null;
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
