package com.example.application.polyflow.operators;

import com.example.application.polyflow.datatypes.Tuple;
import com.example.application.polyflow.datatypes.TuplesOrResult;
import org.streamreasoning.polyflow.api.operators.s2r.execution.assigner.StreamToRelationOperator;
import org.streamreasoning.polyflow.api.sds.timevarying.TimeVarying;

import java.util.List;

public class TimeVaryingTuplesOrResult implements TimeVarying<TuplesOrResult> {

    private final StreamToRelationOperator<Tuple, Tuple, TuplesOrResult> op;
    private final String name;
    private TuplesOrResult content;

    public TimeVaryingTuplesOrResult(StreamToRelationOperator<Tuple,Tuple, TuplesOrResult> op, String name) {
        this.op = op;
        this.name = name;
    }

    @Override
    public void materialize(long l) {
        content = this.op.content(l).coalesce();
        content.setOperatorId(name);
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
