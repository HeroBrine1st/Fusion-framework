module Fusion.framework {
    requires net.dv8tion.jda;
    requires org.jetbrains.annotations;
    requires org.slf4j;

    exports ru.herobrine1st.fusion.api.command;
    exports ru.herobrine1st.fusion.api.command.args.parser;
    exports ru.herobrine1st.fusion.api.command.build;
    exports ru.herobrine1st.fusion.api.exception;
    exports ru.herobrine1st.fusion.api.manager;
    exports ru.herobrine1st.fusion.api.restaction;
}