package com.example.application.inkstream.constraint;

import com.example.application.inkstream.record.EventBean;

public record SpeedConstraintDoubleValueFactory(double maxCoefficient,
                                                double minCoefficient) implements ConstraintFactory<EventBean<Double>> {

    @Override
    public StreamingConstraint<EventBean<Double>> make(EventBean<Double> origin) {
        return new SpeedConstraint<>(origin) {
            @Override
            public long checkConstraint(EventBean<Double> value) {
                if (origin.getValue() + maxCoefficient * (value.getTime() - origin.getTime()) < value.getValue()) {
                    return (long) Math.abs(value.getValue() - (origin.getValue() + maxCoefficient * (value.getTime() - origin.getTime())));
                }
                else return 0;
            }
        };
    }
}
