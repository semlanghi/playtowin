package com.example.application.inkstream.annotation;

import org.streamreasoning.rsp4j.api.secret.content.Content;
import org.streamreasoning.rsp4j.api.secret.content.ContentFactory;
import org.streamreasoning.rsp4j.api.secret.time.Time;

public class ContentRelationFactory<I> implements ContentFactory<I, Relation<I>> {

    private final Time instance;

    public ContentRelationFactory(Time instance) {
        this.instance = instance;
    }

    @Override
    public Content<I, Relation<I>> createEmpty() {
        return new ContentRelation<>(instance);
    }

    @Override
    public Content<I, Relation<I>> create() {
        return new ContentRelation<>(instance);
    }
}
