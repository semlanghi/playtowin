package com.example.application.inkstream.annotation.polynomial;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Polynomial implements Serializable {
    private List<Monomial> monomials;
    private final int degree;   // degree of polynomial (-1 for the zero polynomial)
    private final int monomialsDegreeSum;
    private int cardinality;

    public Polynomial(Monomial a, Monomial b) {
        monomials = new ArrayList<>();
        monomials.add(a);
        monomials.add(b);
        degree = Math.max(a.getDegree(), b.getDegree());
        monomialsDegreeSum = a.getDegree() + b.getDegree();
    }

    public Polynomial(Monomial a) {
        monomials = new ArrayList<>();
        monomials.add(a);
        degree = a.getDegree();
        monomialsDegreeSum = a.getDegree();
    }

    public Polynomial(List<Monomial> monomials, int degree, int monomialsDegreeSum) {
        this.monomials = monomials;
        this.degree = degree;
        this.monomialsDegreeSum = monomialsDegreeSum;
    }

    public Polynomial() {
        monomials = new ArrayList<>();
        monomials.add(new MonomialImplString());
        degree = 0;
        monomialsDegreeSum = 0;
    }

//    // pre-compute the degree and the sum of degrees of the polynomial, in case of leading zero coefficients
//    // (that is, the length of the array need not relate to the degree of the polynomial)
//    private void reduce() {
//        degree = -1;
//        for (Monomial<V> mono: monomials) {
//            degree = Math.max(degree, mono.getDegree());
//            monomialsDegreeSum += mono.getDegree();
//        }
//    }

    /**
     * Returns the degree of this polynomial.
     * @return the degree of this polynomial, -1 for the zero polynomial.
     */
    public int degree() {
        return degree;
    }

    public List<Monomial> getMonomials() {
        return monomials;
    }

    public int getDegree() {
        return degree;
    }

    public int getMonomialsDegreeSum() {
        return monomialsDegreeSum;
    }

    /**
     * Returns the sum of this polynomial and the specified polynomial.
     *
     * @param  that the other polynomial
     * @return the polynomial whose value is {@code (this(x) + that(x))}
     */
    public Polynomial plus(Polynomial that) {
        this.monomials.addAll(that.monomials);
        return new Polynomial(this.monomials, Math.max(this.degree, that.degree),
                this.monomialsDegreeSum+ that.monomialsDegreeSum);
    }

    /**
     * Returns the product of this polynomial and the specified polynomial.
     * Takes time proportional to the product of the degrees.
     * (Faster algorithms are known, e.g., via FFT.)
     *
     * @param  that the other polynomial
     * @return the polynomial whose value is {@code (this(x) * that(x))}
     */
    public Polynomial times(Polynomial that) {
        Polynomial poly = new Polynomial();
        for (Monomial monomial: this.monomials) {
            poly = poly.times(monomial);
        }
        for (Monomial monomial2: that.monomials) {
            poly = poly.times(monomial2);
        }
        return poly;
    }

    public Polynomial times(Monomial that) {
        Polynomial poly = new Polynomial(new ArrayList<>(), this.degree + that.getDegree(), this.monomialsDegreeSum + that.getDegree()*this.monomials.size());
        for (Monomial monomial: this.monomials) {
            poly.monomials.add(monomial.times(that));
        }
        return poly;
    }

    public Polynomial times(Object singleVar, int exp){
        return times(new MonomialImplString(singleVar.toString(), exp));
    }


    /**
     * Compares two polynomials by degree, breaking ties by coefficient of leading term.
     *
     * @param  that the other point
     * @return the value {@code 0} if this polynomial is equal to the argument
     *         polynomial (precisely when {@code equals()} returns {@code true});
     *         a negative integer if this polynomial is less than the argument
     *         polynomial; and a positive integer if this polynomial is greater than the
     *         argument point
     */
    public int compareTo(Polynomial that) {
        return Integer.compare(this.degree, that.degree);
    }


    public int compareSum(Polynomial that) {
        return Integer.compare(this.monomialsDegreeSum, that.monomialsDegreeSum);
    }

    /**
     * Return a string representation of this polynomial.
     * @return a string representation of this polynomial in the format
     *         4x^5 - 3x^2 + 11x + 5
     */
    @Override
    public String toString() {
        if      (degree == -1) return "0";
        else {
            StringBuilder s = new StringBuilder();
            s.append(monomials.get(0));
            if(monomials.size() > 1)
                for (int i = 1; i < monomials.size(); i++) {
                    s.append(" + ").append(monomials.get(i));
                }
            return s.toString();
        }
    }

    public static void main(String[] args){
        Monomial monomial = new MonomialImplString("x", 2);
        Polynomial polynomial = new Polynomial(monomial);
        System.out.println(polynomial);

        Monomial monomial1 = new MonomialImplString("y", 2);
        Monomial monomial2 = new MonomialImplString("x", 2);
        System.out.println(polynomial.plus(new Polynomial(monomial1)).times(monomial2));

    }

//    public static class PolynomialSerde implements Serde<Polynomial> {
//
//        @Override
//        public Serializer<Polynomial> serializer() {
//            return new Serializer<Polynomial>() {
//                @Override
//                public byte[] serialize(String topic, Polynomial data) {
//
//                }
//            };
//        }
//
//        @Override
//        public Deserializer<Polynomial> deserializer() {
//
//        }
//    }

}

