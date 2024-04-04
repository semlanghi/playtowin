package com.example.application.inkstream.annotation;

import com.example.application.inkstream.record.ConsistencyAnnotatedRecord;
import org.apache.commons.rdf.api.IRI;
import org.streamreasoning.rsp4j.api.sds.SDS;
import org.streamreasoning.rsp4j.api.sds.timevarying.TimeVarying;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class AnnotatedRelation<V> {

    List<ConsistencyAnnotatedRecord<V>> annotatedRecords = new ArrayList<>();

    public AnnotatedRelation(ConsistencyAnnotatedRecord<V> eventBeanConsistencyAnnotatedRecord) {
        annotatedRecords.add(eventBeanConsistencyAnnotatedRecord);
    }

    public AnnotatedRelation(List<ConsistencyAnnotatedRecord<V>> annotatedRecords) {
        this.annotatedRecords = annotatedRecords;
    }

    public AnnotatedRelation() {

    }

    public AnnotatedRelation<V> union(AnnotatedRelation<V> eventBeanAnnotatedRelation2) {
        annotatedRecords.addAll(eventBeanAnnotatedRelation2.annotatedRecords);
        return this;
    }


    public int size() {
        return annotatedRecords.size();
    }
}
