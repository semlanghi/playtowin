package com.example.application.polyflow.content.factories;

import com.example.application.polyflow.content.AccumulatorContent;
import com.example.application.polyflow.datatypes.Tuple;
import com.example.application.polyflow.datatypes.Table;
import com.example.application.polyflow.datatypes.Tuple;
import com.example.application.polyflow.datatypes.TuplesOrResult;
import org.streamreasoning.polyflow.api.secret.content.Content;
import org.streamreasoning.polyflow.api.secret.content.ContentFactory;
import org.streamreasoning.polyflow.base.contentimpl.EmptyContent;

import java.util.ArrayList;
import java.util.List;

public class AccumulatorFactory implements ContentFactory<Tuple, Tuple, TuplesOrResult> {

    @Override
    public Content<Tuple, Tuple, TuplesOrResult> createEmpty() {
        return new EmptyContent<>(new TuplesOrResult(new ArrayList<>()));
    }

    @Override
    public Content<Tuple, Tuple, TuplesOrResult> create() {
        return new AccumulatorContent();
    }
}
