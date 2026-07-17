package me.autobot.playerdoll.api.command;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.ReflectionUtil;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class DollCommandSource {
    private static final Method getBukkitSenderMethod;

    static {
        Class<?> commandSourceStackClass = ReflectionUtil.getNMClass("commands.CommandSourceStack");
        Objects.requireNonNull(commandSourceStackClass, "CommandSourceStack.class");

        Method method;
        try {
            method = commandSourceStackClass.getMethod("getBukkitSender");
        } catch (NoSuchMethodException e) {
            method = Arrays.stream(commandSourceStackClass.getMethods())
                    .filter(m -> m.getParameterCount() == 0)
                    .filter(m -> CommandSender.class.isAssignableFrom(m.getReturnType()))
                    .findFirst()
                    .orElseThrow();
        }
        getBukkitSenderMethod = method;
    }

    public static CommandSender toCommandSender(Object commandSource) {
        return ReflectionUtil.invokeMethod(CommandSender.class, getBukkitSenderMethod, commandSource);
    }

    private final CommandContext<Object> context;
    private final CommandSender sender;

    private DollCommandSource(CommandContext<Object> commandSourceStack, DollCommandExecutor executor) {
        // When execute Command, have to getSource()
        sender = toCommandSender(commandSourceStack.getSource());
        this.context = commandSourceStack;
    }

    public static int execute(CommandContext<Object> commandSourceStack, DollCommandExecutor executor) {
        DollCommandSource source = new DollCommandSource(commandSourceStack, executor);
        return executor.onCommand(source.sender, source.context);
    }
}
