package com.example.application.inkstream.record;

import java.util.List;

public interface EventBeanFactory<V> {

    public EventBean<V> make(List<String> attributes, List<V> values, long timestamp);
}
