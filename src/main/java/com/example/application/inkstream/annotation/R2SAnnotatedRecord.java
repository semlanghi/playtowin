package com.example.application.inkstream.annotation;

import com.example.application.inkstream.record.ConsistencyAnnotatedRecord;
import org.streamreasoning.rsp4j.api.operators.r2s.RelationToStreamOperator;

import java.util.function.Function;
import java.util.stream.Stream;

public class R2SAnnotatedRecord<V> implements RelationToStreamOperator<AnnotatedRelation<V>, ConsistencyAnnotatedRecord<V>> {


    @Override
    public Stream<ConsistencyAnnotatedRecord<V>> eval(Stream<AnnotatedRelation<V>> sml, long ts) {
        return sml.flatMap(new Function<AnnotatedRelation<V>, Stream<? extends ConsistencyAnnotatedRecord<V>>>() {
            @Override
            public Stream<? extends ConsistencyAnnotatedRecord<V>> apply(AnnotatedRelation<V> vAnnotatedRelation) {
                return vAnnotatedRelation.annotatedRecords.stream();
            }
        });
    }
}
