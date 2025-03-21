package com.example.application.polyflow.operators;

import org.streamreasoning.polyflow.api.operators.dag.DAG;
import org.streamreasoning.polyflow.api.operators.dag.DAGNode;
import org.streamreasoning.polyflow.api.operators.r2r.RelationToRelationOperator;
import org.streamreasoning.polyflow.api.sds.timevarying.TimeVarying;
import org.streamreasoning.polyflow.base.operatorsimpl.dag.BinaryDAGNodeImpl;
import org.streamreasoning.polyflow.base.operatorsimpl.dag.DAGRootNodeImpl;
import org.streamreasoning.polyflow.base.operatorsimpl.dag.UnaryDAGNodeImpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomDAG<R extends Iterable<?>> implements DAG<R> {


    Map<String, DAGNode<R>> root = new HashMap<>();

    DAGNode<R> tail;



    @Override
    public void addToDAG(RelationToRelationOperator<R> op) {

        DAGNode<R> dagNode;
        if(op.getTvgNames().size() == 2) //Binary R2R
            dagNode = new BinaryDAGNodeImpl<>(op);

        else if(op.getTvgNames().size() == 1) //Unary R2R
            dagNode = new UnaryDAGNodeImpl<>(op);

        else
            dagNode = new NaryDAGNode<>(op);

        root.put(op.getResName(), dagNode);
        for(String prev : op.getTvgNames()){
            DAGNode<R> node = root.get(prev);
            node.setNext(dagNode);
            dagNode.addPrev(node);
        }

    }

    public void addTVGs(Collection<TimeVarying<R>> sds){
        for(TimeVarying<R> tvg : sds){
            root.put(tvg.iri(), new DAGRootNodeImpl<>(tvg));
        }
    }

    @Override
    public void initialize(){
        DAGNode<R> tmp  = root.values().stream().findFirst().get();
        while(tmp.hasNext()){
            tmp = tmp.getNext();
        }
        this.tail = tmp;
    }

    @Override
    public R eval(long ts){
        return this.tail.eval(ts);
    }

    @Override
    public TimeVarying<R> apply(){ return this.tail.apply();}

    @Override
    public DAGNode<R> getTail(){
        return this.tail;
    }

    @Override
    public void printDAG(){
       /* Set<DAGNode<R>> printed = new HashSet<>();
        for(DAGNode<R> node : root.values()){
            while(node != null){
                if(!printed.contains(node)) {
                    printed.add(node);
                    System.out.print("[T:" + node.getOperandsNames() + " O:" + node.getOpName() + "]->");
                    node = node.getNext();
                }
                else if(printed.contains(node) && node.isBinary()){
                    System.out.print("[T:" + node.getOperandsNames() + " O:" + node.getOpName() + "]->");
                    break;
                }
            }
            System.out.print("\n");

        }*/
    }
}