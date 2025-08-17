package com.example.application.polyflow.operators;

import com.example.application.polyflow.datatypes.Tuple;
import com.example.application.polyflow.datatypes.TuplesOrResult;
import org.streamreasoning.polyflow.api.operators.s2r.execution.assigner.StreamToRelationOperator;
import org.streamreasoning.polyflow.api.sds.timevarying.TimeVarying;
import org.streamreasoning.polyflow.api.secret.content.Content;

import java.util.List;

public class TimeVaryingTuplesOrResult implements TimeVarying<TuplesOrResult> {

    private final StreamToRelationOperator<Tuple, Tuple, TuplesOrResult> op;
    private final String name;
    private TuplesOrResult content;
    private TuplesOrResult historicalContent;

    public TimeVaryingTuplesOrResult(StreamToRelationOperator<Tuple,Tuple, TuplesOrResult> op, String name) {
        this.op = op;
        this.name = name;
    }

    @Override
    public void materialize(long l) {
        content = this.op.content(l).coalesce();
        content.setOperatorId(name);
    }

    public void materialize_history(long l){
        historicalContent = op.getContents(l).stream().map(Content::coalesce).reduce(new TuplesOrResult(), (l1, l2)-> {
            l1.getWindowContent().addAll(l2.getWindowContent());
            return l1;
        });
    }

    public TuplesOrResult getHistory(){
        return historicalContent;
    }
    @Override
    public TuplesOrResult get() {
        return content;
    }

    @Override
    public String iri() {
        return name;
    }
}
