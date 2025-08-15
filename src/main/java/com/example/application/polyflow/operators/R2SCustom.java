package com.example.application.polyflow.operators;

import com.example.application.polyflow.datatypes.Tuple;
import com.example.application.polyflow.datatypes.Table;
import com.example.application.polyflow.datatypes.TuplesOrResult;
import dev.mccue.josql.QueryResults;
import org.streamreasoning.polyflow.api.operators.r2s.RelationToStreamOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class R2SCustom implements RelationToStreamOperator<TuplesOrResult, TuplesOrResult> {
    @Override
    public Stream<TuplesOrResult> eval(TuplesOrResult table, long ts){

        List<TuplesOrResult> fakeList = new ArrayList<>();
        fakeList.add(table);
        return fakeList.stream();
    }
}
