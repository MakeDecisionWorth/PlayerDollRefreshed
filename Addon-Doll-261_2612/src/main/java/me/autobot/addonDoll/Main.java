package me.autobot.addonDoll;

import me.autobot.addonDoll.action.TracerImpl;
import me.autobot.addonDoll.argument.GameProfileArgImpl;
import me.autobot.addonDoll.argument.RotationArgImpl;
import me.autobot.addonDoll.argument.Vec3ArgImpl;
import me.autobot.addonDoll.connection.InProcessConnection;
import me.autobot.addonDoll.wrapper.*;
import me.autobot.playerdoll.api.Addon;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.action.pack.Tracer;
import me.autobot.playerdoll.api.wrapper.WrapperRegistry;

public class Main implements Addon {
    @Override
    public void onEnable() {
        new GameProfileArgImpl();
        new RotationArgImpl();
        new Vec3ArgImpl();
        Tracer.setTracer(new TracerImpl());
        PlayerDollAPI.setConnection(new InProcessConnection());
        WrapperRegistry.put(WBlockStateImpl.class);
        WrapperRegistry.put(WEntityImpl.class);
        WrapperRegistry.put(WServerLevelImpl.class);
        WrapperRegistry.put(WDirectionImpl.class);
        WrapperRegistry.put(WServerPlayerActionImpl.class);
        WrapperRegistry.put(WBlockHitResultImpl.class);
        WrapperRegistry.put(WBlockPosImpl.class);
        WrapperRegistry.put(WEntityHitResultImpl.class);
        WrapperRegistry.put(WHitResultImpl.class);
        WrapperRegistry.put(WInteractionResultImpl.class);
        WrapperRegistry.put(WVec2Impl.class);
        WrapperRegistry.put(WVec3Impl.class);
    }

    @Override
    public void onDisable() {

    }
}
