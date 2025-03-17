package com.example.application.polyflow;

import com.example.application.polyflow.content.factories.AccumulatorFactory;
import com.example.application.polyflow.datatypes.CustomRow;
import com.example.application.polyflow.datatypes.GridInputWindowed;
import com.example.application.polyflow.datatypes.GridInputWindowed;
import com.example.application.polyflow.datatypes.GridInputWindowed;
import com.example.application.polyflow.operators.R2RCustom;
import com.example.application.polyflow.operators.R2SCustom;
import com.example.application.polyflow.operators.S2RHopping;
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
import com.example.application.polyflow.reportingStrategies.Always;
import com.example.application.polyflow.stream.DataStreamImpl;

import java.util.List;

public class Main {

    public static void main(String [] args){

        DataStream<GridInputWindowed> inputStream = new DataStreamImpl<>("inputStream");
        DataStream<GridInputWindowed> outputStream = new DataStreamImpl<>("outputStream");

        Time instance = new TimeImpl(0);
        Report report = new ReportImpl();
        report.add(new Always());

        ContentFactory<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> contentFactory = new AccumulatorFactory();
        StreamToRelationOperator<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> s2r_1 = new S2RHopping(
                Tick.TIME_DRIVEN,
                instance,
                "hopping_1",
                contentFactory,
                report,
                1000,
                1000);

        StreamToRelationOperator<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> s2r_2 = new S2RHopping(
                Tick.TIME_DRIVEN,
                instance,
                "hopping_2",
                contentFactory,
                report,
                1000,
                1000);

        R2RCustom r2r = new R2RCustom(List.of("hopping_1", "hopping_2"), "result");
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
        outputStream.addConsumer((out, el, ts) -> {System.out.println(el + " @ "+ts);});

        cp.buildTask(task, List.of(inputStream), List.of(outputStream));



    }
}
