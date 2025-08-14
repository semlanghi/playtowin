package com.example.application.polyflow.datatypes;

import dev.mccue.josql.QueryResults;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TuplesOrResult implements Iterable<Tuple>{

    List<Tuple> windowContent;
    List queryResult;

    public TuplesOrResult(){

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



    @Override
    public Iterator<Tuple> iterator() {
        return null;
    }
}
