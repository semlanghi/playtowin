package com.example.application.polyflow.datatypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Table implements Iterable<Tuple> {

    public String operatorId;
    public String intervalId;
    public List<Tuple> values = new ArrayList<>();

    public Table(){

    }
    public Table(String operatorId, String intervalId, List<Tuple> values){
        this.operatorId = operatorId;
        this.intervalId = intervalId;
        this.values = new ArrayList<>();
        //Avoid passing the same reference in case we transform data in some R2R computation
        for(Tuple t : values){
            this.values.add(t.copy());
        }
    }

    @Override
    public Iterator<Tuple> iterator() {
        return values.iterator();
    }

    @Override
    public String toString(){
        String s1;
        StringBuilder s2= new StringBuilder();
        s1 = "Window: "+operatorId+"; interval: "+intervalId+"; values: \n";
        for(Tuple t: values){
            s2.append(t.toString()).append("\n");
        }
        return s1+s2;
    }
}
