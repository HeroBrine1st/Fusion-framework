package ru.herobrine1st.fusion.api.command.build;

import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;
import ru.herobrine1st.fusion.api.command.CommandExecutor;
import ru.herobrine1st.fusion.api.command.FusionOptionData;
import ru.herobrine1st.fusion.api.command.PermissionHandler;
import ru.herobrine1st.fusion.api.command.args.parser.ParserElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract sealed class FusionBaseCommand<T extends FusionBaseCommand<T, R>, R extends FusionOptionData> extends FusionOptionData permits FusionCommand, FusionSubcommand {
    private CommandExecutor executor = null;
    private PermissionHandler permissionHandler = PermissionHandler.DEFAULT;
    private String shortName;
    private boolean testing = false;

    protected final List<R> options = new ArrayList<>();

    protected FusionBaseCommand(@NotNull String name, @NotNull String description) {
        super(name, description);
        this.shortName = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1).toLowerCase(Locale.ROOT);
    }

    @NotNull
    public List<R> getOptions() {
        return options;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public T setPermissionHandler(PermissionHandler permissionHandler) {
        this.permissionHandler = permissionHandler;
        return (T) this;
    }

    @NotNull
    public PermissionHandler getPermissionHandler() {
        return permissionHandler;
    }

    @NotNull
    public CommandExecutor getExecutor() {
        return executor;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public T setExecutor(CommandExecutor executor) {
        Checks.check(!hasSubcommandGroups() && !hasSubcommands(), "You cannot mix executor with subcommands/groups");
        this.executor = executor;
        return (T) this;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public T addOptions(R... options) {
        Checks.noneNull(options, "Option");
        Checks.notEmpty(options, "Options");
        Checks.check(options.length + this.options.size() <= 25, "Cannot have more than 25 options for a command!");
        this.options.addAll(Arrays.asList(options));
        if (options[0] instanceof ParserElement) { // if R is ParserElement
            Checks.check(
                    this.options.stream()
                            .map(ParserElement.class::cast)
                            .dropWhile(it -> it.getOptionData().isRequired())
                            .noneMatch(it -> it.getOptionData().isRequired()),
                    "You should add non-required arguments after required ones");
        }
        return (T) this;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public T setShortName(@NotNull String name) {
        this.shortName = name;
        return (T) this;
    }

    @NotNull
    public String getShortName() {
        return shortName;
    }

    public boolean hasSubcommands() {
        return options.size() > 0 && options.get(0) instanceof FusionSubcommand;
    }

    public boolean hasSubcommandGroups() {
        return options.size() > 0 && options.get(0) instanceof FusionSubcommandGroup;
    }

    public boolean hasExecutor() {
        return executor != null;
    }

    public boolean isTesting() {
        return testing;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public T setTesting(boolean testing) {
        this.testing = testing;
        return (T) this;
    }
}
