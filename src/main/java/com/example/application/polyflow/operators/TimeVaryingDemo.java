package com.example.application.polyflow.operators;

import com.example.application.polyflow.datatypes.GridInputWindowed;
import org.streamreasoning.polyflow.api.operators.s2r.execution.assigner.StreamToRelationOperator;
import org.streamreasoning.polyflow.api.sds.timevarying.TimeVarying;
import org.streamreasoning.polyflow.api.secret.content.Content;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TimeVaryingDemo implements TimeVarying<List<GridInputWindowed>> {

    private final StreamToRelationOperator<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> op;
    private final String name;
    private List<GridInputWindowed> content;

    public TimeVaryingDemo(StreamToRelationOperator<GridInputWindowed,GridInputWindowed, List<GridInputWindowed>> op, String name) {
        this.op = op;
        this.name = name;
    }

    /**
     * The setTimestamp function merges the element
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
    public List<GridInputWindowed> get() {
        return content;
    }

    @Override
    public String iri() {
        return name;
    }


}
