package com.example.application.inkstream.annotation;

import java.util.Objects;

public class Window {
    private long start, end;

    public Window(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public long start() {
        return start;
    }

    public long end() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Window)) return false;
        Window window = (Window) o;
        return start == window.start && end == window.end;
    }

    @Override
    public String toString() {
        return "[" + start + "," + end + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
