package ru.herobrine1st.fusion.internal.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.herobrine1st.fusion.api.command.CommandContext;
import ru.herobrine1st.fusion.api.command.State;
import ru.herobrine1st.fusion.api.command.option.FusionBaseCommand;
import ru.herobrine1st.fusion.api.exception.CommandException;
import ru.herobrine1st.fusion.internal.listener.ComponentInteractionHandler;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class CommandContextImpl implements CommandContext {
    private static final Logger logger = LoggerFactory.getLogger(CommandContextImpl.class);

    private final Map<String, List<Object>> arguments = new HashMap<>();
    private final FusionBaseCommand<?> command;

    private final Object lock = new Object(); // Защита от долбоёба. Тут конечно ни хуя не случится, но всякое бывает

    @SuppressWarnings("rawtypes") List<ReactiveData> reactiveDataList = new ArrayList<>();

    int currentIndex = -1; // Это к реактивности относится
    private GenericInteractionCreateEvent event;
    private CompletableFuture<GenericComponentInteractionCreateEvent> genericComponentInteractionCreateEventCompletableFuture = null;
    private boolean waitingForComponentInteraction = false;
    private boolean validateUser = true;

    public CommandContextImpl(GenericInteractionCreateEvent event, FusionBaseCommand<?> command) {
        this.event = event;
        this.command = command;
    }

    @Override
    public GenericInteractionCreateEvent getEvent() {
        return event;
    }

    @Override
    public InteractionHook getHook() {
        return event.getHook();
    }

    @Override
    public void putArg(String name, Object value) {
        arguments.computeIfAbsent(name, k -> new ArrayList<>());
        arguments.get(name).add(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getArgument(String name) {
        var list = arguments.get(name);
        if (list != null && list.size() > 0) {
            Object argument = list.get(0);
            return Optional.of((T) argument);
        } else {
            return Optional.empty();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> getArguments(String name) {
        return (Collection<T>) arguments.getOrDefault(name, Collections.emptyList());
    }

    @Override
    public User getUser() {
        return event.getUser();
    }

    @Override
    public FusionBaseCommand<?> getCommand() {
        return command;
    }

    @Override
    public CompletableFuture<GenericComponentInteractionCreateEvent> waitForComponentInteraction(Message message, boolean validateUser) {
        submitComponents(message, true, validateUser);
        return genericComponentInteractionCreateEventCompletableFuture;
    }

    @Override
    public void handleException(Throwable t) {
        if (!event.isAcknowledged()) event.deferReply(true).queue();
        if (t instanceof CompletionException completionException && completionException.getCause() != null) {
            t = completionException.getCause();
        }
        var embed = new EmbedBuilder()
                .setTitle("Error")
                .setColor(0xFF0000);
        if (t instanceof CommandException commandException) {
            if (t.getCause() != null) {
                logger.error(commandException.getMessage(), t.getCause());
            }
            embed.setDescription(commandException.getMessage());
        } else if (t instanceof CancellationException) {
            logger.trace("Caught CancellationException", t);
            return;
        } else if (t instanceof RuntimeException) {
            embed.setDescription("Unknown runtime exception occurred.");
            logger.error("Runtime exception occurred when executing command", t);
        } else {
            embed.setDescription("Unknown exception occurred.");
            logger.error("Error executing command", t);
        }
        event.getHook()
                .editOriginal(new MessageBuilder()
                        .setEmbeds(embed.build())
                        .build())
                .queue();
    }

    @Override
    public InteractionCallbackAction deferReply(boolean ephemeral) {
        if (event instanceof ComponentInteraction componentInteraction) {
            return componentInteraction.deferEdit();
        } else {
            return event.deferReply(ephemeral);
        }
    }

    @Override
    public Message submitComponents(Message message, boolean validateUser) {
        submitComponents(message, false, validateUser);
        return message;
    }

    // Так, объясняю алгоритм, пока помню
    // Смотрим, существует ли элемент в списке
    // Если нет, то создаём с изначальным значением и возвращаем изначальное значение
    // Если есть, то берём этот элемент и если ивент является взаимодействием с компонентом, проверяем айди и если входит в массив, то вызываем коллбек и вставляем новое значение
    // Все if с последующим throw - проверки, которые конечно ни хуя не помогут, но хоть отобразят прогеру, что он сделал то, о чем предупреждалось в джавадоке
    @Override
    @SuppressWarnings("unchecked")
    public <T> T useComponent(T initialState, BiFunction<String, T, T> onComponentInteraction, String... componentIds) {
        currentIndex++;
        if (currentIndex >= reactiveDataList.size()) {
            if (event instanceof ComponentInteraction)
                throw new IllegalStateException("Conditional use of reactive methods"); // На втором проходе уже не должно быть никаких налов и подобной хуеты
            reactiveDataList.add(new StateData<>(initialState));
            return initialState;
        }
        @SuppressWarnings("rawtypes") ReactiveData data = reactiveDataList.get(currentIndex);
        if (!(data instanceof StateData))
            throw new IllegalStateException("Conditional use of reactive methods"); // А это на случай, если мы получим EffectData
        if (event instanceof ComponentInteraction componentInteraction) {
            if (Arrays.stream(componentIds).anyMatch(it -> Objects.equals(it, componentInteraction.getComponentId()))) {
                data.setValue(onComponentInteraction.apply(componentInteraction.getComponentId(), (T) data.getValue()));
            }
            return (T) data.getValue();
        } else throw new RuntimeException("Cannot be thrown");
    }

    static class StateImpl<T> implements State<T> {
        private T value;

        public StateImpl(T initialValue) {
            value = initialValue;
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public void setValue(T value) {
            this.value = value;
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> State<T> useState(T initialState) {
        currentIndex++;
        if (currentIndex >= reactiveDataList.size()) {
            if (event instanceof ComponentInteraction)
                throw new IllegalStateException("Conditional use of reactive methods"); // На втором проходе уже не должно быть никаких налов и подобной хуеты
            StateImpl<T> state = new StateImpl<>(initialState);
            reactiveDataList.add(new StateData<>(state));
            return state;
        }
        @SuppressWarnings("rawtypes") ReactiveData data = reactiveDataList.get(currentIndex);
        if (!(data instanceof StateData stateData))
            throw new IllegalStateException("Conditional use of reactive methods"); // А это на случай, если мы получим EffectData
        if (!(stateData.getValue() instanceof StateImpl))
            throw new IllegalStateException("Conditional use of reactive methods"); // А это на случай, если мы получим ещё какую-нибудь хуету
        return (State<T>) stateData.getValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> CompletableFuture<T> useEffect(Supplier<CompletableFuture<T>> completableFutureSupplier, Object... dependencies) {
        List<Object> listOfDependencies = List.of(dependencies);
        currentIndex++;
        if (currentIndex >= reactiveDataList.size()) {
            if (event instanceof ComponentInteraction)
                throw new IllegalStateException("Conditional use of reactive methods"); // На втором проходе уже не должно быть никаких налов и подобной хуеты
            EffectData<T> effectData = new EffectData<>();
            effectData.setDependencies(listOfDependencies);
            effectData.compareDependencies(List.of(dependencies));
            CompletableFuture<T> completableFuture = completableFutureSupplier.get();
            completableFuture.thenAccept(effectData::setValue);
            reactiveDataList.add(effectData);
            return completableFuture;
        }
        @SuppressWarnings("rawtypes") ReactiveData data = reactiveDataList.get(currentIndex);
        if (!(data instanceof EffectData))
            throw new IllegalStateException("Conditional use of reactive methods"); // А это на случай, если мы получим StateData
        if (data.getValue() == null)
            throw new RuntimeException("Component interaction received before CompletableFuture successful completion or completion exception occurred and suppressed");
        if (!((EffectData<?>) data).compareDependencies(listOfDependencies)) {
            ((EffectData<?>) data).setDependencies(listOfDependencies);
            CompletableFuture<T> completableFuture = completableFutureSupplier.get();
            completableFuture.thenAccept(data::setValue);
            return completableFuture;
        } else {
            return CompletableFuture.completedFuture((T) data.getValue());
        }
    }

    private void submitComponents(Message message, boolean createCompletableFuture, boolean validateUser) {
        synchronized (lock) {
            if (message.getActionRows().isEmpty()) throw new IllegalArgumentException("No action rows in message");
            if (waitingForComponentInteraction)
                throw new IllegalStateException("Already waiting for component interaction");
            waitingForComponentInteraction = true;
            this.validateUser = validateUser;
            this.genericComponentInteractionCreateEventCompletableFuture = createCompletableFuture ? new CompletableFuture<>() : null;
            ComponentInteractionHandler.open(message.getIdLong(), this);
            logger.trace("Opening interaction listener to messageId=%s".formatted(message.getIdLong()));
        }
    }

    public void applyComponentInteractionEvent(GenericComponentInteractionCreateEvent event) {
        synchronized (lock) {
            if (!waitingForComponentInteraction) return;
            waitingForComponentInteraction = false;
            this.event = event;
            this.currentIndex = -1;
            if (genericComponentInteractionCreateEventCompletableFuture != null) {
                if (genericComponentInteractionCreateEventCompletableFuture.isDone()) return;
                genericComponentInteractionCreateEventCompletableFuture.complete(event);
            } else { // This is reactive command
                execute();
            }
        }
    }

    public void cancelComponentInteractionWaiting() {
        synchronized (lock) {
            if (!waitingForComponentInteraction) return;
            waitingForComponentInteraction = false;
            if (genericComponentInteractionCreateEventCompletableFuture != null) {
                genericComponentInteractionCreateEventCompletableFuture.cancel(true);
                genericComponentInteractionCreateEventCompletableFuture = null;
            }
        }
    }

    public void execute() {
        try {
            this.getCommand().getExecutor().execute(this);
        } catch (Throwable t) {
            handleException(t);
        }
    }

    public boolean shouldValidateUser() {
        return validateUser;
    }

    static class ReactiveData<T> {
        private T value;

        private ReactiveData(T initialValue) {
            value = initialValue;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }

    static class StateData<T> extends ReactiveData<T> {
        private StateData(T initialValue) {
            super(initialValue);
        }
    }

    static class EffectData<T> extends ReactiveData<T> {
        private List<Object> dependencies = new ArrayList<>();

        private EffectData() {
            super(null);
        }

        private boolean compareDependencies(List<Object> other) {
            if (dependencies.size() != other.size()) return false;
            for (int i = 0; i < other.size(); i++) { // containsAll будет медленнее
                if (dependencies.get(i) != other.get(i)) return false;
            }
            return true;
        }

        private void setDependencies(List<Object> dependencies) {
            this.dependencies = dependencies;
        }
    }
}