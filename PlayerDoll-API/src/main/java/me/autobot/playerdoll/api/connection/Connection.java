package me.autobot.playerdoll.api.connection;

import com.mojang.authlib.GameProfile;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class Connection {

    public void connect(String name, UUID uuid, Player caller) {
        connect(new GameProfile(uuid, name), caller);
    }
    public abstract void connect(GameProfile profile, Player caller);

    public abstract void shutDown();
}
