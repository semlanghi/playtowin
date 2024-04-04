package com.example.application.inkstream.constraint;


import com.example.application.inkstream.record.EventBean;

public record SpeedConstraintLongValueFactory(double maxCoefficient,
                                              double minCoefficient) implements ConstraintFactory<EventBean<Long>> {

    @Override
    public StreamingConstraint<EventBean<Long>> make(EventBean<Long> origin) {
        return new SpeedConstraint<>(origin) {
            @Override
            public long checkConstraint(EventBean<Long> value) {
                if (origin.getValue() + maxCoefficient * (value.getTime() - origin.getTime()) < value.getValue()) {
                    return (long) Math.abs(value.getValue() - (origin.getValue() + maxCoefficient * (value.getTime() - origin.getTime())));
                } else if (origin.getValue() + minCoefficient * (value.getTime() - origin.getTime()) > value.getValue()) {
                    return (long) Math.abs((value.getValue() - (origin.getValue() + minCoefficient * (value.getTime() - origin.getTime()))));
                } else return 0;
            }
        };
    }
}
