package com.example.application.polyflow.datatypes;

public class CustomRow implements Tuple{

    public String id;
    public long timestamp;
    public String cons_A;
    public String cons_B;


    public CustomRow(String id, long timestamp, String cons_A, String cons_B){
        this.id = id;
        this.timestamp = timestamp;
        this.cons_A = cons_A;
        this.cons_B = cons_B;
    }

    @Override
    public Tuple copy() {
        return new CustomRow(id, timestamp, cons_A, cons_B);
    }

    @Override
    public String toString(){
        return "id: "+ id+"; timestamp: "+timestamp+"; cons_A: "+cons_A+"; cons_b: "+cons_B;
    }

}
