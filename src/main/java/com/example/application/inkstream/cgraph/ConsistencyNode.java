package com.example.application.inkstream.cgraph;

import com.example.application.inkstream.constraint.StreamingConstraint;
import com.example.application.inkstream.record.ConsistencyAnnotatedRecord;

import java.util.ArrayList;
import java.util.List;

public class ConsistencyNode<V> {

    private ConsistencyAnnotatedRecord<V> consistencyAnnotatedRecord;
    private StreamingConstraint<V> constraint;
    private List<ConsistencyNode<V>> connectedNodes;
    private List<ConsistencyNode<V>> inverseConnections;
    private List<ConsistencyNode<V>> inconsistentNodes;

    public ConsistencyNode(ConsistencyAnnotatedRecord<V> consistencyAnnotatedRecord, StreamingConstraint<V> constraint) {
        this.consistencyAnnotatedRecord = consistencyAnnotatedRecord;
        this.constraint = constraint;
        this.connectedNodes = new ArrayList<>();
        this.inconsistentNodes = new ArrayList<>();
        this.inverseConnections = new ArrayList<>();
    }

    public void connectTo(ConsistencyNode<V> destination){
        connectedNodes.add(destination);
//        destination.connectInverse(this);
    }

    private void connectInverse(ConsistencyNode<V> destination){
        inverseConnections.add(destination);
    }

    public void connectInconsistency(ConsistencyNode<V> destination){
        inconsistentNodes.add(destination);
    }

    public double checkConstraint(V EventBean){
        return constraint.checkConstraint(EventBean);
    }

    public StreamingConstraint<V> getConstraint() {
        return constraint;
    }

    public ConsistencyAnnotatedRecord<V> getConsistencyAnnotatedRecord() {
        return consistencyAnnotatedRecord;
    }



    public List<ConsistencyNode<V>> getConnectedNodes() {
        return connectedNodes;
    }

    public List<ConsistencyNode<V>> getInverseConnections() {
        return inverseConnections;
    }

    public List<ConsistencyNode<V>> getInconsistentNodes() {
        return inconsistentNodes;
    }

    public boolean isConnected(){
        return !connectedNodes.isEmpty();
    }

    @Override
    public String toString() {
        return "ConsistencyNode{" + consistencyAnnotatedRecord.getWrappedRecord().toString() +"}";
    }

    public String toStringPrime() {
        return "ConsistencyNode{" + consistencyAnnotatedRecord.getWrappedRecord().toString() +
                    " connectedNodes=" + connectedNodes + "}\n";
    }
}
