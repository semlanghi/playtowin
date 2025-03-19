package com.example.application.services;

/*import com.example.application.inkstream.EventQuery;
import com.example.application.inkstream.annotation.R2RDoubleConsistencyAnnotator;*/
import com.example.application.polyflow.cgraph.ConsistencyGraph;
import com.example.application.polyflow.datatypes.EventBean;
import com.example.application.polyflow.CustomTask;
import com.example.application.polyflow.content.factories.AccumulatorFactory;
import com.example.application.polyflow.datatypes.GridInputWindowed;
import com.example.application.polyflow.datatypes.GridInputWindowed;
import com.example.application.polyflow.operators.R2RCustom;
import com.example.application.polyflow.operators.R2SCustom;
import com.example.application.polyflow.operators.S2RHopping;
import com.example.application.polyflow.reportingStrategies.Always;
import com.example.application.polyflow.stream.DataStreamImpl;
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
import org.streamreasoning.polyflow.base.operatorsimpl.dag.DAGImpl;
import org.streamreasoning.polyflow.base.processing.ContinuousProgramImpl;
import org.streamreasoning.polyflow.base.sds.SDSDefault;


import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

        public List<ConsistencyGraph<EventBean<Long>>> getConsistencyGraphs(){
            return null;
            //return longR2RConsistencyAnnotator.getCurrentGraphs();
        }

        public ContinuousProgram<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>, GridInputWindowed> register(String query) throws ConfigurationException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

            registered = true;

            DataStream<GridInputWindowed> inputStream = new DataStreamImpl<>("inputStream");
            DataStream<GridInputWindowed> outputStream = new DataStreamImpl<>("outputStream");
            this.eventStream = inputStream;

            Time instance = new TimeImpl(0);
            Report report = new ReportImpl();
            report.add(new Always());

            ContentFactory<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> contentFactory = new AccumulatorFactory();
            StreamToRelationOperator<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> s2r_1 = new S2RHopping(
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
                    1);

            R2RCustom r2r = new R2RCustom(List.of("TW1", "TW2"), "result");
            ContinuousProgram<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>, GridInputWindowed> cp = new ContinuousProgramImpl<>();
            Task<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>, GridInputWindowed> task = new CustomTask<>("1");
            RelationToStreamOperator<List<GridInputWindowed>, GridInputWindowed> r2sOp = new R2SCustom();
            task = task
                    .addS2ROperator(s2r_1, inputStream)
                    .addS2ROperator(s2r_2, inputStream)
                    .addR2ROperator(r2r)
                    .addR2SOperator(r2sOp)
                    .addSDS(new SDSDefault<>())
                    .addDAG(new DAGImpl<>())
                    .addTime(instance);
            task.initialize();

            //empty consumer
            outputStream.addConsumer((stream, el, ts)->{out.add(0, el);});

            cp.buildTask(task, List.of(inputStream), List.of(outputStream));

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
            eventStream.put(row, row.getTimestamp());;
        }


        public List<GridInputWindowed> getNextOutput() {
            LinkedList<GridInputWindowed> res = new LinkedList<>();
            res.addAll(out);
            out = new LinkedList<>();
            return res;
        }

}
