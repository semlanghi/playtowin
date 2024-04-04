package com.example.application.inkstream;

import com.example.application.inkstream.record.EventBean;
import com.example.application.inkstream.record.SimpleEvent;
import org.apache.commons.configuration.ConfigurationException;
import org.streamreasoning.rsp4j.api.engine.config.EngineConfiguration;
import org.streamreasoning.rsp4j.api.querying.ContinuousQueryExecution;

import java.lang.reflect.InvocationTargetException;

public class InkStreamExample {

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ConfigurationException {


        //Load engine configuration from yasper/target/classes/csparql.properties
        EngineConfiguration ec = EngineConfiguration.loadConfig("/default.properties");

        // Create new Yasper Engine
        YasperEvent yasperEvent = new YasperEvent<>(ec);

        ContinuousQueryExecution cqe = yasperEvent.register(new EventQuery("id"));

        EventStream<Long> eventStream = new EventStream<>("sgabello");


        EventBean<Long> event1 = new SimpleEvent(34, 1);
        EventBean<Long> event2 = new SimpleEvent(34, 2);
        EventBean<Long> event3 = new SimpleEvent(34, 3);
        EventBean<Long> event4 = new SimpleEvent(34, 4);
        EventBean<Long> event5 = new SimpleEvent(34, 5);
        EventBean<Long> event6 = new SimpleEvent(34, 6);


        eventStream.put(event1, 1);
        eventStream.put(event2, 2);
        eventStream.put(event3, 3);
        eventStream.put(event4, 4);
        eventStream.put(event5, 5);
        eventStream.put(event6, 6);

//        cqe.
//
//        //add Consumer to the outstream that outputs the timestamp, key and value for each update of the output stream
//        AtomicLong consideredTime = new AtomicLong();
//        cqe.outstream().addConsumer((Object arg, long ts) ->
//                {
//                    // pretty console log
//                    if (consideredTime.get() != ts) {
//                        System.out.println("---------------------");
//                        consideredTime.set(ts);
//                    }
//
//
//                    System.out.println(arg);
//                    arg1.forEach((k, v) -> System.out.println(ts + " ---> (" + k + "," + v + ")"));

//                }
//        );

    }
}
