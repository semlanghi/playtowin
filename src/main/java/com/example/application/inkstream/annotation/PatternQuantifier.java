package com.example.application.inkstream.annotation;

import com.example.application.inkstream.annotation.polynomial.Monomial;
import com.example.application.inkstream.record.ConsistencyAnnotatedRecord;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class PatternQuantifier<V> {

    private Set<String> searchedViolations;

    public PatternQuantifier(Set<String> searchedViolations) {
        this.searchedViolations = searchedViolations;
    }

    public int calculateDegree(ConsistencyAnnotatedRecord<V> consistencyAnnotatedRecord){
        int finalDegree = 0;
        for (Monomial<String> monomial: consistencyAnnotatedRecord.getPolynomial().getMonomials()
             ) {
            boolean patternCompleted = true;

            for (String searchedViolation : searchedViolations) {
                Optional<String> any = monomial.getVariables().stream().filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        return s.startsWith(searchedViolation);
                    }
                }).findAny();

                patternCompleted = any.isPresent();
            }
            if (!patternCompleted)
                finalDegree++;
        }
        return finalDegree;
    }
}
