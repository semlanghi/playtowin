package com.example.application.polyflow.reportingStrategies;

import org.streamreasoning.polyflow.api.operators.s2r.execution.instance.Window;
import org.streamreasoning.polyflow.api.secret.content.Content;
import org.streamreasoning.polyflow.api.secret.report.strategies.ReportingStrategy;

public class Periodic implements ReportingStrategy {
    long period;
    long lastReport = 0;
    public Periodic(long period){
        this.period = period;
    }
    @Override
    public boolean match(Window window, Content content, long l, long l1) {
        if(l-lastReport > period){
            lastReport = l;
            return true;
        };
        return false;
    }
}