package ru.herobrine1st.fusion;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.module.FutureModule;
import ru.herobrine1st.fusion.api.module.IModule;
import ru.herobrine1st.fusion.internal.Config;

import javax.security.auth.login.LoginException;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.Set;

public class Fusion {
    private static final Logger logger = LoggerFactory.getLogger(Fusion.class);
    private Set<IModule> modules;
    private void main() {
        final JDA jda;
        try {
            jda = JDABuilder.createLight(Config.INSTANCE.getToken(), EnumSet.noneOf(GatewayIntent.class))
                    .build();
        } catch (LoginException e) {
            logger.error("Invalid discord token");
            System.exit(-1);
            return;
        }
        findModules();
        processModules(jda);
    }

    private void findModules() {
        var disabledModules = Config.INSTANCE.getDisabledModules();
        new Reflections(Config.INSTANCE.getModuleSearchPrefix())
                .getTypesAnnotatedWith(FutureModule.class)
                .stream()
                .filter(it -> it.isInstance(IModule.class))
                .filter(it -> {
                    var moduleId = it.getAnnotation(FutureModule.class).id();
                    return disabledModules.stream().noneMatch(that -> that.equals(moduleId));
                })
                .<Class<? extends IModule>>map(it -> it.asSubclass(IModule.class))
                .forEach(it -> { // Потому что нельзя совместить map и filter
                    try {
                        this.modules.add(it.getDeclaredConstructor().newInstance());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        logger.error("Module instantiation error", e);
                    }
                });
    }

    private void processModules(JDA jda) {
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
