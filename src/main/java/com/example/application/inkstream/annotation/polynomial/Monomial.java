package com.example.application.inkstream.annotation.polynomial;



import com.example.application.inkstream.annotation.Window;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Monomial<R> implements Serializable {

    private int degree;
    private int coefficient;
    protected Map<R, Integer> variables;
    private int cardinality;

    public int getDegree() {
        return degree;
    }

    public int getCardinality() {
        return cardinality;
    }

    public Monomial(R variable, int exp) {
        variables = new HashMap<>();
        variables.put(variable, exp);
        degree = exp;
        coefficient = 1;
        cardinality = 1;
    }

    public Monomial(int coefficient) {
        this.coefficient = coefficient;
        variables = new HashMap<>();
        cardinality = 0;
    }

    private Monomial(int degree, int coefficient, Map<R, Integer> variables, int cardinality) {
        this.degree = degree;
        this.coefficient = coefficient;
        this.variables = variables;
        this.cardinality = cardinality;
    }

    public Set<R> getVariables() {
        return variables.keySet();
    }

    public Monomial() {
        variables = new HashMap<>();
        this.coefficient = 1;
        cardinality = 0;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }

    public void setCardinality(int cardinality) {
        this.cardinality = cardinality;
    }

    public Monomial<R> times(Monomial<R> that){
        Monomial<R> result = emptyMono();
        result.setCoefficient(this.coefficient* that.coefficient);
        result.setDegree(this.degree+that.degree);
        int resCardinality=this.cardinality;
        Map<R, Integer> resVariables = new HashMap<>(this.variables);
        for (R tmp : that.variables.keySet()) {
            resVariables.computeIfPresent(tmp, (v, integer) -> integer + that.variables.get(v));
            if (resVariables.get(tmp) == null){
                resVariables.put(tmp, that.variables.get(tmp));
                resCardinality++;
            }
        }
        result.setVariables(resVariables);
        result.setCardinality(resCardinality);
        return result;
    }

    public void setVariables(Map<R, Integer> variables) {
        this.variables = variables;
    }

    public abstract void slide(Window window);

    public abstract Monomial<R> emptyMono();

    public void times(R variable, int exp){
        this.degree += exp;
        this.variables.computeIfPresent(variable, (v, integer) -> integer + exp);
        this.variables.putIfAbsent(variable, exp);
        this.cardinality++;
    }

    public void timesCoeff(int nwCoeff){
        this.coefficient *= nwCoeff;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(coefficient);
        for (Map.Entry<R, Integer> tmp : variables.entrySet()
             ) {
            if (tmp.getValue()>0)
                sb.append("*" + tmp.getKey().toString()).append("^").append(tmp.getValue());
        }
        return sb.toString();
    }
}
