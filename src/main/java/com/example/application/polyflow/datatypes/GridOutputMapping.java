package com.example.application.polyflow.datatypes;

import com.example.application.data.AbstractEntity;
import jakarta.persistence.Entity;

@Entity
public class GridOutputMapping extends AbstractEntity {

    public String recordId;
    public String opTointervalIds = "";


    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public void add(String opId, String intId){
//        opTointervalIds.put(opId,intId);
        opTointervalIds += opId + "=" + intId + ";";
    }



}
