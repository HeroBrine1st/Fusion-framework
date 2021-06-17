package ru.herobrine1st.fusion;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.internal.utils.Checks;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.module.FutureModule;
import ru.herobrine1st.fusion.internal.Config;


import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Fusion {
    private static final Logger logger = LoggerFactory.getLogger(Fusion.class);
    private Set<Class<?>> modules;

    private void main() {
        final JDA jda;
        try {
            jda = JDABuilder.createLight(Config.INSTANCE.getToken(), EnumSet.noneOf(GatewayIntent.class))
                    .build();
        } catch (LoginException e) {
            logger.error("Invalid discord token");
            System.exit(-1);
        }
        findModules();
    }

    private void findModules() {
        var disabledModules = Config.INSTANCE.getDisabledModules();
        modules = new Reflections(Config.INSTANCE.getModuleSearchPrefix())
                .getTypesAnnotatedWith(FutureModule.class)
                .stream()
                .filter(it -> {
                    var moduleId = it.getAnnotation(FutureModule.class).id();
                    return disabledModules.stream().noneMatch(that -> that.equals(moduleId));
                })
                .collect(Collectors.toUnmodifiableSet());
    }

    private void processModules() {
        modules.forEach(it -> {

        });
    }

    /////////////////
    public static final Fusion INSTANCE = new Fusion();

    private Fusion() {
    }

    public static void main(String[] args) {
        INSTANCE.main();
    }
}
