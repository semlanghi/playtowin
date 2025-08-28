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
import java.util.concurrent.atomic.AtomicBoolean;

public class S2RHopping implements StreamToRelationOperator<Tuple, Tuple, TuplesOrResult> {

    private static final Logger log = Logger.getLogger(org.streamreasoning.polyflow.base.operatorsimpl.s2r.HoppingWindowOpImpl.class);
    protected final Ticker ticker;
    protected Tick tick;
    protected final Time time;
    protected final String name;
    protected final ContentFactory<Tuple, Tuple, TuplesOrResult> cf;
    protected TimeVaryingTuplesOrResult tvg;
    protected Report report;
    private final long width, slide;
    private Map<Window, Content<Tuple, Tuple, TuplesOrResult>> active_windows;
    private Content<Tuple, Tuple, TuplesOrResult> throw_content;
    private List<Window> reported_windows;
    private Set<Window> to_evict;
    private long t0;
    private long toi;

    public S2RHopping(Tick tick, Time time, String name, ContentFactory<Tuple, Tuple, TuplesOrResult> cf, Report report,
                               long width, long slide) {

        this.tick = tick;
        this.time = time;
        this.name = name;
        this.cf = cf;
        this.report = report;
        this.width = width;
        this.slide = slide;
        this.active_windows = new HashMap<>();
        this.reported_windows = new ArrayList<>();
        this.to_evict = new HashSet<>();
        this.t0 = time.getScope();
        this.toi = 0;
        this.ticker = TickerFactory.tick(tick, this);
        Logger.getRootLogger().setLevel(Level.OFF);
        this.throw_content = cf.create();
    }


    @Override
    public Report report() {
        return report;
    }

    @Override
    public Tick tick() {
        return tick;
    }

    @Override
    public Time time() {
        return time;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean named() {
        return !name.isEmpty();
    }

    /**
     * Returns the content of the last window closed before time t_e. If no such window exists, returns an empty content
     */
    @Override
    public Content<Tuple, Tuple, TuplesOrResult> content(long t_e) {
        // If some windows matched the report clause, return the last one that did so
        if (!reported_windows.isEmpty()) {
            return reported_windows.stream()
                    .max(Comparator.comparingLong(Window::getC))
                    .map(w -> (active_windows.get(w))).get();
        }
        //Else return the last window closed
        else {
            Optional<Window> max = active_windows.keySet().stream()
                    .filter(w -> w.getO() < t_e && w.getC() <= t_e)
                    .max(Comparator.comparingLong(Window::getC));

            if (max.isPresent())
                return active_windows.get(max.get());

            return cf.createEmpty();
        }
    }

    /**
     * Returns the content of all the windows closed before time t_e as a list of contents. If no such windows exist, returns an empty list of contents
     */
    @Override
    public List<Content<Tuple, Tuple, TuplesOrResult>> getContents(long t_e) {

        List<Content<Tuple, Tuple, TuplesOrResult>> res = new ArrayList<>();

        active_windows.keySet().stream()
                .sorted(Comparator.comparingLong(Window::getO))
                .forEach(w -> res.add(active_windows.get(w)));

        // res.addAll(active_windows.values());
        res.add(throw_content);
        return res;
    }


    /**
     * Creates all the windows that can possibly contain the given timestamp
     */
    private void scope(long t_e) {

        long c_sup = (long) Math.ceil(((double) Math.abs(t_e - t0) / (double) slide)) * slide;
        long o_i = c_sup - width;
        log.debug("Calculating the Windows to Open. First one opens at [" + o_i + "] and closes at [" + c_sup + "]");

        do {
            log.debug("Computing Window [" + o_i + "," + (o_i + width) + ") if absent");

            /*-------------Custom code for the demo--------------*/
            
            Window interval = new WindowImpl(o_i, o_i+width);
            if(!active_windows.containsKey(interval)){
                active_windows.put(interval, cf.create());
            }
            o_i += slide;

        } while (o_i <= t_e);
    }


    @Override
    public void compute(Tuple arg, long ts) {

        log.debug("Received element (" + arg + "," + ts + ")");

        if (time.getAppTime() > ts) {
            log.error("OUT OF ORDER NOT HANDLED");
            throw new OutOfOrderElementException("(" + arg + "," + ts + ")");
        }

        scope(ts);

        boolean added = false;
        for(Window w : active_windows.keySet()){
            if(w.getO() <= ts && ts < w.getC()){
                /*--- Custom code for the demo---*/
                //We deep copy each element once for every interval in which it is added (to assign to each instance the correct interval ID)
                Tuple el = arg.copy();
                el.setIntervalId(w.toString());
                el.setOperatorId(this.name);

                active_windows.get(w).add(el);
                added = true;
            }
        }

        if(added == false){
            Tuple el = arg.copy();
            el.setIntervalId("throw");
            el.setOperatorId(this.name);

            throw_content.add(el);
        }



        //First version of the demo, we just always report
        time.addEvaluationTimeInstants(new TimeInstant(ts));

        /*active_windows.keySet().stream()
                .filter(w -> report.report(w, getWindowContent(w), ts, System.currentTimeMillis()))
                .max(Comparator.comparingLong(Window::getC))
                .ifPresent(window -> {
                    reported_windows.add(window);
                    time.addEvaluationTimeInstants(new TimeInstant(ts));
                });*/

        time.setAppTime(ts);

    }

    @Override
    public TimeVarying<TuplesOrResult> get() {
        if(tvg != null)
            return tvg;

        return new TimeVaryingTuplesOrResult(this, name);
    }


    private Content<Tuple, Tuple, TuplesOrResult> getWindowContent(Window w) {
        return active_windows.containsKey(w) ? active_windows.get(w) : cf.createEmpty();
    }

    private void schedule_for_eviction(Window w) {
        to_evict.add(w);
    }

    @Override
    public void evict() {
        //No eviction for the demo

        /*to_evict.forEach(w -> {
            log.debug("Evicting [" + w.getO() + "," + w.getC() + ")");
            active_windows.remove(w);
            if (toi < w.getC())
                toi = w.getC() + slide;
        });
        to_evict.clear();
        reported_windows = new ArrayList<>();*/

    }

    @Override
    public void evict(long ts) {
        //No eviction for the demo
        /*active_windows.keySet().forEach(w -> {if (w.getC() < ts) to_evict.add(w);});
        evict();*/
    }


}
