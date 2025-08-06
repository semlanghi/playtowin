package com.example.application.polyflow.operators;

import com.example.application.polyflow.datatypes.Tuple;
import com.example.application.polyflow.datatypes.Tuple;
import dev.mccue.josql.Query;
import dev.mccue.josql.QueryExecutionException;
import org.streamreasoning.polyflow.api.operators.r2r.RelationToRelationOperator;

import java.util.ArrayList;
import java.util.List;


public class R2RSQLSingle implements RelationToRelationOperator<List<Tuple>> {

    public String resName;
    public List<String> tvgNames;
    public List<Tuple> windowedData = new ArrayList<>();
    public Query query;

    public R2RSQLSingle(Query query, List<String> tvgNames, String resName) {
        this.resName = resName;
        this.tvgNames = tvgNames;
        this.query = query;
    }

    @Override
    public List<Tuple> eval(List<List<Tuple>> list) {
        windowedData = new ArrayList<>();
        list.forEach(l -> windowedData.addAll(l));

        try {

            System.out.println(query.execute(windowedData).getResults());

        } catch (QueryExecutionException e) {
            throw new RuntimeException(e);
        }


//        SQLParser<Tuple> parser = new SQLParser<>(Tuple.class);
//
//        Attribute<Tuple, Long> consA = attribute("consA", Tuple::getConsA);
//        Attribute<Tuple, Long> consB = attribute("consB", Tuple::getConsB);
//        Attribute<Tuple, Long> ts = attribute("ts", Tuple::getTimestamp);
//        Attribute<Tuple, Long> id = attribute("id", Tuple::getId);
//
//        parser.registerAttribute(consA);
//        parser.registerAttribute(consB);
//        parser.registerAttribute(ts);
//        parser.registerAttribute(id);
//
//        IndexedCollection<Tuple> cars = new ConcurrentIndexedCollection<Tuple>();
//
//        cars.addAll(windowedData);
//
//        ParseResult<Tuple> q = parser.parse( "SELECT ts FROM cars WHERE 'consA' = 8");
//
//        cars.retrieve(q.getQuery()).stream().map(gridInputWindowed -> gridInputWindowed.getConsA());

        return windowedData;
    }

    @Override
    public List<String> getTvgNames() {
        return tvgNames;
    }

    @Override
    public String getResName() {
        return resName;
    }
}
