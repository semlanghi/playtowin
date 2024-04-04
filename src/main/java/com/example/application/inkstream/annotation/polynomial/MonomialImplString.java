package com.example.application.inkstream.annotation.polynomial;

import com.example.application.inkstream.annotation.Window;

import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MonomialImplString extends Monomial<String> {

    public MonomialImplString(String variable, int exp) {
        super(variable, exp);
    }

    public MonomialImplString() {
        super();
    }

    public MonomialImplString(int coefficient) {
        super(coefficient);
    }

    @Override
    public void slide(Window window) {
        variables.entrySet().removeIf(new Predicate<Map.Entry<String, Integer>>() {
            @Override
            public boolean test(Map.Entry<String, Integer> stringIntegerEntry) {
                Pattern pattern = Pattern.compile("ts=(\\d+)");
                Matcher matcher = pattern.matcher(stringIntegerEntry.getKey());

                if (matcher.find()) {
                    String match = matcher.group();
                    long number = Long.parseLong(match.substring(3));
                    return number >= window.end() || number < window.start();
                } else return true;
            }
        });
    }

    @Override
    public Monomial<String> emptyMono() {
        return new MonomialImplString();
    }

    public void timesGen(Monomial<String> that){
        Monomial<String> monomial = new MonomialImplString();

    }
}
