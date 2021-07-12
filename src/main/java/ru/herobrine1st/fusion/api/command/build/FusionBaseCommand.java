package ru.herobrine1st.fusion.api.command.build;

import net.dv8tion.jda.internal.utils.Checks;
import ru.herobrine1st.fusion.api.command.CommandExecutor;
import ru.herobrine1st.fusion.api.command.FusionOptionData;
import ru.herobrine1st.fusion.api.command.PermissionHandler;
import ru.herobrine1st.fusion.api.command.args.ParserElement;

import javax.annotation.Nonnull;
import java.util.*;

// TODO sealed
public abstract class FusionBaseCommand<T extends FusionBaseCommand<T>> extends FusionOptionData {
    private CommandExecutor executor = null;
    private PermissionHandler permissionHandler = PermissionHandler.DEFAULT;
    private String shortName;
    private boolean isTesting = false;

    protected final List<FusionOptionData> options = new ArrayList<>();

    protected FusionBaseCommand(@Nonnull String name, @Nonnull String description) {
        super(name, description);
        this.shortName = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1).toLowerCase(Locale.ROOT);
    }

    @Nonnull
    public List<FusionOptionData> getOptions() {
        return options;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public T setPermissionHandler(PermissionHandler permissionHandler) {
        this.permissionHandler = permissionHandler;
        return (T) this;
    }

    @Nonnull
    public PermissionHandler getPermissionHandler() {
        return permissionHandler;
    }

    @Nonnull
    public CommandExecutor getExecutor() {
        return executor;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public T setExecutor(CommandExecutor executor) {
        this.executor = executor;
        return (T) this;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public T addArguments(ParserElement... elements) {
        Checks.noneNull(elements, "Argument");
        Checks.check(elements.length + this.options.size() <= 25, "Cannot have more than 25 options for a command!");
        this.options.addAll(Arrays.asList(elements));
        if (options.stream().map(ParserElement.class::cast).allMatch(ParserElement::hasSlashSupport)) {
            Checks.check(
                    options.stream()
                            .map(ParserElement.class::cast)
                            .dropWhile(it -> it.getOptionData().isRequired())
                            .noneMatch(it -> it.getOptionData().isRequired()),
                    "You should add non-required arguments after required ones");
        }
        return (T) this;
    }

    @Nonnull
    public Collection<ParserElement> getArguments() {
        return this.options.stream().map(ParserElement.class::cast).toList();
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public T setShortName(@Nonnull String name) {
        this.shortName = name;
        return (T) this;
    }

    @Nonnull
    public String getShortName() {
        return shortName;
    }

    public boolean hasSubcommands() {
        return options.size() > 0 && options.get(0) instanceof FusionSubcommandData;
    }

    public boolean hasSubcommandGroups() {
        return options.size() > 0 && options.get(0) instanceof FusionSubcommandGroupData;
    }

    public boolean hasExecutor() {
        return executor != null;
    }

    public boolean isTesting() {
        return isTesting;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public T setTesting(boolean testing) {
        isTesting = testing;
        return (T) this;
    }
}
