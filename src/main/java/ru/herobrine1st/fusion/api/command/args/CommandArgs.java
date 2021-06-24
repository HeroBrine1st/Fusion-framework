package ru.herobrine1st.fusion.api.command.args;

import ru.herobrine1st.fusion.internal.command.args.CommandArgsImpl;
import ru.herobrine1st.fusion.internal.command.args.SingleArgImpl;

import java.util.Iterator;
import java.util.Optional;

public interface CommandArgs extends Iterator<SingleArg> {
    Optional<CommandArgsImpl> getKey(String key);

    int getPos();
    void setPos(int pos);

    String getSource();
    SingleArg current();
}
