package com.example.application.polyflow.operators;

import com.example.application.polyflow.datatypes.Tuple;
import com.example.application.polyflow.datatypes.TuplesOrResult;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.streamreasoning.polyflow.api.enums.Tick;
import org.streamreasoning.polyflow.api.exceptions.OutOfOrderElementException;
import org.streamreasoning.polyflow.api.operators.s2r.execution.assigner.StreamToRelationOperator;
import org.streamreasoning.polyflow.api.operators.s2r.execution.instance.Window;
import org.streamreasoning.polyflow.api.operators.s2r.execution.instance.WindowImpl;
import org.streamreasoning.polyflow.api.sds.timevarying.TimeVarying;
import org.streamreasoning.polyflow.api.secret.content.Content;
import org.streamreasoning.polyflow.api.secret.content.ContentFactory;
import org.streamreasoning.polyflow.api.secret.report.Report;
import org.streamreasoning.polyflow.api.secret.tick.Ticker;
import org.streamreasoning.polyflow.api.secret.tick.secret.TickerFactory;
import org.streamreasoning.polyflow.api.secret.time.Time;
import org.streamreasoning.polyflow.api.secret.time.TimeInstant;
import org.streamreasoning.polyflow.base.sds.TimeVaryingObject;

import java.util.*;
import java.util.stream.Collectors;

public class AggregateFrame implements StreamToRelationOperator<Tuple, Tuple, TuplesOrResult> {


    private static final Logger log = Logger.getLogger(org.streamreasoning.polyflow.base.operatorsimpl.s2r.HoppingWindowOpImpl.class);
    protected final Ticker ticker;
    protected Tick tick;
    protected final Time time;
    protected final String name;
    protected final ContentFactory<Tuple, Tuple, TuplesOrResult> cf;
    protected TimeVaryingTuplesOrResult tvg;
    protected Report report;
    private String attributeForComputation;
    private Comparator<Double>  comparator;
    // context
    private Context context;

    // 0 threshold
    // 1 delta
    // 2 aggregate
    // 3 session
    private int frame_type;

    // parameter to compute frame (threshold, delta, aggregate threshold, session gap)
    private int frame_parameter;

    // aggregation function for the aggregate frame (1 sum, 0 avg)
    private int aggregation_function;

    private Window active_window;
    private Content<Tuple, Tuple, TuplesOrResult> active_content;
    //already closed windows
    private Map<Window, Content<Tuple, Tuple, TuplesOrResult>> expired_content;

    //elements that do not belong to ANY window
    private Content<Tuple, Tuple, TuplesOrResult> throw_content;

    public AggregateFrame(Tick tick, Time time, String name, ContentFactory<Tuple, Tuple, TuplesOrResult> cf, Report report,
                          int frame_type, int frame_parameter, int aggregation_function, String attributeForComputation,
                          Comparator<Double> comparator) {

        this.tick = tick;
        this.time = time;
        this.name = name;
        this.cf = cf;
        this.report = report;
        this.expired_content = new HashMap<>();
        this.ticker = TickerFactory.tick(tick, this);
        Logger.getRootLogger().setLevel(Level.OFF);
        this.frame_type = frame_type;
        this.aggregation_function = aggregation_function;
        this.frame_parameter = frame_parameter;
        this.context = new Context(-1, 0, false, 0);

        this.active_content = cf.createEmpty();
        this.active_window = new WindowImpl(0, 0);
        this.throw_content = cf.create();
        this.attributeForComputation = attributeForComputation;
        this.comparator = comparator;
    }


    @Override
    public Report report() {
        return this.report;
    }

    @Override
    public Tick tick() {
        return this.tick;
    }

    @Override
    public Time time() {
        return this.time;
    }

    @Override
    public Content<Tuple, Tuple, TuplesOrResult> content(long l) {
        if(expired_content.keySet().isEmpty())
            return cf.createEmpty();
        return expired_content.get(expired_content.keySet().stream().max((w1, w2)->{
            if(w1.getC()>w2.getC())
                return 1;
            else return -1;
        }).get());
    }

    @Override
    public List<Content<Tuple, Tuple, TuplesOrResult>> getContents(long l) {

        List<Content<Tuple, Tuple, TuplesOrResult>> res = new ArrayList<>();
        expired_content.keySet().stream()
                .sorted(Comparator.comparingLong(Window::getC))
                .forEach(w -> res.add(expired_content.get(w)));
        res.add(active_content);
        res.add(throw_content);
        return res;
    }

    @Override
    public TimeVarying<TuplesOrResult> get() {
        if(tvg != null)
            return tvg;

        return new TimeVaryingTuplesOrResult(this, this.name);
    }

    @Override
    public String getName() {
        return this.name;
    }


    double avg(List<Tuple> content, long size) {
        double sum = 0;
        //We enforce that the field must be a double to make our life easier
        for(int i =0; i<size; i++){
            sum+=content.get(i).getAttributeForComputation(attributeForComputation);
        }
        return sum /size;
    }
    double sum(List<Tuple> content, long size){
        double sum = 0;
        for(int i =0; i<size; i++){
            sum+=content.get(i).getAttributeForComputation(attributeForComputation);
        }
        return sum;
    }

    double aggregation(int agg_function, List<Tuple> content, long size){
        switch (agg_function) {
            case 0: return avg(content, size);
            case 1: return sum(content, size);
            default: throw new RuntimeException("aggregation not valid");
        }
    }

    // Open a new frame and insert the current processed tuple.
    private void open(Tuple arg, long ts) {
        //enqueue(buffer, tuple);
        switch (frame_type) {
            case 0:
                context.count++;
            case 1:
                context.start = true;
                context.v = arg.getAttributeForComputation(attributeForComputation);
            case 2:
                context.v = arg.getAttributeForComputation(attributeForComputation); // aggregation function on a single value corresponds to that value
                context.start = true;
            case 3:
                context.current_timestamp = ts;
                context.start = true;
        }
    }

// Update the current frame, extends it to include the current processed tuple.
    private void update(Tuple arg, long ts) {
        //enqueue(buffer, tuple);
        switch (frame_type) {
            case 0:
                context.count++;
            case 1:
            case 2:
                context.v = aggregation(aggregation_function, active_content.coalesce().getWindowContent(), active_content.size());
            case 3:
                context.current_timestamp = ts;
        }
    }

// Close the current frame and check for global conditions to eventually evict.
// Removes the evicted/discarded frame from the buffer.
    private void close(Tuple arg, long ts) {
        switch (frame_type) {
            case 0:
                context.count = 0;
            case 1:
                context.start = false;
            case 2:
                context.start = false;
            case 3:
                context.start = false;
        }
    }

    boolean open_pred(Tuple arg, long ts) {
        double field = 0;
        if (frame_type != 3) field = arg.getAttributeForComputation(attributeForComputation);
        switch (frame_type) {
            case 0: return (comparator.compare(field, (double) frame_parameter) == 1 && context.count == 0); // threshold
            case 1: return (!(context.start)); // delta
            case 2: return (!(context.start)); // aggregate
            case 3: return (ts - context.current_timestamp <= frame_parameter && !(context.start)); // session
        }
        throw new RuntimeException("error switching on frame type in window operator");
    }

    boolean update_pred(Tuple arg, long ts) {
        double field = 0;
        if (frame_type != 3) field = arg.getAttributeForComputation(attributeForComputation);
        switch (frame_type) {

            case 0: return (comparator.compare(field, (double) frame_parameter) == 1 && context.count > 0);
            case 1: return (comparator.compare(Math.abs(context.v - field), (double) frame_parameter) == 1 && context.start);
            case 2: return (comparator.compare(context.v, (double) frame_parameter) == 1 && context.start);
            case 3: return (ts - context.current_timestamp <= frame_parameter && context.start);
        }
        throw new RuntimeException("error switching on frame type in window operator");

    }

    //Frame closes if the comparator returns -1 ... based on the desired logic, define an appropriate comparator
    boolean close_pred(Tuple arg, long ts) {
        double field = 0;
        if (frame_type != 3) field = arg.getAttributeForComputation(attributeForComputation);
        switch (frame_type) {
            case 0: return (comparator.compare(field, (double) frame_parameter) == -1 && context.count > 0);
            case 1: return (comparator.compare(Math.abs(context.v - field), (double) frame_parameter) == -1  && context.start);
            case 2: return (comparator.compare(context.v, (double) frame_parameter) == -1  && context.start);
            case 3: return (ts - context.current_timestamp > frame_parameter && context.start);
        }
        throw new RuntimeException("error switching on frame type in window operator");
    }

    @Override
    public void compute(Tuple arg, long ts) {

        if (time.getAppTime() > ts) {
            log.error("OUT OF ORDER NOT HANDLED");
            throw new OutOfOrderElementException("(" + arg + "," + ts + ")");
        }

        boolean added = false;
        if (close_pred(arg, ts)) {
            close(arg, ts);
            context.current_timestamp = ts;
            // current window close

            active_window.setC(ts);
            expired_content.put(active_window, active_content);
            //If threshold frame, the element must not be added to the next window, so we need to evict the current one
            if(frame_type == 0){
                active_window = null;
                active_content = cf.createEmpty();
            }
            //Report when the frame closes
            time.addEvaluationTimeInstants(new TimeInstant(ts));

        }

        if (update_pred(arg, ts)) {
            added = true;
            Tuple el = arg.copy();
            el.setIntervalId("starts@"+active_window.getO());
            el.setOperatorId(this.name);
            // add element to current window
            active_content.add(el);

            update(arg, ts);

        }
        if (open_pred(arg, ts)){
            added = true;
            open(arg, ts);
            // open new window with current element
            active_window = new WindowImpl(ts, -1);
            active_content = cf.create();
            Tuple el = arg.copy();
            el.setIntervalId("starts@"+active_window.getO());
            el.setOperatorId(this.name);
            active_content.add(el);
        }

        if(added == false) {
            Tuple el = arg.copy();
            el.setIntervalId("throw");
            el.setOperatorId(this.name);
            throw_content.add(el);
        }


        time.setAppTime(ts);
    }

    @Override
    public void evict() {

    }

    @Override
    public void evict(long l) {

    }

    private class Context{
        double v;
        int count;
        boolean start;
        long current_timestamp;

        public Context(double v, int count, boolean start, long current_timestamp){
            this.v = v;
            this.count = count;
            this.start = start;
            this.current_timestamp = current_timestamp;
        }
    }
}
