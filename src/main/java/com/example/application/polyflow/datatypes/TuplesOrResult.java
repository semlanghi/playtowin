package com.example.application.polyflow.datatypes;

import dev.mccue.josql.QueryResults;

import java.util.*;

public class TuplesOrResult implements Iterable<Tuple>{

    List<Tuple> windowContent;
    List<List> queryResult;

    Map<String, List<List>> resultContainer = new HashMap<>();


    private String operatorId = "";

    public TuplesOrResult(){

    }
    public Map<String, List<List>> getResultContainer(){
        return this.resultContainer;
    }
    public TuplesOrResult (List<Tuple> windowContent){
        this.windowContent = windowContent;
    }


    public void setQueryResult(List result){
        this.queryResult = result;
    }

    public List getQueryResult(){
        return this.queryResult;
    }

    public List<Tuple> getWindowContent(){
        return this.windowContent;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;

    }
    public String getOperatorId(){
        return this.operatorId;
    }


    @Override
    public Iterator<Tuple> iterator() {
        return null;
    }
}
