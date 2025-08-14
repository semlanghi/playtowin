package com.example.application.polyflow.operators;

import com.example.application.polyflow.datatypes.Tuple;
import com.example.application.polyflow.datatypes.Tuple;
import com.example.application.polyflow.datatypes.TuplesOrResult;
import dev.mccue.josql.Query;
import dev.mccue.josql.QueryExecutionException;
import dev.mccue.josql.QueryResults;
import org.streamreasoning.polyflow.api.operators.r2r.RelationToRelationOperator;

import java.util.ArrayList;
import java.util.List;


public class R2RSQLSingle implements RelationToRelationOperator<TuplesOrResult> {

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
    public TuplesOrResult eval(List<TuplesOrResult> list) {
        windowedData = new ArrayList<>();
        list.forEach(l -> windowedData.addAll(l.getWindowContent()));

        try {

            List queryResults = query.execute(windowedData).getResults();

            TuplesOrResult res = new TuplesOrResult(windowedData);
            res.setQueryResult(queryResults);
            return res;

        } catch (QueryExecutionException e) {
            throw new RuntimeException(e);
        }


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
