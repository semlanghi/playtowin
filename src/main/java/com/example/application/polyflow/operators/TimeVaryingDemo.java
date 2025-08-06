package com.example.application.polyflow.operators;

import com.example.application.polyflow.datatypes.Tuple;
import org.streamreasoning.polyflow.api.operators.s2r.execution.assigner.StreamToRelationOperator;
import org.streamreasoning.polyflow.api.sds.timevarying.TimeVarying;
import org.streamreasoning.polyflow.api.secret.content.Content;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TimeVaryingDemo implements TimeVarying<List<Tuple>> {

    private final StreamToRelationOperator<Tuple, Tuple, List<Tuple>> op;
    private final String name;
    private List<Tuple> content;

    public TimeVaryingDemo(StreamToRelationOperator<Tuple,Tuple, List<Tuple>> op, String name) {
        this.op = op;
        this.name = name;
    }

    /**
     * The materialize function merges the element
     * in the content into a single graph
     * and adds it to the current dataset.
     **/
    @Override
    public void materialize(long ts) {
        content = op.getContents(ts).stream().map(Content::coalesce).reduce(new ArrayList<>(), (l1, l2)-> {
            l1.addAll(l2);
            return l1;
        });
    }

    @Override
    public List<Tuple> get() {
        return content;
    }

    @Override
    public String iri() {
        return name;
    }


}
