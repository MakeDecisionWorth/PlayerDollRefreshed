package me.autobot.playerdoll.api.doll;

import me.autobot.playerdoll.api.PlayerDollAPI;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public interface Doll extends BaseEntity {
    void dollDisconnect();
    void setDollMaxUpStep(double d);
    Player getCaller();
    static void resetPhantomStatistic(Player player) {
        player.setStatistic(Statistic.TIME_SINCE_REST, 0);
    }
    static long getTickCount(Player player) {
        return player.getWorld().getGameTime();
    }
    default void applyMetadata() {
        Player player = getBukkitPlayer();
        Plugin plugin = PlayerDollAPI.getInstance();
        FixedMetadataValue value = new FixedMetadataValue(plugin, null);
        for (String metadata : PlayerDollAPI.getConfigLoader().getBasicConfig().dollMetadata.getValue()) {
            if (metadata.isEmpty()) {
                continue;
            }
            player.setMetadata(metadata, value);
        }
    }
}
