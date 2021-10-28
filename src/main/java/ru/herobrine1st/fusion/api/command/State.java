package ru.herobrine1st.fusion.api.command;

public interface State<T> {
    T getValue();

    void setValue(T value);
}
