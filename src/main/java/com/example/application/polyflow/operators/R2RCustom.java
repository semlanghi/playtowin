package com.example.application.polyflow.operators;

import com.example.application.polyflow.datatypes.GridInputWindowed;
import com.example.application.polyflow.datatypes.Table;
import org.streamreasoning.polyflow.api.operators.r2r.RelationToRelationOperator;

import java.util.ArrayList;
import java.util.List;

public class R2RCustom implements RelationToRelationOperator<List<GridInputWindowed>> {

    public String resName;
    public List<String> tvgNames;
    public List<GridInputWindowed> windowedData = new ArrayList<>();

    public R2RCustom(List<String> tvgNames, String resName){
        this.resName = resName;
        this.tvgNames = tvgNames;
    }
    @Override
    public List<GridInputWindowed> eval(List<List<GridInputWindowed>> list) {
        windowedData = new ArrayList<>();
        list.forEach(l-> windowedData.addAll(l));

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
