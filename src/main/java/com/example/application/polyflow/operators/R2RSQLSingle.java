package com.example.application.polyflow.operators;

import com.example.application.polyflow.datatypes.Tuple;
import com.example.application.polyflow.datatypes.Tuple;
import com.example.application.polyflow.datatypes.TuplesOrResult;
import dev.mccue.josql.Query;
import dev.mccue.josql.QueryExecutionException;
import dev.mccue.josql.QueryResults;
import org.streamreasoning.polyflow.api.operators.r2r.RelationToRelationOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        TuplesOrResult finalRes = new TuplesOrResult();
        /*For each Content of each window operator, we compute the query result and add it to a single object.
            This is necessary because in the GUI we need to know the S2R that each element is associated to in order to correctly
             show them separated in the Results Tab
             */
        for(int i = 0; i<list.size(); i++){
            TuplesOrResult curr = list.get(i);
            try{
                finalRes.getResultContainer().put(curr.getOperatorId(), query.execute(curr.getWindowContent()).getResults());
            }catch(QueryExecutionException e){
                throw new RuntimeException(e);
            }

        }
        return finalRes;

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
