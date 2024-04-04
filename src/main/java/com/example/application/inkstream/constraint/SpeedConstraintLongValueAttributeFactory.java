package com.example.application.inkstream.constraint;


import com.example.application.inkstream.record.EventBean;

public record SpeedConstraintLongValueAttributeFactory(double maxCoefficient,
                                                       double minCoefficient, String attribute, String description) implements ConstraintFactory<EventBean<Long>> {

    
    @Override
    public StreamingConstraint<EventBean<Long>> make(EventBean<Long> origin) {
        return new SpeedConstraint<>(origin, description) {
            @Override
            public long checkConstraint(EventBean<Long> value) {
                if (origin.getValue(attribute) + maxCoefficient * (value.getTime() - origin.getTime()) < value.getValue(attribute)) {
                    return (long) Math.abs(value.getValue(attribute) - (origin.getValue(attribute) + maxCoefficient * (value.getTime() - origin.getTime())));
                } else if (origin.getValue(attribute) + minCoefficient * (value.getTime() - origin.getTime()) > value.getValue(attribute)) {
                    return (long) Math.abs((value.getValue(attribute) - (origin.getValue(attribute) + minCoefficient * (value.getTime() - origin.getTime()))));
                } else return 0;
            }
        };
    }
}
