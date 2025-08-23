/*
package com.example.application.polyflow.operators;

import com.example.application.polyflow.datatypes.electricity.GridInputWindowed;
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

import java.util.*;

public class CompositeOperator implements StreamToRelationOperator<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> {
    private static final Logger log = Logger.getLogger(org.streamreasoning.polyflow.base.operatorsimpl.s2r.HoppingWindowOpImpl.class);
    protected final Ticker ticker;
    protected Tick tick;
    protected final Time time;
    protected final String name;
    protected final ContentFactory<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> cf;
    protected Report report;

    private long width;
    private long slide;
    private long t0;
    private long toi;
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

    private Window active_frame_window;
    private Content<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> active_frame_content;
    //already closed windows
    private Map<Window, Content<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>>> expired_frame_content;

    private Map<Window, Content<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>>> active_sliding_windows;

    //private List<Window> reported_windows;
    //private Set<Window> to_evict;

    //elements that do not belong to ANY window
    private Content<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> throw_content;

    public CompositeOperator(Tick tick, Time time, String name, ContentFactory<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> cf, Report report,
                          int frame_type, int frame_parameter, int aggregation_function, long width, long slide ) {

        this.tick = tick;
        this.time = time;
        this.name = name;
        this.cf = cf;
        this.report = report;
        this.ticker = TickerFactory.tick(tick, this);
        Logger.getRootLogger().setLevel(Level.OFF);
        this.frame_type = frame_type;
        this.aggregation_function = aggregation_function;
        this.frame_parameter = frame_parameter;
        this.context = new Context(-1, 0, false, 0);
        this.width = width;
        this.slide = slide;
        this.t0 = time.getScope();
        this.toi = 0;

        this.expired_frame_content = new HashMap<>(); //old frames
        this.active_sliding_windows = new HashMap<>(); //sliding windows
        this.active_frame_content = cf.createEmpty(); //active frame content
        this.active_frame_window = new WindowImpl(0, 0); //active frame window
        this.throw_content = cf.create();
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
    public Content<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> content(long l) {
        //TODO: modify when we diversify the window content and the query result in the demo
        return cf.createEmpty();
    }

    @Override
    public List<Content<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>>> getContents(long l) {
        //Return the intersection of the active window and the active frame (the active window can still be open)
        HashMap<String, GridInputWindowed> events = new HashMap<>();
        List<Content<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>>> res = new ArrayList<>();
        Content<GridInputWindowed, GridInputWindowed, List<GridInputWindowed>> intersection = cf.create();

        active_frame_content.coalesce().forEach(e-> events.put(e.getRecord_Id(), e));

        //get the last active window
        Optional<Window> max = active_sliding_windows.keySet().stream()
                .max(Comparator.comparingLong(Window::getC));

        if(max.isPresent()){ //if max is not present, the intersection is empty
            active_sliding_windows.get(max.get()).coalesce().forEach(e->{

                if(events.containsKey(e.getRecord_Id())){
                    GridInputWindowed el = new GridInputWindowed();
                    el.setIntervalId("sliding:"+e.getIntervalId()+" frame: "+events.get(e.getRecord_Id()).getIntervalId());
                    el.setOperatorId(this.name);
                    el.setConsA(e.getConsA());
                    el.setConsB(e.getConsB());
                    el.setRecord_Id(e.getRecord_Id());
                    el.setTimestamp(e.getTimestamp());
                    el.setCursor(e.getCursor());
                    intersection.add(el);
                }
            });
        }

        res.add(intersection);
        return res;
    }

    @Override
    public TimeVarying<List<GridInputWindowed>> get() {
        return new TimeVaryingDemo(this, this.name);
    }

    @Override
    public String getName() {
        return this.name;
    }


    double avg(List<GridInputWindowed> content, long size){
        double sum = 0;
        for(int i =0; i<size; i++){
            sum+=content.get(i).getConsA();
        }
        return sum /size;
    }
    double sum(List<GridInputWindowed> content, long size){
        double sum = 0;
        for(int i =0; i<size; i++){
            sum+=content.get(i).getConsA();
        }
        return sum;
    }

    double aggregation(int agg_function, List<GridInputWindowed> content, long size){
        switch (agg_function) {
            case 0: return avg(content, size);
            case 1: return sum(content, size);
            default: throw new RuntimeException("aggregation not valid");
        }
    }

    // Open a new frame and insert the current processed tuple.
    private void open(GridInputWindowed arg, long ts) {
        //enqueue(buffer, tuple);
        switch (frame_type) {
            case 0:
                context.count++;
            case 1:
                context.start = true;
                context.v = arg.getConsA();
            case 2:
                context.v = arg.getConsA(); // aggregation function on a single value corresponds to that value
                context.start = true;
            case 3:
                context.current_timestamp = ts;
                context.start = true;
        }
    }

    // Update the current frame, extends it to include the current processed tuple.
    private void update(GridInputWindowed arg, long ts) {
        //enqueue(buffer, tuple);
        switch (frame_type) {
            case 0:
                context.count++;
            case 1:
            case 2:
                context.v = aggregation(aggregation_function, active_frame_content.coalesce(), active_frame_content.size());
            case 3:
                context.current_timestamp = ts;
        }
    }

    // Close the current frame and check for global conditions to eventually evict.
// Removes the evicted/discarded frame from the buffer.
    private void close(GridInputWindowed arg, long ts) {
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

    boolean open_pred(GridInputWindowed arg, long ts) {
        switch (frame_type) {
            case 0: return (arg.getConsA() >= frame_parameter && context.count == 0); // threshold
            case 1: return (!(context.start)); // delta
            case 2: return (!(context.start)); // aggregate
            case 3: return (ts - context.current_timestamp <= frame_parameter && !(context.start)); // session
        }
        throw new RuntimeException("error switching on frame type in window operator");

    }

    boolean update_pred(GridInputWindowed arg, long ts) {
        switch (frame_type) {
            case 0: return (arg.getConsA() >= frame_parameter && context.count > 0);
            case 1: return (Math.abs(context.v - arg.getConsA()) < frame_parameter && context.start);
            case 2: return (context.v < frame_parameter && context.start);
            case 3: return (ts - context.current_timestamp <= frame_parameter && context.start);
        }
        throw new RuntimeException("error switching on frame type in window operator");

    }

    boolean close_pred(GridInputWindowed arg, long ts) {
        switch (frame_type) {
            case 0: return (arg.getConsA() < frame_parameter && context.count > 0);
            case 1: return (Math.abs(context.v - arg.getConsA()) >= frame_parameter && context.start);
            case 2: return (context.v >= frame_parameter && context.start);
            case 3: return (ts - context.current_timestamp > frame_parameter && context.start);
        }
        throw new RuntimeException("error switching on frame type in window operator");
    }

    private void scope(long t_e) {

        long c_sup = (long) Math.ceil(((double) Math.abs(t_e - t0) / (double) slide)) * slide;
        long o_i = c_sup - width;
        log.debug("Calculating the Windows to Open. First one opens at [" + o_i + "] and closes at [" + c_sup + "]");

        do {
            log.debug("Computing Window [" + o_i + "," + (o_i + width) + ") if absent");

            */
/*-------------Custom code for the demo--------------*//*


            Window interval = new WindowImpl(o_i, o_i+width);
            if(!active_sliding_windows.containsKey(interval)){
                active_sliding_windows.put(interval, cf.create());
            }
            o_i += slide;

        } while (o_i <= t_e);
    }

    @Override
    public void compute(GridInputWindowed arg, long ts) {

        if (time.getAppTime() > ts) {
            log.error("OUT OF ORDER NOT HANDLED");
            throw new OutOfOrderElementException("(" + arg + "," + ts + ")");
        }

        */
/*----- Sliding window logic ------*//*

        scope(ts);

        boolean added = false;
        for(Window w : active_sliding_windows.keySet()){
            if(w.getO() <= ts && ts < w.getC()){
                */
/*--- Custom code for the demo---*//*

                //We deep copy each element once for every interval in which it is added (to assign to each instance the correct interval ID)
                GridInputWindowed el = new GridInputWindowed();
                el.setIntervalId(w.toString());
                el.setOperatorId(this.name);
                el.setConsA(arg.getConsA());
                el.setConsB(arg.getConsB());
                el.setRecord_Id(arg.getRecord_Id());
                el.setTimestamp(arg.getTimestamp());
                el.setCursor(arg.getCursor());

                active_sliding_windows.get(w).add(el);
                added = true;
            }
        }

        if(added == false){
            GridInputWindowed el = new GridInputWindowed();
            el.setIntervalId("throw");
            el.setOperatorId(this.name);
            el.setConsA(arg.getConsA());
            el.setConsB(arg.getConsB());
            el.setRecord_Id(arg.getRecord_Id());
            el.setTimestamp(arg.getTimestamp());
            el.setCursor(arg.getCursor());

            throw_content.add(el);
        }



        */
/*----- Frame window logic ------*//*

        added = false;
        if (close_pred(arg, ts)) {
            close(arg, ts);
            context.current_timestamp = ts;
            // current window close

            active_frame_window.setC(ts);
            expired_frame_content.put(active_frame_window, active_frame_content);
            //If threshold frame, the element must not be added to the next window, so we need to evict the current one
            if(frame_type == 0){
                active_frame_window = null;
                active_frame_content = cf.createEmpty();
            }

        }

        if (update_pred(arg, ts)) {
            added = true;
            GridInputWindowed el = new GridInputWindowed();
            el.setIntervalId(active_frame_window.getO()+"");
            el.setOperatorId(this.name);
            el.setConsA(arg.getConsA());
            el.setConsB(arg.getConsB());
            el.setRecord_Id(arg.getRecord_Id());
            el.setTimestamp(arg.getTimestamp());
            el.setCursor(arg.getCursor());
            // add element to current window
            active_frame_content.add(el);

            update(arg, ts);

        }
        if (open_pred(arg, ts)){
            added = true;
            open(arg, ts);
            // open new window with current element
            active_frame_window = new WindowImpl(ts, -1);
            active_frame_content = cf.create();
            GridInputWindowed el = new GridInputWindowed();
            el.setIntervalId(active_frame_window.getO()+"");
            el.setOperatorId(this.name);
            el.setConsA(arg.getConsA());
            el.setConsB(arg.getConsB());
            el.setRecord_Id(arg.getRecord_Id());
            el.setTimestamp(arg.getTimestamp());
            el.setCursor(arg.getCursor());
            active_frame_content.add(el);
        }

        if(added == false){
            GridInputWindowed el = new GridInputWindowed();
            el.setIntervalId("throw");
            el.setOperatorId(this.name);
            el.setConsA(arg.getConsA());
            el.setConsB(arg.getConsB());
            el.setRecord_Id(arg.getRecord_Id());
            el.setTimestamp(arg.getTimestamp());
            el.setCursor(arg.getCursor());
            throw_content.add(el);
        }

        //First version of the demo, we just always report
        time.addEvaluationTimeInstants(new TimeInstant(ts));

        */
/*active_windows.keySet().stream()
                .filter(w -> report.report(w, getWindowContent(w), ts, System.currentTimeMillis()))
                .max(Comparator.comparingLong(Window::getC))
                .ifPresent(window -> {
                    reported_windows.add(window);
                    time.addEvaluationTimeInstants(new TimeInstant(ts));
                });*//*


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
*/
