package com.example.application.services;

/*import com.example.application.inkstream.EventQuery;
import com.example.application.inkstream.annotation.R2RDoubleConsistencyAnnotator;*/

import com.example.application.polyflow.CustomTask;
import com.example.application.polyflow.cgraph.ConsistencyGraph;
import com.example.application.polyflow.content.factories.AccumulatorFactory;
import com.example.application.polyflow.datatypes.EventBean;
import com.example.application.polyflow.datatypes.GridInputWindowed;
import com.example.application.polyflow.operators.*;
import com.example.application.polyflow.reportingStrategies.Always;
import com.example.application.polyflow.stream.DataStreamImpl;
import com.example.application.views.myview.PlayToWin;
import dev.mccue.josql.Query;
import dev.mccue.josql.QueryParseException;
import org.apache.commons.configuration.ConfigurationException;
import org.springframework.stereotype.Service;
import org.streamreasoning.polyflow.api.enums.Tick;
import org.streamreasoning.polyflow.api.operators.r2s.RelationToStreamOperator;
import org.streamreasoning.polyflow.api.operators.s2r.execution.assigner.StreamToRelationOperator;
import org.streamreasoning.polyflow.api.processing.ContinuousProgram;
import org.streamreasoning.polyflow.api.processing.Task;
import org.streamreasoning.polyflow.api.secret.content.ContentFactory;
import org.streamreasoning.polyflow.api.secret.report.Report;
import org.streamreasoning.polyflow.api.secret.report.ReportImpl;
import org.streamreasoning.polyflow.api.secret.time.Time;
import org.streamreasoning.polyflow.api.secret.time.TimeImpl;
import org.streamreasoning.polyflow.api.stream.data.DataStream;
import org.streamreasoning.polyflow.base.processing.ContinuousProgramImpl;
import org.streamreasoning.polyflow.base.sds.SDSDefault;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PolyflowService {
    //private final EngineConfiguration ec;
    private final AtomicInteger eventCounter = new AtomicInteger(1);
    private ContinuousProgram<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>, GridInputWindowed> cp;
    private ConsistencyGraph<Long> consistencyGraph;
    private List<GridInputWindowed> out;
    private DataStream<GridInputWindowed> eventStream;
    //private EventQuery<Long> id;
    //private R2RDoubleConsistencyAnnotator<Long> longR2RConsistencyAnnotator;
    private boolean registered = false;

    public PolyflowService() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ConfigurationException {
        //Load engine configuration from yasper/target/classes/csparql.properties
        //ec = new EngineConfiguration("/home/ale/University/playtowin/src/main/resources/default.properties");
        out = new LinkedList<>();
    }

    public List<ConsistencyGraph<EventBean<Long>>> getConsistencyGraphs() {
        return null;
        //return longR2RConsistencyAnnotator.getCurrentGraphs();
    }

    public ContinuousProgram<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>, GridInputWindowed> register(String scenario, String query, List<PlayToWin.WindowRowSummary> windowRowSummaries) throws ConfigurationException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        registered = true;


        DataStream<GridInputWindowed> inputStream = new DataStreamImpl<>("inputStream");
        DataStream<GridInputWindowed> outputStream = new DataStreamImpl<>("outputStream");
        this.eventStream = inputStream;

        Time instance = new TimeImpl(0);
        Report report = new ReportImpl();
        report.add(new Always());

        ContentFactory<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> contentFactory = new AccumulatorFactory();

        List<StreamToRelationOperator<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>>> streamToRelationOperatorList = getStreamToRelationOperators(windowRowSummaries, instance, contentFactory, report);
            /*StreamToRelationOperator<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> s2r_1 = new S2RHopping(
                    Tick.TIME_DRIVEN,
                    instance,
                    "TW1",
                    contentFactory,
                    report,
                    3,
                    1);

            StreamToRelationOperator<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> s2r_2 = new S2RHopping(
                    Tick.TIME_DRIVEN,
                    instance,
                    "TW2",
                    contentFactory,
                    report,
                    3,
                    1);*/
//            StreamToRelationOperator<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> s2r_1 = new AggregateFrame(
//                    Tick.TIME_DRIVEN,
//                    instance,
//                    "TW1",
//                    contentFactory,
//                    report,
//                    3,
//                    2,
//                    1);
//
//            StreamToRelationOperator<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> s2r_2 = new AggregateFrame(
//                    Tick.TIME_DRIVEN,
//                    instance,
//                    "TW2",
//                    contentFactory,
//                    report,
//                    0,
//                    32,
//                    1);


//        try {
//            PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(query, parser -> parser
//                    .withSquareBracketQuotation(true));
//
//        } catch (JSQLParserException e) {
//            throw new RuntimeException(e);
//        }

        Query q = new Query();

        try {
            String replace = query.replace("[window]", "com.example.application.polyflow.datatypes.GridInputWindowed");
            System.out.println(replace);
            q.parse(replace);

            R2RSQLSingle r2r = new R2RSQLSingle(q, streamToRelationOperatorList.stream().map(StreamToRelationOperator::getName).toList(), "result");

            ContinuousProgram<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>, GridInputWindowed> cp = new ContinuousProgramImpl<>();
            Task<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>, GridInputWindowed> task = new CustomTask<>("1");
            RelationToStreamOperator<List<GridInputWindowed>, GridInputWindowed> r2sOp = new R2SCustom();
            for (StreamToRelationOperator<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> tmp : streamToRelationOperatorList) {
                task.addS2ROperator(tmp, inputStream);
            }

            task = task
                    .addR2ROperator(r2r)
                    .addR2SOperator(r2sOp)
                    .addSDS(new SDSDefault<>())
                    .addDAG(new CustomDAG<>())
                    .addTime(instance);
            task.initialize();

            //empty consumer
            outputStream.addConsumer((stream, el, ts) -> {out.add(0, el);});

            cp.buildTask(task, List.of(inputStream), List.of(outputStream));
        } catch (QueryParseException e) {
            throw new RuntimeException(e);
        }
        this.cp = cp;
        return cp;
    }

    public ConsistencyGraph<EventBean<Long>> getCurrentGraph() {
        //return longR2RConsistencyAnnotator.getCurrentGraph();
        return null;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void nextEvent(GridInputWindowed row) {
        eventStream.put(row, row.getTimestamp());
        ;
    }


    public List<GridInputWindowed> getNextOutput() {
        LinkedList<GridInputWindowed> res = new LinkedList<>();
        res.addAll(out);
        out = new LinkedList<>();
        return res;
    }

    public List<StreamToRelationOperator<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>>> getStreamToRelationOperators(List<PlayToWin.WindowRowSummary> windowRowSummaries, Time instance, ContentFactory<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> contentFactory, Report report) {
        return windowRowSummaries.stream().map(new Function<PlayToWin.WindowRowSummary, StreamToRelationOperator<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>>>() {
            @Override
            public StreamToRelationOperator<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> apply(PlayToWin.WindowRowSummary windowRowSummary) {
                if (windowRowSummary.getName().contains("TW")) {
                    return new S2RHopping(
                            Tick.TIME_DRIVEN,
                            instance,
                            windowRowSummary.getName(),
                            contentFactory,
                            report,
                            windowRowSummary.getSize(),
                            windowRowSummary.getSlide());
                } else if (windowRowSummary.getName().contains("F") || windowRowSummary.getName().contains("SW")) {
                    int frameType = getFrameType(windowRowSummary.getName());
                    return new AggregateFrame(
                            Tick.TIME_DRIVEN,
                            instance,
                            windowRowSummary.getName(),
                            contentFactory,
                            report,
                            frameType,
                            frameType == 3 ? (int) windowRowSummary.getTimeout() : (int) windowRowSummary.getRange(),
                            getAggregationFunction(windowRowSummary.getAttribute()));
                } else {
                    //TODO: for the moment, we support only intersection

                    if (windowRowSummary.getName().contains("Union")) {
                        throw new RuntimeException("Support only for intersection");
                    }

                    long size, slide;
                    if (windowRowSummary.getCompositeInternalWindow1().getName().contains("TW")) {
                        size = windowRowSummary.getCompositeInternalWindow1().getSize();
                        slide = windowRowSummary.getCompositeInternalWindow1().getSlide();
                    } else if (windowRowSummary.getCompositeInternalWindow2().getName().contains("TW")) {
                        size = windowRowSummary.getCompositeInternalWindow2().getSize();
                        slide = windowRowSummary.getCompositeInternalWindow2().getSlide();
                    } else {
                        throw new RuntimeException("At least one window should be time-based");
                    }


                    int frameType, timeoutOrRange, attribute;
                    if (windowRowSummary.getCompositeInternalWindow1().getName().contains("F") || windowRowSummary.getCompositeInternalWindow1().getName().contains("SW")) {
                        frameType = getFrameType(windowRowSummary.getCompositeInternalWindow1().getName());
                        timeoutOrRange = frameType == 3 ? (int) windowRowSummary.getCompositeInternalWindow1().getTimeout() : (int) windowRowSummary.getCompositeInternalWindow1().getRange();
                        attribute = getAggregationFunction(windowRowSummary.getCompositeInternalWindow1().getAttribute());
                    } else if (windowRowSummary.getCompositeInternalWindow2().getName().contains("F") || windowRowSummary.getCompositeInternalWindow2().getName().contains("SW")) {
                        frameType = getFrameType(windowRowSummary.getCompositeInternalWindow2().getName());
                        timeoutOrRange = frameType == 3 ? (int) windowRowSummary.getCompositeInternalWindow2().getTimeout() : (int) windowRowSummary.getCompositeInternalWindow2().getRange();
                        attribute = getAggregationFunction(windowRowSummary.getCompositeInternalWindow2().getAttribute());
                    } else {
                        throw new RuntimeException("At least one window should be frame or session");
                    }
                    return new CompositeOperator(
                            Tick.TIME_DRIVEN,
                            instance,
                            windowRowSummary.getName(),
                            contentFactory,
                            report,
                            frameType,
                            timeoutOrRange,
                            attribute,
                            size,
                            slide);
                }
            }
        }).collect(Collectors.toCollection(LinkedList::new));
        //return longR2RConsistencyAnnotator.getStreamToRelationOperators();
    }

    //TODO: right now aggregation works by default on attribute consA, we need to add a way to specify the attribute
    private int getAggregationFunction(String attribute) {
        return switch (attribute.split("\\(")[0]) {
            case "avg" -> 0;
            case "sum" -> 1;
            default -> 1;
        };
    }

    private int getFrameType(String name) {
        if (name.contains("FAgg")) {
            return 2;
        } else if (name.contains("Del")) {
            return 1;
        } else if (name.contains("Thr")) {
            return 0;
        } else if (name.contains("SW")) {
            return 3;
        } else {
            throw new IllegalArgumentException("Unknown frame type: " + name);
        }
    }

}
