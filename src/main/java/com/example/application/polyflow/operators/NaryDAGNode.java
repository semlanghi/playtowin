package com.example.application.polyflow.operators;

import org.streamreasoning.polyflow.api.operators.dag.DAGNode;
import org.streamreasoning.polyflow.api.operators.r2r.RelationToRelationOperator;
import org.streamreasoning.polyflow.api.sds.timevarying.LazyTimeVarying;
import org.streamreasoning.polyflow.api.sds.timevarying.TimeVarying;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NaryDAGNode<R extends Iterable<?>> implements DAGNode<R> {

    private RelationToRelationOperator<R> r2rOperator;
    private DAGNode<R> next;
    private List<DAGNode<R>> prev;
    private String resName;

    public NaryDAGNode(RelationToRelationOperator<R> r2rOperator) {
        this.r2rOperator = r2rOperator;
        this.resName = r2rOperator.getResName();
        this.prev = new ArrayList<>();
    }


    @Override
    public List<String> getOperandsNames() {
        return null;
    }


    @Override
    public RelationToRelationOperator<R> getR2rOperator() {
        return r2rOperator;
    }

    @Override
    public void setNext(DAGNode<R> next) {
        this.next = next;
    }

    @Override
    public void addPrev(DAGNode<R> prev) {
        this.prev.add(prev);
    }

    @Override
    public DAGNode<R> getNext() {
        return this.next;
    }

    @Override
    public List<DAGNode<R>> getPrev() {
        return this.prev;
    }

    @Override
    public boolean hasNext() {
        return this.next != null;
    }

    @Override
    public boolean hasPrev() {
        return !this.prev.isEmpty();
    }

    @Override
    public R eval(long ts) {
        List<R> res = prev.stream().map(p->p.eval(ts)).collect(Collectors.toList());
        return this.r2rOperator.eval(res);
    }

    @Override
    public TimeVarying<R> apply() {
        return new LazyTimeVarying<>(this, resName);
    }
}
