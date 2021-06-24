package ru.herobrine1st.fusion.internal.command.args;

import ru.herobrine1st.fusion.api.command.args.SingleArg;

import java.util.Objects;

public record SingleArgImpl(int start, int end, String value) implements SingleArg {

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
        if (!(o instanceof SingleArgImpl singleArgImpl)) {
            return false;
        }
        return this.start == singleArgImpl.start && this.end == singleArgImpl.end && Objects.equals(this.value, singleArgImpl.value);
    }
}