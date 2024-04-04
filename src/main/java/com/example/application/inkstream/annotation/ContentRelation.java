package com.example.application.inkstream.annotation;

import org.streamreasoning.rsp4j.api.secret.content.Content;
import org.streamreasoning.rsp4j.api.secret.time.Time;

public class ContentRelation<I> implements Content<I,Relation<I>> {

    private Time instance;
    private Relation<I> records;
    private long lastUpdate = -1;

    public ContentRelation(Time instance) {
        this.instance = instance;
        this.records = new Relation<>();
    }

    public ContentRelation(Relation<I> records) {
        this.records = records;
    }

    @Override
    public int size() {
        return records.size();
    }

    @Override
    public void add(I e) {
        records.add(e);
        lastUpdate = instance.getAppTime();
    }

    @Override
    public Long getTimeStampLastUpdate() {
        return lastUpdate;
    }

    @Override
    public Relation<I> coalesce() {
        return records;
    }
}
