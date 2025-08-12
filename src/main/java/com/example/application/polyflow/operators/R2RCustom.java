package com.example.application.polyflow.operators;

import com.example.application.polyflow.datatypes.electricity.GridInputWindowed;
import dev.mccue.josql.Query;
import dev.mccue.josql.QueryExecutionException;
import dev.mccue.josql.QueryParseException;
import org.streamreasoning.polyflow.api.operators.r2r.RelationToRelationOperator;

import java.util.ArrayList;
import java.util.List;


public class R2RCustom implements RelationToRelationOperator<List<GridInputWindowed>> {

    public String resName;
    public List<String> tvgNames;
    public List<GridInputWindowed> windowedData = new ArrayList<>();

    public R2RCustom(List<String> tvgNames, String resName) {
        this.resName = resName;
        this.tvgNames = tvgNames;
    }

    @Override
    public List<GridInputWindowed> eval(List<List<GridInputWindowed>> list) {
        windowedData = new ArrayList<>();
        list.forEach(l -> windowedData.addAll(l));

        var query = new Query();
        try {
            query.parse("""
                    SELECT
                        consA,
                        timestamp
                    FROM
                        com.example.application.polyflow.datatypes.GridInputWindowed
                    WHERE
                        consA < 8
                    """);
            System.out.println(query.execute(windowedData).getResults());

        } catch (QueryParseException e) {
            throw new RuntimeException(e);
        } catch (QueryExecutionException e) {
            throw new RuntimeException(e);
        }


//        SQLParser<GridInputWindowed> parser = new SQLParser<>(GridInputWindowed.class);
//
//        Attribute<GridInputWindowed, Long> consA = attribute("consA", GridInputWindowed::getConsA);
//        Attribute<GridInputWindowed, Long> consB = attribute("consB", GridInputWindowed::getConsB);
//        Attribute<GridInputWindowed, Long> ts = attribute("ts", GridInputWindowed::getTimestamp);
//        Attribute<GridInputWindowed, Long> id = attribute("id", GridInputWindowed::getId);
//
//        parser.registerAttribute(consA);
//        parser.registerAttribute(consB);
//        parser.registerAttribute(ts);
//        parser.registerAttribute(id);
//
//        IndexedCollection<GridInputWindowed> cars = new ConcurrentIndexedCollection<GridInputWindowed>();
//
//        cars.addAll(windowedData);
//
//        ParseResult<GridInputWindowed> q = parser.parse( "SELECT ts FROM cars WHERE 'consA' = 8");
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
