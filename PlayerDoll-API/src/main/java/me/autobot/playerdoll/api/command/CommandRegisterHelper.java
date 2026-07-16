package me.autobot.playerdoll.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.autobot.playerdoll.api.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class CommandRegisterHelper {
    private static final CommandDispatcher<Object> brigadierDispatcher;

    static {
        Class<?> nmsCommandsClass = ReflectionUtil.getNMClass("commands.Commands");
        Object minecraftServerInstance = ReflectionUtil.getDedicatedServerInstance();

        Method commandsMethod = Arrays.stream(minecraftServerInstance.getClass().getMethods())
                .filter(method -> method.getReturnType() == nmsCommandsClass)
                .findFirst()
                .orElseThrow();
        commandsMethod.setAccessible(true);
        Object vanillaCommandDispatcherInstance = ReflectionUtil.invokeMethod(commandsMethod, minecraftServerInstance);


        Field commandDispatcherField = Arrays.stream(vanillaCommandDispatcherInstance.getClass().getDeclaredFields())
                .filter(field -> field.getType() == CommandDispatcher.class)
                .findFirst()
                .orElseThrow();
        commandDispatcherField.setAccessible(true);

        brigadierDispatcher = (CommandDispatcher<Object>) ReflectionUtil.getField(commandDispatcherField, vanillaCommandDispatcherInstance);
    }

    @SuppressWarnings("unchecked")
    public static void registerCommand(LiteralCommandNode<?> node) {
        try {
            // CraftBukkit patched method, not in vanilla brigadier artifact
            Method removeCommand = CommandNode.class.getMethod("removeCommand", String.class);
            removeCommand.invoke(brigadierDispatcher.getRoot(), node.getName());
        } catch (ReflectiveOperationException ignored) {
        }
        brigadierDispatcher.getRoot().addChild((CommandNode<Object>) node);
    }

}
