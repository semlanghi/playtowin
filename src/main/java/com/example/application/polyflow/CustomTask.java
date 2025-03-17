package com.example.application.polyflow;

import org.apache.log4j.Logger;
import org.streamreasoning.polyflow.api.operators.dag.DAG;
import org.streamreasoning.polyflow.api.operators.r2r.RelationToRelationOperator;
import org.streamreasoning.polyflow.api.operators.r2s.RelationToStreamOperator;
import org.streamreasoning.polyflow.api.operators.s2r.execution.assigner.StreamToRelationOperator;
import org.streamreasoning.polyflow.api.processing.Task;
import org.streamreasoning.polyflow.api.sds.SDS;
import org.streamreasoning.polyflow.api.sds.timevarying.TimeVarying;
import org.streamreasoning.polyflow.api.secret.time.Time;
import org.streamreasoning.polyflow.api.stream.data.DataStream;

import java.util.*;
import java.util.stream.Collectors;

public class CustomTask<I, W, R extends Iterable<?>, O> implements Task<I, W, R, O> {


    private static final Logger log = Logger.getLogger(CustomTask.class);
    private List<StreamToRelationOperator<I, W, R>> s2rOperators;
    private List<RelationToRelationOperator<R>> r2rOperators;
    private RelationToStreamOperator<R, O> r2sOperator;
    private Map<DataStream<I>, List<StreamToRelationOperator<I, W, R>>> registeredS2R;
    private Time time;

    private DAG<R> dag;

    private SDS<R> sds;
    private String taskId = "";

    public CustomTask(){

        this.s2rOperators = new ArrayList<>();
        this.r2rOperators = new ArrayList<>();
        this.registeredS2R = new HashMap<>();
    }

    public CustomTask(String taskId){

        this.taskId = taskId;
        this.s2rOperators = new ArrayList<>();
        this.r2rOperators = new ArrayList<>();
        this.registeredS2R = new HashMap<>();
    }

    @Override
    public List<StreamToRelationOperator<I, W, R>> getS2Rs() {
        return s2rOperators;
    }

    @Override
    public List<RelationToRelationOperator<R>> getR2Rs() {
        return r2rOperators;
    }

    @Override
    public RelationToStreamOperator<R, O> getR2Ss() {
        return r2sOperator;
    }

    @Override
    public Task<I, W, R, O> addS2ROperator(StreamToRelationOperator<I, W, R> s2rOperator, DataStream<I> inputStream) {

        for(StreamToRelationOperator<I, W, R> op : s2rOperators){
            if(op.getName().equals(s2rOperator.getName())){
                throw new RuntimeException("S2R Operator with same name already present");
            }
        }
        this.s2rOperators.add(s2rOperator);
        if(!registeredS2R.containsKey(inputStream)){
            registeredS2R.put(inputStream, new ArrayList<>());
        }
        if(registeredS2R.get(inputStream).contains(s2rOperator)){
            throw new RuntimeException("S2R operator already registered on this input stream");
        }
        registeredS2R.get(inputStream).add(s2rOperator);
        return this;
    }

    @Override
    public Task<I, W, R, O> addR2ROperator(RelationToRelationOperator<R> r2rOperator) {
        this.r2rOperators.add(r2rOperator);
        return this;
    }

    @Override
    public Task<I, W, R, O> addR2SOperator(RelationToStreamOperator<R, O> r2sOperator) {
        this.r2sOperator= r2sOperator;
        return this;
    }

    @Override
    public Task<I, W, R, O> addTime(Time time){
        this.time = time;
        return this;
    }

    @Override
    public Task<I, W, R, O> addSDS(SDS<R> sds){
        this.sds = sds;
        return this;
    }

    @Override
    public Task<I, W, R, O> addDAG(DAG<R> dag){
        this.dag = dag;
        return this;
    }

    @Override
    public DAG<R> getDAG(){
        return this.dag;
    }

    @Override
    public SDS<R> getSDS(){ return this.sds;}

    @Override
    public Time getTime() {
        return time;
    }

    @Override
    public String getId(){
        return this.taskId;
    }

    @Override
    public void initialize(){
        for(StreamToRelationOperator<I, W, R> operator: s2rOperators){
            TimeVarying<R> tvg = operator.get();
            this.sds.add(tvg);
            if(tvg.named()){
                this.sds.add(tvg.iri(), tvg);
            }
        }


        dag.addTVGs(sds.asTimeVaryingEs());
        for (RelationToRelationOperator<R> op : r2rOperators){
            dag.addToDAG(op);
        }
        dag.initialize();

    }


    @Override
    public void evictWindows() {
        for(StreamToRelationOperator<I, W, R> s2r : s2rOperators){
            s2r.evict();
        }
    }
    @Override
    public void evictWindows(long ts){
        for(StreamToRelationOperator<I, W, R> s2r : s2rOperators){
            s2r.evict(ts);
        }
    }



    @Override
    public void elaborateElement(DataStream<I> inputStream, I element, long timestamp) {
        if(registeredS2R.containsKey(inputStream)) {
            for (StreamToRelationOperator<I, W, R> s2r : registeredS2R.get(inputStream)) {
                s2r.compute(element, timestamp);
            }
        }

    }

    public Collection<Collection<O>> compute(){
        Collection<Collection<O>> res = new ArrayList<>();
        while(time.hasEvaluationInstant()){
            long t = time.getEvaluationTime().t;
            R partialRes = eval(t);
            res.add(r2sOperator.eval(partialRes, t).collect(Collectors.toList()));
            evictWindows(t);
        }

        return res;
    }

    public Collection<O> computeLazy(long ts){
        R result = eval(ts);
        return r2sOperator.eval(result, ts).collect(Collectors.toList());
        //We do not evict windows since it was an on-demand query

    }

    @Override
    public TimeVarying<R> apply(){
        return this.dag.apply();
    }

    private R eval(long ts){

        R result = null;

        result = dag.eval(ts);
        if(result == null){
            throw new RuntimeException("Result of DAG computation is null");
        }
        return result;


    }




}