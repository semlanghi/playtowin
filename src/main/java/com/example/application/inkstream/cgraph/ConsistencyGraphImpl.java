package com.example.application.inkstream.cgraph;

import com.example.application.inkstream.constraint.ConstraintFactory;
import com.example.application.inkstream.constraint.StreamingConstraint;
import com.example.application.inkstream.record.ConsistencyAnnotatedRecord;


import java.util.*;

public class ConsistencyGraphImpl<V> implements ConsistencyGraph<V> {

    private List<ConsistencyNode<V>> rootNodes;
    private ConstraintFactory<V> factory;
    private List<ConsistencyNode<V>> debugNodeCollection = new ArrayList<>();

    public ConsistencyGraphImpl(ConstraintFactory<V> constraintFactory) {
        this.rootNodes = new LinkedList<>();
        this.factory = constraintFactory;
    }

    private ConsistencyGraphImpl(List<ConsistencyNode<V>> rootNodes, ConstraintFactory<V> factory) {
        this.rootNodes = rootNodes;
        this.factory = factory;
    }

    @Override
    public List<ConsistencyNode<V>> getRootNodes() {
        return rootNodes;
    }

    @Override
    public ConsistencyAnnotatedRecord<V> add(V dataPoint){

        ConsistencyAnnotatedRecord<V> consistencyAnnotatedRecord = new ConsistencyAnnotatedRecord<>(dataPoint);
        StreamingConstraint<V> make = factory.make(dataPoint);
        ConsistencyNode<V> consistencyNode = new ConsistencyNode<>(consistencyAnnotatedRecord, make);

        Iterator<ConsistencyNode<V>> iterator = rootNodes.iterator();

        Queue<ConsistencyNode<V>> nodeQueue = new ArrayDeque<>(rootNodes);

        while (nodeQueue.peek()!=null){
            ConsistencyNode<V> poll = nodeQueue.poll();

            //boolean flag to prevent calls to iterator.remove() which are not backed by a iterator.next() call
            boolean canRemove = false;
            if (iterator.hasNext()){
                iterator.next();
                canRemove = true;
            }

            double result = poll.checkConstraint(dataPoint);

            if (result < 0 || result > 0) {
                consistencyAnnotatedRecord.setPolynomial(consistencyNode.getConsistencyAnnotatedRecord().getPolynomial().times(poll.getConstraint().getDescription(), (int) Math.ceil(result)));
                nodeQueue.addAll(poll.getConnectedNodes());
            } else {
                consistencyNode.connectTo(poll);
                //Remove the connected node, as it is no more a root
                if (canRemove)
                    iterator.remove();
            }
        }

        rootNodes.add(consistencyNode);

        return consistencyAnnotatedRecord;
    }

    @Override
    public ConsistencyGraph<V> union(ConsistencyGraph<V> other) {
        List<ConsistencyNode<V>> allRoots = new LinkedList<>();
        allRoots.addAll(this.rootNodes);
        allRoots.addAll(other.getRootNodes());
        return new ConsistencyGraphImpl<>(allRoots, this.factory);
    }

    public ConstraintFactory<V> getFactory() {
        return factory;
    }

    @Override
    public ConsistencyAnnotatedRecord<V> add(ConsistencyAnnotatedRecord<V> consistencyAnnotatedRecord){

        StreamingConstraint<V> make = factory.make(consistencyAnnotatedRecord.getWrappedRecord());

        ConsistencyNode<V> consistencyNode = new ConsistencyNode<>(consistencyAnnotatedRecord, make);

        Iterator<ConsistencyNode<V>> iterator = rootNodes.iterator();

        Queue<ConsistencyNode<V>> nodeQueue = new ArrayDeque<>(rootNodes);

        List<ConsistencyNode<V>> toExclude = new ArrayList<>();
        while (nodeQueue.peek()!=null){
            ConsistencyNode<V> poll = nodeQueue.poll();

            //boolean flag to prevent calls to iterator.remove() which are not backed by a iterator.next() call
            boolean canRemove = false;
            if (iterator.hasNext()){
                iterator.next();
                canRemove = true;
            }

            double result = poll.checkConstraint(consistencyAnnotatedRecord.getWrappedRecord());

            if (result < 0 || result > 0) {
                consistencyAnnotatedRecord.setPolynomial(consistencyNode.getConsistencyAnnotatedRecord().getPolynomial().times(poll.getConstraint().getDescription(), (int) Math.ceil(result)));
                nodeQueue.addAll(poll.getConnectedNodes());
            } else {

                if (!toExclude.contains(poll)){
                    consistencyNode.connectTo(poll);
                    toExclude.addAll(poll.getConnectedNodes());
                }


                //Remove the connected node, as it is no more a root
                if (canRemove)
                    iterator.remove();
            }
        }

        rootNodes.add(consistencyNode);

        return consistencyAnnotatedRecord;
    }


    @Override
    public List<ConsistencyNode<V>> getDebugNodeCollection() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (ConsistencyNode<V> tmp: rootNodes) {
            stringBuilder.append(tmp.toStringPrime());
        }
        return stringBuilder.toString();
    }

}
