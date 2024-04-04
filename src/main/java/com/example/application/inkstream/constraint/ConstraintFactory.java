package com.example.application.inkstream.constraint;

/**
 * We use the factory pattern to create {@link StreamingConstraint}, depending on the type of constraint, a new
 * factory is created. We exploit this by defining the constraints predicates within the factory object.
 * @param <V> the value handled by the constraint
 */
public interface ConstraintFactory<V> {
    public StreamingConstraint<V> make(V origin);
}
