package com.example.application.polyflow.content.factories;

import com.example.application.polyflow.content.AccumulatorContent;
import com.example.application.polyflow.datatypes.GridInputWindowed;
import com.example.application.polyflow.datatypes.Table;
import com.example.application.polyflow.datatypes.Tuple;
import org.streamreasoning.polyflow.api.secret.content.Content;
import org.streamreasoning.polyflow.api.secret.content.ContentFactory;
import org.streamreasoning.polyflow.base.contentimpl.EmptyContent;

import java.util.ArrayList;
import java.util.List;

public class AccumulatorFactory implements ContentFactory<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> {

    @Override
    public Content<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> createEmpty() {
        return new EmptyContent<>(new ArrayList<>());
    }

    @Override
    public Content<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> create() {
        return new AccumulatorContent();
    }
}
