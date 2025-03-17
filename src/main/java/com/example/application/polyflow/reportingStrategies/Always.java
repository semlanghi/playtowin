package com.example.application.polyflow.reportingStrategies;

import org.streamreasoning.polyflow.api.operators.s2r.execution.instance.Window;
import org.streamreasoning.polyflow.api.secret.content.Content;
import org.streamreasoning.polyflow.api.secret.report.strategies.ReportingStrategy;

public class Always implements ReportingStrategy {
    @Override
    public boolean match(Window window, Content content, long l, long l1) {
        return true;
    }
}
