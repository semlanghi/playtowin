package com.example.application.inkstream;

import com.example.application.inkstream.annotation.AnnotatedRelation;
import com.example.application.inkstream.annotation.ContentRelationFactory;
import com.example.application.inkstream.annotation.Relation;
import com.example.application.inkstream.annotation.SDSEventImpl;
import com.example.application.inkstream.record.ConsistencyAnnotatedRecord;
import com.example.application.inkstream.record.EventBean;
import org.apache.commons.rdf.api.Graph;
import org.streamreasoning.rsp4j.api.operators.s2r.execution.assigner.StreamToRelationOp;
import org.streamreasoning.rsp4j.api.secret.time.TimeImpl;
import org.streamreasoning.rsp4j.api.stream.data.DataStream;
import org.streamreasoning.rsp4j.yasper.content.BindingContentFactory;
import org.streamreasoning.rsp4j.yasper.content.GraphContentFactory;
import org.apache.commons.rdf.api.IRI;
import org.streamreasoning.rsp4j.api.RDFUtils;
import org.streamreasoning.rsp4j.api.engine.config.EngineConfiguration;
import org.streamreasoning.rsp4j.api.engine.features.QueryRegistrationFeature;
import org.streamreasoning.rsp4j.api.engine.features.StreamRegistrationFeature;
import org.streamreasoning.rsp4j.api.enums.ReportGrain;
import org.streamreasoning.rsp4j.api.enums.Tick;
import org.streamreasoning.rsp4j.api.format.QueryResultFormatter;
import org.streamreasoning.rsp4j.api.operators.s2r.StreamToRelationOperatorFactory;
import org.streamreasoning.rsp4j.api.operators.s2r.syntax.WindowNode;
import org.streamreasoning.rsp4j.api.querying.ContinuousQuery;
import org.streamreasoning.rsp4j.api.querying.ContinuousQueryExecution;
import org.streamreasoning.rsp4j.api.querying.result.SolutionMapping;
import org.streamreasoning.rsp4j.api.sds.SDS;
import org.streamreasoning.rsp4j.api.sds.timevarying.TimeVarying;
import org.streamreasoning.rsp4j.api.secret.content.ContentFactory;
import org.streamreasoning.rsp4j.api.secret.report.Report;
import org.streamreasoning.rsp4j.api.secret.time.Time;
import org.streamreasoning.rsp4j.yasper.ContinuousQueryExecutionImpl;
import org.streamreasoning.rsp4j.yasper.querying.operators.Rstream;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;


public class YasperEvent<V> implements QueryRegistrationFeature<ContinuousQuery>, StreamRegistrationFeature<EventStream<V>, EventStream<V>> {

    private final long t0;
    private final String baseUri;
    private final String S2RFactory = "yasper.window_operator_factory";
    private final String contentFactoryConfig = "yasper.content_factory";
    private final String windowOperatorFactory;
    private final Time time;
    private final StreamToRelationOperatorFactory<EventBean<V>, Relation<EventBean<V>>> wf;
    private Report report;
    private Tick tick;
    protected EngineConfiguration rsp_config;
    protected Map<String, SDS> assignedSDS;
    protected Map<String, ContinuousQueryExecution> queryExecutions;
    protected Map<String, ContinuousQuery> registeredQueries;
    protected Map<String, List<QueryResultFormatter>> queryObservers;
    protected Map<String, EventStream<V>> registeredStreams;
    private ReportGrain report_grain;
    private Function<Collection<EventBean<V>>, SolutionMapping<EventBean<V>>> agg;
    private ContentFactory<EventBean<V>, Relation<EventBean<V>>> cf;



    public YasperEvent(EngineConfiguration rsp_config) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.rsp_config = rsp_config;
        this.report = rsp_config.getReport();
        this.baseUri = rsp_config.getBaseIRI();
        this.report_grain = rsp_config.getReportGrain();
        this.tick = rsp_config.getTick();
        this.t0 = rsp_config.gett0();
        this.windowOperatorFactory = rsp_config.getString(S2RFactory);
        this.assignedSDS = new HashMap<>();
        this.registeredQueries = new HashMap<>();
        this.registeredStreams = new HashMap<>();
        this.queryObservers = new HashMap<>();
        this.queryExecutions = new HashMap<>();
        this.time = new TimeImpl(0);
        this.cf = new ContentRelationFactory<>(time);
        this.wf = new CQELSTimeWindowOperatorFactoryGeneric<>(
                        time,
                        tick,
                        report,
                        report_grain,
                        cf);
    }

    public Time time() {
        return time;
    }

    @Override
    public ContinuousQueryExecution<Object, Relation<EventBean<Long>>, AnnotatedRelation<EventBean<Long>>, EventBean<V>> register(ContinuousQuery q) {
        return null;
    }

    public ContinuousQueryExecution<EventBean<V>, Relation<EventBean<V>>, AnnotatedRelation<EventBean<V>>, ConsistencyAnnotatedRecord<EventBean<V>>> register(EventQuery<V> id) {

        SDS<Relation<EventBean<V>>> sds = new SDSEventImpl<>();

        DataStream<ConsistencyAnnotatedRecord<EventBean<V>>> out = new AnnotatedEventStream<>(id.getID());

        ContinuousQueryExecutionImpl<EventBean<V>, Relation<EventBean<V>>, AnnotatedRelation<EventBean<V>>, ConsistencyAnnotatedRecord<EventBean<V>>> cqe = new ContinuousQueryExecutionImpl<>(sds, id, out, id.r2r(), id.r2s());

        id.getWindowMap().forEach((WindowNode wo, DataStream<EventBean<V>> s) -> {

            IRI iri = RDFUtils.createIRI(wo.iri());

            StreamToRelationOp<EventBean<V>, Relation<EventBean<V>>> build = wf.build(4, 2, 0);
            StreamToRelationOp<EventBean<V>, Relation<EventBean<V>>> wop = build.link(cqe);


            TimeVarying<Relation<EventBean<V>>> tvg = wop.apply(registeredStreams.get(s.getName()));

            if (wo.named()) {
                sds.add(iri, tvg);
            } else {
                sds.add(tvg);
            }

        });
        return cqe;
    }


    @Override
    public EventStream<V> register(EventStream<V> s) {
        registeredStreams.put(s.stream_uri, s);
        return s;
    }

    public EventStream<V> get(String s) {
        return registeredStreams.get(s);
    }

}
