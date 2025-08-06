package com.example.application.polyflow.operators;

import com.example.application.polyflow.datatypes.Tuple;
import com.example.application.polyflow.datatypes.Table;
import org.streamreasoning.polyflow.api.operators.r2s.RelationToStreamOperator;

import java.util.List;
import java.util.stream.Stream;

public class R2SCustom implements RelationToStreamOperator<List<Tuple>, Tuple> {
    @Override
    public Stream<Tuple> eval(List<Tuple> table, long ts){
        return table.stream();
    }
}
