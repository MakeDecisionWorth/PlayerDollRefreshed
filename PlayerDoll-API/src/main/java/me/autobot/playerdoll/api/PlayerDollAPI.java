package me.autobot.playerdoll.api;

import me.autobot.playerdoll.api.command.CommandBuilder;
import me.autobot.playerdoll.api.config.ConfigLoader;
import me.autobot.playerdoll.api.connection.Connection;
import me.autobot.playerdoll.api.registry.AddonRegistry;
import me.autobot.playerdoll.api.scheduler.SchedulerAPI;

import java.util.logging.Logger;

public final class PlayerDollAPI {
    private static PlayerDollPlugin instance = null;
    private static SchedulerAPI scheduler = null;

    private static Connection connection = null;
    private PlayerDollAPI() {}


    public static PlayerDollPlugin getInstance() {
        if (instance == null || !instance.isEnabled()) {
            throw new IllegalStateException("Cannot Find PlayerDoll or Plugin has not Enabled.");
        }
        return instance;
    }
    public static void setInstance(PlayerDollPlugin plugin) {
        instance = plugin;
    }
    public static void removeInstance() {
        instance = null;
    }


    public static Logger getLogger() {
        return instance.getLogger();
    }


    public static SchedulerAPI getScheduler() {
        if (scheduler == null) {
            throw new IllegalStateException("Cannot Find Scheduler.");
        }
        return scheduler;
    }
    public static void setScheduler(SchedulerAPI schedulerImpl) {
        scheduler = schedulerImpl;
    }


    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connectionImpl) {
        connection = connectionImpl;
    }


    public static FileUtil getFileUtil() {
        return instance.getFileUtil();
    }
    public static ConfigLoader getConfigLoader() {
        return instance.getConfigLoader();
    }
    public static int getOriginalMaxPlayer() {
        return instance.getOriginalMaxPlayer();
    }

    public static AddonRegistry getAddonRegistry() {
        return instance.getAddonRegistry();
    }

    public static CommandBuilder getCommandBuilder() {
        return instance.getCommandBuilder();
    }

}
