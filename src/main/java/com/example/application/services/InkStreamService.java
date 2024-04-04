package com.example.application.services;

import com.example.application.inkstream.EventQuery;
import com.example.application.inkstream.EventStream;
import com.example.application.inkstream.YasperEvent;
import com.example.application.inkstream.annotation.*;
import com.example.application.inkstream.cgraph.ConsistencyGraph;
import com.example.application.inkstream.constraint.SpeedConstraintLongValueAttributeFactory;
import com.example.application.inkstream.record.ConsistencyAnnotatedRecord;
import com.example.application.inkstream.record.EventBean;
import org.apache.commons.configuration.ConfigurationException;
import org.springframework.stereotype.Service;
import org.streamreasoning.rsp4j.api.engine.config.EngineConfiguration;
import org.streamreasoning.rsp4j.api.querying.ContinuousQueryExecution;
import org.streamreasoning.rsp4j.api.secret.time.Time;
import org.streamreasoning.rsp4j.yasper.querying.operators.windowing.WindowNodeImpl;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class InkStreamService {

    private final EngineConfiguration ec;
    private YasperEvent<Long> yasperEvent;
    private final AtomicInteger eventCounter = new AtomicInteger(1);
    private ContinuousQueryExecution<EventBean<Long>, Relation<EventBean<Long>>, AnnotatedRelation<EventBean<Long>>, ConsistencyAnnotatedRecord<EventBean<Long>>> cqe;
    private ConsistencyGraph<Long> consistencyGraph;
    private List<ConsistencyAnnotatedRecord<EventBean<Long>>> outputStream;
    private EventStream<Long> eventStream;
    private EventQuery<Long> id;
    private R2RDoubleConsistencyAnnotator<Long> longR2RConsistencyAnnotator;
    private boolean registered = false;

    public InkStreamService() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ConfigurationException {
        //Load engine configuration from yasper/target/classes/csparql.properties
        ec = new EngineConfiguration("/Users/samuelelanghi/Documents/projects/inkstreamui/src/main/resources/default.properties");
        outputStream = new LinkedList<>();
    }

    public List<ConsistencyGraph<EventBean<Long>>> getConsistencyGraphs(){
        return longR2RConsistencyAnnotator.getCurrentGraphs();
    }

    public ContinuousQueryExecution<EventBean<Long>, Relation<EventBean<Long>>, AnnotatedRelation<EventBean<Long>>, ConsistencyAnnotatedRecord<EventBean<Long>>> register(String query) throws ConfigurationException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        registered = true;

        YasperEvent<Long> sr = new YasperEvent<>(ec);

        Time time = sr.time();

        //STREAM DECLARATION
        eventStream = new EventStream<>("sgabello");


        sr.register(eventStream);




//        RSPQL<Binding> q = new SimpleRSPQLQuery<>("q1", stream, time, new WindowNodeImpl("w1", 2, 2, 0), s, pp, o, r2s);

        id = new EventQuery<>("id", eventStream, time, new WindowNodeImpl("w1", 4, 2, 0), 5L, 0, new R2SAnnotatedRecord<>(), new SpeedConstraintLongValueAttributeFactory(2, -2, "consA", "SC1"), new SpeedConstraintLongValueAttributeFactory(2, -2, "consB", "SC2"));
        longR2RConsistencyAnnotator = (R2RDoubleConsistencyAnnotator<Long>) id.r2r();

        ContinuousQueryExecution<EventBean<Long>, Relation<EventBean<Long>>, AnnotatedRelation<EventBean<Long>>, ConsistencyAnnotatedRecord<EventBean<Long>>> cqe = sr.register(id);


//        longR2RConsistencyAnnotator = new R2RConsistencyAnnotator<>(5, 2, new SpeedConstraintLongValueFactory(0.5, 0.5));
//        ContinuousQueryExecution<EventBean<Long>, Relation<EventBean<Long>>, AnnotatedRelation<EventBean<Long>>, ConsistencyAnnotatedRecord<EventBean<Long>>> cqe = yasperEvent.register(id);



//        EventBean<Long> event1 = new SimpleEvent(34, 1);
//        EventBean<Long> event2 = new SimpleEvent(34, 2);
//        EventBean<Long> event3 = new SimpleEvent(34, 3);
//        EventBean<Long> event4 = new SimpleEvent(34, 4);
//        EventBean<Long> event5 = new SimpleEvent(34, 5);
//        EventBean<Long> event6 = new SimpleEvent(34, 6);
//
//
//        eventStream.put(event1, 1);
//        eventStream.put(event2, 2);
//        eventStream.put(event3, 3);
//        eventStream.put(event4, 4);
//        eventStream.put(event5, 5);
//        eventStream.put(event6, 6);

        cqe.outstream().addConsumer((ConsistencyAnnotatedRecord<EventBean<Long>> arg, long ts) -> {
            outputStream.add(0, arg);
        });

        return cqe;
    }

    public ConsistencyGraph<EventBean<Long>> getCurrentGraph() {
        return longR2RConsistencyAnnotator.getCurrentGraph();
    }

    public boolean isRegistered() {
        return registered;
    }

    public void nextEvent(EventBean<Long> eventBean) {
        eventStream.put(eventBean, eventBean.getTime());
    }


    public List<ConsistencyAnnotatedRecord<EventBean<Long>>> getNextOutput() {
        return outputStream;
    }
}
