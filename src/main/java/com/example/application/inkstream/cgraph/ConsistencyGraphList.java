package com.example.application.inkstream.cgraph;

import com.example.application.inkstream.constraint.ConstraintFactory;
import com.example.application.inkstream.constraint.StreamingConstraint;
import com.example.application.inkstream.record.ConsistencyAnnotatedRecord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConsistencyGraphList<V> implements ConsistencyGraph<V> {

    private ConstraintFactory<V> factory;
    private List<List<ConsistencyAnnotatedRecord<V>>> paths;


    public ConsistencyGraphList(ConstraintFactory<V> constraintFactory) {
        this.paths = new ArrayList<>();
        this.factory = constraintFactory;
    }



    @Override
    public ConsistencyAnnotatedRecord<V> add(V dataPoint){

        ConsistencyAnnotatedRecord<V> consistencyAnnotatedRecord = new ConsistencyAnnotatedRecord<>(dataPoint);
        StreamingConstraint<V> make = factory.make(dataPoint);

        int size = paths.size();
        if(size >0) {
            boolean[] pathsClosed = new boolean[size];
            int[] indexOfTraversal = new int[size];
            boolean canContinue = true, attached = false;
            Set<ConsistencyAnnotatedRecord<V>> consistentRecordsWithoutRedundancy = new HashSet<>();

            while (canContinue) {
                canContinue = false;
                for (int i = 0; i < size; i++) {
                    if (indexOfTraversal[i] >= paths.get(i).size())
                        pathsClosed[i] = true;
                    if (!pathsClosed[i]) {
                        List<ConsistencyAnnotatedRecord<V>> consistencyAnnotatedRecords = paths.get(i);
                        int sizeOfPath = consistencyAnnotatedRecords.size();
                        ConsistencyAnnotatedRecord<V> EventBeanConsistencyAnnotatedRecord = consistencyAnnotatedRecords.get(sizeOfPath - 1 - indexOfTraversal[i]);
                        V originPoint = EventBeanConsistencyAnnotatedRecord.getWrappedRecord();
                        double result = factory.make(originPoint).checkConstraint(dataPoint);
                        int resultInt = (int) Math.ceil(Math.abs(result));

                        if (resultInt != 0) {
                            consistencyAnnotatedRecord.setPolynomial(consistencyAnnotatedRecord.getPolynomial().times(originPoint, resultInt));
                            indexOfTraversal[i]++;
                            canContinue = true;
                        } else {
                            attached = true;
                            if (indexOfTraversal[i] == 0)
                                consistencyAnnotatedRecords.add(consistencyAnnotatedRecord);
                            else {
                                //Create new path only if the new path has not been created yet
                                if (!consistentRecordsWithoutRedundancy.contains(EventBeanConsistencyAnnotatedRecord)){
                                    List<ConsistencyAnnotatedRecord<V>> nwPath = new ArrayList<>();
                                    nwPath.add(EventBeanConsistencyAnnotatedRecord);
                                    nwPath.add(consistencyAnnotatedRecord);
                                    paths.add(nwPath);
                                    consistentRecordsWithoutRedundancy.add(EventBeanConsistencyAnnotatedRecord);
                                }
                            }
                            pathsClosed[i] = true;
                        }
                    }
                }
            }
            if (!attached){
                List<ConsistencyAnnotatedRecord<V>> nwPath = new ArrayList<>();
                nwPath.add(consistencyAnnotatedRecord);
                paths.add(nwPath);
            }
        } else {
            List<ConsistencyAnnotatedRecord<V>> nwPath = new ArrayList<>();
            nwPath.add(consistencyAnnotatedRecord);
            paths.add(nwPath);
        }

        return consistencyAnnotatedRecord;
    }

    @Override
    public ConsistencyGraph<V> union(ConsistencyGraph<V> other) {
        return null;
    }

    @Override
    public ConsistencyAnnotatedRecord<V> add(ConsistencyAnnotatedRecord<V> consistencyAnnotatedRecord){

        StreamingConstraint<V> make = factory.make(consistencyAnnotatedRecord.getWrappedRecord());

        int size = paths.size();
        if(size >0) {
            boolean[] pathsClosed = new boolean[size];
            int[] indexOfTraversal = new int[size];
            boolean canContinue = true, attached = false;
            Set<ConsistencyAnnotatedRecord<V>> consistentRecordsWithoutRedundancy = new HashSet<>();

            while (canContinue) {
                canContinue = false;
                for (int i = 0; i < size; i++) {
                    if (indexOfTraversal[i] >= paths.get(i).size())
                        pathsClosed[i] = true;
                    if (!pathsClosed[i]) {
                        List<ConsistencyAnnotatedRecord<V>> consistencyAnnotatedRecords = paths.get(i);
                        int sizeOfPath = consistencyAnnotatedRecords.size();
                        ConsistencyAnnotatedRecord<V> EventBeanConsistencyAnnotatedRecord = consistencyAnnotatedRecords.get(sizeOfPath - 1 - indexOfTraversal[i]);
                        V originPoint = EventBeanConsistencyAnnotatedRecord.getWrappedRecord();
                        long result = factory.make(originPoint).checkConstraint(consistencyAnnotatedRecord.getWrappedRecord());
                        int resultInt = (int) result;

                        if (resultInt != 0) {
                            consistencyAnnotatedRecord.setPolynomial(consistencyAnnotatedRecord.getPolynomial().times(originPoint, resultInt));
                            indexOfTraversal[i]++;
                            canContinue = true;
                        } else {
                            attached = true;
                            if (indexOfTraversal[i] == 0)
                                consistencyAnnotatedRecords.add(consistencyAnnotatedRecord);
                            else {
                                //Create new path only if the new path has not been created yet
                                if (!consistentRecordsWithoutRedundancy.contains(EventBeanConsistencyAnnotatedRecord)){
                                    List<ConsistencyAnnotatedRecord<V>> nwPath = new ArrayList<>();
                                    nwPath.add(EventBeanConsistencyAnnotatedRecord);
                                    nwPath.add(consistencyAnnotatedRecord);
                                    paths.add(nwPath);
                                    consistentRecordsWithoutRedundancy.add(EventBeanConsistencyAnnotatedRecord);
                                }
                            }
                            pathsClosed[i] = true;
                        }
                    }
                }
            }
            if (!attached){
                List<ConsistencyAnnotatedRecord<V>> nwPath = new ArrayList<>();
                nwPath.add(consistencyAnnotatedRecord);
                paths.add(nwPath);
            }
        } else {
            List<ConsistencyAnnotatedRecord<V>> nwPath = new ArrayList<>();
            nwPath.add(consistencyAnnotatedRecord);
            paths.add(nwPath);
        }

        return consistencyAnnotatedRecord;
    }

    @Override
    public List<ConsistencyNode<V>> getDebugNodeCollection() {
        return null;
    }

    @Override
    public List<ConsistencyNode<V>> getRootNodes() {
        return null;
    }

    @Override
    public String toString() {
        return "ConsistencyGraphList{" +
                "paths=" + paths +
                '}';
    }

}
