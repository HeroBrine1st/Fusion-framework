package ru.herobrine1st.fusion.api.command.args;

import java.util.Iterator;
import java.util.Optional;

public interface CommandArgs extends Iterator<SingleArg> {
    Optional<CommandArgs> getKey(String key);

    int getPos();
    void setPos(int pos);

    String getSource();
    SingleArg current();
}
