package com.example.application.polyflow.content;

import com.example.application.polyflow.datatypes.GridInputWindowed;
import com.example.application.polyflow.datatypes.Table;
import com.example.application.polyflow.datatypes.Tuple;
import org.streamreasoning.polyflow.api.secret.content.Content;

import java.util.ArrayList;
import java.util.List;

public class AccumulatorContent implements Content<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> {
    public List<GridInputWindowed> content = new ArrayList<>();

    @Override
    public int size() {
        return content.size();
    }

    @Override
    public void add(GridInputWindowed tuple) {
        content.add(tuple);
    }

    @Override
    public List<GridInputWindowed> coalesce() {
        //This is the state, if you modify it elsewhere it will also change in here
        return content;
    }

}
