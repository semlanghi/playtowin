package com.example.application.polyflow.datatypes;

import com.example.application.data.AbstractEntity;
import jakarta.persistence.Entity;

@Entity
public class GridOutputMapping extends AbstractEntity {

    public String record_Id;
    public String opTointervalIds = "";


    public String getRecord_Id() {
        return record_Id;
    }

    public void setRecord_Id(String record_Id) {
        this.record_Id = record_Id;
    }

    public void add(String opId, String intId){
//        opTointervalIds.put(opId,intId);
        opTointervalIds += opId + "=" + intId + ";";
    }



}
