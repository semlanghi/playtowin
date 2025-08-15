package com.example.application.polyflow.content;

import com.example.application.polyflow.datatypes.Tuple;
import com.example.application.polyflow.datatypes.Table;
import com.example.application.polyflow.datatypes.Tuple;
import com.example.application.polyflow.datatypes.TuplesOrResult;
import org.streamreasoning.polyflow.api.secret.content.Content;

import java.util.ArrayList;
import java.util.List;

public class AccumulatorContent implements Content<Tuple, Tuple, TuplesOrResult> {

    private List<Tuple> content = new ArrayList<>();
    private String operatorId = "";

    public List<Tuple> getWindowContent(){
        return this.content;
    }

    public void setOperatorId(String operatorId){
        this.operatorId = operatorId;
    }
    public String getOperatorId(){
        return operatorId;
    }

    @Override
    public int size() {
        return content.size();
    }

    @Override
    public void add(Tuple tuple) {
        content.add(tuple);
    }

    @Override
    public TuplesOrResult coalesce() {
        //This is the state, if you modify it elsewhere it will also change in here
        return new TuplesOrResult(content);
    }

}
