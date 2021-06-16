package ru.herobrine1st.fusion.api.command.declare;

import net.dv8tion.jda.api.interactions.commands.build.BaseCommand;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.internal.utils.Checks;
import ru.herobrine1st.fusion.api.command.CommandExecutor;
import ru.herobrine1st.fusion.api.command.PermissionHandler;
import ru.herobrine1st.fusion.api.command.args.ParserElement;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public abstract class FusionBaseCommand<T extends FusionBaseCommand<T>> {
    private final List<ParserElement> arguments = new ArrayList<>();
    private CommandExecutor executor = null;
    private PermissionHandler permissionHandler = PermissionHandler.DEFAULT;
    private String name, description, shortName;

    protected final List<Object> options = new ArrayList<>();

    public FusionBaseCommand(@Nonnull String name, @Nonnull String description) {
        Checks.notEmpty(name, "Name");
        Checks.notEmpty(description, "Description");
        Checks.notLonger(name, 32, "Name");
        Checks.notLonger(description, 100, "Description");
        Checks.matches(name, Checks.ALPHANUMERIC_WITH_DASH, "Name");
        Checks.isLowercase(name, "Name");
        this.name = name;
        this.description = description;
        this.shortName = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1).toLowerCase(Locale.ROOT);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public T setName(@Nonnull String name) {
        Checks.notEmpty(name, "Name");
        Checks.notLonger(name, 32, "Name");
        Checks.isLowercase(name, "Name");
        Checks.matches(name, Checks.ALPHANUMERIC_WITH_DASH, "Name");
        this.name = name;
        return (T) this;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public T setDescription(@Nonnull String description) {
        Checks.notEmpty(description, "Description");
        Checks.notLonger(description, 100, "Description");
        this.description = description;
        return (T) this;
    }


    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getDescription() {
        return description;
    }

    @Nonnull
    public List<Object> getOptions() {
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
        this.options.addAll(List.of(elements));
        return (T) this;
    }

    @Nonnull
    public Collection<ParserElement> getArguments() {
        return this.arguments;
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
}
