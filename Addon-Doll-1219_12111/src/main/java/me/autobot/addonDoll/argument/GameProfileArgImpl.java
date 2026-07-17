package me.autobot.addonDoll.argument;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.autobot.playerdoll.api.command.argument.GameProfileArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.players.NameAndId;

import java.util.Collection;
import java.util.List;

public class GameProfileArgImpl extends GameProfileArgument {

    public GameProfileArgImpl() {
        super();
    }
    @Override
    public ArgumentType<?> getGameProfileArgument() {
        return net.minecraft.commands.arguments.GameProfileArgument.gameProfile();
    }

    @Override
    public Collection<GameProfile> getGameProfiles(CommandContext<?> commandcontext, String s) throws CommandSyntaxException {
        Collection<NameAndId> nameAndIds = net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles((CommandContext<CommandSourceStack>) commandcontext, s);
        return nameAndIds.stream().map(n -> new GameProfile(n.id(), n.name())).collect(java.util.stream.Collectors.toList());
    }
}
