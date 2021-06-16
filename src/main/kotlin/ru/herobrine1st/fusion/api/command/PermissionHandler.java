package ru.herobrine1st.fusion.api.command;

import net.dv8tion.jda.api.events.Event;

public abstract class PermissionHandler {
    public static PermissionHandler DEFAULT = new Default();

    private static class Default extends PermissionHandler {

        @Override
        public boolean shouldBeFound(Event event) {
            return true;
        }

        @Override
        public boolean shouldBeExecuted(CommandContext ctx) {
            return true;
        }

        @Override
        public String requirements(CommandContext ctx) {
            return "";
        }

        @Override
        public Type allowedTypes() {
            return Type.BOTH;
        }
    }

    public static class Typed extends PermissionHandler {

        private final Type type;

        public Typed(Type type) {
            this.type = type;
        }

        @Override
        public boolean shouldBeFound(Event event) {
            return true;
        }

        @Override
        public boolean shouldBeExecuted(CommandContext ctx) {
            return true;
        }

        @Override
        public String requirements(CommandContext ctx) {
            return "";
        }

        @Override
        public Type allowedTypes() {
            return type;
        }
    }

    public enum Type {
        SLASH,
        MESSAGE,
        BOTH
    }

    /**
     * Должна ли команда быть найденной (влияет на help и на поиск команд после ввода пользователем)
     * @param event Контекст выполнения
     * @return true, если команда должна быть найдена, иначе false
     */
    public abstract boolean shouldBeFound(Event event);
    /**
     * Исполняется перед выполнением команды. Определяет, выполнять команду или выкинуть ошибку прав
     * @param ctx Контекст выполнения
     * @return true, если команда должна быть выполнена, иначе false
     */
    public abstract boolean shouldBeExecuted(CommandContext ctx);

    /**
     * Требования для запуска
     * @param ctx Контекст выполнения
     * @return Строка необходимых требований
     */
    public abstract String requirements(CommandContext ctx);

    /**
     * Разрешенные типы
     * @return
     * MESSAGE - только сообщение с префиксом
     * SLASH - только слэш-команда
     * BOTH - оба
     */
    public abstract Type allowedTypes();
}