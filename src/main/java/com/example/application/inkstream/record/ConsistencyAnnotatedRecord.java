package com.example.application.inkstream.record;

import com.example.application.inkstream.annotation.polynomial.Polynomial;

public class ConsistencyAnnotatedRecord<V> {
    private Polynomial polynomial;
    private final V wrappedRecord;

    public ConsistencyAnnotatedRecord(V wrappedRecord) {
        this.wrappedRecord = wrappedRecord;
        this.polynomial = new Polynomial();
    }

    public ConsistencyAnnotatedRecord(Polynomial polynomial, V wrappedRecord) {
        this.wrappedRecord = wrappedRecord;
        this.polynomial = polynomial;
    }

    public <VR> ConsistencyAnnotatedRecord<VR> withRecord(VR nwRecord){
        return new ConsistencyAnnotatedRecord<>(polynomial, nwRecord);
    }

    public ConsistencyAnnotatedRecord<V> withPolynomial(Polynomial nwPoly){
        return new ConsistencyAnnotatedRecord<>(nwPoly, wrappedRecord);
    }

    public static <V> ConsistencyAnnotatedRecord<V> makeCopyOf(ConsistencyAnnotatedRecord<V> consistencyAnnotatedRecord){
        return new ConsistencyAnnotatedRecord<>(consistencyAnnotatedRecord.getWrappedRecord());
    }

    public Polynomial getPolynomial() {
        return polynomial;
    }

    public void setPolynomial(Polynomial polynomial) {
        this.polynomial = polynomial;
    }

    public V getWrappedRecord() {
        return wrappedRecord;
    }

    @Override
    public String toString() {
        return "ConsistencyAnnotatedRecord{" +
                "polynomial=" + polynomial +
                ", wrappedRecord=" + wrappedRecord +
                '}';
    }

    public boolean isTheSame(ConsistencyAnnotatedRecord<V> consistencyAnnotatedRecord){
        return this.getWrappedRecord().equals(consistencyAnnotatedRecord.getWrappedRecord());
    }

}
