package com.example.application.inkstream.annotation;

import com.example.application.inkstream.annotation.polynomial.Polynomial;
import com.example.application.inkstream.record.ConsistencyAnnotatedRecord;
import com.example.application.inkstream.record.EventBean;
import org.streamreasoning.rsp4j.api.operators.r2r.RelationToRelationOperator;
import org.streamreasoning.rsp4j.api.querying.result.SolutionMapping;
import org.streamreasoning.rsp4j.api.sds.SDS;
import org.streamreasoning.rsp4j.api.sds.timevarying.TimeVarying;

import java.sql.Array;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class R2RAnnProject<V> implements RelationToRelationOperator<AnnotatedRelation<EventBean<V>>,AnnotatedRelation<EventBean<V>>> {

    List<String> attributes;

    @Override
    public Stream<AnnotatedRelation<EventBean<V>>> eval(Stream<AnnotatedRelation<EventBean<V>>> sds) {
        return sds.map(eventBeanAnnotatedRelation -> {
            Map<EventBean<V>, Polynomial> projectedEvents = new HashMap<>();
            eventBeanAnnotatedRelation.annotatedRecords
                    .forEach(eventBeanConsistencyAnnotatedRecord -> {
                        EventBean<V> wrappedRecord = eventBeanConsistencyAnnotatedRecord.getWrappedRecord().project(attributes);
                        if (projectedEvents.containsKey(wrappedRecord))
                            projectedEvents.put(wrappedRecord, eventBeanConsistencyAnnotatedRecord.getPolynomial()
                                    .plus(projectedEvents.get(wrappedRecord)));
                        else {
                            projectedEvents.put(wrappedRecord, eventBeanConsistencyAnnotatedRecord.getPolynomial());
                        }
                    });
            return new AnnotatedRelation<>(projectedEvents.entrySet().stream().map(eventBeanPolynomialEntry -> new ConsistencyAnnotatedRecord<>(eventBeanPolynomialEntry.getValue(),eventBeanPolynomialEntry.getKey())).collect(Collectors.toList()));
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
