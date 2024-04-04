package com.example.application.inkstream;

import com.example.application.inkstream.annotation.AnnotatedRelation;
import com.example.application.inkstream.annotation.R2RConsistencyAnnotator;
import com.example.application.inkstream.annotation.R2RDoubleConsistencyAnnotator;
import com.example.application.inkstream.annotation.Relation;
import com.example.application.inkstream.constraint.ConstraintFactory;
import com.example.application.inkstream.record.ConsistencyAnnotatedRecord;
import com.example.application.inkstream.record.EventBean;
import com.example.application.inkstream.record.SimpleEvent;
import org.apache.commons.rdf.api.Graph;
import org.streamreasoning.rsp4j.api.enums.StreamOperator;
import org.streamreasoning.rsp4j.api.operators.r2r.RelationToRelationOperator;
import org.streamreasoning.rsp4j.api.operators.r2r.Var;
import org.streamreasoning.rsp4j.api.operators.r2r.utils.R2RPipe;
import org.streamreasoning.rsp4j.api.operators.r2s.RelationToStreamOperator;
import org.streamreasoning.rsp4j.api.operators.s2r.execution.assigner.StreamToRelationOp;
import org.streamreasoning.rsp4j.api.operators.s2r.syntax.WindowNode;
import org.streamreasoning.rsp4j.api.querying.Aggregation;
import org.streamreasoning.rsp4j.api.querying.ContinuousQuery;
import org.streamreasoning.rsp4j.api.secret.time.Time;
import org.streamreasoning.rsp4j.api.stream.data.DataStream;
import org.streamreasoning.rsp4j.io.DataStreamImpl;
import org.streamreasoning.rsp4j.yasper.querying.operators.Dstream;
import org.streamreasoning.rsp4j.yasper.querying.operators.Istream;
import org.streamreasoning.rsp4j.yasper.querying.operators.Rstream;
import org.streamreasoning.rsp4j.yasper.querying.operators.r2r.*;
import org.streamreasoning.rsp4j.yasper.sds.DataSetImpl;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventQuery<V> implements ContinuousQuery<EventBean<V>, Relation<EventBean<V>>, AnnotatedRelation<EventBean<V>>, ConsistencyAnnotatedRecord<EventBean<V>>> {

    private DataSetImpl defaultGraph;
    private RelationToStreamOperator<AnnotatedRelation<EventBean<V>>, ConsistencyAnnotatedRecord<EventBean<V>>> r2s;
    private String id;

    private DataStream<ConsistencyAnnotatedRecord<EventBean<V>>> outputStream;
    private Map<String, List<EventBean<V>>> events = new HashMap<>();

    private Map<WindowNode, DataStream<EventBean<V>>> windowMap = new HashMap<>();
    private List<String> graphURIs = new ArrayList<>();
    private List<String> namedwindowsURIs = new ArrayList<>();
    private List<String> namedGraphURIs = new ArrayList<>();
    private List<Aggregation> aggregations = new ArrayList<>();
    private StreamOperator streamOperator = StreamOperator.NONE;
    private Time time;
    private List<Var> projections;
    private Map<String, List<Predicate<Binding>>> windowsToFilters = new HashMap<>();
    private ConstraintFactory<EventBean<V>> speedConstraintLongValueFactory;
    private ConstraintFactory<EventBean<V>> speedConstraintLongValueFactory2;
    private RelationToRelationOperator<Relation<EventBean<V>>, AnnotatedRelation<EventBean<V>>> consistencyAnnotator;

    public EventQuery(String id, DataStream<EventBean<V>> stream, Time time, WindowNode win, V s, long timestamp,
                      RelationToStreamOperator<AnnotatedRelation<EventBean<V>>, ConsistencyAnnotatedRecord<EventBean<V>>> r2s, ConstraintFactory<EventBean<V>> speedConstraintLongValueFactory) {
        this.id = id;
        this.outputStream = new DataStreamImpl<>(id);
        List<EventBean<V>> triplesTemp = new ArrayList<>();
        EventBean<V> triple = new SimpleEvent<>(s, timestamp);
        triplesTemp.add(triple);
        events.putIfAbsent(win.iri(), triplesTemp);
        if (win != null && stream != null) {
            windowMap.put(win, stream);
        }
        this.r2s = r2s;
        this.time = time;
        this.projections = new ArrayList<>();
        this.speedConstraintLongValueFactory = speedConstraintLongValueFactory;
    }

    public EventQuery(String id, DataStream<EventBean<V>> stream, Time time, WindowNode win, V s, long timestamp,
                      RelationToStreamOperator<AnnotatedRelation<EventBean<V>>, ConsistencyAnnotatedRecord<EventBean<V>>> r2s, ConstraintFactory<EventBean<V>> speedConstraintLongValueFactory,
                      ConstraintFactory<EventBean<V>> speedConstraintLongValueFactory2) {
        this.id = id;
        this.outputStream = new DataStreamImpl<>(id);
        List<EventBean<V>> triplesTemp = new ArrayList<>();
        EventBean<V> triple = new SimpleEvent<>(s, timestamp);
        triplesTemp.add(triple);
        events.putIfAbsent(win.iri(), triplesTemp);
        if (win != null && stream != null) {
            windowMap.put(win, stream);
        }
        this.r2s = r2s;
        this.time = time;
        this.projections = new ArrayList<>();
        this.speedConstraintLongValueFactory = speedConstraintLongValueFactory;
        this.speedConstraintLongValueFactory2 = speedConstraintLongValueFactory2;
    }

//    public EventQuery(String id, DataStream<EventBean<V>> stream, Time time, WindowNode win, Map<String, List<DataStream<EventBean<V>>>> triplePatterns, RelationToStreamOperator<Binding, O> r2s) {
//        this.id = id;
//        this.outputStream = new DataStreamImpl<O>(id);
//        this.events = triplePatterns;
//
//        if (win != null && stream != null) {
//            windowMap.put(win, stream);
//        }
//        this.r2s = r2s;
//        this.time = time;
//        this.projections = new ArrayList<>();
//
//    }
//
//    public EventQuery(String id, DataStream<EventBean<V>> stream, Time time, WindowNode win, Map<String, List<EventBean<V>>> triplePatterns, RelationToStreamOperator<Binding, O> r2s, String defaultGraphIRI) {
//        this(id, stream, time, win, triplePatterns, r2s);
//        //load default graph
//        this.defaultGraph = new DataSetImpl("default", defaultGraphIRI, RDFBase.NT);
//
//    }

    public EventQuery(String id) {
        this.id = id;
    }

    public EventQuery(String id, RelationToRelationOperator<Relation<EventBean<V>>, AnnotatedRelation<EventBean<V>>> consistencyAnnotator) {
        this.consistencyAnnotator = consistencyAnnotator;
    }

    public void addFiltersIfDefined(Map<String, List<Predicate<Binding>>> windowsToFilters) {
        this.windowsToFilters = windowsToFilters;
    }

    @Override
    public void addNamedWindow(String streamUri, WindowNode wo) {
        windowMap.put(wo, new DataStreamImpl<>(streamUri));
    }

    @Override
    public void setIstream() {
        streamOperator = StreamOperator.ISTREAM;
    }

    @Override
    public void setRstream() {
        streamOperator = StreamOperator.RSTREAM;
    }

    @Override
    public void setDstream() {
        streamOperator = StreamOperator.DSTREAM;
    }

    @Override
    public boolean isIstream() {
        return streamOperator.equals(StreamOperator.ISTREAM);
    }

    @Override
    public boolean isRstream() {
        return streamOperator.equals(StreamOperator.RSTREAM);
    }

    @Override
    public boolean isDstream() {
        return streamOperator.equals(StreamOperator.DSTREAM);
    }

    @Override
    public void setSelect() {

    }

    @Override
    public void setConstruct() {

    }

    @Override
    public boolean isSelectType() {
        return false;
    }

    @Override
    public boolean isConstructType() {
        return false;
    }

    @Override
    public DataStream<ConsistencyAnnotatedRecord<EventBean<V>>> getOutputStream() {
        return outputStream;
    }

    @Override
    public void setOutputStream(String uri) {
        this.outputStream = new DataStreamImpl<>(uri);
    }

    @Override
    public String getID() {
        return id;
    }


    @Override
    public Map<WindowNode, DataStream<EventBean<V>>> getWindowMap() {
        return windowMap;
    }


    @Override
    public Time getTime() {
        return this.time;
    }

    @Override
    public RelationToRelationOperator<Relation<EventBean<V>>, AnnotatedRelation<EventBean<V>>> r2r() {
//        Map<String, RelationToRelationOperator<Graph, Binding>> r2rs = new LinkedHashMap<>();
//        for (Map.Entry<String, List<EventBean<V>>> entry : events.entrySet()) {
//            if (!entry.getValue().isEmpty()) {
        if (consistencyAnnotator == null){
            if (speedConstraintLongValueFactory2==null)
                consistencyAnnotator = new R2RConsistencyAnnotator<>(5, 2, speedConstraintLongValueFactory);
            else consistencyAnnotator = new R2RDoubleConsistencyAnnotator<>(5, 2, speedConstraintLongValueFactory, speedConstraintLongValueFactory2);
        }

//                RelationToRelationOperator<EventBean<V>, Relation<EventBean<V>>> filteredBgp = addFiltersIfDefined(entry.getKey(), bgp);
//                r2rs.put(entry.getKey(), filteredBgp);
//            } else if (windowsToFilters.containsKey(entry.getKey())) {
//                r2rs.put(entry.getKey(), createFilter(entry.getKey()));
//            }
//        }


        return  consistencyAnnotator;//MultipleGraphR2R(r2rs);

    }



    private RelationToRelationOperator<Graph, Binding> createFilter(String graph) {
        return addFiltersIfDefined(graph, null);
    }

    private RelationToRelationOperator<Graph, Binding> addFiltersIfDefined(String graph, RelationToRelationOperator<Graph, Binding> bgp) {
        if (windowsToFilters.containsKey(graph)) {
            List<RelationToRelationOperator> r2rList = windowsToFilters.get(graph).stream().map(p -> new Filter(Stream.empty(), p)).collect(Collectors.toList());
            if (bgp != null) {
                r2rList.add(0, bgp); // add the bgp pattern as first
            }
            R2RPipe<Graph, Binding> pipe = new R2RPipe(r2rList.toArray(new RelationToRelationOperator[0]));
            return pipe;
        } else {
            return bgp;
        }
    }

//    private RelationToRelationOperator<Graph, Binding> createR2R(List<EventBean<V>> triples) {
//        if (triples.size() == 1) {
//            return createTP(triples.get(0));
//        } else {
//            return createBGP(triples);
//        }
//    }

//    private BGP createBGP(List<EventBean<V>> bgpTriples) {
//        EventBean<V> triple = bgpTriples.get(0);
//        BGP bgp = BGP.createFrom(new TP(triple.s, triple.p, triple.o));
//        for (int i = 1; i < bgpTriples.size(); i++) {
//            bgp.addTP(new TP(bgpTriples.get(i).s, bgpTriples.get(i).p, bgpTriples.get(i).o));
//        }
//        return bgp.build();
//    }
//
//    private TP createTP(EventBean<V> singleTP) {
//        return new TP(singleTP.s, singleTP.p, singleTP.o);
//    }

    @Override
    public StreamToRelationOp<EventBean<V>, Relation<EventBean<V>>>[] s2r() {
        return new StreamToRelationOp[0];
    }

    @Override
    public RelationToStreamOperator<AnnotatedRelation<EventBean<V>>, ConsistencyAnnotatedRecord<EventBean<V>>> r2s() {
        return r2s;
    }

    @Override
    public List<Aggregation> getAggregations() {
        return aggregations;
    }
}
