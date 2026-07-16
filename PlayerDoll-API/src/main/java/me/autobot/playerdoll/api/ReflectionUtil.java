package me.autobot.playerdoll.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class ReflectionUtil {

    private static Method getBukkitPlayerMethod;
    private static Object dedicatedServerInstance;

    public static Class<?> getNMClass(String shortName) {
        return getClass("net.minecraft." + shortName);
    }

    public static Object getDedicatedServerInstance() {
        if (dedicatedServerInstance == null) {
            try {
                Field consoleField = org.bukkit.Bukkit.getServer().getClass().getDeclaredField("console");
                consoleField.setAccessible(true);
                dedicatedServerInstance = consoleField.get(org.bukkit.Bukkit.getServer());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return dedicatedServerInstance;
    }

    public static <T> T invokeStaticMethod(Class<T> clazz, Method method, Object... args) {
        return invokeMethod(clazz, method, null, args);
    }
    public static Object invokeStaticMethod(Method method, Object... args) {
        return invokeMethod(method, null, args);
    }

    public static <T> T invokeMethod(Class<T> clazz, Method method, Object instance, Object... args) {
        try {
            return clazz.cast(method.invoke(instance, args));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static Object invokeMethod(Method method, Object instance, Object... args) {
        try {
            return method.invoke(instance, args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> T getField(Class<T> clazz, Field field, Object instance) {
        try {
            return clazz.cast(field.get(instance));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static Object getField(Field field, Object instance) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setField(Field field, Object instance, Object value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <C> C newInstance(Class<C> clazz, Constructor<?> constructor, Object... args) {
        return clazz.cast(newInstance(constructor, args));
    }
    public static Object newInstance(Constructor<?> constructor, Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean hasClass(String className) {
        return getClass(className) != null;
    }
    public static boolean hasAddonClass(String className, Addon addonMain) {
        return getAddonClass(className, addonMain) != null;
    }

    public static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    public static Class<?> getAddonClass(String className, Addon addonMain) {
        try {
            return Class.forName(className, true, addonMain.getClass().getClassLoader());
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    public static Player NMSToBukkitPlayer(Object nmsPlayer) {
        if (getBukkitPlayerMethod == null) {
            try {
                getBukkitPlayerMethod = nmsPlayer.getClass().getMethod("getBukkitEntity");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return invokeMethod(Player.class, getBukkitPlayerMethod, nmsPlayer);
    }

    public static Object bukkitToNMSPlayer(Player player) {
        try {
            return invokeMethod(player.getClass().getMethod("getHandle"), player);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Entity getCraftEntity(Object nmsEntity) {
        try {
            return invokeMethod(Entity.class, nmsEntity.getClass().getMethod("getBukkitEntity"), nmsEntity);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static ItemStack NMSToBukkitItemStack(Object itemStack) {
        try {
            Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit.inventory.CraftItemStack");
            Method asBukkitCopy = craftItemStack.getMethod("asBukkitCopy", itemStack.getClass());
            return invokeStaticMethod(ItemStack.class, asBukkitCopy, itemStack);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
