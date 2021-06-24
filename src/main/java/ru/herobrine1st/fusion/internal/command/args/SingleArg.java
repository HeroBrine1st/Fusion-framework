package ru.herobrine1st.fusion.internal.command.args;

import java.util.Objects;

public record SingleArg(int start, int end, String value) {

    public String getValue() {
        return this.value;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SingleArg singleArg)) {
            return false;
        }
        return this.start == singleArg.start && this.end == singleArg.end && Objects.equals(this.value, singleArg.value);
    }
}