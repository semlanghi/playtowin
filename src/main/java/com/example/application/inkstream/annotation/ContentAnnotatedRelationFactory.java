package com.example.application.inkstream.annotation;

import com.example.application.inkstream.constraint.ConstraintFactory;
import org.streamreasoning.rsp4j.api.secret.content.Content;
import org.streamreasoning.rsp4j.api.secret.content.ContentFactory;
import org.streamreasoning.rsp4j.api.secret.time.Time;

public class ContentAnnotatedRelationFactory<I> implements ContentFactory<I, AnnotatedRelation<I>> {

    private final Time instance;
    private final ConstraintFactory<I> cf;

    public ContentAnnotatedRelationFactory(Time instance, ConstraintFactory<I> cf) {
        this.instance = instance;
        this.cf = cf;
    }

    @Override
    public Content<I, AnnotatedRelation<I>> createEmpty() {
        return new ContentAnnotatedRelation<>(instance, cf);
    }

    @Override
    public Content<I, AnnotatedRelation<I>> create() {
        return new ContentAnnotatedRelation<>(instance, cf);
    }
}
